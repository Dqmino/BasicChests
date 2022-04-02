package tk.dqmino.basicchest.listener;

import io.lettuce.core.api.StatefulRedisConnection;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import tk.dqmino.basicchest.BasicChest;
import tk.dqmino.basicchest.blocking.interfaces.Storage;

import java.io.IOException;
import java.util.UUID;

public class JoinListener implements Listener {

    private final Storage db;
    private final StatefulRedisConnection<String, String> connection;

    public JoinListener(Storage database, StatefulRedisConnection<String, String> connection) {
        this.db = database;
        this.connection = connection;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();

        Bukkit.getScheduler().runTaskAsynchronously(BasicChest.getInstance(), () -> {
            try {
                connection.async().set(uuid.toString(), db.getEncoder().toBase64(db.loadInventory(uuid)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

    }
}
