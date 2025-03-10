package luna724.iloveichika.binsniper

import net.minecraft.inventory.Container
import net.minecraft.inventory.ContainerChest
import net.minecraft.inventory.Slot
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagList

class ContainerUtil {
    fun isAuctionBrowser(containerChest: Container): Boolean {
        if (!isGuiChestOpened()) return false
        if (!isContainerOpened()) return false

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

    fun isBinOnly(containerChest: ContainerChest): Boolean {
        if (!getUserConfig().Active) return false
        if (!isGuiChestOpened()) return false
        if (!isContainerOpened()) return false

        val itemStack: ItemStack = containerChest.getSlot(52).getStack() ?: return false

        var lore: String = ""
        val loreLists: NBTTagList = itemStack.tagCompound.getCompoundTag("display").getTagList("Lore", 8)
        for (i in 0 until loreLists.tagCount()) {
            lore = loreLists.getStringTagAt(i)
            if (lore.contains("▶ BIN Only")) {
                return true
            }
        }
        return false
    }

    fun isLowest(containerChest: ContainerChest): Boolean {
        if (!getUserConfig().Active) return false
        if (!isGuiChestOpened()) return false
        if (!isContainerOpened()) return false

        val slot: Slot = containerChest.getSlot(50)
        if (!slot.hasStack) return false
        val stack: ItemStack = slot.stack ?: return false

        var lore: String = ""
        val loreLists: NBTTagList = stack.tagCompound.getCompoundTag("display").getTagList("Lore", 8)
        for (i in 0 until loreLists.tagCount()) {
            lore = loreLists.getStringTagAt(i)
            if (lore.contains("▶ Lowest Price")) {
                return true
            }
        }
        return false
    }

    fun isNoFilter(containerChest: ContainerChest): Boolean {
        if (!getUserConfig().Active) return false
        if (!isGuiChestOpened()) return false
        if (!isContainerOpened()) return false

        val slot: Slot = containerChest.getSlot(51)
        if (!slot.hasStack) return false
        val stack: ItemStack = slot.stack ?: return false

        var lore: String = ""
        val loreLists: NBTTagList = stack.tagCompound.getCompoundTag("display").getTagList("Lore", 8)
        for (i in 0 until loreLists.tagCount()) {
            lore = loreLists.getStringTagAt(i)
            if (lore.contains("▶ No filter")) {
                return true
            }
            else if (lore.contains("▶ Ultimate")) {
                return false
            } // 上から順に処理するためスナイプ対象が Ultimate でも問題ない
        }
        return false
    }

    fun isAnvil(containerChest: ContainerChest): Boolean {
        if (!isGuiChestOpened()) return false
        if (!isContainerOpened()) return false

        val itemStack = containerChest.getSlot(22).stack ?: return false
        return itemStack.displayName.contains("§aCombine Items")
    }

    fun isPending(containerChest: ContainerChest): Boolean {
        if (!isGuiChestOpened()) return false
        if (!isContainerOpened()) return false

        val itemStack = containerChest.getSlot(13).stack ?: return false
        return itemStack.displayName.contains("§aManage Bids") || itemStack.displayName.contains("§aView Bids")
    }
}