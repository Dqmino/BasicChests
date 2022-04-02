package tk.dqmino.basicchest.blocking.interfaces;

import org.bukkit.inventory.Inventory;

import java.io.IOException;
import java.util.UUID;

public interface Storage {

    Inventory loadInventory(UUID uuid) throws IOException;

    boolean putInventory(UUID uuid, Inventory inventory);

    void updateInventory(UUID uuid, Inventory inventory);

    Base64Encoder<Inventory> getEncoder();

}
