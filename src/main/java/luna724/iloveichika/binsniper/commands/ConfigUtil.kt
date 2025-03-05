package luna724.iloveichika.binsniper.commands

import luna724.iloveichika.binsniper.BinSniper.Companion.ChatLib
import luna724.iloveichika.binsniper.BinSniper.Companion.mc
import luna724.iloveichika.binsniper.utils.Util
import tv.twitch.chat.Chat

class ConfigUtil {
    private fun getPlayerId(): String {
        return mc.session.profile.id.toString()
    }

    /**
     * BooleanConfigをオンオフする
     * 変更後の値を返す
     */
    fun toggleConfig(targetConfig: String): Boolean {
        try {
            val playerId = getPlayerId()
            Util.config().set(
                "$playerId.$targetConfig",
                !Util.config().getBoolean("$playerId.$targetConfig")
            )
            Util.save()
            return Util.config().getBoolean("$playerId.$targetConfig")
        }
        catch (e: Exception) {
            e.printStackTrace()
            ChatLib.chat("§4設定の変更時にエラーが発生しました。詳しくはログみろ")
            return false
        }
    }

    /**
     * その他値を持つ設定の内容を変更する
     * 変更後の値を返す
     */
    fun setConfig(targetConfig: String, value: Any?): Any? {
        try {
            val playerId = getPlayerId()
            Util.config().set(
                "$playerId.$targetConfig",
                value
            )
            Util.save()
            return Util.config().get("$playerId.$targetConfig")
        }
        catch (e: Exception) {
            e.printStackTrace()
            ChatLib.chat("§4設定の変更時にエラーが発生しました。($targetConfig)")
            return false
        }
    }

    /**
     * その他の値を持つ設定の内容を返す
     */
    fun getConfig(targetConfig: String): Any? {
        try {
            val playerId = getPlayerId()
            return Util.config().get("$playerId.$targetConfig")
        }
        catch (e: Exception) {
            e.printStackTrace()
            ChatLib.chat("§4設定の取得時にエラーが発生しました。($targetConfig)")
            return null
        }
    }

    /**
     * その他の値を持つ設定の内容を返す
     */
    fun getBooleanConfig(targetConfig: String): Boolean {
        try {
            val playerId = getPlayerId()
            return Util.config().getBoolean("$playerId.$targetConfig")
        }
        catch (e: Exception) {
            e.printStackTrace()
            ChatLib.chat("§4Boolean設定の取得時にエラーが発生しました。($targetConfig) ($e)")
            return false
        }
    }
}