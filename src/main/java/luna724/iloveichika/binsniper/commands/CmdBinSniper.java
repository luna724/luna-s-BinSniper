/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  net.minecraft.command.CommandBase
 *  net.minecraft.command.CommandException
 *  net.minecraft.command.ICommandSender
 */
package luna724.iloveichika.binsniper.commands;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import luna724.iloveichika.binsniper.BinSniper;
import net.luna724.iloveichika.binsniper.Main;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.luna724.iloveichika.binsniper.utils.Util;
import net.luna724.iloveichika.binsniper.utils.Wrapper;
import org.apache.commons.lang3.math.NumberUtils;

import static net.luna724.iloveichika.binsniper.commands.configUtil.toggleConfig;
import static net.luna724.iloveichika.binsniper.logics.BinSnipeLogic.formatCoin;
import static net.luna724.iloveichika.binsniper.logics.sentLimbo.sentToLimbo;

public class CmdBinSniper
        extends CommandBase {
    private static void categoryHelp() {
        Util.sendAir();
        Util.send("§6BinSniper ("+ Main.MODID +") by Tomochie @NotTomochie");
        Util.sendAir();
        Util.send("§7カテゴリ番号 0. カテゴリ限定を無視 (デフォルト)");
        Util.send("§7カテゴリ番号 1. Weapons");
        Util.send("§7カテゴリ番号 2. Armor");
        Util.send("§7カテゴリ番号 3. Accessories");
        Util.send("§7カテゴリ番号 4. Consumables");
        Util.send("§7カテゴリ番号 5. Blocks");
        Util.send("§7カテゴリ番号 6. Tools & Misc");
        Util.send("§f/binsniper category <番号> - 番号を指定して下さい");
        Util.sendAir();
    }

    private static boolean isDecimal(String input) {
        boolean isDecimals = false;
        if (CmdBinSniper.argsNotNull(input)) {
            Pattern pattern = Pattern.compile("^([1-9]\\d*|0)(\\.\\d+)?$");
            isDecimals = pattern.matcher(input).matches();
        }
        return isDecimals;
    }

    @Override
    public List<String> getCommandAliases() {
        ArrayList<String> aliases = new ArrayList<String>();
        aliases.add("bs");
        return aliases;
    }


    public CmdBinSniper() {
        CmdBinSniper cmdBinSniper;
    }

    @Override
    public String getCommandName() {
        return "binsniper";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return null;
    }

    private static boolean argsNotNull(Object object) {
        return object != null;
    }

    private static void help() {
        Util.sendAir();
        Util.send("§dLuna's BinSni" +
                "per ("+ BinSniper.VERSION +") by. luna724");
        Util.sendAir();
        Util.send("§f/bs timeout 10000 * タイムアウトまでの時間 (設定非推奨)");
        Util.send("§f/bs delay 1000 * 遅延する 時間の指定 (設定非推奨)");
        Util.send("§f/bs coin 1m * 購入金額の指定");
        Util.send("§f/bs name Wither Catalyst * アイテムの名前を限定します");
        Util.send("§f/bs fastmode * 一つのページから検索するモード");
        Util.send("§f/bs allmode * 全ページから検索するモード");
        Util.send("§f/bs message * ログの表示/非表示の設定");
        Util.send("§f/bs reconnect * /ah で再接続するモード (CookieBuff必須)");
        Util.send("§f/bs category * カテゴリを限定します (CookieBuff必須)");
        Util.send("§f/bs save Snipe1 * 設定のプリセット機能 (セーブ)");
        Util.send("§f/bs load Snipe1 * 設定のプリセット機能 (ロード)");
        Util.send("§f/bs delete Snipe1 * 設定のプリセット機能 (削除)");
        Util.send("§f/bs list * 保存済みプリセットの表示");
        Util.send("§f/bs s (settings) * 現在の設定を表示します");
        Util.send("§d/bs forceStop * スナイプを強制停止します");
        Util.send("§d/bs uuidmode * UUIDモードを使用します");
        Util.send("§d/bs binsleep * スリープ中だったBINをスキップしません");
        Util.send("§d/bs limbo * Limboへ飛びます");
        Util.send("§d/bs toggleAAM * マクロ対策機能対策を使用します");

        Util.sendAir();
    }

    private static int CompareDouble(double d, double d2) {
        return Double.compare(d, d2);
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        String playerId = Wrapper.mc.getSession().getProfile().getId().toString();
        if (args.length == 0) {
            CmdBinSniper.help();
            return;
        }

        String trigger = args[0];
        if (trigger.equalsIgnoreCase("limbo")) { // Limboed
            sentToLimbo();
            return;
        }

        if (trigger.equalsIgnoreCase("npe")) {
            boolean toggleNPECatcher = toggleConfig("onTickNPECatcher");
            if (toggleNPECatcher) {
                Util.send("§aNPE Catcherを §d§l有効 §r§aに変更しました");
            }
            else {
                Util.send("§aNPE Catcherを §7§l無効 §r§aに変更しました");
            }
            return;
        }

        if (trigger.equalsIgnoreCase("toggleaam")) { // Toggle Anti-AntiMacro
            boolean toggleAAMValue = toggleConfig("antiantimacro");
            if (toggleAAMValue) {
                Util.send("§aAnti-AntiMacroを §d§l有効 §r§aに変更しました");
            }
            else {
                Util.send("§aAnti-AntiMacroを §7§l無効 §r§aに変更しました");
            }
            return;
        }

        if (trigger.equalsIgnoreCase("uuidmode")) {
            boolean uuidModeValue = toggleConfig("uuidMode");
            if (uuidModeValue) {
                Util.send("§aUUIDモードを §d§l有効 §r§aに変更しました");
            }
            else {
                Util.send("§aUUIDモードを §7§l無効 §r§aに変更しました");
            }
            return;
        }

        if (trigger.equalsIgnoreCase("binsleep")) {
            boolean uuidModeValue = toggleConfig("sleepOptimization");
            if (uuidModeValue) {
                Util.send("§aBIN Sleep最適化モードを §d§l有効 §r§aに変更しました");
            }
            else {
                Util.send("§aBIN Sleep最適化モードを §7§l無効 §r§aに変更しました");
            }
            return;
        }

        if (trigger.equalsIgnoreCase("forceStop")) {
            Util.config().set(playerId + ".Active", false);
            Util.save();
            Util.send("§4動作の強制停止");
        }

        if (trigger.equalsIgnoreCase("category")) {
            if (args.length != 2) {
                CmdBinSniper.categoryHelp();
                return;
            }
            String category = args[1];
            if (!NumberUtils.isDigits(category)) {
                Util.send("§c型が正しくありません 値は整数である必要があります");
                return;
            }
            int categoryInteger = Integer.parseInt(args[1]);
            int[] acceptableCategory = new int[]{0,1,2,3,4,5,6};
            int acLength = acceptableCategory.length;
            int i = 0;
            while (i < acLength) {
                int compareAC = acceptableCategory[i];
                if (categoryInteger == compareAC) {
                    Util.config().set(playerId + ".Category", categoryInteger);
                    Util.save();
                    Util.send("§aカテゴリは §6§l" + categoryInteger + "番 §r§aに設定されました");
                    return;
                }
                ++i;
            }
            CmdBinSniper.categoryHelp();
            return;
        }
        if (trigger.equalsIgnoreCase("reconnect")) {
            boolean currentReconnectValue = toggleConfig("Reconnect");
            if (currentReconnectValue) {
                Util.send("§a再接続モードを §7§l無効 §r§aに変更しました");
            }
            else {
                Util.send("§a再接続モードを §d§l有効 §r§aに変更しました");
            }
            return;
        }
        if (trigger.equalsIgnoreCase("message")) {
            boolean currentMessageStatus = toggleConfig("Message");
            if (currentMessageStatus) {
                Util.send("§aメッセージの表示を §7§l無効 §r§aに変更しま した");
            }
            else {
                Util.send("§aメッセージの表示を §d§l有効 §r§aに変更しました");
            }
            return;
        }
        if (trigger.equalsIgnoreCase("amount")) {
            Util.send("§c/bs amount は廃止されました。 (最終サポート: v2.0.βbuild24)");
            return;
        }
        if (trigger.equalsIgnoreCase("timeout")) {
            if (args.length != 2) {
                CmdBinSniper.help();
                return;
            }

            String timeoutMs = args[1];
            if (!NumberUtils.isDigits(timeoutMs)) {
                Util.send("§c型が正しくありません 値は整数である必要があります");
                return;
            }
            int timeoutMills = Integer.parseInt(args[1]);
            Util.config().set(playerId + ".Timeout", timeoutMills);
            Util.save();
            Util.send("§aタイムアウト時間は §6§l" + timeoutMills + "ms §r§aに設定されました");
            return;
        }
        if (trigger.equalsIgnoreCase("delay")) {
            if (args.length != 2) {
                CmdBinSniper.help();
                return;
            }

            String delay = args[1];
            if (!NumberUtils.isDigits(delay)) {
                Util.send("§c型が正しくありません 値は整数である必要があります");
                return;
            }
            int delayInt = Integer.parseInt(delay);
            Util.config().set(playerId + ".Delay", delayInt);
            Util.save();
            Util.send("§a指定の遅延時間は §6§l" + delayInt + "ms §r§aに設定されました");
            return;
        }
        if (trigger.equalsIgnoreCase("fastmode")) {
            Util.config().set(playerId + ".Mode", "FASTMODE");
            Util.save();
            Util.send("§aモードを §6§lFASTMODE §r§aに変更しました");
            return;
        }
        if (trigger.equalsIgnoreCase("allmode")) {
            Util.config().set(playerId + ".Mode", "ALLMODE");
            Util.save();
            Util.send("§aモードを §6§lALLMODE §r§aに変更しました");
            Util.send("§4ALLMODEは将来廃止される可能性があります");
            return;
        }
        if (trigger.equalsIgnoreCase("save")) {
            if (args.length != 2) {
                CmdBinSniper.help();
                return;
            }
            String presetName = args[1];
            int settingCost = Util.config().getInt(playerId + ".Cost");
            int settingCategory = Util.config().getInt(playerId + ".Category");
            int settingAmount = Util.config().getInt(playerId + ".Amount");
            String settingMode = Util.config().getString(playerId + ".Mode");
            String settingName = Util.config().getString(playerId + ".Name");
            Util.configMain().set("Preset." + presetName + ".Cost", settingCost);
            Util.configMain().set("Preset." + presetName + ".Category", settingCategory);
            Util.configMain().set("Preset." + presetName + ".Amount", settingAmount);
            Util.configMain().set("Preset." + presetName + ".Mode", settingMode);
            Util.configMain().set("Preset." + presetName + ".Name", settingName);
            Util.saveMain();
            NumberFormat numberInstance = NumberFormat.getNumberInstance();
            Util.sendAir();
            Util.send("§7現在の設定一覧:");
            Util.send("§7- Coin: §6" + numberInstance.format(Util.config().getInt(playerId + ".Cost")) + "/コイン");
            Util.send("§7- Category: §6" + Util.config().getInt(playerId + ".Category") + "/番");
            Util.send("§7- Amount: §6" + Util.config().getInt(playerId + ".Amount") + "/個");
            Util.send("§7- Name: §6" + Util.config().getString(playerId + ".Name"));
            Util.send("§7- Mode: §6" + Util.config().getString(playerId + ".Mode"));
            Util.sendAir();
            Util.send("§a現在の設定をプリセットの §6§l" + presetName + "§r§a に保存しました");
            return;
        }
        if (trigger.equalsIgnoreCase("load")) {
            if (args.length != 2) {
                CmdBinSniper.help();
                return;
            }
            String presetName = args[1];
            if (!Util.configMain().contains("Preset." + presetName)) {
                Util.send("§c入力したプリセットは存在しません");
                return;
            }

            int settingCost = Util.configMain().getInt("Preset." + presetName + ".Cost");
            int settingCategory = Util.configMain().getInt("Preset." + presetName + ".Category"); //edited
            int settingAmount = Util.configMain().getInt("Preset." + presetName + ".Amount");
            String settingMode = Util.configMain().getString("Preset." + presetName + ".Mode");
            String settingName = Util.configMain().getString("Preset." + presetName + ".Name");
            Util.config().set(playerId + ".Cost", settingCost);
            Util.config().set(playerId + ".Category", settingCategory);
            Util.config().set(playerId + ".Amount", settingAmount);
            Util.config().set(playerId + ".Mode", settingMode);
            Util.config().set(playerId + ".Name", settingName);
            Util.save();
            NumberFormat numberInstance = NumberFormat.getNumberInstance();
            Util.sendAir();
            Util.send("§7ロードした設定一覧:");
            Util.send("§7- Coin: §6" + numberInstance.format(Util.config().getInt(playerId + ".Cost")) + "/コイン");
            Util.send("§7- Category: §6" + Util.config().getInt(playerId + ".Category") + "/番");
            Util.send("§7- Amount: §6" + Util.config().getInt(playerId + ".Amount") + "/個");
            Util.send("§7- Name: §6" + Util.config().getString(playerId + ".Name"));
            Util.send("§7- Mode: §6" + Util.config().getString(playerId + ".Mode"));
            Util.sendAir();
            Util.send("§aプリセットの §6§l" + presetName + "§r§a をロードしました");
            return;
        }
        if (trigger.equalsIgnoreCase("delete")) {
            if (args.length != 2) {
                CmdBinSniper.help();
                return;
            }
            String presetName = args[1];
            if (!Util.configMain().contains("Preset." + presetName)) {
                Util.send("§c入力したプリセットは存在しません");
                return;
            }
            Util.configMain().set("Preset." + presetName, null);
            Util.saveMain();
            Util.send("§aプリセットの §6§l" + presetName + "§r§a を削除しました");
            return;
        }
        if (trigger.equalsIgnoreCase("list")) {
            Collection<String> presetList = Util.configMain().getSection("Preset").getKeys();
            Iterator<String> presetListIterator = presetList.iterator();
            while (presetListIterator.hasNext()) {
                String presetName = presetListIterator.next();
                Util.send("§7- " + presetName);
            }
            Util.send("§aプリセット一覧");
            return;
        }
        if (trigger.equalsIgnoreCase("settings") || trigger.equalsIgnoreCase("setting") || trigger.equalsIgnoreCase("s")) {
            NumberFormat numberInstance = NumberFormat.getNumberInstance();
            Util.sendAir();
            Util.send("§7現在の設定一覧:");
            Util.send("§7- Coin: §6" + numberInstance.format(Util.config().getInt(playerId + ".Cost")) + "/コイン");
            Util.send("§7- Category: §6" + Util.config().getInt(playerId + ".Category") + "/番");
            Util.send("§7- Amount: §6" + Util.config().getInt(playerId + ".Amount") + "/個");
            Util.send("§7- Name: §6" + Util.config().getString(playerId + ".Name"));
            Util.send("§7- Mode: §6" + Util.config().getString(playerId + ".Mode"));
            Util.sendAir();
            return;
        }
        if (trigger.equalsIgnoreCase("coin")) {
            if (args.length != 2) {
                CmdBinSniper.help();
                return;
            }
            String coinString = args[1];
            if (coinString.endsWith("k") || coinString.endsWith("m") || coinString.endsWith("b")) {
                coinString = coinString.substring(0, coinString.length() - 1);
            }
            if (!CmdBinSniper.isDecimal(coinString)) {
                CmdBinSniper.help();
                return;
            }
            double  parsedCoin = Double.parseDouble(coinString);
            if (args[0].length() > 64) {
                Util.send("§c金額が大きすぎです");
                return;
            }
            if (CmdBinSniper.CompareDouble(parsedCoin, 0.0) <= 0) {
                Util.send("§c金額が不正です");
                return;
            }
            if (args[1].endsWith("k")) {
                parsedCoin *= 1000.0; // 1,000
            } else if (args[1].endsWith("m")) {
                parsedCoin *= 1000000.0; // 1,000,000
            } else if (args[1].endsWith("b")) {
                parsedCoin *= 1.0E9; // 1,000,000,000
            }
            Util.config().set(playerId + ".Cost", parsedCoin);
            Util.save();
            NumberFormat numberInstance = NumberFormat.getNumberInstance();
            Util.send("§a指定の金額は §6§l" + formatCoin(parsedCoin) + "コイン §r§aに設定されました");
            return;
        }
        if (trigger.equalsIgnoreCase("name") || trigger.equalsIgnoreCase("item")) {
            if (args.length == 1) {
                CmdBinSniper.help();
                return;
            }
            String itemName = "";
            int i = 1;
            while (i < args.length) {
                if (args.length - 1 == i) {
                    itemName = itemName + args[i];
                } else {
                    itemName = itemName + args[i] + " ";
                }
                ++i;
            }
            Util.config().set(playerId + ".Name", itemName);
            Util.save();
            Util.send("§a指定のアイテム名は §6§l" + itemName + " §r§aに設定されました");
            Util.send("§cこの機能が必要ない場合は \"/binsniper None\" と入力して下さい");
            return;
        }
        CmdBinSniper.help();
    }

}