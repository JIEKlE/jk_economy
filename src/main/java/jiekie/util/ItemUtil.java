package jiekie.util;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemUtil {
    public static boolean isSameItem(ItemStack a, ItemStack b) {
        if(a == null || b == null) return false;
        if(a.getType() != b.getType()) return false;

        ItemMeta metaA = a.getItemMeta();
        ItemMeta metaB = b.getItemMeta();

        if(metaA == null && metaB == null) return true;
        if(metaA == null || metaB == null) return false;

        if(metaA.hasDisplayName() != metaB.hasDisplayName()) return false;
        if(metaA.hasDisplayName() && !metaA.getDisplayName().equals(metaB.getDisplayName())) return false;

        if(metaA.hasCustomModelData() != metaB.hasCustomModelData()) return false;
        if(metaA.hasCustomModelData() && metaA.getCustomModelData() != metaB.getCustomModelData()) return false;

        if(!(metaA instanceof Damageable) || !(metaB instanceof Damageable)) return true;
        Damageable damageableA = (Damageable) metaA;
        Damageable damageableB = (Damageable) metaB;
        if(damageableA.getDamage() != damageableB.getDamage()) return false;

        return true;
    }
}
