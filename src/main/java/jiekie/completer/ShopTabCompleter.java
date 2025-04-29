package jiekie.completer;

import jiekie.EconomyPlugin;
import jiekie.api.NicknameAPI;
import jiekie.model.ShopType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ShopTabCompleter implements TabCompleter {
    private final EconomyPlugin plugin;

    public ShopTabCompleter(EconomyPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!sender.hasPermission("jk.shop.command")) return Collections.emptyList();
        if(!(sender instanceof Player)) return Collections.emptyList();

        int length = args.length;
        if(length == 1) {
            return Arrays.asList("열기", "생성", "제거", "활성화", "비활성화"
                    , "인벤토리수설정", "권한설정", "변동주기설정", "아이템설정", "구매가격설정"
                    , "판매가격설정", "재고설정", "최대변동룰설정", "재고초기화", "아이템초기화"
                    , "가격초기화", "정보", "도움말");
        }

        String commandType = args[0];
        if(length == 2 && !commandType.equals("도움말"))
            return plugin.getShopManager().getShopNameList();

        if(length == 3) {
            if(commandType.equals("열기"))
                return NicknameAPI.getInstance().getPlayerNameAndNicknameList();

            if(commandType.equals("생성"))
                return ShopType.getDisplayNames();

            if(commandType.equals("인벤토리수설정"))
                return Arrays.asList("27", "54");

            if(commandType.equals("권한설정"))
                return Arrays.asList("영어권한명");

            if(commandType.equals("변동주기설정"))
                return Arrays.asList("30", "60", "90");

            if(commandType.equals("구매가격설정") || commandType.equals("판매가격설정") || commandType.equals("재고설정") || commandType.equals("최대변동룰설정"))
                return Arrays.asList("슬롯번호");
        }

        if(length == 4) {
            if(commandType.equals("생성"))
                return Arrays.asList("27", "54");

            if(commandType.equals("권한설정"))
                return Arrays.asList("한글권한명");

            if(commandType.equals("구매가격설정") || commandType.equals("판매가격설정"))
                return Arrays.asList("기본가");

            if(commandType.equals("재고설정"))
                return Arrays.asList("재고");

            if(commandType.equals("최대변동룰설정"))
                return Arrays.asList("%");
        }

        if(length == 5) {
            if(commandType.equals("구매가격설정") || commandType.equals("판매가격설정"))
                return Arrays.asList("최고가");
        }

        if(length == 6) {
            if(commandType.equals("구매가격설정") || commandType.equals("판매가격설정"))
                return Arrays.asList("최저가");
        }

        return Collections.emptyList();
    }
}
