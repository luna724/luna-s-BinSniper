package luna724.iloveichika.binsniper

import luna724.iloveichika.binsniper.BinSniper.Companion.config
import luna724.iloveichika.binsniper.BinSniper.Companion.mc
import luna724.iloveichika.binsniper.config.SessionConfig
import net.minecraft.client.gui.inventory.GuiChest
import net.minecraft.inventory.ContainerChest
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagList

fun getUserConfig(): SessionConfig {
    return config.loadPlayer()
}


// Getter
fun getPlayerId(): String = mc.session.profile.id.toString()
fun getContainerChest(): ContainerChest = mc.thePlayer.openContainer as ContainerChest
fun getLoreFromItemStack(i: ItemStack): NBTTagList = i.tagCompound.getCompoundTag("display").getTagList("Lore", 8)

// Checker
fun isContainerOpened(): Boolean = mc.thePlayer.openContainer is ContainerChest
fun isGuiChestOpened(): Boolean = mc.currentScreen is GuiChest
