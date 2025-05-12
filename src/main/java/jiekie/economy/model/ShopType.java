package jiekie.economy.model;

import java.util.Arrays;
import java.util.List;

public enum ShopType {
    NORMAL("일반상점")
    , MARKET("시세상점");

    private final String displayName;

    ShopType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static ShopType getShopType(String displayName) {
        for(ShopType type : values()) {
            if(type.getDisplayName().equals(displayName)) return type;
        }

        return null;
    }

    public static List<String> getDisplayNames() {
        return Arrays.stream(values()).map(ShopType::getDisplayName).toList();
    }
}
