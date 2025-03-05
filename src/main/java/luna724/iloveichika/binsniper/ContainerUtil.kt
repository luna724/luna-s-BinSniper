package luna724.iloveichika.binsniper

import luna724.iloveichika.binsniper.BinSniper.Companion.mc
import net.minecraft.client.gui.inventory.GuiChest
import net.minecraft.inventory.Container
import net.minecraft.inventory.ContainerChest

class ContainerUtil {
    fun isAuctionBrowser(containerChest: Container): Boolean {
        if (containerChest !is ContainerChest) return false
        if (mc.currentScreen !is GuiChest) return false
        if (mc.thePlayer.openContainer !is ContainerChest) return false

        val ints = intArrayOf(0, 9, 18, 27, 36, 45)
        for (i in ints) {
            val itemStack = containerChest.getSlot(i).stack
            if (itemStack == null) {
                return false
            }
            if (i == 0 && !itemStack.displayName.equals("§6Weapons", ignoreCase = true)) {
                return false
            }
            if (i == 9 && !itemStack.displayName.equals("§bArmor", ignoreCase = true)) {
                return false
            }
            if (i == 18 && !itemStack.displayName.equals("§2Accessories", ignoreCase = true)) {
                return false
            }
            if (i == 27 && !itemStack.displayName.equals("§cConsumables", ignoreCase = true)) {
                return false
            }
            if (i == 36 && !itemStack.displayName.equals("§eBlocks", ignoreCase = true)) {
                return false
            }
            if (i == 45 && !itemStack.displayName.equals("§dTools & Misc", ignoreCase = true)) {
                return false
            }
        }
        return true
    }
}