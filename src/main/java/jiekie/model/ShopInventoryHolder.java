package jiekie.model;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public record ShopInventoryHolder(String name, boolean isSettingMode) implements InventoryHolder {
    @Override
    public @NotNull Inventory getInventory() {
        return null;
    }
}
