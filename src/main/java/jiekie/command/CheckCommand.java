package jiekie.command;

import jiekie.EconomyPlugin;
import jiekie.manager.MoneyManager;
import jiekie.util.ChatUtil;
import jiekie.util.NumberUtil;
import jiekie.util.SoundUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class CheckCommand implements CommandExecutor {
    private final EconomyPlugin plugin;

    public CheckCommand(EconomyPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if(!(sender instanceof Player player)) {
            ChatUtil.notPlayer(sender);
            return true;
        }

        if(args == null || args.length == 0) {
            ChatUtil.checkCommandHelper(player);
            return true;
        }

        if (args[0].equals("도움말"))
            ChatUtil.checkCommandList(player);
        else
            createCheck(player, args);

        return true;
    }

    private void createCheck(Player player, String[] args) {
        int amountOfMoney;
        int amountOfItem = 1;

        try {
            amountOfMoney = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            ChatUtil.showErrorMessage(player, ChatUtil.MONEY_NOT_NUMBER);
            return;
        }

         if(amountOfMoney <= 0) {
             ChatUtil.showErrorMessage(player, ChatUtil.MINUS_MONEY);
             return;
         }

        if(args.length > 1) {
            try {
                amountOfItem = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                ChatUtil.showErrorMessage(player, ChatUtil.AMOUNT_OF_ITEM_NOT_NUMBER);
                return;
            }

            if(amountOfItem <= 0) {
                ChatUtil.showErrorMessage(player, ChatUtil.MINUS_AMOUNT_OF_ITEM);
                return;
            }
        }

        MoneyManager moneyManager = plugin.getMoneyManager();
        UUID uuid = player.getUniqueId();
        int totalAmountOfMoney = amountOfMoney * amountOfItem;
        int playerMoney = moneyManager.getMoney(uuid);
        if(playerMoney < totalAmountOfMoney) {
            ChatUtil.showErrorMessage(player, ChatUtil.NOT_ENOUGH_MONEY);
            return;
        }

        PlayerInventory inventory = player.getInventory();
        if(inventory.firstEmpty() == -1) {
            ChatUtil.showErrorMessage(player, ChatUtil.INVENTORY_FULL);
            return;
        }

        moneyManager.subtractMoney(uuid, totalAmountOfMoney);

        ItemStack check = new ItemStack(Material.PAPER);
        ItemMeta meta = check.getItemMeta();
        meta.setDisplayName(ChatColor.RESET + NumberUtil.getFormattedMoney(amountOfMoney));
        meta.setCustomModelData(150);
        check.setItemMeta(meta);
        check.setAmount(amountOfItem);
        inventory.addItem(check);

        ChatUtil.createCheck(player);
        SoundUtil.playNoteBlockBell(player);
    }
}
