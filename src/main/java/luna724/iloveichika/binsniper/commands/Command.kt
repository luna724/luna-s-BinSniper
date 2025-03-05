package luna724.iloveichika.binsniper.commands

import luna724.iloveichika.binsniper.BinSniper
import luna724.iloveichika.binsniper.BinSniper.Companion.ChatLib
import luna724.iloveichika.binsniper.BinSniper.Companion.configUtil
import luna724.iloveichika.binsniper.BinSniper.Companion.logic
import luna724.iloveichika.binsniper.BinSniper.Companion.mc
import luna724.iloveichika.binsniper.Logic
import luna724.iloveichika.binsniper.getPlayerId
import luna724.iloveichika.binsniper.utils.Util
import net.minecraft.command.CommandBase
import net.minecraft.command.ICommandSender
import net.minecraft.util.BlockPos
import java.text.NumberFormat

class Command : CommandBase() {
    override fun getCommandName(): String {
        return "binsniper"
    }

    override fun getCommandAliases(): List<String> {
        return listOf("bs")
    }

    override fun getCommandUsage(sender: ICommandSender): String? {
        return null
    }

    override fun getRequiredPermissionLevel(): Int {
        return 0
    }

    fun help() {
        Util.sendAir();
        Util.send("§dLuna's BinSni" +
                "per ("+ BinSniper.VERSION +") by. luna724");
        Util.sendAir();
        Util.send("§f/bs timeout <Timeout:MS:10000> * タイムアウトまでの時間");
        Util.send("§f/bs delay <Delay:MS:1000> * 遅延する 時間の指定");
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

    fun categoryHelp() {
        Util.sendAir()
        Util.send("§6BinSniper ("+ BinSniper.MODID +") by luna724");
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

    fun showCurrentSettings() {
        val numberInstance = NumberFormat.getNumberInstance()
        Util.sendAir()
        Util.send("§7現在の設定一覧:")
        Util.send("§7- Coin: §6" + numberInstance.format(configUtil.getConfig("Cost")) + "/コイン")
        Util.send("§7- Category: §6" + configUtil.getConfig("Category") + "/番")
        Util.send("§7- Amount: §6" + configUtil.getConfig("Amount") + "/個")
        Util.send("§7- Name: §6" + configUtil.getConfig("Name"))
        Util.send("§7- Mode: §6" + configUtil.getConfig("Mode"))
        Util.sendAir()
    }

    override fun processCommand(sender: ICommandSender, args: Array<String>) {
        val playerId: String = mc.session.profile.id.toString()
        if (args.isEmpty()) {
            help()
            return
        }
        val trigger = args.getOrNull(0)?.lowercase() ?: run {
            help()
        }



        if (trigger == "limbo") {
            ChatLib.chat("§4/bs limboは消滅しました。二度と復活しません")
            return
        }
        else if (trigger == "me") {
            ChatLib.chat("Welcome to Binsniper! Your PlayerId are ${getPlayerId()}")
            return
        }
        else if (trigger == "npe") {
            val value = configUtil.toggleConfig("onTickNPECatcher")
            ChatLib.chat(
                "§8NPE Catcher§r§f: §9§l${if (value) "ON" else "OFF"}"
            )
            return
        }
        else if (trigger == "toggleaam") {
            val value = configUtil.toggleConfig("antiantimacro")
            ChatLib.chat(
                "§cAnti-AntiMacro§r§f: §9§l${if (value) "ON" else "OFF"}"
            )
            return
        }
        else if (trigger == "uuidmode") {
            val value = configUtil.toggleConfig("uuidMode")
            ChatLib.chat(
                "§5UUID-Mode§r§f: §9§l${if (value) "ON" else "OFF"}"
            )
            return
        }
        else if (trigger == "binsleep") {
            val value = configUtil.toggleConfig("sleepOptimization")
            ChatLib.chat(
                "§dBIN Sleep Optimization§r§f: §9§l${if (value) "ON" else "OFF"}"
            )
            return
        }
        else if (trigger == "reconnect") {
            val value = configUtil.toggleConfig("Reconnect")
            ChatLib.chat(
                "§3Reconnect§r§f: §9§l${if (value) "ON" else "OFF"}"
            )
            return
        }
        else if (trigger == "forcestop") {
            logic.stopSnipe()
        }
        else if (trigger == "category") {
            if (args.size < 2) {
                ChatLib.chat("§c/bs category <Category:LiteralInt>")
                return
            }
            val category: Int = args.getOrNull(1)?.toIntOrNull() ?: run {
                ChatLib.chat("§cFailed to parse int.")
                0
            }
            if (0 <= category && category <= 6) {
                val value = configUtil.setConfig("Category", category)
                ChatLib.chat(
                    "§aCategory§r§f: §9§l${value}"
                )
                return
            }
            else {
                categoryHelp()
                return
            }
            return
        }
        else if (trigger == "message") {
            val value = configUtil.toggleConfig("Message")
            ChatLib.chat(
                "§dSnipe Count§r§f: §9§l${if (value) "ON" else "OFF"}"
            )
            return
        }
        else if (trigger == "amount") {
            ChatLib.chat(
                "§4/bs amount はいらなすぎるので消しました。そのうち復活させます"
            )
            return
        }
        else if (trigger == "timeout") {
            if (args.size < 2) {
                ChatLib.chat("§c/bs timeout <Timeout:MS:Int>")
                return
            }
            val timeoutMs = args.getOrNull(1)?.toIntOrNull() ?: run {
                ChatLib.chat("§c/bs timeout <Timeout:MS:Int>")
                null
            }
            timeoutMs?: return
            val value = configUtil.setConfig("Timeout", timeoutMs)
            ChatLib.chat("§2Timeout§r§f: §9§l${value}ms")
            return
        }
        else if (trigger == "delay") {
            if (args.size < 2) {
                ChatLib.chat("§c/bs delay <Delay:MS>")
                return
            }
            val delay = args.getOrNull(1)?.toIntOrNull() ?: run {
                ChatLib.chat("§c/bs delay <Delay:MS:Int>")
                null
            }
            delay?: return
            val value = configUtil.setConfig("Delay", delay)
            ChatLib.chat("§2Delay§r§f: §9§l${value}ms")
            return
        }
        else if (trigger == "fastmode" || trigger == "allmode" || trigger == "swapmode") {
            var value: Any? = null
            if (trigger == "fastmode" || trigger == "allmode") {
                ChatLib.chat("§c/bs fastmode/allmode は廃止される可能性があります。§d/bs swapmode §cを使用してください")
                value = configUtil.setConfig("Mode", trigger.toString().uppercase())
            }
            else {
                val currentMode = configUtil.getConfig("Mode")
                value = configUtil.setConfig(
                    "Mode", if (currentMode == "FASTMODE") "ALLMODE" else "FASTMODE"
                )
            }
            ChatLib.chat("§bMode§r§f: §9§l$value")
            return
        }
        else if (trigger == "name" || trigger == "item") {
            if (args.size < 2) {
                ChatLib.chat("§c/bs name <ItemName>")
            }
            var itemName: String = ""
            itemName = args.toList().subList(1, args.size).joinToString(" ")
            val value = configUtil.setConfig(
                "Name", itemName
            )
            ChatLib.chat("§bTarget Item§r§f: §9§l$value")
            ChatLib.chat("§cこの機能が必要ない場合は \"/binsniper None\" と入力して下さい")

            return
        }
        else if (trigger == "back_compatibility") {
            val value = configUtil.toggleConfig("BackCompatibility")
            ChatLib.chat(
                "§dBack Compatibility§r§f: §9§l${if (value) "ON" else "OFF"}"
            )
            return
        }
        else if (trigger == "settings" || trigger == "s") {
            showCurrentSettings()
            return
        }
        else if (trigger == "save") {
            if (args.size < 2) {
                ChatLib.chat("§c/bs save <ConfigName>")
                return
            }
            val presetName: String = args.getOrNull(1) ?: return

            Util.configMain().set("Preset.$presetName.Cost", configUtil.getConfig("Cost"))
            Util.configMain().set("Preset.$presetName.Category", configUtil.getConfig("Category"))
            Util.configMain().set("Preset.$presetName.Amount", configUtil.getConfig("Amount"))
            Util.configMain().set("Preset.$presetName.Mode", configUtil.getConfig("Mode"))
            Util.configMain().set("Preset.$presetName.Name", configUtil.getConfig("Name"))
            Util.saveMain()

            Util.sendAir()
            Util.send("§7現在の設定一覧:")
            showCurrentSettings()
            Util.sendAir()
            Util.send("§a現在の設定をプリセットの §6§l$presetName§r§a に保存しました")
            return
        }
        else if (trigger == "load") {
            if (args.size < 2) {
                ChatLib.chat("§c/bs load <ConfigName>")
                return
            }
            val presetName: String = args.getOrNull(1) ?: return

            val settingCost = Util.configMain().getInt("Preset.$presetName.Cost")
            val settingCategory = Util.configMain().getInt("Preset.$presetName.Category") //edited
            val settingAmount = Util.configMain().getInt("Preset.$presetName.Amount")
            val settingMode = Util.configMain().getString("Preset.$presetName.Mode")
            val settingName = Util.configMain().getString("Preset.$presetName.Name")
            configUtil.setConfig("Cost", settingCost)
            configUtil.setConfig("Category", settingCategory)
            configUtil.setConfig("Amount", settingAmount)
            configUtil.setConfig("Mode", settingMode)
            configUtil.setConfig("Name", settingName)

            Util.sendAir()
            Util.send("§7ロードした設定一覧:")
            showCurrentSettings()
            Util.send("§aプリセットの §6§l$presetName§r§a をロードしました")
        }
        else if (trigger == "delete") {
            if (args.size < 2) {
                ChatLib.chat("§c/bs delete <ConfigName>")
                return
            }
            val presetName: String = args.getOrNull(1) ?: return

            if (!Util.configMain().contains("Preset.$presetName")) {
                Util.send("§c入力したプリセットは存在しません")
                return
            }
            Util.configMain().set("Preset.$presetName", null)
            Util.saveMain()
            Util.send("§aプリセットの §6§l$presetName§r§a を削除しました")
            return
        }
        else if (trigger == "list") {
            val presetList = Util.configMain().getSection("Preset").keys
            for (presetName in presetList) {
                Util.send("§7- $presetName")
            }
            Util.send("§aプリセット一覧")
            return
        }
        else {
            help()
        }
    }

    override fun addTabCompletionOptions(
        sender: ICommandSender,
        args: Array<out String>,
        pos: BlockPos
    ): List<String>? {
        if (args.size == 1) {
            // 第一引数の補完を提供
            return getListOfStringsMatchingLastWord(
                args,
                "timeout",
                "delay",
                "coin",
                "value",
                "fastmode",
                "allmode",
                "swapmode",
                "mesage",
                "reconnect",
                "category",
                "save",
                "load",
                "delete",
                "list",
                "s",
                "settings",
                "forcestop",
                "uuidmode",
                "binsleep",
                "limbo",
                "toggleaam",
                "back_compatibility"
            )
        }
        return null
    }
}