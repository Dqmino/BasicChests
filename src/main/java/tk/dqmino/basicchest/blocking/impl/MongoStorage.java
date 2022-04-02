package tk.dqmino.basicchest.blocking.impl;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import tk.dqmino.basicchest.blocking.interfaces.Base64Encoder;
import tk.dqmino.basicchest.blocking.interfaces.Storage;

import java.io.IOException;
import java.util.UUID;

public class MongoStorage implements Storage {

    private final MongoDatabase db;
    private final Base64Encoder<Inventory> encoder;

    public MongoStorage(MongoDatabase database, Base64Encoder<Inventory> encoder) {
        this.db = database;
        this.encoder = encoder;
    }

    @Override
    public Inventory loadInventory(UUID uuid) throws IOException {
        DBObject object = new BasicDBObject("uuid", uuid);
        DBObject found = db.getPlayersCollection().findOne(object);
        if (found == null) {
            putInventory(uuid, Bukkit.createInventory(null, 27));
            return null;
        }
        return encoder.fromBase64((String) found.get("playerchest"));
    }

    @Override
    public boolean putInventory(UUID uuid, Inventory inventoryToPut) {
        DBObject object = new BasicDBObject("uuid", uuid);
        String encodedInventory = encoder.toBase64(inventoryToPut);
        object.put("playerchest", encodedInventory);
        db.getPlayersCollection().insert(object);
        return db.getPlayersCollection().find(object).size() != 0;
    }

    @Override
    public void updateInventory(UUID uuid, Inventory inventory) {
        DBObject object = new BasicDBObject("uuid", uuid);
        DBObject found = db.getPlayersCollection().findOne(object);
        if (found == null) {
            putInventory(uuid, inventory);
            return;
        }

        BasicDBObject set = new BasicDBObject("$set", object);
        set.append("$set", new BasicDBObject("playerchest", encoder.toBase64(inventory)));
        db.getPlayersCollection().update(found, set);
    }

    public Base64Encoder<Inventory> getEncoder() {
        return encoder;
    }
}
