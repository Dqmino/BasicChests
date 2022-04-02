package tk.dqmino.basicchest.listener;

import io.lettuce.core.api.StatefulRedisConnection;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import tk.dqmino.basicchest.BasicChest;
import tk.dqmino.basicchest.blocking.interfaces.Base64Encoder;

public class InventoryCloseListener implements Listener {

    private final StatefulRedisConnection<String, String> connection;
    private final Base64Encoder<Inventory> encoder;

    public InventoryCloseListener(StatefulRedisConnection<String, String> stringStringStatefulRedisConnection, Base64Encoder<Inventory> encoder) {
        connection = stringStringStatefulRedisConnection;
        this.encoder = encoder;
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        // if event inventory is a player chest
        if (!BasicChest.getInstance().getPlayerChestsOpen().removeIf((event.getInventory()::equals))) return;
        Bukkit.getScheduler().runTaskAsynchronously(BasicChest.getInstance(), () -> connection.async()
                .set(event.getPlayer().getUniqueId().toString(), encoder.toBase64(event.getInventory())));
    }

}
