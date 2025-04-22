package jiekie.util;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChatUtil {
    /* 에러 메시지 */
    public static String INVALID_ENVIRONMENT = "사용할 수 없는 월드 환경입니다. (NORMAL | NETHER | THE_END)";

    public static String getWarnPrefix() {
        return "[ " + ChatColor.YELLOW + "❗" + ChatColor.WHITE + " ] ";
    }

    /* 유효성 검사 */
    public static void notPlayer(CommandSender sender) {
        sender.sendMessage(getWarnPrefix() + "플레이어가 아닙니다.");
    }

    public static void notOp(Player player) {
        player.sendMessage(getWarnPrefix() + "권한이 없습니다.");
    }

    public static String wrongCommand() {
        return getWarnPrefix() + "명령어 사용법이 잘못되었습니다.";
    }

    /* 피드백 */
    public static void showErrorMessage(Player player, String message) {
        player.sendMessage(getWarnPrefix() + message);
    }

    public static void createWorld(Player player) {
        player.sendMessage(getWarnPrefix() + "월드를 생성했습니다.");
    }

    /* 명령어 설명 */
    public static void moneyCommandHelper(Player player) {
        player.sendMessage(getWarnPrefix() + "/돈 도움말" + ChatColor.GRAY + " : 사용 가능한 명령어를 확인할 수 있습니다.");
    }

    public static void moneyCommandList(Player player) {
        player.sendMessage(getWarnPrefix() + "돈 명령어 목록");
        player.sendMessage("　　　① /돈 확인");
        player.sendMessage(ChatColor.GRAY + "　　　　　: 본인의 소지금을 확인합니다.");

        if(player.isOp()) {
            player.sendMessage("　　　② /돈 확인 플레이어ID|닉네임");
            player.sendMessage(ChatColor.GRAY + "　　　　　: 플레이어의 소지금을 확인합니다.");
            player.sendMessage("　　　③ /돈 송금 플레이어ID|닉네임 금액");
            player.sendMessage(ChatColor.GRAY + "　　　　　: 플레이어에게 돈을 송금합니다.");
            player.sendMessage("　　　④ /돈 추가 플레이어ID|닉네임 금액");
            player.sendMessage(ChatColor.GRAY + "　　　　　: 플레이어의 소지금을 추가합니다.");
            player.sendMessage("　　　⑤ /돈 차감 플레이어ID|닉네임 금액");
            player.sendMessage(ChatColor.GRAY + "　　　　　: 플레이어의 소지금을 차감합니다.");
            player.sendMessage("　　　⑥ /돈 설정 플레이어ID|닉네임 금액");
            player.sendMessage(ChatColor.GRAY + "　　　　　: 플레이어의 소지금을 설정합니다.");
            player.sendMessage("　　　⑦ /돈 순위");
            player.sendMessage(ChatColor.GRAY + "　　　　　: 소지금이 가장 많은 플레이어 5명을 확인합니다.");
            player.sendMessage("　　　⑧ /돈 도움말");
            player.sendMessage(ChatColor.GRAY + "　　　　　: 사용 가능한 명령어를 확인할 수 있습니다.");

        } else {
            player.sendMessage("　　　② /돈 송금 플레이어ID|닉네임 금액");
            player.sendMessage(ChatColor.GRAY + "　　　　　: 플레이어에게 돈을 송금합니다.");
            player.sendMessage("　　　③ /돈 도움말");
            player.sendMessage(ChatColor.GRAY + "　　　　　: 사용 가능한 명령어를 확인할 수 있습니다.");
        }
    }

    public static void checkCommandHelper(Player player) {
        player.sendMessage(getWarnPrefix() + "/수표 도움말" + ChatColor.GRAY + " : 사용 가능한 명령어를 확인할 수 있습니다.");
    }

    public static void checkCommandList(Player player) {
        player.sendMessage(getWarnPrefix() + "수표 명령어 목록");
        player.sendMessage("　　　① /수표 금액 [개수]");
        player.sendMessage(ChatColor.GRAY + "　　　　　: 수표를 발행합니다.");
        player.sendMessage("　　　② /수표 도움말");
        player.sendMessage(ChatColor.GRAY + "　　　　　: 사용 가능한 명령어를 확인할 수 있습니다.");
    }
}
