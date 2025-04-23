package jiekie.command;

import jiekie.EconomyPlugin;
import jiekie.manager.MoneyManager;
import jiekie.util.ChatUtil;
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

import java.util.UUID;

public class CheckCommand implements CommandExecutor {
    private final EconomyPlugin plugin;

    public CheckCommand(EconomyPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) {
            ChatUtil.notPlayer(sender);
            return true;
        }

        Player player = (Player) sender;
        if(args == null || args.length == 0) {
            ChatUtil.checkCommandHelper(player);
            return true;
        }

        switch(args[0]) {
            case "도움말":
                ChatUtil.moneyCommandList(player);
                break;

            default:
                createCheck(player, args);
                break;
        }

        return true;
    }

    private void createCheck(Player player, String[] args) {
        int amountOfMoney = 0;
        int amountOfItem = 1;

        try {
            amountOfMoney = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            ChatUtil.showErrorMessage(player, ChatUtil.INVALID_AMOUNT_OF_MONEY);
            return;
        }

        if(args.length > 1) {
            try {
                amountOfItem = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                ChatUtil.showErrorMessage(player, ChatUtil.INVALID_AMOUNT_OF_ITEM);
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
        meta.setDisplayName(ChatColor.RESET + moneyManager.getFormattedMoney(amountOfMoney));
        meta.setCustomModelData(150);
        check.setItemMeta(meta);
        check.setAmount(amountOfItem);
        inventory.addItem(check);

        ChatUtil.createCheck(player);
        SoundUtil.playNoteBlockBell(player);
    }
}
