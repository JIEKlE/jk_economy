package jiekie.api;

import jiekie.manager.MoneyManager;
import jiekie.util.NumberUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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

    public List<UUID> getTopRichestPlayers(int count) {
        List<UUID> uuids = new ArrayList<>();

        List<Map.Entry<UUID, Integer>> sortedMoneyList = this.moneyManager.getSortedMoneyList();
        for(Map.Entry<UUID, Integer> entry : sortedMoneyList) {
            uuids.add(entry.getKey());
            if(uuids.size() >= count) break;
        }

        return uuids;
    }
}
