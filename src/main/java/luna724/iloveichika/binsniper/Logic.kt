package luna724.iloveichika.binsniper

import luna724.iloveichika.binsniper.BinSniper.Companion.ChatLib
import luna724.iloveichika.binsniper.BinSniper.Companion.configUtil
import luna724.iloveichika.binsniper.BinSniper.Companion.mc
import luna724.iloveichika.binsniper.utils.ItemChecker
import net.minecraft.client.gui.inventory.GuiChest
import net.minecraft.inventory.Container
import net.minecraft.inventory.ContainerChest
import net.minecraft.item.Item
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import gg.skytils.skytilsmod.utils.ItemUtil
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagList
import net.minecraft.util.EnumChatFormatting
import org.apache.commons.lang3.math.NumberUtils

class Logic {
    companion object {
        val containerUtil = ContainerUtil()
        val itemChecker = ItemChecker()

        var reforges: MutableList<String> = mutableListOf()
        val accessoryReforges: List<String> = itemChecker.getAccessoryReforges()
    }

    // TODO 設定できるようにしろって値
    private val worldMovingCooldown: Long = 5000L // TODO 5000L を設定できるように
    private val maxUUIDCacheSize: Int = 40

    // すでに購入した UUID を管理するセット
    private var sessionUUID: MutableList<String> = mutableListOf()
    private var lastTime: Long = 0L
    private var lastSeller: String = ""
    private var sessionBuyed: Int = 0
    private var sessionStartAt: Int = 0
    private var sessionCheckCount: Int = 0
    private var isWorldChanged: Boolean = false

    /**
     * セッションを初期化する
     * 開始時に必ず実行する
     */
    private fun resetSession() {
        sessionBuyed = 0
        sessionStartAt = 0
        sessionCheckCount = 0
        lastSeller = ""
        isWorldChanged = false
        lastTime = System.currentTimeMillis()
        sessionUUID.clear()
        reforges = itemChecker.getPlainReforges().toMutableList()

        if (configUtil.getBooleanConfig("BackCompatibility")) {
            reforges.addAll(accessoryReforges)
        }
    }

    // Getter
    private fun getPlayerId(): String = mc.session.profile.id.toString()

    // Checker
    private fun isBinSniperActive(): Boolean = configUtil.getBooleanConfig("Active")
    private fun isContainerOpened(): Boolean = mc.thePlayer.openContainer is ContainerChest
    private fun isGuiChestOpened(): Boolean = mc.currentScreen is GuiChest
    private fun isAuctionBrowserOpened(): Boolean = containerUtil.isAuctionBrowser(mc.thePlayer.openContainer)
    private fun isUUIDCheckerEnable(): Boolean = configUtil.getBooleanConfig("uuidMode")
    private fun isBuyedUUID(targetUUID: String): Boolean = sessionUUID.contains(targetUUID)

    /** */
    private fun stopSnipe() {
        val playerId = getPlayerId()
        configUtil.setConfig("Active", false)
        ChatLib.chat("§c動作の停止")
        return
    }

    /** 設定に基づいてメッセージを送る */
    private fun sendMessage(msg: String) {
        if (configUtil.getBooleanConfig("Message")) {
            ChatLib.chat(msg)
        }
    }

    /** 指定されたスロットをクリックする */
    private fun clickSlot(windowSlot1: Int, windowSlot2: Int) {
        val player = mc.thePlayer
        mc.playerController.windowClick(
            player.openContainer.windowId, windowSlot1, windowSlot2, 0, player
        )
    }

    /** UUID を購入済みリストに追加 */
    private fun registerUUID(newUUID: String) {
        if (sessionUUID.size >= maxUUIDCacheSize) {
            sessionUUID.removeAt(0)
        }
        sessionUUID.add(newUUID)
        return
    }

    /** アイテムから販売者のIDを取得 */
    private fun getSeller(itemStack: ItemStack?): String {
        if (!isBinSniperActive()) return "!None"
        if (!isGuiChestOpened()) return "!None"
        if (!isContainerOpened()) return "!None"
        if (itemStack == null) return "!None"

        var lore: String = "!None"
        val loreList: NBTTagList = itemStack.tagCompound.getCompoundTag("display").getTagList("Lore", 8)
        for (i in 0 until loreList.tagCount()) {
            lore = loreList.getStringTagAt(i).toString()
            if (lore.startsWith("§7Seller: ")) {
                lore = lore.replace("§7Seller: ", "");
                break
            }
        }
        return EnumChatFormatting.getTextWithoutFormattingCodes(lore);
    }

    /** アイテムからアイテム販売価格を取得 */
    private fun getCost(itemStack: ItemStack?): Int {
        if (!isBinSniperActive()) return -1
        if (!isGuiChestOpened()) return -1
        if (!isContainerOpened()) return -1
        if (itemStack == null) return -1

        if (!configUtil.getConfig("Name").toString().equals("None", true)) {
            // Noneじゃないのにアイテム名が一致しないなら
            val targetItemName = EnumChatFormatting.getTextWithoutFormattingCodes(
                itemStack.displayName.toString()
            )
            if (!targetItemName.equals(configUtil.getConfig("Name").toString(), true)) return -1
        }

        // メイン処理
        var price: Int = -1
        var lore: String = ""
        val loreList: NBTTagList = itemStack.tagCompound.getCompoundTag("display").getTagList("Lore", 8)
        for (i in 0 until loreList.tagCount()) {
            lore = loreList.getStringTagAt(i)
            if (lore.startsWith("§7Status: §aSold!")) break

            if (lore.startsWith("§7Buy it now: ")) {
                // コインを取得
                lore = lore.replace(
                    "§7Buy it now: §6", ""
                ).replace(
                    " coins", ""
                ).replace(
                    ",", ""
                )

                if (!NumberUtils.isDigits(lore)) break
                price = lore.toIntOrNull() ?: break
                price /= itemStack.stackSize
            }
        }
        return price
    }

