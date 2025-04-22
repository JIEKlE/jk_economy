package jiekie.command;

import jiekie.EconomyPlugin;
import jiekie.util.ChatUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MoneyCommand implements CommandExecutor {
    private final EconomyPlugin plugin;

    public MoneyCommand(EconomyPlugin plugin) {
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
            ChatUtil.moneyCommandHelper(player);
            return true;
        }

        switch(args[0]) {
            case "확인":
                checkMoney(player, args);
                break;

            case "송금":
                payMoney(player, args);
                break;

            case "추가":
                addMoney(player, args);
                break;

            case "차감":
                subtractMoney(player, args);
                break;

            case "설정":
                setMoney(player, args);
                break;

            case "순위":
                moneyRank(player, args);
                break;

            case "도움말":
                ChatUtil.moneyCommandList(player);
                break;

            default:
                ChatUtil.moneyCommandHelper(player);
                break;
        }

        return true;
    }

    private void checkMoney(Player player, String[] args) {

    }

    private void payMoney(Player player, String[] args) {

    }

    private void addMoney(Player player, String[] args) {

    }

    private void subtractMoney(Player player, String[] args) {

    }

    private void setMoney(Player player, String[] args) {

    }

    private void moneyRank(Player player, String[] args) {

    }
}
