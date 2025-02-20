package luna724.iloveichika.binsniper.commands

import luna724.iloveichika.binsniper.BinSniper
import luna724.iloveichika.binsniper.BinSniper.Companion.ChatLib
import luna724.iloveichika.binsniper.BinSniper.Companion.mc
import luna724.iloveichika.binsniper.commands.configUtil.toggleConfig
import luna724.iloveichika.binsniper.utils.Util
import net.minecraft.command.CommandBase
import net.minecraft.command.ICommandSender

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

    override fun processCommand(sender: ICommandSender, args: Array<String>) {
        val playerId: String = mc.session.profile.id.toString()
        if (args.size == 0) {
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
        else if (trigger == "npe") {
            val value = toggleConfig("onTickNPECatcher")
            ChatLib.chat(
                "§8NPE Catcher§r§f: §9§l${if (value) "ON" else "OFF"}"
            )
            return
        }
        else if (trigger == "toggleaam") {
            val value = toggleConfig("antiantimacro")
            ChatLib.chat(
                "§cAnti-AntiMacro§r§f: §9§l${if (value) "ON" else "OFF"}"
            )
            return
        }
        else if (trigger == "uuidmode") {
            val value = toggleConfig("uuidMode")
            ChatLib.chat(
                "§5UUID-Mode§r§f: §9§l${if (value) "ON" else "OFF"}"
            )
            return
        }
        else if (trigger == "binsleep") {
            val value = toggleConfig("sleepOptimization")
            ChatLib.chat(
                "§dBIN Sleep Optimization§r§f: §9§l${if (value) "ON" else "OFF"}"
            )
            return
        }
        else if (trigger == "reconnect") {
            val value = toggleConfig("Reconnect")
            ChatLib.chat(
                "§3Reconnect§r§f: §9§l${if (value) "ON" else "OFF"}"
            )
            return
        }
        else if (trigger == "forcestop") {
            TODO()
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
                Util.config().set("$playerId.Category", category)
                ChatLib.chat(
                    "§aCategory§r§f: §9§l${category}"
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
            val value = toggleConfig("Message")
            ChatLib.chat(
                "§dInternal Message§r§f: §9§l${if (value) "ON" else "OFF"}"
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
                ChatLib.chat("§c/bs timeout <Timeout:MS>")
                return
            }
        }
    }
}