    /** Step: 0 */
    private fun mainLogic() {
        if (!isAuctionBrowserOpened()) return
        val openContainer: ContainerChest = mc.thePlayer.openContainer as ContainerChest

        // AH の 4x6 スロットを繰り返す
        for (i in 0 until 24) {
            val row = i / 6      // 0～3
            val col = i % 6      // 0～5

            // 元コードの clickTarget1 = 11 に相当し、行ごとに +9、列ごとに +1 していた部分
            val slotIndex = 11 + row * 9 + col
            val itemStack = openContainer.getSlot(slotIndex).stack

            if (itemStack != null && itemStack.hasTagCompound()) {
                val cost = getCost(itemStack)
                if (cost != -1) {
                    sessionCheckCount++
                }

                // antiantimacro
                if (configUtil.getBooleanConfig("antiantimacro")) {
                    if (Item.getIdFromItem(itemStack.item) == 166) {
                        ChatLib.command("limbo")
                        stopSnipe()
                    }
                }

                // コスト・UUID・lastseller のチェック
                if (
                    cost <= configUtil.getConfig("Cost").toString().toInt() &&
                    cost != -1 &&
                    !(lastSeller == getSeller(itemStack) && !isUUIDCheckerEnable())
                ) {
                    // UUIDChecker が有効ならUUIDベースで重複購入を防ぐ
                    if (isUUIDCheckerEnable()) {
                        val extraAttr = ItemUtil.getExtraAttributes(itemStack)
                        if (extraAttr != null) {
                            val itemUUID = extraAttr.getString("uuid")
                            if (isBuyedUUID(itemUUID)) {
                                ChatLib.chat("§6[Debug]: itemUUID: $itemUUID")
                                ChatLib.chat("§c購入のスキップ.. (購入済みのUUID)")
                            }
                            else {
                                // 購入開始処理
                                lastSeller = getSeller(itemStack)
                                registerUUID(itemUUID)
                                clickSlot(slotIndex, 0)
                                lastTime = System.currentTimeMillis()

                                currentStep = 1
                                return
                            }
                        }
                    }
                }
            }

            // 1行 (6列) 処理ごとに row が増える: 4行目 (row=3) を処理し終えたらページ切り替え
            if (col == 5 && row == 3) {
                changePage(openContainer)
                lastTime = System.currentTimeMillis()
            }
        }
    }

    /** Step: 3 */
    private fun reconnectToAuctionHouse() {
        if (!configUtil.getBooleanConfig("Reconnect")) {
            stopSnipe()
            return
        }
        ChatLib.command("ah")
        lastTime = System.currentTimeMillis()

        openAuctionBrowser()
        return
    }

    /** Step: 4 */
    private fun openAuctionBrowser() {
        if (!isContainerOpened()) return
        clickSlot(11, 0)
        lastTime = System.currentTimeMillis()

        isAuctionBrowser()
        return
    }

    /** Step: 5 */
    private fun isAuctionBrowser() {
        if (isAuctionBrowserOpened()) {
            lastTime = System.currentTimeMillis()
            moveCategory()
            return
        }
        return
    }

    /** Step: 6 */
    private fun moveCategory() {
        val categoryType: Int = configUtil.getConfig("Category") as Int
        clickSlot(
            9 * (categoryType - 1), 0
        )
        lastTime = System.currentTimeMillis()
        mainLogic()

        return
    }

    /** Step: 12345 */
    private fun backToIsland() {
        ChatLib.command("is")
        lastTime = System.currentTimeMillis() + worldMovingCooldown

        reconnectToAuctionHouse()
        return
    }

    @SubscribeEvent
    fun onClientTick(event: TickEvent.ClientTickEvent) {
        if (!isBinSniperActive()) return

        val reconnect = configUtil.getBooleanConfig("Reconnect")
        val timeout: Long = configUtil.getConfig("Timeout") as Long

        try {
            // タイムアウト処理
            if (System.currentTimeMillis() - lastTime > timeout) {
                mc.thePlayer.inventory.openInventory(mc.thePlayer)
                if (!reconnect) {
                    stopSnipe()
                    return
                }
                ChatLib.command("ah")
                lastTime = System.currentTimeMillis()
                sendMessage("§b動作のタイムアウト オークションの復帰を試みます")

                openAuctionBrowser()
                return
            }

            // なぜかメニューを開いていない場合
            if (!isGuiChestOpened()) {
                if (isWorldChanged) {
                    isWorldChanged = false
                    lastTime = System.currentTimeMillis() + worldMovingCooldown // TODO 5000L を設定できるように

                    backToIsland()
                    return
                }
            }

            // チェストを開いていないならパス
            if (!isContainerOpened()) return


        }
        catch (e: Exception) {}
    }

    /** ワールド変更検知 */
    @SubscribeEvent
    fun onWorldChange(event: WorldEvent.Unload) {
        if (mc.isGamePaused) return
        if (!isBinSniperActive()) return
        if (!configUtil.getBooleanConfig("Reconnect")) return

        isWorldChanged = true
    }
}