package jiekie.event;

import jiekie.EconomyPlugin;
import jiekie.manager.MoneyManager;
import jiekie.util.SoundUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.UUID;

public class PlayerEvent implements Listener {
    private final EconomyPlugin plugin;

    public PlayerEvent(EconomyPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        setMoney(e);
    }

    private void setMoney(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        MoneyManager moneyManager = plugin.getMoneyManager();
        UUID uuid = player.getUniqueId();

        if(moneyManager.containsPlayer(uuid)) return;

        moneyManager.setMoney(uuid, 30000);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        removeCheck(e);
    }

    private void removeCheck(PlayerInteractEvent e) {
        if (!e.getHand().equals(EquipmentSlot.HAND)) return;
        Player player = e.getPlayer();
        PlayerInventory inventory = player.getInventory();
        ItemStack item = inventory.getItemInMainHand();

        if(item == null || item.getType() != Material.PAPER) return;

        ItemMeta meta = item.getItemMeta();
        if(meta == null || !meta.hasDisplayName()) return;
        if(meta.getCustomModelData() != 150) return;

        MoneyManager moneyManager = plugin.getMoneyManager();
        int amountOfMoney = moneyManager.getUnformattedMoney(meta.getDisplayName());
        int amountOfItem = 1;
        if(player.isSneaking())
            amountOfItem = item.getAmount();

        int totalAmountOfMoney = amountOfMoney * amountOfItem;
        moneyManager.addMoney(player.getUniqueId(), totalAmountOfMoney);

        int totalAmountOfItem = item.getAmount() - amountOfItem;
        if(totalAmountOfItem > 0)
            item.setAmount(totalAmountOfItem);
        else
            inventory.setItemInMainHand(null);

        SoundUtil.playNoteBlockBell(player);
    }
}
