package jiekie.economy.api;

import jiekie.economy.manager.MoneyManager;
import jiekie.economy.util.NumberUtil;

import java.util.*;

public class MoneyAPI {
    private static MoneyAPI instance;
    private final MoneyManager moneyManager;

    private MoneyAPI(MoneyManager moneyManager) {
        this.moneyManager = moneyManager;
    }

    public static void initialize(MoneyManager moneyManager) {
        if(instance == null)
            instance = new MoneyAPI(moneyManager);
    }

    public static boolean isInitialized() {
        return instance != null;
    }

    public static MoneyAPI getInstance() {
        return instance;
    }

    public int getPlayerMoney(UUID uuid) {
        return this.moneyManager.getMoney(uuid);
    }

    public String getPlayerMoneyFormatted(UUID uuid) {
        int money = this.moneyManager.getMoney(uuid);
        return NumberUtil.getFormattedMoney(money);
    }

    public Map<Integer, UUID> getTopRichestPlayers(int count) {
        Map<Integer, UUID> uuids = new HashMap<>();

        int index = 0;
        List<Map.Entry<UUID, Integer>> sortedMoneyList = this.moneyManager.getSortedMoneyList();
        for(Map.Entry<UUID, Integer> entry : sortedMoneyList) {
            uuids.put(++index, entry.getKey());
            if(uuids.size() >= count) break;
        }

        return uuids;
    }
}
