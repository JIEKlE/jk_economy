package jiekie.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class Shop {
    private final String name;
    private final ShopType type;
    private int size;
    private boolean enabled;
    private int interval;
    private long nextUpdateTime;
    private String englishPermission;
    private String koreanPermission;
    private String templateId;
    private Map<Integer, ShopItem> items;

    public Shop(String name, ShopType type, int size) {
        this.name = name;
        this.type = type;
        this.size = size;
        this.enabled = true;

        if(type == ShopType.MARKET) {
            this.interval = 60;
            scheduleNextUpdate();
        }
    }

    public String getName() {
        return name;
    }

    public ShopType getType() {
        return type;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
        scheduleNextUpdate();
    }

    public boolean shouldUpdateNow() {
        return System.currentTimeMillis() >= nextUpdateTime;
    }

    public void scheduleNextUpdate() {
        this.nextUpdateTime = System.currentTimeMillis() + interval * 1000L * 60L;
    }

    public String getFormattedNextUpdateTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formatter.format(new Date(this.nextUpdateTime));
    }

    public String getEnglishPermission() {
        return englishPermission;
    }

    public void setEnglishPermission(String englishPermission) {
        this.englishPermission = englishPermission;
    }

    public String getKoreanPermission() {
        return koreanPermission;
    }

    public void setKoreanPermission(String koreanPermission) {
        this.koreanPermission = koreanPermission;
    }

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public Map<Integer, ShopItem> getItems() {
        return items;
    }

    public void setItems(Map<Integer, ShopItem> items) {
        this.items = items;
    }
}
