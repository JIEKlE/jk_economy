package jiekie.economy.completer;

import jiekie.nickname.api.NicknameAPI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MoneyTabCompleter implements TabCompleter {
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player player)) return Collections.emptyList();

        int length = args.length;
        if(length == 1) {
            if(player.isOp())
                return Arrays.asList("확인", "송금", "추가", "차감", "설정", "순위", "도움말");
            else
                return Arrays.asList("확인", "송금", "도움말");
        }

        String commandType = args[0];
        if(length == 2) {
            if(commandType.equals("확인") && player.isOp())
                return NicknameAPI.getInstance().getPlayerNameAndNicknameList();

            if(commandType.equals("송금"))
                return List.of("금액");

            if((commandType.equals("추가") || commandType.equals("차감") || commandType.equals("설정")) && player.isOp())
                return List.of("금액");

            if(commandType.equals("순위") && player.isOp())
                return List.of("페이지수");
        }

        if(length == 3) {
            if(commandType.equals("송금"))
                return NicknameAPI.getInstance().getPlayerNameAndNicknameList();

            if((commandType.equals("추가") || commandType.equals("차감") || commandType.equals("설정")) && player.isOp())
                return NicknameAPI.getInstance().getPlayerNameAndNicknameList();
        }

        return Collections.emptyList();
    }
}
