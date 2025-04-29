package jiekie.model;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public class ShopInventoryHolder implements InventoryHolder {
    private final String name;
    private final boolean isSettingMode;

    public ShopInventoryHolder(String name, boolean isSettingMode) {
        this.name = name;
        this.isSettingMode = isSettingMode;
    }

    public String getName() {
        return name;
    }

    public boolean isSettingMode() {
        return isSettingMode;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return null;
    }
}
