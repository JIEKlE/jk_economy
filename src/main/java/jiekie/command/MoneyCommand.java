package jiekie.command;

import jiekie.EconomyPlugin;
import jiekie.api.NicknameAPI;
import jiekie.manager.MoneyManager;
import jiekie.model.PlayerNameData;
import jiekie.util.ChatUtil;
import jiekie.util.NumberUtil;
import jiekie.util.SoundUtil;
import jiekie.util.StringUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MoneyCommand implements CommandExecutor {
    private final EconomyPlugin plugin;

    public MoneyCommand(EconomyPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if(args == null || args.length == 0) {
            ChatUtil.moneyCommandHelper(sender);
            return true;
        }

        switch(args[0]) {
            case "확인":
                checkMoney(sender, args);
                break;

            case "송금":
                payMoney(sender, args);
                break;

            case "추가":
                setMoney(sender, args, "ADD");
                break;

            case "차감":
                setMoney(sender, args, "SUBTRACT");
                break;

            case "설정":
                setMoney(sender, args, "SET");
                break;

            case "순위":
                moneyRank(sender, args);
                break;

            case "도움말":
                ChatUtil.moneyCommandList(sender);
                break;

            default:
                ChatUtil.moneyCommandHelper(sender);
                break;
        }

        return true;
    }

    private void checkMoney(CommandSender sender, String[] args) {
        Player targetPlayer = (Player) sender;
        String targetPlayerName = targetPlayer.getName();
        UUID targetPlayerUuid = targetPlayer.getUniqueId();

        if(args.length > 1) {
            if(!sender.isOp()) {
                ChatUtil.notOp(sender);
                return;
            }

            targetPlayerName = StringUtil.getContents(args, 1);
            targetPlayer = NicknameAPI.getInstance().getPlayerByNameOrNickname(targetPlayerName);
            if(targetPlayer == null) {
                ChatUtil.showMessage(sender, ChatUtil.PLAYER_NOT_FOUND);
                return;
            }
            targetPlayerUuid = targetPlayer.getUniqueId();
        }

        int money = plugin.getMoneyManager().getMoney(targetPlayerUuid);
        String formattedMoney = NumberUtil.getFormattedMoney(money);

        PlayerNameData playerNameData = NicknameAPI.getInstance().getPlayerNameData(targetPlayerUuid);
        if(playerNameData != null)
            targetPlayerName = playerNameData.getNickname();

        ChatUtil.checkMoney(sender, targetPlayerName, formattedMoney);
        SoundUtil.playNoteBlockBell((Player) sender);
    }

    private void payMoney(CommandSender sender, String[] args) {
        // validation
        if(!(sender instanceof Player player)) {
            ChatUtil.notPlayer(sender);
            return;
        }

        if(args.length < 3) {
            player.sendMessage(ChatUtil.wrongCommand() + " (/돈 송금 금액 플레이어ID|닉네임)");
            return;
        }

        int amountOfMoney;
        try {
            amountOfMoney = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            ChatUtil.showMessage(player, ChatUtil.MONEY_NOT_NUMBER);
            return;
        }

        if(amountOfMoney <= 0) {
            ChatUtil.showMessage(player, ChatUtil.MINUS_MONEY);
            return;
        }

        MoneyManager moneyManager = plugin.getMoneyManager();
        UUID playerUuid = player.getUniqueId();
        int playerMoney = moneyManager.getMoney(playerUuid);
        if(playerMoney < amountOfMoney) {
            ChatUtil.showMessage(player, ChatUtil.NOT_ENOUGH_MONEY);
            return;
        }

        String targetPlayerName = StringUtil.getContents(args, 2);
        Player targetPlayer = NicknameAPI.getInstance().getPlayerByNameOrNickname(targetPlayerName);
        if(targetPlayer == null) {
            ChatUtil.showMessage(player, ChatUtil.PLAYER_NOT_FOUND);
            return;
        }

        // pay
        UUID targetPlayerUuid = targetPlayer.getUniqueId();
        moneyManager.subtractMoney(playerUuid, amountOfMoney);
        moneyManager.addMoney(targetPlayerUuid, amountOfMoney);

        PlayerNameData playerNameData = NicknameAPI.getInstance().getPlayerNameData(playerUuid);
        String playerName = player.getName();
        if(playerNameData != null)
            playerName = playerNameData.getNickname();

        String formattedMoney = NumberUtil.getFormattedMoney(amountOfMoney);
        ChatUtil.payMoney(player, targetPlayerName, formattedMoney);
        SoundUtil.playNoteBlockBell(player);

        ChatUtil.moneyIsPaid(targetPlayer, playerName, formattedMoney);
        SoundUtil.playNoteBlockBell(targetPlayer);
    }

    private void setMoney(CommandSender sender, String[] args, String operation) {
        // validation
        if(args.length < 3) {
            sender.sendMessage(ChatUtil.wrongCommand() + " (/돈 추가|차감|설정 금액 플레이어ID|닉네임)");
            return;
        }

        if(!sender.isOp()) {
            ChatUtil.notOp(sender);
            return;
        }

        int amountOfMoney;
        try {
            amountOfMoney = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            ChatUtil.showMessage(sender, ChatUtil.MONEY_NOT_NUMBER);
            return;
        }

        if(!operation.equals("SET") && amountOfMoney <= 0) {
            ChatUtil.showMessage(sender, ChatUtil.MINUS_MONEY);
            return;
        }

        String targetPlayerName = StringUtil.getContents(args, 2);
        Player targetPlayer = NicknameAPI.getInstance().getPlayerByNameOrNickname(targetPlayerName);
        if(targetPlayer == null) {
            ChatUtil.showMessage(sender, ChatUtil.PLAYER_NOT_FOUND);
            return;
        }

        MoneyManager moneyManager = plugin.getMoneyManager();
        String formattedMoney = NumberUtil.getFormattedMoney(amountOfMoney);

        // operation
        switch (operation) {
            case "ADD" -> moneyManager.addMoney(targetPlayer.getUniqueId(), amountOfMoney);
            case "SUBTRACT" -> moneyManager.subtractMoney(targetPlayer.getUniqueId(), amountOfMoney);
            case "SET" -> moneyManager.setMoney(targetPlayer.getUniqueId(), amountOfMoney);
        }

        if(sender instanceof Player) {
            // chat
            switch (operation) {
                case "ADD" -> {
                    ChatUtil.addMoney(sender, targetPlayerName, formattedMoney);
                    ChatUtil.moneyIsAdded(targetPlayer, formattedMoney);
                }
                case "SUBTRACT" -> {
                    ChatUtil.subtractMoney(sender, targetPlayerName, formattedMoney);
                    ChatUtil.moneyIsSubtracted(targetPlayer, formattedMoney);
                }
                case "SET" -> {
                    ChatUtil.setMoney(sender, targetPlayerName, formattedMoney);
                    ChatUtil.moneyIsSet(targetPlayer, formattedMoney);
                }
            }

            // sound effect
            SoundUtil.playNoteBlockBell((Player) sender);
            SoundUtil.playNoteBlockBell(targetPlayer);
        }
    }

    private void moneyRank(CommandSender sender, String[] args) {
        if(!sender.isOp()) {
            ChatUtil.notOp(sender);
            return;
        }

        int page = 1;
        if(args.length > 1) {
            try {
                page = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                ChatUtil.showMessage(sender, ChatUtil.PAGE_NOT_NUMBER);
                return;
            }
        }

        List<Map.Entry<UUID, Integer>> sortedMoneyList = plugin.getMoneyManager().getSortedMoneyList();

        int pageSize = 10;
        int start = (page - 1) * pageSize;
        int end = Math.min(start + pageSize, sortedMoneyList.size());

        if(start >= sortedMoneyList.size()) {
            ChatUtil.showMessage(sender, ChatUtil.PAGE_DOES_NOT_EXIST);
            return;
        }

        ChatUtil.rankMoneyPrefix(sender);

        for(int i = start; i < end; i++) {
            UUID uuid = sortedMoneyList.get(i).getKey();
            String name = uuid.toString();
            int rank = i + 1;
            int money = sortedMoneyList.get(i).getValue();
            String formattedMoney = NumberUtil.getFormattedMoney(money);

            PlayerNameData playerNameData = NicknameAPI.getInstance().getPlayerNameData(uuid);
            if(playerNameData != null)
                name = playerNameData.getNickname();

            ChatUtil.rankMoney(sender, rank, name, formattedMoney);
        }

        ChatUtil.horizontalLineSuffix(sender);
    }
}
