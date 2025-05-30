package jiekie.economy.manager;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import jiekie.economy.EconomyPlugin;
import jiekie.economy.util.ChatUtil;
import jiekie.economy.util.NumberUtil;
import jiekie.economy.util.PacketNames;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class MoneyManager {
    private final EconomyPlugin plugin;
    private final Map<UUID, Integer> moneyMap = new HashMap<>();
    private static final String CONFIG_FILE_NAME = "money.yml";

    public MoneyManager(EconomyPlugin plugin) {
        this.plugin = plugin;
    }

    public void load() {
        makeMoneyFile();
        loadPlayerMoney();
    }

    public void sendPacket() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for(Player player : Bukkit.getOnlinePlayers()) {
                    ByteArrayDataOutput out = ByteStreams.newDataOutput();

                    int money = getMoney(player.getUniqueId());
                    String formattedMoney = NumberUtil.getFormattedMoney(money);

                    out.writeUTF(formattedMoney);
                    player.sendPluginMessage(plugin, PacketNames.MONEY_UPDATE, out.toByteArray());
                }
            }
        }.runTaskTimer(plugin, 0, 10L);
    }

    public boolean containsPlayer(UUID uuid) {
        return moneyMap.containsKey(uuid);
    }

    public int getMoney(UUID uuid) {
        return moneyMap.getOrDefault(uuid, 0);
    }

    public void setMoney(UUID uuid, int money) {
        moneyMap.put(uuid, money);
    }

    public void addMoney(UUID uuid, int money) {
        moneyMap.put(uuid, moneyMap.getOrDefault(uuid, 0) + money);
    }

    public void subtractMoney(UUID uuid, int money) {
        int previousMoney = moneyMap.getOrDefault(uuid, 0);
        int currentMoney = Math.max(previousMoney - money, 0);
        moneyMap.put(uuid, currentMoney);
    }

    public List<Map.Entry<UUID, Integer>> getSortedMoneyList() {
        List<Map.Entry<UUID, Integer>> sortedList = new ArrayList<>(moneyMap.entrySet());
        sortedList.sort((a, b) -> b.getValue().compareTo(a.getValue()));
        return sortedList;
    }

    private void makeMoneyFile() {
        File file = new File(plugin.getDataFolder(), CONFIG_FILE_NAME);
        if(file.exists()) return;

        plugin.saveResource(CONFIG_FILE_NAME, false);
    }

    private void loadPlayerMoney() {
        File file = new File(plugin.getDataFolder(), CONFIG_FILE_NAME);
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        for(String key : config.getKeys(false)) {
            UUID uuid = UUID.fromString(key);
            int money = config.getInt(key + ".money");
            moneyMap.put(uuid, money);
        }
    }

    public void save() {
        File file = new File(plugin.getDataFolder(), CONFIG_FILE_NAME);
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        for(Map.Entry<UUID, Integer> entry : moneyMap.entrySet()) {
            String uuid = entry.getKey().toString();
            int money = entry.getValue();
            config.set(uuid + ".money", money);
        }

        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
            Bukkit.broadcastMessage(ChatUtil.FAIL_TO_SAVE_MONEY_CONFIG);
        }
    }
}
