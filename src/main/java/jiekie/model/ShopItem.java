package jiekie.model;

import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class ShopItem {
    private ItemStack item;
    private int currentBuyPrice;
    private int currentSellPrice;
    private int maxStock;
    private int availableStock;
    private int originalBuyPrice;
    private int originalSellPrice;
    private int maxBuyPrice;
    private int minBuyPrice;
    private int maxSellPrice;
    private int minSellPrice;
    private int buyFluctuationPercent;
    private int sellFluctuationPercent;
    private int maxFluctuationRate;

    public ShopItem(ItemStack itemStack) {
        this.item = itemStack;
    }

    public ItemStack getItem() {
        return item;
    }

    public void setItem(ItemStack itemStack) {
        this.item = itemStack;
    }

    public int getCurrentBuyPrice() {
        return currentBuyPrice;
    }

    public void setCurrentBuyPrice(int currentBuyPrice) {
        this.currentBuyPrice = currentBuyPrice;
    }

    public int getCurrentSellPrice() {
        return currentSellPrice;
    }

    public void setCurrentSellPrice(int currentSellPrice) {
        this.currentSellPrice = currentSellPrice;
    }

    public int getMaxStock() {
        return maxStock;
    }

    public void setMaxStock(int maxStock) {
        this.maxStock = maxStock;
    }

    public int getAvailableStock() {
        return availableStock;
    }

    public void setAvailableStock(int availableStock) {
        this.availableStock = availableStock;
    }

    public int getOriginalBuyPrice() {
        return originalBuyPrice;
    }

    public void setOriginalBuyPrice(int originalBuyPrice) {
        this.originalBuyPrice = originalBuyPrice;
    }

    public int getOriginalSellPrice() {
        return originalSellPrice;
    }

    public void setOriginalSellPrice(int originalSellPrice) {
        this.originalSellPrice = originalSellPrice;
    }

    public int getMaxBuyPrice() {
        return maxBuyPrice;
    }

    public void setMaxBuyPrice(int maxBuyPrice) {
        this.maxBuyPrice = maxBuyPrice;
    }

    public int getMinBuyPrice() {
        return minBuyPrice;
    }

    public void setMinBuyPrice(int minBuyPrice) {
        this.minBuyPrice = minBuyPrice;
    }

    public int getMaxSellPrice() {
        return maxSellPrice;
    }

    public void setMaxSellPrice(int maxSellPrice) {
        this.maxSellPrice = maxSellPrice;
    }

    public int getMinSellPrice() {
        return minSellPrice;
    }

    public void setMinSellPrice(int minSellPrice) {
        this.minSellPrice = minSellPrice;
    }

    public int getBuyFluctuationPercent() {
        return buyFluctuationPercent;
    }

    public int getSellFluctuationPercent() {
        return sellFluctuationPercent;
    }

    public int getMaxFluctuationRate() {
        return maxFluctuationRate;
    }

    public void setMaxFluctuationRate(int maxFluctuationRate) {
        this.maxFluctuationRate = maxFluctuationRate;
    }

    public void updateFluctuation() {
        Random random = new Random();

        // change buy price
        buyFluctuationPercent = random.nextInt(maxFluctuationRate * 2 + 1) - maxFluctuationRate;
        double multiplier = 1.0 + (buyFluctuationPercent / 100.0);
        int newBuyPrice = (int) Math.round(currentBuyPrice * multiplier);
        newBuyPrice = Math.max(minBuyPrice, Math.min(maxBuyPrice, newBuyPrice));
        currentBuyPrice = newBuyPrice;

        // change sell price
        sellFluctuationPercent = random.nextInt(maxFluctuationRate * 2 + 1) - maxFluctuationRate;
        multiplier = 1.0 + (sellFluctuationPercent / 100.0);
        int newSellPrice = (int) Math.round(currentSellPrice * multiplier);
        newSellPrice = Math.max(minSellPrice, Math.min(maxSellPrice, newSellPrice));
        currentSellPrice = newSellPrice;
    }
}
