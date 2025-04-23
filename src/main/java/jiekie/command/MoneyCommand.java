package jiekie.command;

import jiekie.EconomyPlugin;
import jiekie.api.NicknameAPI;
import jiekie.money.MoneyManager;
import jiekie.util.ChatUtil;
import jiekie.util.PlayerNameData;
import jiekie.util.SoundUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.UUID;

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
                setMoney(player, args, "ADD");
                break;

            case "차감":
                setMoney(player, args, "SUBTRACT");
                break;

            case "설정":
                setMoney(player, args, "SET");
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
        Player targetPlayer = player;
        String targetPlayerName = targetPlayer.getName();
        UUID targetPlayerUuid = targetPlayer.getUniqueId();

        if(args.length > 1) {
            if(!player.isOp()) {
                ChatUtil.notOp(player);
                return;
            }

            targetPlayerName = getContents(args, 1);
            targetPlayer = NicknameAPI.getInstance().getPlayerByNameOrNickname(targetPlayerName);
            if(targetPlayer == null) {
                ChatUtil.showErrorMessage(targetPlayer, ChatUtil.PLAYER_NOT_FOUND);
                return;
            }
            targetPlayerUuid = targetPlayer.getUniqueId();
        }

        MoneyManager moneyManager = plugin.getMoneyManager();
        int money = moneyManager.getMoney(targetPlayerUuid);
        String formattedMoney = moneyManager.getFormattedMoney(money);

        PlayerNameData playerNameData = NicknameAPI.getInstance().getPlayerNameData(targetPlayerUuid);
        if(playerNameData != null)
            targetPlayerName = playerNameData.getNickname();

        ChatUtil.checkMoney(player, targetPlayerName, formattedMoney);
        SoundUtil.playNoteBlockBell((Player) player);
    }

    private void payMoney(Player player, String[] args) {
        if(args.length < 3) {
            player.sendMessage(ChatUtil.wrongCommand() + " (/돈 송금 금액 플레이어ID|닉네임)");
            return;
        }

        int amountOfMoney;
        try {
            amountOfMoney = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            ChatUtil.showErrorMessage(player, ChatUtil.INVALID_AMOUNT_OF_MONEY);
            return;
        }

        MoneyManager moneyManager = plugin.getMoneyManager();
        UUID playerUuid = player.getUniqueId();
        int playerMoney = moneyManager.getMoney(playerUuid);
        if(playerMoney < amountOfMoney) {
            ChatUtil.showErrorMessage(player, ChatUtil.NOT_ENOUGH_MONEY);
            return;
        }

        String targetPlayerName = getContents(args, 2);
        Player targetPlayer = NicknameAPI.getInstance().getPlayerByNameOrNickname(targetPlayerName);
        if(targetPlayer == null) {
            ChatUtil.showErrorMessage(player, ChatUtil.PLAYER_NOT_FOUND);
            return;
        }

        UUID targetPlayerUuid = targetPlayer.getUniqueId();
        moneyManager.subtractMoney(playerUuid, amountOfMoney);
        moneyManager.addMoney(targetPlayerUuid, amountOfMoney);

        PlayerNameData playerNameData = NicknameAPI.getInstance().getPlayerNameData(playerUuid);
        String playerName = player.getName();
        if(playerNameData != null)
            playerName = playerNameData.getNickname();

        String formattedMoney = moneyManager.getFormattedMoney(amountOfMoney);
        ChatUtil.payMoney(player, targetPlayerName, formattedMoney);
        SoundUtil.playNoteBlockBell(player);

        ChatUtil.moneyIsPaid(targetPlayer, playerName, formattedMoney);
        SoundUtil.playNoteBlockBell(targetPlayer);
    }

    private void setMoney(Player player, String[] args, String operation) {
        if(args.length < 3) {
            player.sendMessage(ChatUtil.wrongCommand() + " (/돈 추가|차감|설정 금액 플레이어ID|닉네임)");
            return;
        }

        if(!player.isOp()) {
            ChatUtil.notOp(player);
            return;
        }

        int amountOfMoney;
        try {
            amountOfMoney = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            ChatUtil.showErrorMessage(player, ChatUtil.INVALID_AMOUNT_OF_MONEY);
            return;
        }

        String targetPlayerName = getContents(args, 2);
        Player targetPlayer = NicknameAPI.getInstance().getPlayerByNameOrNickname(targetPlayerName);
        if(targetPlayer == null) {
            ChatUtil.showErrorMessage(player, ChatUtil.PLAYER_NOT_FOUND);
            return;
        }

        MoneyManager moneyManager = plugin.getMoneyManager();
        String formattedMoney = moneyManager.getFormattedMoney(amountOfMoney);

        if(operation.equals("ADD")) {
            moneyManager.addMoney(targetPlayer.getUniqueId(), amountOfMoney);
            ChatUtil.addMoney(player, targetPlayerName, formattedMoney);
            ChatUtil.moneyIsAdded(targetPlayer, formattedMoney);

        } else if(operation.equals("SUBTRACT")) {
            moneyManager.subtractMoney(targetPlayer.getUniqueId(), amountOfMoney);
            ChatUtil.subtractMoney(player, targetPlayerName, formattedMoney);
            ChatUtil.moneyIsSubtracted(targetPlayer, formattedMoney);

        } else if(operation.equals("SET")) {
            moneyManager.setMoney(targetPlayer.getUniqueId(), amountOfMoney);
            ChatUtil.setMoney(player, targetPlayerName, formattedMoney);
            ChatUtil.moneyIsSet(targetPlayer, formattedMoney);
        }

        SoundUtil.playNoteBlockBell(player);
        SoundUtil.playNoteBlockBell(targetPlayer);
    }

    private void moneyRank(Player player, String[] args) {
        if(!player.isOp()) {
            ChatUtil.notOp(player);
            return;
        }

        int page = 1;
        if(args.length > 1) {
            try {
                page = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                ChatUtil.showErrorMessage(player, ChatUtil.INVALID_PAGE);
                return;
            }
        }

        MoneyManager moneyManager = plugin.getMoneyManager();
        List<Map.Entry<UUID, Integer>> sortedMoneyList = moneyManager.getSortedMoneyList();

        int pageSize = 10;
        int start = (page - 1) * pageSize;
        int end = Math.min(start + pageSize, sortedMoneyList.size());

        if(start >= sortedMoneyList.size()) {
            ChatUtil.showErrorMessage(player, ChatUtil.PAGE_DOES_NOT_EXIST);
            return;
        }

        ChatUtil.rankMoneyPrefix(player);

        for(int i = start; i < end; i++) {
            UUID uuid = sortedMoneyList.get(i).getKey();
            String name = uuid.toString();
            int rank = i + 1;
            int money = sortedMoneyList.get(i).getValue();
            String formattedMoney = moneyManager.getFormattedMoney(money);

            PlayerNameData playerNameData = NicknameAPI.getInstance().getPlayerNameData(uuid);
            if(playerNameData != null)
                name = playerNameData.getNickname();

            ChatUtil.rankMoney(player, rank, name, formattedMoney);
        }

        ChatUtil.rankMoneySuffix(player);
    }

    private String getContents(String[] args, int startIndex) {
        StringBuffer sb = new StringBuffer();

        for(int i = startIndex; i < args.length; ++i) {
            if (i != startIndex) {
                sb.append(" ");
            }

            sb.append(args[i]);
        }

        String contents = sb.toString();
        return ChatColor.translateAlternateColorCodes('&', contents);
    }
}
