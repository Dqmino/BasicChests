package tk.dqmino.basicchest.listener;

import io.lettuce.core.api.StatefulRedisConnection;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import tk.dqmino.basicchest.BasicChest;
import tk.dqmino.basicchest.blocking.interfaces.Storage;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class QuitListener implements Listener {


    private final Storage db;
    private final StatefulRedisConnection<String, String> connection;

    public QuitListener(Storage database, StatefulRedisConnection<String, String> connection) {
        this.db = database;
        this.connection = connection;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();

        Bukkit.getScheduler().runTaskAsynchronously(BasicChest.getInstance(), () -> {
            try {
                String encodedInv = connection.async().get(uuid.toString()).get();
                Inventory inventory = db.getEncoder().fromBase64(encodedInv);
                db.updateInventory(uuid, inventory);
            } catch (IOException | ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

}
