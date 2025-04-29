package jiekie;

import jiekie.api.MoneyPlaceholder;
import jiekie.command.CheckCommand;
import jiekie.command.MoneyCommand;
import jiekie.command.ShopCommand;
import jiekie.completer.CheckTabCompleter;
import jiekie.completer.MoneyTabCompleter;
import jiekie.completer.ShopTabCompleter;
import jiekie.event.PlayerEvent;
import jiekie.manager.MoneyManager;
import jiekie.manager.ShopManager;
import jiekie.util.PacketNames;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class EconomyPlugin extends JavaPlugin {
    private MoneyManager moneyManager;
    private ShopManager shopManager;

    @Override
    public void onEnable() {
        setupConfig();
        setupManagers();
        setupEvents();
        setupCommands();
        setupTabCompleter();
        setupScheduler();
        setupPlaceholders();
        setupPackets();

        getLogger().info("경제 플러그인 by Jiekie");
        getLogger().info("Copyright © 2025 Jiekie. All rights reserved.");
    }

    private void setupConfig() {
        saveDefaultConfig();
        reloadConfig();
    }

    private void setupManagers() {
        moneyManager = new MoneyManager(this);
        moneyManager.load();
        shopManager = new ShopManager(this);
        shopManager.load();
    }

    private void setupEvents() {
        getServer().getPluginManager().registerEvents(new PlayerEvent(this), this);
    }

    private void setupCommands() {
        getCommand("돈").setExecutor(new MoneyCommand(this));
        getCommand("수표").setExecutor(new CheckCommand(this));
        getCommand("상점").setExecutor(new ShopCommand(this));
    }

    private void setupTabCompleter() {
        getCommand("돈").setTabCompleter(new MoneyTabCompleter(this));
        getCommand("수표").setTabCompleter(new CheckTabCompleter(this));
        getCommand("상점").setTabCompleter(new ShopTabCompleter(this));
    }

    private void setupScheduler() {
        shopManager.updateShopItemPrice();
    }

    private void setupPlaceholders() {
        new MoneyPlaceholder(this).register();
    }

    private void setupPackets() {
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, PacketNames.MONEY_UPDATE);
        moneyManager.sendPacket();
    }

    public MoneyManager getMoneyManager() {
        return moneyManager;
    }

    public ShopManager getShopManager() {return shopManager;}

    @Override
    public void onDisable() {
        moneyManager.save();
        shopManager.save();
    }
}
