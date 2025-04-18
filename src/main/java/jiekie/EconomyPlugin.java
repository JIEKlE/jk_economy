package jiekie;

import org.bukkit.plugin.java.JavaPlugin;

public final class EconomyPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("경제 플러그인 by Jiekie");
        getLogger().info("Copyright © 2025 Jiekie. All rights reserved.");
    }

    @Override
    public void onDisable() {}
}
