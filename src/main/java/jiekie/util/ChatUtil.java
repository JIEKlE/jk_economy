package jiekie.util;

import jiekie.model.Shop;
import jiekie.model.ShopType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class ChatUtil {
    /* 에러 메시지 */
    public static String MONEY_NOT_NUMBER = "금액은 숫자만 입력할 수 있습니다.";
    public static String AMOUNT_OF_ITEM_NOT_NUMBER = "개수는 숫자만 입력할 수 있습니다.";
    public static String PAGE_NOT_NUMBER = "페이지 수는 숫자만 입력할 수 있습니다.";
    public static String SLOT_NOT_NUMBER = "슬롯 번호는 숫자만 입력할 수 있습니다.";
    public static String STOCK_NOT_NUMBER = "재고는 숫자만 입력할 수 있습니다.";
    public static String FLUCTUATION_NOT_NUMBER = "변동률은 숫자만 입력할 수 있습니다.";
    public static String INTERVAL_NOT_NUMBER = "변동 주기는 숫자만 입력 가능합니다.";
    public static String INVENTORY_SIZE_NOT_NUMBER = "인벤토리 수는 숫자만 입력 가능합니다.";

    public static String MINUS_MONEY = "금액은 0 이상만 입력 가능합니다.";
    public static String MINUS_AMOUNT_OF_ITEM = "개수는 0 이상만 입력 가능합니다.";
    public static String MINUS_STOCK = "재고는 0 이상만 입력 가능합니다.";
    public static String MINUS_FLUCTUATION = "변동률은 0 이상만 입력 가능합니다.";
    public static String INTERVAL_LESS_THAN_ONE = "변동 주기는 1 이상만 입력 가능합니다.";
    public static String SLOT_LESS_THAN_ONE = "슬롯 번호는 1 이상만 입력 가능합니다.";
    public static String SLOT_MORE_THAN_FULL = "슬롯 번호는 54 이하만 입력 가능합니다.";
    public static String INVENTORY_SIZE_LESS_THAN_ONE = "인벤토리 수는 1 이상만 입력 가능합니다.";
    public static String INVENTORY_SIZE_MORE_THAN_FULL = "인벤토리 수는 54 이하만 입력 가능합니다.";

    public static String INVALID_INTERVAL = "변동 주기는 10분 단위로 입력해야 합니다.";
    public static String INVALID_INVENTORY_SIZE = "인벤토리 수는 9의 배수로 입력해야 합니다.";

    public static String PAGE_DOES_NOT_EXIST = "해당 페이지는 존재하지 않습니다.";
    public static String PLAYER_NOT_FOUND = "해당 이름을 가진 플레이어를 찾을 수 없습니다.";
    public static String NOT_ENOUGH_MONEY = "소지금이 부족합니다.";
    public static String INVENTORY_FULL = "인벤토리가 가득 찼습니다. 인벤토리를 1칸 이상 비워주시기 바랍니다.";
    public static String SHOP_NOT_FOUND = "해당 이름을 가진 상점을 찾을 수 없습니다.";
    public static String SHOP_ALREADY_EXIST = "해당 상점은 이미 존재합니다.";
    public static String INVALID_SHOP_TYPE = "해당하는 상점 유형이 없습니다.";
    public static String SHOP_DISABLED = "상점이 비활성화 된 상태입니다.";
    public static String NO_PERMISSION = "필요한 권한이 없습니다.";
    public static String NOT_MARKET_SHOP = "해당 설정은 시세 상점일 경우에만 가능합니다.";
    public static String NO_ITEM_IN_SHOP = "상점에 설정된 물품이 없는 칸입니다.";

    public static String BUY_NOT_ALLOWED = "구매할 수 없는 물품입니다.";
    public static String SELL_NOT_ALLOWED = "판매할 수 없는 물품입니다.";
    public static String OUT_OF_STOCK = "재고가 없습니다.";
    public static String NO_ITEM_TO_SELL = "판매할 물품을 가지고 있지 않습니다.";

    public static String FAIL_TO_SAVE_MONEY_CONFIG = "money.yml 파일 저장에 실패했습니다.";
    public static String FAIL_TO_SAVE_SHOP_CONFIG = "shop.yml 파일 저장에 실패했습니다.";

    public static String getWarnPrefix() {
        return "[ " + ChatColor.YELLOW + "❗" + ChatColor.WHITE + " ] ";
    }

    /* 유효성 검사 */
    public static void notPlayer(CommandSender sender) {
        sender.sendMessage(getWarnPrefix() + "플레이어가 아닙니다.");
    }

    public static void notOp(CommandSender sender) {
        sender.sendMessage(getWarnPrefix() + "권한이 없습니다.");
    }

    public static String wrongCommand() {
        return getWarnPrefix() + "명령어 사용법이 잘못되었습니다.";
    }

    /* 피드백 */
    public static void showErrorMessage(CommandSender sender, String message) {
        sender.sendMessage(getWarnPrefix() + message);
    }

    // money
    public static void checkMoney(CommandSender sender, String name, String formattedMoney) {
        sender.sendMessage(getWarnPrefix() + name + "님의 소지금은 " + ChatColor.AQUA + formattedMoney + ChatColor.WHITE + "입니다.");
    }

    public static void payMoney(CommandSender sender, String name, String formattedMoney) {
        sender.sendMessage(getWarnPrefix() + name + "님에게 " + ChatColor.RED + formattedMoney + ChatColor.WHITE + "을 송금했습니다.");
    }

    public static void moneyIsPaid(CommandSender sender, String name, String formattedMoney) {
        sender.sendMessage(getWarnPrefix() + name + "님에게서 " + ChatColor.GREEN + formattedMoney + ChatColor.WHITE + "을 받았습니다.");
    }

    public static void addMoney(CommandSender sender, String name, String formattedMoney) {
        sender.sendMessage(getWarnPrefix() + name + "님의 소지금을 " + ChatColor.GREEN + formattedMoney + ChatColor.WHITE + " 추가했습니다.");
    }

    public static void moneyIsAdded(CommandSender sender, String formattedMoney) {
        sender.sendMessage(getWarnPrefix() + "당신의 소지금이 " + ChatColor.GREEN + formattedMoney + ChatColor.WHITE + " 추가되었습니다.");
    }

    public static void subtractMoney(CommandSender sender, String name, String formattedMoney) {
        sender.sendMessage(getWarnPrefix() + name + "님의 소지금을 " + ChatColor.RED + formattedMoney + ChatColor.WHITE + " 차감했습니다.");
    }

    public static void moneyIsSubtracted(CommandSender sender, String formattedMoney) {
        sender.sendMessage(getWarnPrefix() + "당신의 소지금이 " + ChatColor.RED + formattedMoney + ChatColor.WHITE + " 차감되었습니다.");
    }

    public static void setMoney(CommandSender sender, String name, String formattedMoney) {
        sender.sendMessage(getWarnPrefix() + name + "님의 소지금을 " + ChatColor.AQUA + formattedMoney + ChatColor.WHITE + "으로 설정했습니다.");
    }

    public static void moneyIsSet(CommandSender sender, String formattedMoney) {
        sender.sendMessage(getWarnPrefix() + "당신의 소지금이 " + ChatColor.AQUA + formattedMoney + ChatColor.WHITE + "으로 설정되었습니다.");
    }

    public static void rankMoneyPrefix(CommandSender sender) {
        sender.sendMessage("");
        sender.sendMessage("─────────── 돈 순위 ───────────");
        sender.sendMessage("");
    }

    public static void rankMoney(CommandSender sender, int rank, String name, String formattedMoney) {
        sender.sendMessage("　　" + ChatColor.YELLOW + rank + "위. " + ChatColor.WHITE + name + " (" + formattedMoney + ")");
    }

    public static void horizontalLineSuffix(CommandSender sender) {
        sender.sendMessage("");
        sender.sendMessage("──────────────────────────");
        sender.sendMessage("");
    }

    // check
    public static void createCheck(CommandSender sender) {
        sender.sendMessage(getWarnPrefix() + "수표를 발행했습니다.");
    }

    // shop
    public static void createShop(CommandSender sender) {
        sender.sendMessage(getWarnPrefix() + "상점을 생성했습니다.");
    }

    public static void removeShop(CommandSender sender) {
        sender.sendMessage(getWarnPrefix() + "상점을 제거했습니다.");
    }

    public static void activateShop(CommandSender sender) {
        sender.sendMessage(getWarnPrefix() + "상점을 활성화했습니다.");
    }

    public static void deactivateShop(CommandSender sender) {
        sender.sendMessage(getWarnPrefix() + "상점을 비활성화했습니다.");
    }

    public static void setInventorySize(CommandSender sender) {
        sender.sendMessage(getWarnPrefix() + "상점의 인벤토리 수를 설정했습니다.");
    }

    public static void setPermission(CommandSender sender) {
        sender.sendMessage(getWarnPrefix() + "상점의 권한을 설정했습니다.");
    }

    public static void resetPermission(CommandSender sender) {
        sender.sendMessage(getWarnPrefix() + "상점의 권한을 해제했습니다.");
    }

    public static void setInterval(CommandSender sender) {
        sender.sendMessage(getWarnPrefix() + "상점의 변동 주기를 설정했습니다.");
    }

    public static void setGui(CommandSender sender) {
        sender.sendMessage(getWarnPrefix() + "상점의 GUI를 설정했습니다.");
    }

    public static void resetGui(CommandSender sender) {
        sender.sendMessage(getWarnPrefix() + "상점의 GUI를 해제했습니다.");
    }

    public static void setBuyPrice(CommandSender sender) {
        sender.sendMessage(getWarnPrefix() + "물품의 구매 가격을 설정했습니다.");
    }

    public static void setSellPrice(CommandSender sender) {
        sender.sendMessage(getWarnPrefix() + "물품의 판매 가격을 설정했습니다.");
    }

    public static void setStock(CommandSender sender) {
        sender.sendMessage(getWarnPrefix() + "물품의 재고를 설정했습니다.");
    }

    public static void setMaxFluctuation(CommandSender sender) {
        sender.sendMessage(getWarnPrefix() + "물품의 최대 변동률을 설정했습니다.");
    }

    public static void resetStock(CommandSender sender) {
        sender.sendMessage(getWarnPrefix() + "상점의 재고를 보충했습니다.");
    }

    public static void resetItems(CommandSender sender) {
        sender.sendMessage(getWarnPrefix() + "상점의 물품 목록을 초기화했습니다.");
    }

    public static void resetPrice(CommandSender sender) {
        sender.sendMessage(getWarnPrefix() + "상점의 물품을 기본 가격로 초기화했습니다.");
    }

    public static void setShopItems(CommandSender sender) {
        sender.sendMessage(getWarnPrefix() + "상점의 물품 목록을 설정했습니다.");
    }

    public static void buyItem(CommandSender sender, String formattedMoney) {
        sender.sendMessage(getWarnPrefix() + "물품을 구매했습니다. " + ChatColor.RED + "( -" + formattedMoney + " )");
    }

    public static void sellItem(CommandSender sender, String formattedMoney) {
        sender.sendMessage(getWarnPrefix() + "물품을 판매했습니다. " + ChatColor.GREEN + "( +" + formattedMoney + " )");
    }

    public static void shopInfoPrefix(CommandSender sender) {
        sender.sendMessage("");
        sender.sendMessage("─────────── 상점정보 ───────────");
        sender.sendMessage("");
    }

    public static void shopInfo(CommandSender sender, Shop shop) {
        sender.sendMessage("　상점명 : " + ChatColor.GREEN + ChatColor.BOLD + shop.getName());
        sender.sendMessage("　상점 유형 : " + shop.getType().getDisplayName());
        sender.sendMessage("　인벤토리 수 : " + shop.getSize());
        String enable = shop.isEnabled() ? ChatColor.GREEN + "활성화" : ChatColor.RED + "비활성화";
        sender.sendMessage("　활성화여부 : " + enable);
        
        if(shop.getEnglishPermission() != null)
            sender.sendMessage("　영어 권한명 : " + shop.getName());
        if(shop.getKoreanPermission() != null)
            sender.sendMessage("　한글 권한명 : " + shop.getName());
        
        if(shop.getType() == ShopType.MARKET) {
            sender.sendMessage("　변동 주기 : " + shop.getInterval() + "분");
            sender.sendMessage("　다음 업데이트 시각 : " + shop.getFormattedNextUpdateTime());
        }
    }

    public static void broadcastPriceChanged(String shopName) {
        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage(getWarnPrefix()  + ChatColor.AQUA + "[" + shopName + "]" + ChatColor.WHITE + "의 시세가 변경되었습니다 !");
        Bukkit.broadcastMessage("");
    }

    /* 명령어 설명 */
    public static void moneyCommandHelper(CommandSender sender) {
        sender.sendMessage(getWarnPrefix() + "/돈 도움말" + ChatColor.GRAY + " : 사용 가능한 명령어를 확인할 수 있습니다.");
    }

    public static void moneyCommandList(CommandSender sender) {
        sender.sendMessage(getWarnPrefix() + "돈 명령어 목록");
        sender.sendMessage("　　　① /돈 확인");
        sender.sendMessage(ChatColor.GRAY + "　　　　　: 본인의 소지금을 확인합니다.");

        if(sender.isOp()) {
            sender.sendMessage("　　　② /돈 확인 플레이어ID|닉네임");
            sender.sendMessage(ChatColor.GRAY + "　　　　　: 플레이어의 소지금을 확인합니다.");
            sender.sendMessage("　　　③ /돈 송금 금액 플레이어ID|닉네임");
            sender.sendMessage(ChatColor.GRAY + "　　　　　: 플레이어에게 돈을 송금합니다.");
            sender.sendMessage("　　　④ /돈 추가 금액 플레이어ID|닉네임");
            sender.sendMessage(ChatColor.GRAY + "　　　　　: 플레이어의 소지금을 추가합니다.");
            sender.sendMessage("　　　⑤ /돈 차감 금액 플레이어ID|닉네임");
            sender.sendMessage(ChatColor.GRAY + "　　　　　: 플레이어의 소지금을 차감합니다.");
            sender.sendMessage("　　　⑥ /돈 설정 금액 플레이어ID|닉네임");
            sender.sendMessage(ChatColor.GRAY + "　　　　　: 플레이어의 소지금을 설정합니다.");
            sender.sendMessage("　　　⑦ /돈 순위 [페이지수]");
            sender.sendMessage(ChatColor.GRAY + "　　　　　: 소지금이 많은 플레이어들을 10명씩 확인합니다.");
            sender.sendMessage("　　　⑧ /돈 도움말");
            sender.sendMessage(ChatColor.GRAY + "　　　　　: 사용 가능한 명령어를 확인할 수 있습니다.");

        } else {
            sender.sendMessage("　　　② /돈 송금 금액 플레이어ID|닉네임");
            sender.sendMessage(ChatColor.GRAY + "　　　　　: 플레이어에게 돈을 송금합니다.");
            sender.sendMessage("　　　③ /돈 도움말");
            sender.sendMessage(ChatColor.GRAY + "　　　　　: 사용 가능한 명령어를 확인할 수 있습니다.");
        }
    }

    public static void checkCommandHelper(CommandSender sender) {
        sender.sendMessage(getWarnPrefix() + "/수표 도움말" + ChatColor.GRAY + " : 사용 가능한 명령어를 확인할 수 있습니다.");
    }

    public static void checkCommandList(CommandSender sender) {
        sender.sendMessage(getWarnPrefix() + "수표 명령어 목록");
        sender.sendMessage("　　　① /수표 금액 [개수]");
        sender.sendMessage(ChatColor.GRAY + "　　　　　: 수표를 발행합니다.");
        sender.sendMessage("　　　② /수표 도움말");
        sender.sendMessage(ChatColor.GRAY + "　　　　　: 사용 가능한 명령어를 확인할 수 있습니다.");
    }

    public static void shopCommandHelper(CommandSender sender) {
        sender.sendMessage(getWarnPrefix() + "/상점 도움말" + ChatColor.GRAY + " : 사용 가능한 명령어를 확인할 수 있습니다.");
    }

    public static void shopCommandList(CommandSender sender) {
        sender.sendMessage(getWarnPrefix() + "상점 명령어 목록");
        sender.sendMessage("　　　① /상점 열기 상점명 [플레이어ID|닉네임]");
        sender.sendMessage(ChatColor.GRAY + "　　　　　: 본인 또는 플레이어에게 상점 인벤토리를 엽니다.");
        sender.sendMessage("　　　② /상점 생성 상점명 유형 인벤토리수");
        sender.sendMessage(ChatColor.GRAY + "　　　　　: 상점을 생성합니다.");
        sender.sendMessage("　　　③ /상점 제거 상점명");
        sender.sendMessage(ChatColor.GRAY + "　　　　　: 상점을 제거합니다.");
        sender.sendMessage("　　　④ /상점 활성화|비활성화 상점명");
        sender.sendMessage(ChatColor.GRAY + "　　　　　: 상점을 활성화 또는 비활성화합니다.");
        sender.sendMessage("　　　⑤ /상점 인벤토리수설정 상점명 인벤토리수");
        sender.sendMessage(ChatColor.GRAY + "　　　　　: 상점의 인벤토리 수를 설정합니다. (9의 배수로 설정)");
        sender.sendMessage("　　　⑥ /상점 권한설정 상점명 영어권한명 한글권한명");
        sender.sendMessage(ChatColor.GRAY + "　　　　　: 상점을 열 수 있는 권한을 설정합니다. (공백일 경우 권한 해제)");
        sender.sendMessage("　　　⑦ /상점 변동주기설정 상점명 숫자(분)");
        sender.sendMessage(ChatColor.GRAY + "　　　　　: 상점의 시세 변동 주기를 설정합니다. (10분 단위로 설정)");
        sender.sendMessage("　　　⑧ /상점 GUI설정 상점명 [GUI_ID]");
        sender.sendMessage(ChatColor.GRAY + "　　　　　: 상점의 GUI를 등록합니다. (공백일 경우 GUI 해제)");
        sender.sendMessage("　　　⑨ /상점 아이템설정 상점명");
        sender.sendMessage(ChatColor.GRAY + "　　　　　: 상점 인벤토리를 열어 구매·판매 물품을 설정합니다.");
        sender.sendMessage("　　　⑩ /상점 구매가격설정 상점명 슬롯번호 기본가 [최고가] [최저가]");
        sender.sendMessage(ChatColor.GRAY + "　　　　　: 물품의 구매 가격을 설정합니다. (0원으로 설정 시 구매 금지)");
        sender.sendMessage(ChatColor.GRAY + "　　　　　: 시세 상점일 경우 최고가와 최저가도 모두 설정해야 합니다.");
        sender.sendMessage("　　　⑪ /상점 판매가격설정 상점명 슬롯번호 기본가 [최고가] [최저가]");
        sender.sendMessage(ChatColor.GRAY + "　　　　　: 물품의 판매 가격을 설정합니다. (0원으로 설정 시 판매 금지)");
        sender.sendMessage(ChatColor.GRAY + "　　　　　: 시세 상점일 경우 최고가와 최저가도 모두 설정해야 합니다.");
        sender.sendMessage("　　　⑫ /상점 재고설정 상점명 슬롯번호 재고");
        sender.sendMessage(ChatColor.GRAY + "　　　　　: 물품의 재고를 설정합니다. (0으로 설정 시 무제한)");
        sender.sendMessage("　　　⑬ /상점 최대변동률설정 상점명 슬롯번호 숫자(%)");
        sender.sendMessage(ChatColor.GRAY + "　　　　　: 물품의 최대 시세 변동률을 설정합니다.");
        sender.sendMessage("　　　⑭ /상점 재고초기화 상점명");
        sender.sendMessage(ChatColor.GRAY + "　　　　　: 상점의 재고를 보충합니다.");
        sender.sendMessage("　　　⑮ /상점 아이템초기화 상점명");
        sender.sendMessage(ChatColor.GRAY + "　　　　　: 물품 목록을 초기화합니다.");
        sender.sendMessage("　　　⑯ /상점 가격초기화 상점명");
        sender.sendMessage(ChatColor.GRAY + "　　　　　: 물품의 가격을 기본가로 초기화합니다.");
        sender.sendMessage("　　　⑰ /상점 정보 상점명");
        sender.sendMessage(ChatColor.GRAY + "　　　　　: 상점의 정보를 조회합니다.");
        sender.sendMessage("　　　⑱ /상점 도움말");
        sender.sendMessage(ChatColor.GRAY + "　　　　　: 사용 가능한 명령어를 확인할 수 있습니다.");
    }
}
