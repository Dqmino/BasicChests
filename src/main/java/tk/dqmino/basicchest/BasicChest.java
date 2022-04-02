package tk.dqmino.basicchest;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;
import tk.dqmino.basicchest.blocking.impl.InventoryEncoder;
import tk.dqmino.basicchest.blocking.impl.MongoDatabase;
import tk.dqmino.basicchest.blocking.impl.MongoStorage;
import tk.dqmino.basicchest.blocking.interfaces.Base64Encoder;
import tk.dqmino.basicchest.blocking.interfaces.Storage;
import tk.dqmino.basicchest.commands.PlayerChestCommand;
import tk.dqmino.basicchest.enums.DatabaseCredential;
import tk.dqmino.basicchest.listener.JoinListener;
import tk.dqmino.basicchest.listener.QuitListener;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static tk.dqmino.basicchest.enums.DatabaseCredential.*;

public final class BasicChest extends JavaPlugin {

    private static BasicChest instance;
    private RedisClient redisClient;
    private StatefulRedisConnection<String, String> connection;
    private Set<Inventory> playerChestsOpen;

    public static BasicChest getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        this.playerChestsOpen = new HashSet<>();
        saveDefaultConfig();
        redisClient = RedisClient.create("redis://localhost/0");
        connection = redisClient.connect();
        MongoDatabase db = new MongoDatabase(getCredential(USERNAME),
                Integer.parseInt(getCredential(PORT)), getCredential(IP), getCredential(PASSWORD));
        Base64Encoder<Inventory> encoder = new InventoryEncoder();
        Storage storage = new MongoStorage(db, encoder);
        getServer().getPluginManager().registerEvents(new JoinListener(storage, connection), this);
        getServer().getPluginManager().registerEvents(new QuitListener(storage, connection), this);
        Objects.requireNonNull(getCommand("chest")).setExecutor(new PlayerChestCommand(connection, encoder));
    }

    @Override
    public void onDisable() {
        connection.close();
        redisClient.shutdown();
    }

    public String getCredential(DatabaseCredential type) {
        String typeName = type.toString().toLowerCase();
        String result = getConfig().getString(typeName);
        if (result == null) {
            String errorMessage = "Credentail %cred% was not found in config.yml, please fill it.".replace("%cred%", typeName);
            getLogger().warning(errorMessage);
            throw new IllegalArgumentException(errorMessage, new NullPointerException());
        }
        return result;
    }

    public Set<Inventory> getPlayerChestsOpen() {
        return playerChestsOpen;
    }
}
