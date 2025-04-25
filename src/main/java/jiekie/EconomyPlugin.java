package jiekie;

import jiekie.api.MoneyPlaceholder;
import jiekie.command.CheckCommand;
import jiekie.command.MoneyCommand;
import jiekie.completer.MoneyTabCompleter;
import jiekie.event.PlayerEvent;
import jiekie.manager.MoneyManager;
import jiekie.util.PacketNames;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class EconomyPlugin extends JavaPlugin {
    private MoneyManager moneyManager;

    @Override
    public void onEnable() {
        // config
        saveDefaultConfig();
        reloadConfig();

        // manager
        moneyManager = new MoneyManager(this);
        moneyManager.load();

        // event
        getServer().getPluginManager().registerEvents(new PlayerEvent(this), this);

        // command
        getCommand("돈").setExecutor(new MoneyCommand(this));
        getCommand("수표").setExecutor(new CheckCommand(this));

        // tab completer
        getCommand("돈").setTabCompleter(new MoneyTabCompleter(this));

        // placeholder
        new MoneyPlaceholder(this).register();

        // packet
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, PacketNames.MONEY_UPDATE);
        moneyManager.sendPacket();

        getLogger().info("경제 플러그인 by Jiekie");
        getLogger().info("Copyright © 2025 Jiekie. All rights reserved.");
    }

    public MoneyManager getMoneyManager() {
        return moneyManager;
    }

    @Override
    public void onDisable() {
        moneyManager.save();
    }
}
