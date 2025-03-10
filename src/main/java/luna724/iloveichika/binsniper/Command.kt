package luna724.iloveichika.binsniper

import luna724.iloveichika.binsniper.BinSniper.Companion.ChatLib
import luna724.iloveichika.binsniper.BinSniper.Companion.config
import luna724.iloveichika.binsniper.BinSniper.Companion.logic
import luna724.iloveichika.binsniper.config.SessionConfig
import net.minecraft.command.CommandBase
import net.minecraft.command.ICommandSender
import net.minecraft.util.BlockPos

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
        ChatLib.chat(" ");
        ChatLib.chat("§dLuna's BinSni" +
                "per ("+ BinSniper.VERSION +") by. luna724");
        ChatLib.chat(" ");
        ChatLib.chat("§f/bs timeout <Timeout:MS:10000> * タイムアウトまでの時間");
        ChatLib.chat("§f/bs delay <Delay:MS:1000> * 遅延する 時間の指定");
        ChatLib.chat("§f/bs coin 1m * 購入金額の指定");
        ChatLib.chat("§f/bs name Wither Catalyst * アイテムの名前を限定します");
        ChatLib.chat("§f/bs fastmode * 一つのページから検索するモード");
        ChatLib.chat("§f/bs allmode * 全ページから検索するモード");
        ChatLib.chat("§f/bs message * ログの表示/非表示の設定");
        ChatLib.chat("§f/bs reconnect * /ah で再接続するモード (CookieBuff必須)");
        ChatLib.chat("§f/bs category * カテゴリを限定します (CookieBuff必須)");
        ChatLib.chat("§f/bs save Snipe1 * 設定のプリセット機能 (セーブ)");
        ChatLib.chat("§f/bs load Snipe1 * 設定のプリセット機能 (ロード)");
        ChatLib.chat("§f/bs delete Snipe1 * 設定のプリセット機能 (削除)");
        ChatLib.chat("§f/bs list * 保存済みプリセットの表示");
        ChatLib.chat("§f/bs s (settings) * 現在の設定を表示します");
        ChatLib.chat("§d/bs forceStop * スナイプを強制停止します");
        ChatLib.chat("§d/bs uuidmode * UUIDモードを使用します");
        ChatLib.chat("§d/bs binsleep * スリープ中だったBINをスキップしません");
        ChatLib.chat("§d/bs limbo * Limboへ飛びます");
        ChatLib.chat("§d/bs toggleAAM * マクロ対策機能対策を使用します");
        ChatLib.chat(" ");
        ChatLib.chat("§dこのリストは更新されていません。最新の情報は README を確認してください (/bs doc)")
        ChatLib.chat(" ");
    }

    fun categoryHelp() {
        ChatLib.chat("§7カテゴリ番号 0. カテゴリ限定を無視 (デフォルト)");
        ChatLib.chat("§7カテゴリ番号 1. Weapons");
        ChatLib.chat("§7カテゴリ番号 2. Armor");
        ChatLib.chat("§7カテゴリ番号 3. Accessories");
        ChatLib.chat("§7カテゴリ番号 4. Consumables");
        ChatLib.chat("§7カテゴリ番号 5. Blocks");
        ChatLib.chat("§7カテゴリ番号 6. Tools & Misc");
        ChatLib.chat("§f/binsniper category <番号> - 番号を指定して下さい");
        ChatLib.chat(" ")
    }

    override fun processCommand(sender: ICommandSender, args: Array<String>) {
        val userConfig: SessionConfig = getUserConfig()
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
        else if (trigger == "toggleaam") {
            userConfig.AntiAntiMacro = !userConfig.AntiAntiMacro
            ChatLib.chat(
                "§cAnti-AntiMacro§r§f: §9§l${if (userConfig.AntiAntiMacro) "ON" else "OFF"}"
            )
            config.savePlayer(userConfig)
            return
        }
        else if (trigger == "uuidmode") {
            userConfig.UUIDMode = !userConfig.UUIDMode
            ChatLib.chat(
                "§5UUID-Mode§r§f: §9§l${if (userConfig.UUIDMode) "ON" else "OFF"}"
            )
            config.savePlayer(userConfig)
            return
        }
        else if (trigger == "binsleep" || trigger == "sleepoptimization") {
            userConfig.SleepOptimization = !userConfig.SleepOptimization
            ChatLib.chat(
                "§dBIN Sleep Optimization§r§f: §9§l${if (userConfig.SleepOptimization) "ON" else "OFF"}"
            )
            config.savePlayer(userConfig)
            return
        }
        else if (trigger == "reconnect") {
            userConfig.Reconnect = !userConfig.Reconnect
            ChatLib.chat(
                "§3Reconnect§r§f: §9§l${if (userConfig.Reconnect) "ON" else "OFF"}"
            )
            config.savePlayer(userConfig)
            return
        }
        else if (trigger == "dev") {
            userConfig.Debug = !userConfig.Debug
            ChatLib.chat(
                "§6Debug Message§r§f: §9§l${if (userConfig.Debug) "ON" else "OFF"}"
            )
            config.savePlayer(userConfig)
            return
        }
        else if (trigger == "forcestop") {
            logic.stopSnipe("/bs forcestop")
        }
        else if (trigger == "forcestart") {
            logic.startSnipe()
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
                userConfig.Category = category
                ChatLib.chat(
                    "§aCategory§r§f: §9§l${userConfig.Category}"
                )
                config.savePlayer(userConfig)
                return
            }
            else {
                categoryHelp()
                return
            }
            return
        }
        else if (trigger == "message") {
            userConfig.Message = !userConfig.Message
            ChatLib.chat(
                "§dSnipe Count§r§f: §9§l${if (userConfig.Message) "ON" else "OFF"}"
            )
            return
        }
        else if (trigger == "amount") {
            ChatLib.chat(
                "§4/bs amount はいらなすぎるので消しました。そのうち復活させます"
            )
            return
        }
        else if (trigger == "coin" || trigger == "value") {
            if (args.size < 2) {
                ChatLib.chat("§c/bs coin <Coin:String>")
                return
            }
            var coinString = args.getOrNull(1)?.lowercase() ?: return
            var coin = ""
            if (coinString.endsWith("k") || coinString.endsWith("m") || coinString.endsWith("b")) {
                coin = coinString.substring(0, coinString.length - 1)
            }
            else {
                coin = coinString
            }
            if (!isDecimal(coin)) {
                ChatLib.chat("§c/bs coin <Coin:String>")
                return
            }
            var parsedCoin = coin.toDoubleOrNull() ?: return
            if (parsedCoin <= 0) {
                ChatLib.chat("§c金額がプラスである必要があります")
                return
            }
            if (coinString.endsWith("k")) {
                parsedCoin *= 1000
            } else if (coinString.endsWith("m")) {
                parsedCoin *= 1000000
            } else if (coinString.endsWith("b")) {
                parsedCoin *= 1000000000
            }
            userConfig.Cost = parsedCoin.toInt()
            config.savePlayer(userConfig)

            ChatLib.chat("§a指定の金額は §6§l${formatCoinToString(parsedCoin)} コイン §r§aに設定されました")
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
            userConfig.Timeout = timeoutMs.toLong()
            ChatLib.chat("§2Timeout§r§f: §9§l${userConfig.Timeout}ms")
            config.savePlayer(userConfig)
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
            userConfig.Delay = delay.toLong()
            ChatLib.chat("§2Delay§r§f: §9§l${userConfig.Delay}ms")
            config.savePlayer(userConfig)
            return
        }
        else if (trigger == "fastmode" || trigger == "allmode" || trigger == "swapmode") {
            if (trigger == "fastmode" || trigger == "allmode") {
                ChatLib.chat("§c/bs fastmode/allmode は廃止される可能性があります。§d/bs swapmode §cを使用してください")
                userConfig.Mode = trigger.toString().uppercase()
            }
            else {
                userConfig.Mode = if (userConfig.Mode == "FASTMODE") "ALLMODE" else "FASTMODE"
            }
            ChatLib.chat("§bMode§r§f: §9§l${userConfig.Mode}")
            config.savePlayer(userConfig)
            return
        }
        else if (trigger == "name" || trigger == "item") {
            if (args.size < 2) {
                ChatLib.chat("§c/bs name <ItemName>")
            }
            var itemName: String = ""
            itemName = args.toList().subList(1, args.size).joinToString(" ")
            userConfig.Name = itemName
            ChatLib.chat("§bTarget Item§r§f: §9§l${userConfig.Name}")
            ChatLib.chat("§cこの機能が必要ない場合は \"/binsniper None\" と入力して下さい")

            config.savePlayer(userConfig)
            return
        }
        else if (trigger == "back_compatibility") {
            userConfig.BackCompatibility = !userConfig.BackCompatibility
            ChatLib.chat(
                "§dBack Compatibility§r§f: §9§l${if (userConfig.BackCompatibility) "ON" else "OFF"}"
            )
            return
        }
        else if (trigger == "settings" || trigger == "s") {
            logic.showCurrentSettings(userConfig)
            return
        }
        else if (trigger == "save") {
            if (args.size < 2) {
                ChatLib.chat("§c/bs save <ConfigName>")
                return
            }
            val presetName: String = args.getOrNull(1) ?: return
            if (presetName.contains("-")) {
                ChatLib.chat("§cプリセット名にハイフンを含むことはできません")
                return
            }

            // プリセット名をキーとした Map を追加
            config.savePlayer(userConfig, presetName)

            ChatLib.chat(" ")
            ChatLib.chat("§7現在の設定一覧:")
            logic.showCurrentSettings(userConfig)
            ChatLib.chat(" ")
            ChatLib.chat("§a現在の設定をプリセットの §6§l$presetName§r§a に保存しました")
            return
        }
        else if (trigger == "load") {
            if (args.size < 2) {
                ChatLib.chat("§c/bs load <ConfigName>")
                return
            }
            val presetName: String = args.getOrNull(1) ?: return
            val allObj = config.loadAll()

            if (presetName !in allObj.keys) {
                ChatLib.chat("§c対象プリセットが見つかりませんでした")
                return
            }
            val cfg: SessionConfig = allObj[presetName]!!
            config.savePlayer(cfg)

            ChatLib.chat(" ")
            ChatLib.chat("§7ロードした設定一覧:")
            logic.showCurrentSettings(getUserConfig())
            ChatLib.chat("§aプリセットの §6§l$presetName§r§a をロードしました")
        }
        else if (trigger == "delete") {
            if (args.size < 2) {
                ChatLib.chat("§c/bs delete <ConfigName>")
                return
            }
            val presetName: String = args.getOrNull(1) ?: return
            val allObj = config.loadAll()

            if (presetName !in allObj.keys) {
                ChatLib.chat("§c入力したプリセットは存在しません")
                return
            }
            allObj.remove(presetName)
            config.savePlayer(userConfig)

            ChatLib.chat("§aプリセットの §6§l$presetName§r§a を削除しました")
            return
        }
        else if (trigger == "list") {
            val presetList = config.loadAll().keys
            for (presetName in presetList) {
                ChatLib.chat("§7- $presetName")
            }
            ChatLib.chat("§aプリセット一覧 (UUIDのプリセットはプレイヤーごとのプリセット)")
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