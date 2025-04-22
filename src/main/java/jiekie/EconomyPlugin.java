package jiekie;

import jiekie.command.CheckCommand;
import jiekie.command.MoneyCommand;
import jiekie.completer.MoneyTabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

public final class EconomyPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        // config
        saveDefaultConfig();
        reloadConfig();

        // manager

        // command
        getCommand("돈").setExecutor(new MoneyCommand(this));
        getCommand("수표").setExecutor(new CheckCommand(this));

        // tab completer
        getCommand("돈").setTabCompleter(new MoneyTabCompleter(this));

        getLogger().info("경제 플러그인 by Jiekie");
        getLogger().info("Copyright © 2025 Jiekie. All rights reserved.");
    }

    @Override
    public void onDisable() {}
}
