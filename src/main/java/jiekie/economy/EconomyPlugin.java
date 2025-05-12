package jiekie.economy;

import jiekie.economy.api.MoneyAPI;
import jiekie.economy.api.MoneyPlaceholder;
import jiekie.economy.command.CheckCommand;
import jiekie.economy.command.MoneyCommand;
import jiekie.economy.command.ShopCommand;
import jiekie.economy.completer.CheckTabCompleter;
import jiekie.economy.completer.MoneyTabCompleter;
import jiekie.economy.completer.ShopTabCompleter;
import jiekie.economy.event.InventoryEvent;
import jiekie.economy.event.PlayerEvent;
import jiekie.economy.manager.MoneyManager;
import jiekie.economy.manager.ShopManager;
import jiekie.economy.util.PacketNames;
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
        setupAPI();
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
        getServer().getPluginManager().registerEvents(new InventoryEvent(this), this);
    }

    private void setupCommands() {
        getCommand("돈").setExecutor(new MoneyCommand(this));
        getCommand("수표").setExecutor(new CheckCommand(this));
        getCommand("상점").setExecutor(new ShopCommand(this));
    }

    private void setupTabCompleter() {
        getCommand("돈").setTabCompleter(new MoneyTabCompleter());
        getCommand("수표").setTabCompleter(new CheckTabCompleter());
        getCommand("상점").setTabCompleter(new ShopTabCompleter(this));
    }

    private void setupScheduler() {
        shopManager.updateShopItemPrice();
    }

    private void setupAPI() {
        new MoneyPlaceholder(this).register();
        MoneyAPI.initialize(moneyManager);
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
