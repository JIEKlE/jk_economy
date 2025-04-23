package jiekie.util;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class ChatUtil {
    /* 에러 메시지 */
    public static String INVALID_AMOUNT_OF_MONEY = "금액은 숫자만 입력할 수 있습니다.";
    public static String INVALID_AMOUNT_OF_ITEM = "개수는 숫자만 입력할 수 있습니다.";
    public static String INVALID_PAGE = "페이지 수는 숫자만 입력할 수 있습니다.";
    public static String PAGE_DOES_NOT_EXIST = "해당 페이지는 존재하지 않습니다.";
    public static String PLAYER_NOT_FOUND = "해당 이름을 가진 플레이어를 찾을 수 없습니다.";
    public static String NOT_ENOUGH_MONEY = "소지금이 입력한 금액보다 적습니다.";
    public static String INVENTORY_FULL = "인벤토리가 가득 찼습니다. 인벤토리를 1칸 이상 비워주시기 바랍니다.";

    public static String FAIL_TO_SAVE_MONEY_CONFIG = "config 파일 저장에 실패했습니다.";

    public static String getWarnPrefix() {
        return "[ " + ChatColor.YELLOW + "❗" + ChatColor.WHITE + " ] ";
    }

    /* 유효성 검사 */
    public static void notPlayer(CommandSender sender) {
        sender.sendMessage(getWarnPrefix() + "플레이어가 아닙니다.");
    }

    public static void notOp(CommandSender sender) {
        sender.sendMessage(getWarnPrefix() + "권한이 없습니다.");
    }

    public static String wrongCommand() {
        return getWarnPrefix() + "명령어 사용법이 잘못되었습니다.";
    }

    /* 피드백 */
    public static void showErrorMessage(CommandSender sender, String message) {
        sender.sendMessage(getWarnPrefix() + message);
    }

    public static void checkMoney(CommandSender sender, String name, String formattedMoney) {
        sender.sendMessage(getWarnPrefix() + name + "님의 소지금은 " + ChatColor.AQUA + formattedMoney + ChatColor.WHITE + "입니다.");
    }

    public static void payMoney(CommandSender sender, String name, String formattedMoney) {
        sender.sendMessage(getWarnPrefix() + name + "님에게 " + ChatColor.RED + formattedMoney + ChatColor.WHITE + "을 송금했습니다.");
    }

    public static void moneyIsPaid(CommandSender sender, String name, String formattedMoney) {
        sender.sendMessage(getWarnPrefix() + name + "님에게서 " + ChatColor.GREEN + formattedMoney + ChatColor.WHITE + "을 받았습니다.");
    }

    public static void addMoney(CommandSender sender, String name, String formattedMoney) {
        sender.sendMessage(getWarnPrefix() + name + "님의 소지금을 " + ChatColor.GREEN + formattedMoney + ChatColor.WHITE + " 추가했습니다.");
    }

    public static void moneyIsAdded(CommandSender sender, String formattedMoney) {
        sender.sendMessage(getWarnPrefix() + "당신의 소지금이 " + ChatColor.GREEN + formattedMoney + ChatColor.WHITE + " 추가되었습니다.");
    }

    public static void subtractMoney(CommandSender sender, String name, String formattedMoney) {
        sender.sendMessage(getWarnPrefix() + name + "님의 소지금을 " + ChatColor.RED + formattedMoney + ChatColor.WHITE + " 차감했습니다.");
    }

    public static void moneyIsSubtracted(CommandSender sender, String formattedMoney) {
        sender.sendMessage(getWarnPrefix() + "당신의 소지금이 " + ChatColor.RED + formattedMoney + ChatColor.WHITE + " 차감되었습니다.");
    }

    public static void setMoney(CommandSender sender, String name, String formattedMoney) {
        sender.sendMessage(getWarnPrefix() + name + "님의 소지금을 " + ChatColor.AQUA + formattedMoney + ChatColor.WHITE + "으로 설정했습니다.");
    }

    public static void moneyIsSet(CommandSender sender, String formattedMoney) {
        sender.sendMessage(getWarnPrefix() + "당신의 소지금이 " + ChatColor.AQUA + formattedMoney + ChatColor.WHITE + "으로 설정되었습니다.");
    }

    public static void rankMoneyPrefix(CommandSender sender) {
        sender.sendMessage("─────────── 돈 순위 ───────────");
    }

    public static void rankMoney(CommandSender sender, int rank, String name, String formattedMoney) {
        sender.sendMessage("　　" + rank + "위. " + name + " (" + formattedMoney + ")");
    }

    public static void rankMoneySuffix(CommandSender sender) {
        sender.sendMessage("──────────────────────────");
    }

    public static void createCheck(CommandSender sender) {
        sender.sendMessage(getWarnPrefix() + "수표를 발행했습니다.");
    }

    /* 명령어 설명 */
    public static void moneyCommandHelper(CommandSender sender) {
        sender.sendMessage(getWarnPrefix() + "/돈 도움말" + ChatColor.GRAY + " : 사용 가능한 명령어를 확인할 수 있습니다.");
    }

    public static void moneyCommandList(CommandSender sender) {
        sender.sendMessage(getWarnPrefix() + "돈 명령어 목록");
        sender.sendMessage("　　　① /돈 확인");
        sender.sendMessage(ChatColor.GRAY + "　　　　　: 본인의 소지금을 확인합니다.");

        if(sender.isOp()) {
            sender.sendMessage("　　　② /돈 확인 플레이어ID|닉네임");
            sender.sendMessage(ChatColor.GRAY + "　　　　　: 플레이어의 소지금을 확인합니다.");
            sender.sendMessage("　　　③ /돈 송금 금액 플레이어ID|닉네임");
            sender.sendMessage(ChatColor.GRAY + "　　　　　: 플레이어에게 돈을 송금합니다.");
            sender.sendMessage("　　　④ /돈 추가 금액 플레이어ID|닉네임");
            sender.sendMessage(ChatColor.GRAY + "　　　　　: 플레이어의 소지금을 추가합니다.");
            sender.sendMessage("　　　⑤ /돈 차감 금액 플레이어ID|닉네임");
            sender.sendMessage(ChatColor.GRAY + "　　　　　: 플레이어의 소지금을 차감합니다.");
            sender.sendMessage("　　　⑥ /돈 설정 금액 플레이어ID|닉네임");
            sender.sendMessage(ChatColor.GRAY + "　　　　　: 플레이어의 소지금을 설정합니다.");
            sender.sendMessage("　　　⑦ /돈 순위 [페이지수]");
            sender.sendMessage(ChatColor.GRAY + "　　　　　: 소지금이 많은 플레이어들을 10명씩 확인합니다.");
            sender.sendMessage("　　　⑧ /돈 도움말");
            sender.sendMessage(ChatColor.GRAY + "　　　　　: 사용 가능한 명령어를 확인할 수 있습니다.");

        } else {
            sender.sendMessage("　　　② /돈 송금 금액 플레이어ID|닉네임");
            sender.sendMessage(ChatColor.GRAY + "　　　　　: 플레이어에게 돈을 송금합니다.");
            sender.sendMessage("　　　③ /돈 도움말");
            sender.sendMessage(ChatColor.GRAY + "　　　　　: 사용 가능한 명령어를 확인할 수 있습니다.");
        }
    }

    public static void checkCommandHelper(CommandSender sender) {
        sender.sendMessage(getWarnPrefix() + "/수표 도움말" + ChatColor.GRAY + " : 사용 가능한 명령어를 확인할 수 있습니다.");
    }

    public static void checkCommandList(CommandSender sender) {
        sender.sendMessage(getWarnPrefix() + "수표 명령어 목록");
        sender.sendMessage("　　　① /수표 금액 [개수]");
        sender.sendMessage(ChatColor.GRAY + "　　　　　: 수표를 발행합니다.");
        sender.sendMessage("　　　② /수표 도움말");
        sender.sendMessage(ChatColor.GRAY + "　　　　　: 사용 가능한 명령어를 확인할 수 있습니다.");
    }
}
