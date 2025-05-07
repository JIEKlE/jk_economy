package jiekie.manager;

import jiekie.EconomyPlugin;
import jiekie.exception.ShopException;
import jiekie.model.Shop;
import jiekie.model.ShopInventoryHolder;
import jiekie.model.ShopItem;
import jiekie.model.ShopType;
import jiekie.util.ChatUtil;
import jiekie.util.ItemUtil;
import jiekie.util.NumberUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class ShopManager {
    private final EconomyPlugin plugin;
    private final Map<String, Shop> shopMap = new HashMap<>();
    private static final String CONFIG_FILE_NAME = "shop.yml";
    private static final String CONFIG_PREFIX = "shops";

    public ShopManager(EconomyPlugin plugin) {
        this.plugin = plugin;
    }

    /* load */
    public void load() {
        makeShopFile();
        loadShops();
    }

    private void makeShopFile() {
        File file = new File(plugin.getDataFolder(), CONFIG_FILE_NAME);
        if(file.exists()) return;

        plugin.saveResource(CONFIG_FILE_NAME, false);
    }

    private void loadShops() {
        shopMap.clear();
        File file = new File(plugin.getDataFolder(), CONFIG_FILE_NAME);
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection shopSection = config.getConfigurationSection(CONFIG_PREFIX);

        if(shopSection == null) return;
        for(String name : shopSection.getKeys(false)) {
            // set shop info
            String typeDisplayName = shopSection.getString(name + ".type");
            ShopType type = ShopType.getShopType(typeDisplayName);

            Shop shop = new Shop(name, type, shopSection.getInt(name + ".size"));
            shop.setEnabled(shopSection.getBoolean(name + ".enabled"));
            shop.setEnglishPermission(shopSection.getString(name + ".english_permission", null));
            shop.setKoreanPermission(shopSection.getString(name + ".korean_permission", null));
            shop.setGuiId(shopSection.getString(name + ".gui_id", null));

            if(type == ShopType.MARKET) {
                int interval = shopSection.getInt(name + ".interval");
                shop.setInterval(interval);
            }
            
            // set shop items
            Map<Integer, ShopItem> items = getShopItemsFromConfig(config, CONFIG_PREFIX + "." + name + ".items", type);
            shop.setItems(items);

            shopMap.put(name, shop);
        }
    }
    
    private Map<Integer, ShopItem> getShopItemsFromConfig(YamlConfiguration config, String path, ShopType type) {
        Map<Integer, ShopItem> items = new HashMap<>();
        ConfigurationSection itemSection = config.getConfigurationSection(path);

        if(itemSection == null) return items;
        for(String slot : itemSection.getKeys(false)) {
            try {
                // 아이템 설정
                String itemData = itemSection.getString(slot + ".item");
                ShopItem shopItem = new ShopItem(itemFromBase64(itemData));
                shopItem.setCurrentBuyPrice(itemSection.getInt(slot + ".current_buy_price"));
                shopItem.setCurrentSellPrice(itemSection.getInt(slot + ".current_sell_price"));
                shopItem.setMaxStock(itemSection.getInt(slot + ".max_stock"));
                shopItem.setAvailableStock(itemSection.getInt(slot + ".available_stock"));

                // 시세상점
                if(type == ShopType.MARKET) {
                    shopItem.setOriginalBuyPrice(itemSection.getInt(slot + ".original_buy_price"));
                    shopItem.setOriginalSellPrice(itemSection.getInt(slot + ".original_sell_price"));
                    shopItem.setMaxBuyPrice(itemSection.getInt(slot + ".max_buy_price"));
                    shopItem.setMinBuyPrice(itemSection.getInt(slot + ".min_buy_price"));
                    shopItem.setMaxSellPrice(itemSection.getInt(slot + ".max_sell_price"));
                    shopItem.setMinSellPrice(itemSection.getInt(slot + ".min_sell_price"));
                    shopItem.setMaxFluctuationRate(itemSection.getInt(slot + ".max_fluctuation_rate"));
                }

                items.put(Integer.parseInt(slot), shopItem);

            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                plugin.getLogger().warning("상점 물품 불러오기 실패");
            }
        }

        return items;
    }

    /* scheduler */
    public void updateShopItemPrice() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for(Shop shop : shopMap.values()) {
                    if(shop.getType() != ShopType.MARKET) continue;
                    if(!shop.isEnabled()) continue;
                    if(!shop.shouldUpdateNow()) continue;

                    for(ShopItem item : shop.getItems().values())
                        item.updateFluctuation();

                    ChatUtil.broadcastPriceChanged(shop.getName());
                    shop.scheduleNextUpdate();
                }
            }
        }.runTaskTimer(plugin, 0, 20L * 60);
    }

    /* function */
    public void openShop(Player player, String name, boolean isSettingMode) throws ShopException {
        Shop shop = getShopOrThrow(name);
        if(!shop.isEnabled())
            throw new ShopException(ChatUtil.SHOP_DISABLED);

        String englishPermission = shop.getEnglishPermission();
        if(englishPermission != null && !player.hasPermission(englishPermission)) {
            String permission = shop.getKoreanPermission() == null ? englishPermission : shop.getKoreanPermission();
            throw new ShopException(ChatUtil.NO_PERMISSION + " (필요 권한 : " + permission + ")");
        }

        String guiId = shop.getGuiId();
        String chestName = guiId == null ? "" : ":offset_-16::" + guiId + ":";
        int size = shop.getSize();

        ShopInventoryHolder holder = new ShopInventoryHolder(name, isSettingMode);
        Inventory inventory = Bukkit.createInventory(holder, size, chestName);

        if(isSettingMode)
            setInventoryForSetting(inventory, name);
        else
            setInventoryForTrade(inventory, name);

        player.openInventory(inventory);
    }

    public void setInventoryForSetting(Inventory inventory, String name) throws ShopException {
        Shop shop = getShopOrThrow(name);
        Map<Integer, ShopItem> items = shop.getItems();

        if(items == null || items.isEmpty()) {
            return;
        }

        items.forEach((slot, item) -> {
            ItemStack slotItem = item.getItem().clone();
            inventory.setItem(slot, slotItem);
        });
    }

    public void setInventoryForTrade(Inventory inventory, String name) throws ShopException {
        Shop shop = getShopOrThrow(name);
        Map<Integer, ShopItem> items = shop.getItems();
        ShopType type = shop.getType();

        if(items == null || items.isEmpty()) {
            return;
        }

        for(int i=0 ; i<inventory.getSize() ; i++) {
            inventory.setItem(i, null);
            if(!items.containsKey(i)) continue;

            ShopItem shopItem = items.get(i);
            ItemStack slotItem = shopItem.getItem().clone();
            ItemMeta meta = slotItem.getItemMeta();

            meta.setLore(buildShopItemLore(shopItem, type));
            slotItem.setItemMeta(meta);

            inventory.setItem(i, slotItem);
        }
    }

    private List<String> buildShopItemLore(ShopItem item, ShopType type) {
        List<String> lore = new ArrayList<>();

        lore.add("");
        lore.add(buildBuyPriceLore(item, type));
        lore.add(buildSellPriceLore(item, type));
        lore.add("");
        lore.add(buildOneStackBuyPrice(item));
        lore.add(buildOneStackSellPrice(item));
        lore.add("");

        if(item.getMaxStock() > 0) {
            lore.add(ChatColor.GOLD + "남은 수량" + ChatColor.WHITE + " : " + item.getAvailableStock() + " / " + item.getMaxStock());
            lore.add("");
        }

        lore.add(ChatColor.GRAY + "\uA012 : 구매");
        lore.add(ChatColor.GRAY + "\uA013 : 판매");
        lore.add(ChatColor.GRAY + "\uA011 + \uA012 : 64개 구매 or 전체 수량");
        lore.add(ChatColor.GRAY + "\uA011 + \uA013 : 64개 판매 or 전체 판매");

        return lore;
    }

    private String buildBuyPriceLore(ShopItem item, ShopType type) {
        String buyPrice = (item.getCurrentBuyPrice() > 0) ? NumberUtil.getFormattedMoney(item.getCurrentBuyPrice()) : "구매금지";
        if(type == ShopType.MARKET && item.getCurrentBuyPrice() > 0) {
            buyPrice += " " + formatFluctuation(item.getBuyFluctuationPercent());
        }

        return ChatColor.YELLOW + "구매가격" + ChatColor.WHITE + " : " + buyPrice;
    }

    private String buildSellPriceLore(ShopItem item, ShopType type) {
        String sellPrice = (item.getCurrentSellPrice() > 0) ? NumberUtil.getFormattedMoney(item.getCurrentSellPrice()) : "판매금지";
        if(type == ShopType.MARKET && item.getCurrentSellPrice() > 0) {
            sellPrice += " " + formatFluctuation(item.getSellFluctuationPercent());
        }

        return ChatColor.GOLD + "판매가격" + ChatColor.WHITE + " : " + sellPrice;
    }

    private String buildOneStackBuyPrice(ShopItem item) {
        String buyPrice = (item.getCurrentBuyPrice() > 0) ? NumberUtil.getFormattedMoney(item.getCurrentBuyPrice() * 64) : "구매금지";
        return ChatColor.YELLOW + "64개 구매가격" + ChatColor.WHITE + " : " + buyPrice;
    }

    private String buildOneStackSellPrice(ShopItem item) {
        String sellPrice = (item.getCurrentSellPrice() > 0) ? NumberUtil.getFormattedMoney(item.getCurrentSellPrice() * 64) : "판매금지";
        return ChatColor.GOLD + "64개 판매가격" + ChatColor.WHITE + " : " + sellPrice;
    }

    private String formatFluctuation(int fluctuation) {
        if(fluctuation > 0) return ChatColor.GREEN + "[ ▲ " + fluctuation + "% ]";
        if(fluctuation == 0) return ChatColor.YELLOW + "[ - ]";
        return ChatColor.RED + "[ ▼ " + fluctuation + "% ]";
    }

    public void createShop(String name, String typeDisplayName, String sizeString) throws ShopException {
        if(shopMap.containsKey(name))
            throw new ShopException(ChatUtil.SHOP_ALREADY_EXIST);

        ShopType type = ShopType.getShopType(typeDisplayName);
        if(type == null)
            throw new ShopException(ChatUtil.INVALID_SHOP_TYPE);

        int size = getInventorySizeFromString(sizeString);

        Shop shop = new Shop(name, type, size);
        shopMap.put(name, shop);
    }

    public void removeShop(String name) throws ShopException {
        if(!shopMap.containsKey(name))
            throw new ShopException(ChatUtil.SHOP_NOT_FOUND);

        shopMap.remove(name);
    }

    public void activateShop(String name, boolean activate) throws ShopException {
        Shop shop = getShopOrThrow(name);
        shop.setEnabled(activate);
        if(activate)
            shop.scheduleNextUpdate();
    }

    public void setInventorySize(String name, String sizeString) throws ShopException {
        Shop shop = getShopOrThrow(name);

        int size = getInventorySizeFromString(sizeString);
        shop.setSize(size);

        Map<Integer, ShopItem> items = shop.getItems();
        if(items == null || items.isEmpty()) return;

        List<Integer> removeList = new ArrayList<>();
        items.forEach((slot, item) -> {
            if(slot > size) removeList.add(slot);
        });
        removeList.forEach(items::remove);
    }

    public void setPermission(String name, String englishPermission, String koreanPermission) throws ShopException {
        Shop shop = getShopOrThrow(name);
        shop.setEnglishPermission(englishPermission);
        shop.setKoreanPermission(koreanPermission);
    }

    public void setInterval(String name, String intervalString) throws ShopException {
        Shop shop = getShopOrThrow(name);
        if(shop.getType() != ShopType.MARKET)
            throw new ShopException(ChatUtil.NOT_MARKET_SHOP);

        int interval = getIntervalFromString(intervalString);
        shop.setInterval(interval);
    }

    public void setGuiTemplate(String name, String guiId) throws ShopException {
        Shop shop = getShopOrThrow(name);
        shop.setGuiId(guiId);
    }

    public void setBuyPrice(String[] args) throws ShopException {
        String name = args[1];
        Shop shop = getShopOrThrow(name);
        if(shop.getType() == ShopType.MARKET && args.length < 6)
            throw new ShopException(ChatUtil.wrongCommand() + " (/상점 구매가격설정 상점명 슬롯번호 기본가 최고가 최저가)");

        String slotString = args[2];
        ShopItem item = getShopItemOrThrow(name, getSlotFromString(slotString));

        item.setCurrentBuyPrice(getPriceFromString(args[3]));
        if(shop.getType() == ShopType.MARKET) {
            item.setOriginalBuyPrice(getPriceFromString(args[3]));
            item.setMaxBuyPrice(getPriceFromString(args[4]));
            item.setMinBuyPrice(getPriceFromString(args[5]));
        }
    }

    public void setSellPrice(String[] args) throws ShopException {
        String name = args[1];
        Shop shop = getShopOrThrow(name);
        if(shop.getType() == ShopType.MARKET && args.length < 6)
            throw new ShopException(ChatUtil.wrongCommand() + " (/상점 판매가격설정 상점명 슬롯번호 기본가 최고가 최저가)");

        String slotString = args[2];
        ShopItem item = getShopItemOrThrow(name, getSlotFromString(slotString));

        item.setCurrentSellPrice(getPriceFromString(args[3]));
        if(shop.getType() == ShopType.MARKET) {
            item.setOriginalSellPrice(getPriceFromString(args[3]));
            item.setMaxSellPrice(getPriceFromString(args[4]));
            item.setMinSellPrice(getPriceFromString(args[5]));
        }
    }

    public void setStock(String name, String slotString, String stockString) throws ShopException {
        ShopItem item = getShopItemOrThrow(name, getSlotFromString(slotString));

        int stock = getStockFromString(stockString);
        item.setMaxStock(stock);
        item.setAvailableStock(stock);
    }

    public void setMaxFluctuation(String name, String slotString, String fluctuationString) throws ShopException {
        Shop shop = getShopOrThrow(name);
        if(shop.getType() != ShopType.MARKET)
            throw new ShopException(ChatUtil.NOT_MARKET_SHOP);

        ShopItem item = getShopItemOrThrow(name, getSlotFromString(slotString));

        int fluctuation = getFluctuationFromString(fluctuationString);
        item.setMaxFluctuationRate(fluctuation);
    }

    public void resetStock(String name) throws ShopException {
        Shop shop = getShopOrThrow(name);
        Map<Integer, ShopItem> items = shop.getItems();
        if(items == null || items.isEmpty()) return;

        items.forEach((slot, item) -> {
            int maxStock = item.getMaxStock();
            if(maxStock > 0)
                item.setAvailableStock(maxStock);
        });
    }

    public void resetItems(String name) throws ShopException {
        Shop shop = getShopOrThrow(name);
        Map<Integer, ShopItem> items = shop.getItems();
        if(items == null || items.isEmpty()) return;
        items.clear();
    }

    public void resetPrice(String name) throws ShopException {
        Shop shop = getShopOrThrow(name);
        if(shop.getType() != ShopType.MARKET)
            throw new ShopException(ChatUtil.NOT_MARKET_SHOP);

        Map<Integer, ShopItem> items = shop.getItems();
        if(items == null || items.isEmpty()) return;

        items.forEach((slot, item) -> {
            int originalBuyPrice = item.getOriginalBuyPrice();
            int originalSellPrice = item.getOriginalSellPrice();

            item.setCurrentBuyPrice(originalBuyPrice);
            item.setCurrentSellPrice(originalSellPrice);
        });
    }

    public void setShopItems(String name, Inventory inventory) throws ShopException {
        Shop shop = getShopOrThrow(name);
        Map<Integer, ShopItem> items = shop.getItems();
        if(items == null)
            items = new HashMap<>();

        List<Integer> removeList = new ArrayList<>();
        for(int i=0 ; i<inventory.getSize() ; i++) {
            ItemStack item = inventory.getItem(i);
            if(item == null || item.getType() == Material.AIR) {
                if(items.containsKey(i)) removeList.add(i);
                continue;
            }

            ShopItem existingShopItem = items.get(i);
            ItemStack slotItem = item.clone();
            slotItem.setAmount(1);

            if(existingShopItem != null) {
                if(!ItemUtil.isSameItem(item, existingShopItem.getItem()))
                    existingShopItem.setItem(slotItem);
                continue;
            }

            ShopItem shopItem = new ShopItem(slotItem);
            items.put(i, shopItem);
        }

        removeList.forEach(items::remove);
        shop.setItems(items);
    }

    public Shop getShopOrThrow(String name) throws ShopException {
        if(!shopMap.containsKey(name))
            throw new ShopException(ChatUtil.SHOP_NOT_FOUND);

        Shop shop = shopMap.get(name);
        if(shop == null)
            throw new ShopException(ChatUtil.SHOP_NOT_FOUND);

        return shop;
    }

    public ShopItem getShopItemOrThrow(String name, int slot) throws ShopException {
        Shop shop = getShopOrThrow(name);
        Map<Integer, ShopItem> items = shop.getItems();

        if(items == null || items.isEmpty())
            throw new ShopException(ChatUtil.NO_ITEM_IN_SHOP);

        if(!items.containsKey(slot))
            throw new ShopException(ChatUtil.NO_ITEM_IN_SHOP);

        return items.get(slot);
    }

    private int getInventorySizeFromString(String sizeString) throws ShopException {
        int size;
        try {
            size = Integer.parseInt(sizeString);
        } catch (NumberFormatException e) {
            throw new ShopException(ChatUtil.INVENTORY_SIZE_NOT_NUMBER);
        }

        if(size <= 0)
            throw new ShopException(ChatUtil.INVENTORY_SIZE_LESS_THAN_ONE);

        if(size > 54)
            throw new ShopException(ChatUtil.INVENTORY_SIZE_MORE_THAN_FULL);

        if(size % 9 > 0)
            throw new ShopException(ChatUtil.INVALID_INVENTORY_SIZE);

        return size;
    }

    private int getIntervalFromString(String intervalString) throws ShopException {
        int interval;
        try {
            interval = Integer.parseInt(intervalString);
        } catch (NumberFormatException e) {
            throw new ShopException(ChatUtil.INTERVAL_NOT_NUMBER);
        }

        if(interval <= 0)
            throw new ShopException(ChatUtil.INTERVAL_LESS_THAN_ONE);

        if(interval % 10 > 0)
            throw new ShopException(ChatUtil.INVALID_INTERVAL);

        return interval;
    }

    private int getSlotFromString(String slotString) throws ShopException {
        int slot;
        try {
            slot = Integer.parseInt(slotString);
        } catch (NumberFormatException e) {
            throw new ShopException(ChatUtil.SLOT_NOT_NUMBER);
        }

        if(slot < 1)
            throw new ShopException(ChatUtil.SLOT_LESS_THAN_ONE);

        if(slot > 54)
            throw new ShopException(ChatUtil.SLOT_MORE_THAN_FULL);

        return slot - 1;
    }

    private int getPriceFromString(String moneyString) throws ShopException {
        int money;
        try {
            money = Integer.parseInt(moneyString);
        } catch (NumberFormatException e) {
            throw new ShopException(ChatUtil.MONEY_NOT_NUMBER);
        }

        if(money < 0)
            throw new ShopException(ChatUtil.MINUS_MONEY);

        return money;
    }

    private int getStockFromString(String stockString) throws ShopException {
        int stock;
        try {
            stock = Integer.parseInt(stockString);
        } catch (NumberFormatException e) {
            throw new ShopException(ChatUtil.STOCK_NOT_NUMBER);
        }

        if(stock < 0)
            throw new ShopException(ChatUtil.MINUS_STOCK);

        return stock;
    }

    private int getFluctuationFromString(String fluctuationString) throws ShopException {
        int fluctuation;
        try {
            fluctuation = Integer.parseInt(fluctuationString);
        } catch (NumberFormatException e) {
            throw new ShopException(ChatUtil.FLUCTUATION_NOT_NUMBER);
        }

        if(fluctuation < 0)
            throw new ShopException(ChatUtil.MINUS_FLUCTUATION);

        return fluctuation;
    }

    /* tab completer */
    public List<String> getShopNameList() {
        if(shopMap.isEmpty()) return Collections.emptyList();
        return new ArrayList<>(shopMap.keySet());
    }

    /* save */
    public void save() {
        File file = new File(plugin.getDataFolder(), CONFIG_FILE_NAME);
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        config.set(CONFIG_PREFIX, null);
        if(shopMap.isEmpty()) return;
        for(Map.Entry<String, Shop> entry : shopMap.entrySet()) {
            // set shop info
            String name = entry.getKey();
            Shop shop = entry.getValue();
            String path = CONFIG_PREFIX + "." + name;
            ShopType type = shop.getType();

            config.set(path + ".type", type.getDisplayName());
            config.set(path + ".size", shop.getSize());
            config.set(path + ".enabled", shop.isEnabled());
            config.set(path + ".english_permission", shop.getEnglishPermission());
            config.set(path + ".korean_permission", shop.getKoreanPermission());
            config.set(path + ".gui_id", shop.getGuiId());

            if(type == ShopType.MARKET)
                config.set(path + ".interval", shop.getInterval());
            
            // set shop items
            setShopItemsToConfig(config, path + ".items", type, shop.getItems());
        }

        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().warning(ChatUtil.FAIL_TO_SAVE_SHOP_CONFIG);
            e.printStackTrace();
        }
    }

    private void setShopItemsToConfig(YamlConfiguration config, String basePath, ShopType type, Map<Integer, ShopItem> items) {
        if(items == null || items.isEmpty()) return;
        for(Map.Entry<Integer, ShopItem> entry : items.entrySet()) {

            try {
                String slot = String.valueOf(entry.getKey());
                ShopItem shopItem = entry.getValue();
                String path = basePath + "." + slot;

                config.set(path + ".item", itemStackToBase64(shopItem.getItem()));
                config.set(path + ".current_buy_price", shopItem.getCurrentBuyPrice());
                config.set(path + ".current_sell_price", shopItem.getCurrentSellPrice());
                config.set(path + ".max_stock", shopItem.getMaxStock());
                config.set(path + ".available_stock", shopItem.getAvailableStock());

                if(type == ShopType.MARKET) {
                    config.set(path + ".original_buy_price", shopItem.getOriginalBuyPrice());
                    config.set(path + ".original_sell_price", shopItem.getOriginalSellPrice());
                    config.set(path + ".max_buy_price", shopItem.getMaxBuyPrice());
                    config.set(path + ".min_buy_price", shopItem.getMinBuyPrice());
                    config.set(path + ".max_sell_price", shopItem.getMaxSellPrice());
                    config.set(path + ".min_sell_price", shopItem.getMinSellPrice());
                    config.set(path + ".max_fluctuation_rate", shopItem.getMaxFluctuationRate());
                }

            } catch (IOException e) {
                e.printStackTrace();
                plugin.getLogger().warning("상점 물품 저장 실패");
            }
        }
    }

    private String itemStackToBase64(ItemStack item) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

        dataOutput.writeObject(item);
        dataOutput.close();
        return Base64.getEncoder().encodeToString(outputStream.toByteArray());
    }

    private ItemStack itemFromBase64(String base64) throws IOException, ClassNotFoundException {
        byte[] data = Base64.getDecoder().decode(base64);
        BukkitObjectInputStream inputStream = new BukkitObjectInputStream(new ByteArrayInputStream(data));
        ItemStack item = (ItemStack) inputStream.readObject();
        inputStream.close();
        return item;
    }
}
