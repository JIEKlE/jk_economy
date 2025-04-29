package jiekie.completer;

import jiekie.EconomyPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CheckTabCompleter implements TabCompleter {
    private final EconomyPlugin plugin;

    public CheckTabCompleter(EconomyPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        int length = args.length;
        if(length == 1) {
            return Arrays.asList("금액", "도움말");
        }

        if(length == 2) {
            return Arrays.asList("개수");
        }

        return Collections.emptyList();
    }
}
