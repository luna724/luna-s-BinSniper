package luna724.iloveichika.binsniper.utils

import luna724.iloveichika.binsniper.BinSniper.Companion.HEADER
import luna724.iloveichika.binsniper.BinSniper.Companion.mc
import net.minecraft.util.ChatComponentText

class _ChatLib {
    fun chat(message: String) {
        mc.thePlayer?.addChatMessage(
            ChatComponentText(HEADER + message)
        ) ?: println(message)
        return
    }

    fun command(message: String) {
        mc.thePlayer?.sendChatMessage("/${message}") ?:
        return
    }
}