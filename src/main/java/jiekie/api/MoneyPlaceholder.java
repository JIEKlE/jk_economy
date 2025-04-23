package jiekie.api;

import jiekie.EconomyPlugin;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MoneyPlaceholder extends PlaceholderExpansion {
    private final EconomyPlugin plugin;

    public MoneyPlaceholder(EconomyPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "jkeconomy";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Jiekie";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        if(player == null) {
            return "";
        }

        int money = plugin.getMoneyManager().getMoney(player.getUniqueId());
        switch(params) {
            case "money":
                return String.valueOf(money);

            case "formatted_money":
                return plugin.getMoneyManager().getFormattedMoney(money);

            default:
                return null;
        }
    }
}
