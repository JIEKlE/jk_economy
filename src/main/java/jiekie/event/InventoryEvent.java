package jiekie.event;

import jiekie.EconomyPlugin;
import jiekie.exception.ShopException;
import jiekie.model.ShopInventoryHolder;
import jiekie.model.ShopItem;
import jiekie.util.ChatUtil;
import jiekie.util.ItemUtil;
import jiekie.util.NumberUtil;
import jiekie.util.SoundUtil;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryEvent implements Listener {
    private final EconomyPlugin plugin;

    public InventoryEvent(EconomyPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        onShopInventoryClick(e);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        onShopInventoryClose(e);
    }

    private void onShopInventoryClick(InventoryClickEvent e) {
        HumanEntity humanEntity = e.getWhoClicked();
        if(!(humanEntity instanceof Player player)) return;

        Inventory inventory = e.getClickedInventory();
        if(inventory == null) return;
        if(!(inventory.getHolder() instanceof ShopInventoryHolder holder)) return;
        if(holder.isSettingMode()) return;

        e.setCancelled(true);

        ItemStack item = e.getCurrentItem();
        if(item == null || item.getType() == Material.AIR) return;

        try {
            ShopItem shopItem = plugin.getShopManager().getShopItemOrThrow(holder.name(), e.getSlot());

            boolean shiftClick = e.isShiftClick();
            boolean rightClick = e.getClick().isRightClick();

            if(rightClick)
                handleSell(player, shopItem, shiftClick);
            else
                handleBuy(player, shopItem, shiftClick);

            plugin.getShopManager().setInventoryForTrade(inventory, holder.name());

        } catch (ShopException ex) {
            ChatUtil.showMessage(player, ex.getMessage());
        }
    }

    private void handleBuy(Player player, ShopItem shopItem, boolean shiftClick) {
        if(shopItem.getCurrentBuyPrice() <= 0) {
            ChatUtil.showMessage(player, ChatUtil.BUY_NOT_ALLOWED);
            return;
        }

        if(player.getInventory().firstEmpty() == -1) {
            ChatUtil.showMessage(player, ChatUtil.INVENTORY_FULL);
            return;
        }

        int amountOfItem = shiftClick ? 64 : 1;
        if(shopItem.getMaxStock() > 0) {
            int available = shopItem.getAvailableStock();
            if(available < amountOfItem)
                amountOfItem = available;

            if(amountOfItem == 0) {
                ChatUtil.showMessage(player, ChatUtil.OUT_OF_STOCK);
                return;
            }
        }

        int totalCost = shopItem.getCurrentBuyPrice() * amountOfItem;
        if(plugin.getMoneyManager().getMoney(player.getUniqueId()) < totalCost) {
            ChatUtil.showMessage(player, ChatUtil.NOT_ENOUGH_MONEY);
            return;
        }

        plugin.getMoneyManager().subtractMoney(player.getUniqueId(), totalCost);
        player.getInventory().addItem(createItem(shopItem.getItem(), amountOfItem));

        if(shopItem.getMaxStock() > 0)
            shopItem.setAvailableStock(shopItem.getAvailableStock() - amountOfItem);

        ChatUtil.buyItem(player, NumberUtil.getFormattedMoney(totalCost));
        SoundUtil.playNoteBlockBell(player);
    }

    private ItemStack createItem(ItemStack item, int amount) {
        ItemStack clone = item.clone();
        clone.setAmount(amount);
        return clone;
    }

    private void handleSell(Player player, ShopItem shopItem, boolean shiftClick) {
        if(shopItem.getCurrentSellPrice() <= 0) {
            ChatUtil.showMessage(player, ChatUtil.SELL_NOT_ALLOWED);
            return;
        }

        int amountOfItem = shiftClick ? 64 : 1;
        int count = countItem(player.getInventory(), shopItem.getItem());

        if(count < amountOfItem)
            amountOfItem = count;

        if(amountOfItem == 0) {
            ChatUtil.showMessage(player, ChatUtil.NO_ITEM_TO_SELL);
            return;
        }

        removeItem(player.getInventory(), shopItem.getItem(), amountOfItem);
        int totalCost = shopItem.getCurrentSellPrice() * amountOfItem;
        plugin.getMoneyManager().addMoney(player.getUniqueId(), totalCost);

        ChatUtil.sellItem(player, NumberUtil.getFormattedMoney(totalCost));
        SoundUtil.playNoteBlockBell(player);
    }

    private int countItem(Inventory inventory, ItemStack target) {
        int count = 0;

        for(ItemStack item : inventory.getContents()) {
            if(!ItemUtil.isSameItem(item, target)) continue;
            count += item.getAmount();
        }

        return count;
    }

    private void removeItem(Inventory inventory, ItemStack target, int amount) {
        for(ItemStack item : inventory.getContents()) {
            if(!ItemUtil.isSameItem(item, target)) continue;
            int toRemove = Math.min(item.getAmount(), amount);
            item.setAmount(item.getAmount() - toRemove);
            amount -= toRemove;
            if(amount <= 0) break;
        }
    }

    private void onShopInventoryClose(InventoryCloseEvent e) {
        Inventory inventory = e.getInventory();
        if(!(inventory.getHolder() instanceof ShopInventoryHolder holder)) return;
        if(!holder.isSettingMode()) return;

        HumanEntity humanEntity = e.getPlayer();
        if(!(humanEntity instanceof Player player)) return;

        try {
            plugin.getShopManager().setShopItems(holder.name(), inventory);
            ChatUtil.showMessage(player, ChatUtil.SET_SHOP_ITEMS);
            SoundUtil.playNoteBlockBell(player);

        } catch (ShopException ex) {
            ChatUtil.showMessage(player, ex.getMessage());
        }
    }
}
