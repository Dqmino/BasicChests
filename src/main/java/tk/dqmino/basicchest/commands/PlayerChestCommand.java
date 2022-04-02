package tk.dqmino.basicchest.commands;

import io.lettuce.core.RedisFuture;
import io.lettuce.core.api.StatefulRedisConnection;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import tk.dqmino.basicchest.BasicChest;
import tk.dqmino.basicchest.blocking.interfaces.Base64Encoder;

import java.io.IOException;
import java.util.UUID;

public class PlayerChestCommand implements CommandExecutor {

    private final StatefulRedisConnection<String, String> connection;
    private final Base64Encoder<Inventory> encoder;

    public PlayerChestCommand(StatefulRedisConnection<String, String> stringStringStatefulRedisConnection,
                              Base64Encoder<Inventory> encoder) {
        connection = stringStringStatefulRedisConnection;
        this.encoder = encoder;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        UUID uuid = ((Player) sender).getUniqueId();
        RedisFuture<String> query = connection.async().get(uuid.toString());
        query.whenComplete((encodedInventory, err) -> {
            try {
                Inventory inv = encoder.fromBase64(encodedInventory);
                ((Player) sender).openInventory(inv);
                BasicChest.getInstance().getPlayerChestsOpen().add(inv);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        return true;
    }
}
