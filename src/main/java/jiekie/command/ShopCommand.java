package jiekie.command;

import jiekie.EconomyPlugin;
import jiekie.api.NicknameAPI;
import jiekie.exception.ShopException;
import jiekie.model.CommandContext;
import jiekie.model.Shop;
import jiekie.util.ChatUtil;
import jiekie.util.SoundUtil;
import jiekie.util.StringUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class ShopCommand implements CommandExecutor {
    private final EconomyPlugin plugin;
    private final Map<String, Consumer<CommandContext>> commandMap = new HashMap<>();

    public ShopCommand(EconomyPlugin plugin) {
        this.plugin = plugin;
        registerCommands();
    }

    private void registerCommands() {
        commandMap.put("열기", this::openShop);
        commandMap.put("생성", this::createShop);
        commandMap.put("제거", this::removeShop);
        commandMap.put("활성화", ctx -> activateShop(ctx, true));
        commandMap.put("비활성화", ctx -> activateShop(ctx, false));
        commandMap.put("인벤토리수설정", this::setInventorySize);
        commandMap.put("권한설정", this::setPermission);
        commandMap.put("변동주기설정", this::setInterval);
        commandMap.put("템플릿설정", this::setGuiTemplate);
        commandMap.put("아이템설정", this::setItems);
        commandMap.put("구매가격설정", this::setBuyPrice);
        commandMap.put("판매가격설정", this::setSellPrice);
        commandMap.put("재고설정", this::setStock);
        commandMap.put("최대변동률설정", this::setMaxFluctuation);
        commandMap.put("재고초기화", this::resetStock);
        commandMap.put("아이템초기화", this::resetItems);
        commandMap.put("가격초기화", this::resetPrice);
        commandMap.put("정보", this::showShopInfo);
        commandMap.put("도움말", ctx -> ChatUtil.shopCommandList(ctx.sender()));
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if(!sender.isOp()) {
            ChatUtil.notOp(sender);
            return true;
        }

        if(args == null || args.length == 0) {
            ChatUtil.shopCommandHelper(sender);
            return true;
        }

        Consumer<CommandContext> executer = commandMap.get(args[0]);
        if(executer == null) {
            ChatUtil.shopCommandHelper(sender);
            return true;
        }

        executer.accept(new CommandContext(sender, args));
        return true;
    }

    private void openShop(CommandContext context) {
        CommandSender sender = context.sender();
        String[] args = context.args();

        if(args.length < 2) {
            sender.sendMessage(ChatUtil.wrongCommand() + " (/상점 열기 상점명 [플레이어ID|닉네임])");
            return;
        }

        Player targetPlayer = null;
        if(args.length == 2) {
            targetPlayer = asPlayer(sender);
            if(targetPlayer == null) return;
        }

        if(args.length > 2) {
            String targetPlayerName = StringUtil.getContents(args, 2);
            targetPlayer = NicknameAPI.getInstance().getPlayerByNameOrNickname(targetPlayerName);
            if(targetPlayer == null) {
                ChatUtil.showErrorMessage(sender, ChatUtil.PLAYER_NOT_FOUND);
                return;
            }
        }

        try {
            plugin.getShopManager().openShop(targetPlayer, args[1], false);
        } catch (ShopException e) {
            ChatUtil.showErrorMessage(targetPlayer, e.getMessage());
        }
    }

    private void createShop(CommandContext context) {
        CommandSender sender = context.sender();
        String[] args = context.args();

        if(args.length < 4) {
            sender.sendMessage(ChatUtil.wrongCommand() + " (/상점 생성 상점명 유형 인벤토리수)");
            return;
        }

        Player player = asPlayer(sender);
        if(player == null) return;

        try {
            plugin.getShopManager().createShop(args[1], args[2], args[3]);
            ChatUtil.createShop(player);
            SoundUtil.playNoteBlockBell(player);

        } catch (ShopException e) {
            ChatUtil.showErrorMessage(sender, e.getMessage());
        }
    }

    private void removeShop(CommandContext context) {
        CommandSender sender = context.sender();
        String[] args = context.args();

        if(args.length < 2) {
            sender.sendMessage(ChatUtil.wrongCommand() + " (/상점 제거 상점명)");
            return;
        }

        Player player = asPlayer(sender);
        if(player == null) return;

        try {
            plugin.getShopManager().removeShop(args[1]);
            ChatUtil.removeShop(player);
            SoundUtil.playNoteBlockBell(player);

        } catch (ShopException e) {
            ChatUtil.showErrorMessage(sender, e.getMessage());
        }
    }

    private void activateShop(CommandContext context, boolean activate) {
        CommandSender sender = context.sender();
        String[] args = context.args();

        if(args.length < 2) {
            sender.sendMessage(ChatUtil.wrongCommand() + " (/상점 활성화|비활성화 상점명)");
            return;
        }

        Player player = asPlayer(sender);
        if(player == null) return;

        try {
            plugin.getShopManager().activateShop(args[1], activate);

            if(activate)
                ChatUtil.activateShop(player);
            else
                ChatUtil.deactivateShop(player);
            SoundUtil.playNoteBlockBell(player);

        } catch (ShopException e) {
            ChatUtil.showErrorMessage(sender, e.getMessage());
        }
    }

    private void setInventorySize(CommandContext context) {
        CommandSender sender = context.sender();
        String[] args = context.args();

        if(args.length < 3) {
            sender.sendMessage(ChatUtil.wrongCommand() + " (/상점 인벤토리수설정 상점명 인벤토리수)");
            return;
        }

        Player player = asPlayer(sender);
        if(player == null) return;

        try {
            plugin.getShopManager().setInventorySize(args[1], args[2]);
            ChatUtil.setInventorySize(player);
            SoundUtil.playNoteBlockBell(player);

        } catch (ShopException e) {
            ChatUtil.showErrorMessage(sender, e.getMessage());
        }
    }

    private void setPermission(CommandContext context) {
        CommandSender sender = context.sender();
        String[] args = context.args();

        if(args.length < 2) {
            sender.sendMessage(ChatUtil.wrongCommand() + " (/상점 권한설정 상점명 영어권한명 한글권한명)");
            return;
        }

        try {
            boolean setPermission = args.length >= 4;
            String englishPermission = args.length >= 4 ? args[2] : null;
            String koreanPermission = args.length >= 4 ? args[3] : null;
            plugin.getShopManager().setPermission(args[1], englishPermission, koreanPermission);

            if(setPermission)
                ChatUtil.setPermission(sender);
            else
                ChatUtil.resetPermission(sender);

            if(sender instanceof Player)
                SoundUtil.playNoteBlockBell((Player) sender);

        } catch (ShopException e) {
            ChatUtil.showErrorMessage(sender, e.getMessage());
        }
    }

    private void setInterval(CommandContext context) {
        CommandSender sender = context.sender();
        String[] args = context.args();

        if(args.length < 3) {
            sender.sendMessage(ChatUtil.wrongCommand() + " (/상점 변동주기설정 상점명 숫자(분))");
            return;
        }

        Player player = asPlayer(sender);
        if(player == null) return;

        try {
            plugin.getShopManager().setInterval(args[1], args[2]);
            ChatUtil.setInterval(player);
            SoundUtil.playNoteBlockBell(player);

        } catch (ShopException e) {
            ChatUtil.showErrorMessage(sender, e.getMessage());
        }
    }

    private void setGuiTemplate(CommandContext context) {
        CommandSender sender = context.sender();
        String[] args = context.args();

        if(args.length < 2) {
            sender.sendMessage(ChatUtil.wrongCommand() + " (/상점 템플릿설정 상점명 GUI명)");
            return;
        }

        Player player = asPlayer(sender);
        if(player == null) return;

        try {
            String templateId = args.length > 2 ? args[2] : null;
            plugin.getShopManager().setGuiTemplate(args[1], templateId);

            if(templateId == null)
                ChatUtil.resetGuiTemplate(player);
            else
                ChatUtil.setGuiTemplate(player);

            SoundUtil.playNoteBlockBell(player);

        } catch (ShopException e) {
            ChatUtil.showErrorMessage(sender, e.getMessage());
        }
    }

    private void setItems(CommandContext context) {
        CommandSender sender = context.sender();
        String[] args = context.args();

        if(args.length < 2) {
            sender.sendMessage(ChatUtil.wrongCommand() + " (/상점 아이템설정 상점명)");
            return;
        }

        Player player = asPlayer(sender);
        if(player == null) return;

        try {
            plugin.getShopManager().openShop(player, args[1], true);
        } catch (ShopException e) {
            ChatUtil.showErrorMessage(sender, e.getMessage());
        }
    }

    private void setBuyPrice(CommandContext context) {
        CommandSender sender = context.sender();
        String[] args = context.args();

        if(args.length < 4) {
            sender.sendMessage(ChatUtil.wrongCommand() + " (/상점 구매가격설정 상점명 슬롯번호 기본가 [최고가] [최저가])");
            return;
        }

        Player player = asPlayer(sender);
        if(player == null) return;

        try {
            plugin.getShopManager().setBuyPrice(args);
            ChatUtil.setBuyPrice(player);
            SoundUtil.playNoteBlockBell(player);

        } catch (ShopException e) {
            ChatUtil.showErrorMessage(sender, e.getMessage());
        }
    }

    private void setSellPrice(CommandContext context) {
        CommandSender sender = context.sender();
        String[] args = context.args();

        if(args.length < 4) {
            sender.sendMessage(ChatUtil.wrongCommand() + " (/상점 판매가격설정 상점명 슬롯번호 기본가 [최고가] [최저가])");
            return;
        }

        Player player = asPlayer(sender);
        if(player == null) return;

        try {
            plugin.getShopManager().setSellPrice(args);
            ChatUtil.setSellPrice(player);
            SoundUtil.playNoteBlockBell(player);

        } catch (ShopException e) {
            ChatUtil.showErrorMessage(sender, e.getMessage());
        }
    }

    private void setStock(CommandContext context) {
        CommandSender sender = context.sender();
        String[] args = context.args();

        if(args.length < 4) {
            sender.sendMessage(ChatUtil.wrongCommand() + " (/상점 재고설정 상점명 슬롯번호 재고)");
            return;
        }

        Player player = asPlayer(sender);
        if(player == null) return;

        try {
            plugin.getShopManager().setStock(args[1], args[2], args[3]);
            ChatUtil.setStock(player);
            SoundUtil.playNoteBlockBell(player);

        } catch (ShopException e) {
            ChatUtil.showErrorMessage(sender, e.getMessage());
        }
    }

    private void setMaxFluctuation(CommandContext context) {
        CommandSender sender = context.sender();
        String[] args = context.args();

        if(args.length < 4) {
            sender.sendMessage(ChatUtil.wrongCommand() + " (/상점 최대변동룰설정 상점명 슬롯번호 숫자(%))");
            return;
        }

        Player player = asPlayer(sender);
        if(player == null) return;

        try {
            plugin.getShopManager().setMaxFluctuation(args[1], args[2], args[3]);
            ChatUtil.setMaxFluctuation(player);
            SoundUtil.playNoteBlockBell(player);

        } catch (ShopException e) {
            ChatUtil.showErrorMessage(sender, e.getMessage());
        }
    }

    private void resetStock(CommandContext context) {
        CommandSender sender = context.sender();
        String[] args = context.args();

        if(args.length < 2) {
            sender.sendMessage(ChatUtil.wrongCommand() + " (/상점 재고초기화 상점명)");
            return;
        }

        Player player = asPlayer(sender);
        if(player == null) return;

        try {
            plugin.getShopManager().resetStock(args[1]);
            ChatUtil.resetStock(player);
            SoundUtil.playNoteBlockBell(player);

        } catch (ShopException e) {
            ChatUtil.showErrorMessage(sender, e.getMessage());
        }
    }

    private void resetItems(CommandContext context) {
        CommandSender sender = context.sender();
        String[] args = context.args();

        if(args.length < 2) {
            sender.sendMessage(ChatUtil.wrongCommand() + " (/상점 아이템초기화 상점명)");
            return;
        }

        Player player = asPlayer(sender);
        if(player == null) return;

        try {
            plugin.getShopManager().resetItems(args[1]);
            ChatUtil.resetItems(player);
            SoundUtil.playNoteBlockBell(player);

        } catch (ShopException e) {
            ChatUtil.showErrorMessage(sender, e.getMessage());
        }
    }

    private void resetPrice(CommandContext context) {
        CommandSender sender = context.sender();
        String[] args = context.args();

        if(args.length < 2) {
            sender.sendMessage(ChatUtil.wrongCommand() + " (/상점 가격초기화 상점명)");
            return;
        }

        Player player = asPlayer(sender);
        if(player == null) return;

        try {
            plugin.getShopManager().resetPrice(args[1]);
            ChatUtil.resetPrice(player);
            SoundUtil.playNoteBlockBell(player);

        } catch (ShopException e) {
            ChatUtil.showErrorMessage(sender, e.getMessage());
        }
    }

    private void showShopInfo(CommandContext context) {
        CommandSender sender = context.sender();
        String[] args = context.args();

        if(args.length < 2) {
            sender.sendMessage(ChatUtil.wrongCommand() + " (/상점 정보 상점명)");
            return;
        }

        try {
            Shop shop = plugin.getShopManager().getShopOrThrow(args[1]);

            ChatUtil.shopInfoPrefix(sender);
            ChatUtil.shopInfo(sender, shop);
            ChatUtil.horizontalLineSuffix(sender);

            if(sender instanceof Player)
                SoundUtil.playNoteBlockBell((Player) sender);

        } catch (ShopException e) {
            ChatUtil.showErrorMessage(sender, e.getMessage());
        }
    }

    private Player asPlayer(CommandSender sender) {
        if(!(sender instanceof Player)) {
            ChatUtil.notPlayer(sender);
            return null;
        }

        return (Player) sender;
    }
}
