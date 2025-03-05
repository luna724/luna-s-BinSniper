package luna724.iloveichika.binsniper

import gg.skytils.skytilsmod.utils.ItemUtil
import luna724.iloveichika.binsniper.BinSniper.Companion.ChatLib
import luna724.iloveichika.binsniper.BinSniper.Companion.config
import luna724.iloveichika.binsniper.BinSniper.Companion.configUtil
import luna724.iloveichika.binsniper.BinSniper.Companion.keyBinSniper
import luna724.iloveichika.binsniper.BinSniper.Companion.logger
import luna724.iloveichika.binsniper.BinSniper.Companion.mc
import luna724.iloveichika.binsniper.config.SessionConfig
import luna724.iloveichika.binsniper.logics.debugUtil.autoErrorReportingService
import luna724.iloveichika.binsniper.utils.Analytics
import luna724.iloveichika.binsniper.utils.ItemChecker
import luna724.iloveichika.binsniper.utils.ScoreboardUtil.getPurse
import luna724.iloveichika.binsniper.utils.Util
import net.minecraft.inventory.ContainerChest
import net.minecraft.inventory.Slot
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagList
import net.minecraft.util.EnumChatFormatting
import net.minecraftforge.client.event.ClientChatReceivedEvent
import net.minecraftforge.client.event.GuiScreenEvent
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import org.apache.commons.lang3.math.NumberUtils
import org.lwjgl.input.Keyboard
import java.text.NumberFormat

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

    private var userConfig: SessionConfig = SessionConfig()
    // すでに購入した UUID を管理するセット
    private var sessionUUID: MutableList<String> = mutableListOf()
    private var lastTime: Long = 0L
    private var lastSeller: String = ""
    private var sessionBuyed: Int = 0
    private var sessionStartAt: Int = 0
    private var sessionCheckCount: Int = 0
    private var isWorldChanged: Boolean = false

    // StackOverflowError を回避するため onTick から Logic を呼び出すための変数
    private var runLogic: Boolean = true
    private var currentStep: Int = 0
    private var isError: Boolean = false

    /**
     * セッションを初期化する
     * 開始時に必ず実行する
     */
    private fun resetSession() {
        sessionBuyed = 0
        sessionStartAt = 0
        sessionCheckCount = 0
        currentStep = 0
        lastSeller = ""
        isWorldChanged = false
        lastTime = System.currentTimeMillis()
        sessionUUID.clear()
        reforges = itemChecker.getPlainReforges().toMutableList()

        if (configUtil.getBooleanConfig("BackCompatibility")) {
            reforges.addAll(accessoryReforges)
        }

        userConfig = config.loadPlayer()
    }
    // Checker
    private fun isAuctionBrowserOpened(): Boolean = containerUtil.isAuctionBrowser(mc.thePlayer.openContainer)
    private fun isUUIDCheckerEnable(): Boolean = userConfig.UUIDMode
    private fun isBuyedUUID(targetUUID: String): Boolean = sessionUUID.contains(targetUUID)

    /** */
    fun stopSnipe() {
        val playerId = getPlayerId()
        configUtil.setConfig("Active", false)
        ChatLib.chat("§c動作の停止")
        return
    }

    /** */
    fun startSnipe() {
        configUtil.setConfig("Active", true)
        ChatLib.chat("§a動作の開始")
        resetSession()
        return
    }

    /** 設定に基づいてメッセージを送る */
    private fun sendMessage(msg: String) {
        if (userConfig.Message) {
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
        if (!userConfig.Active) return "!None"
        if (!isGuiChestOpened()) return "!None"
        if (!isContainerOpened()) return "!None"
        if (itemStack == null) return "!None"

        var lore = "!None"
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
        if (!userConfig.Active) return -1
        if (!isGuiChestOpened()) return -1
        if (!isContainerOpened()) return -1
        if (itemStack == null) return -1

        if (!userConfig.Name.equals("None", ignoreCase = true)) {
            // Noneじゃないのにアイテム名が一致しないなら
            val targetItemName = EnumChatFormatting.getTextWithoutFormattingCodes(
                itemStack.displayName.toString()
            )
            if (!targetItemName.equals(userConfig.Name, ignoreCase = true)) return -1
        }

        // メイン処理
        var price: Int = -1
        var lore = ""
        val loreList = getLoreFromItemStack(itemStack)
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

    /** 実質メイン処理 ページをスワップし続けて検索、購入開始処理を行う */
    private fun changeAuctionPage(containerChest: ContainerChest) {
        if (!userConfig.Active) return
        if (!isGuiChestOpened()) return
        if (!isContainerOpened()) return

        val snipeMode = userConfig.Mode
        val nextPageSlot: Slot = containerChest.getSlot(53)
        if (!nextPageSlot.hasStack) return
        val itemStack: ItemStack = nextPageSlot.stack ?: return

        if (!containerUtil.isBinOnly(containerChest)) {
            // BIN Only に戻す
            clickSlot(52, 0)
            sendMessage("§b検索条件を BIN Only に変更しています..")
            return
        }

        if (!containerUtil.isLowest(containerChest)) {
            // Lowest Price に戻す
            clickSlot(50, 0)
            sendMessage("§b最低金額から検索できるように変更しています..")
            return
        }

        sendMessage("検索中.. (${sessionCheckCount}回目のチェック) (購入確定: ${sessionBuyed})")

        if (snipeMode.equals("ALLMODE", ignoreCase = true)) {
            // ALLMODE: 多分更新しない
            if (Item.getIdFromItem(itemStack.item) != 262) {
                val itemStackSlot46 = containerChest.getSlot(46).stack
                if (Item.getIdFromItem(itemStackSlot46.item) != 262) {
                    if (containerUtil.isNoFilter(containerChest)) {
                        clickSlot(51, 1)
                    } else {
                        clickSlot(51, 0)
                    }
                    return
                }
                clickSlot(46, 1)
                return
            }
            clickSlot(53, 0)
            return
        }
        else if (snipeMode.equals("FASTMODE", true)) {
            if (containerUtil.isNoFilter(containerChest)) {
                clickSlot(51, 1)
            }
            else {
                clickSlot(51, 0)
            }
        }
    }

    /** Step: 0 */
    private fun mainLogic() {
        if (!isAuctionBrowserOpened()) return
        val openContainer: ContainerChest = getContainerChest()

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
                if (userConfig.AntiAntiMacro) {
                    if (Item.getIdFromItem(itemStack.item) == 166) {
                        ChatLib.command("limbo")
                        stopSnipe()
                        return
                    }
                }

                // コスト・UUID・lastseller のチェック
                if (
                    cost <= userConfig.Cost &&
                    cost != -1 &&
                    !(lastSeller == getSeller(itemStack) && !isUUIDCheckerEnable())
                ) {
                    // UUIDChecker が有効ならUUIDベースで重複購入を防ぐ
                    if (isUUIDCheckerEnable()) {
                        val extraAttr = ItemUtil.getExtraAttributes(itemStack)
                        if (extraAttr != null) {
                            val itemUUID = extraAttr.getString("uuid")
                            if (isBuyedUUID(itemUUID)) {
                                // UUIDの重複
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
                changeAuctionPage(openContainer)
                lastTime = System.currentTimeMillis()
            }
        }
    }

    /** Step: 1 */
    private fun tryToBuyItem() {
        val openContainer: ContainerChest = getContainerChest()
        val item: Item = openContainer.getSlot(31)?.stack?.item ?: return
        val itemId: Int = Item.getIdFromItem(item)

        // 金塊の場合、さっさと買う
        if (itemId == 371) {
            clickSlot(31, 0)
            lastTime = System.currentTimeMillis()

            currentStep = 2
            return
        }

        // ベッドの場合
        else if (itemId == 355) {
            if (userConfig.SleepOptimization) {
                // TODO
            }
            else { // Optimization がオフなら戻って購入しようとした履歴を消す
                clickSlot(49, 0)
                lastSeller = ""
                sessionUUID.removeAt(sessionUUID.size - 1)
                lastTime = System.currentTimeMillis()
                sendMessage("§c購入のキャンセル (アイテムがまだ購入可能でありません)")

                currentStep = 0
                return
            }
        }
    }

    /** Step: 2 */
    private fun purchaseItem() {
        val openContainer: ContainerChest = getContainerChest()
        val slot: Slot? = openContainer.getSlot(11)
        if (
            slot == null ||
            slot.stack == null ||
            slot.stack.item == null ||
            Item.getIdFromItem(
                slot.stack.item
            ) != 159
        ) { // 購入のキャンセル
            mc.thePlayer.inventory.openInventory(mc.thePlayer)
            lastTime = System.currentTimeMillis()
            sendMessage("§c購入のキャンセル 再検索を開始します..")

            currentStep = 3
            return
        }

        // 購入処理
        val purchaseAmount: Int = 0 // TODO
        clickSlot(11, 0)
        sendMessage("§a§lスナイプを実行しました")
        sendMessage("§7- チェック回数: $sessionCheckCount")

        if (!userConfig.Reconnect) {
            stopSnipe()
            return
        }

        currentStep = 100
        sessionBuyed += 1
        sessionCheckCount = 0
        lastTime = System.currentTimeMillis()
        return
    }

    /** Step: 3 */
    private fun reconnectToAuctionHouse() {
        if (!userConfig.Reconnect) {
            stopSnipe()
            return
        }
        ChatLib.command("ah")
        lastTime = System.currentTimeMillis()

        currentStep = 4
        return
    }

    /** Step: 4 */
    private fun openAuctionBrowser() {
        if (!isContainerOpened()) return
        clickSlot(11, 0)
        lastTime = System.currentTimeMillis()

        currentStep = 5
        return
    }

    /** Step: 5 */
    private fun isAuctionBrowser() {
        if (isAuctionBrowserOpened()) {
            lastTime = System.currentTimeMillis()
            currentStep = 6
            return
        }
        return
    }

    /** Step: 6 */
    private fun moveCategory() {
        clickSlot(
            9 * (userConfig.Category - 1), 0
        )
        lastTime = System.currentTimeMillis()
        currentStep = 0

        return
    }

    /** Step: 100 */
    private fun confirmPurchase() {
        currentStep = 3
        isError = false
        lastTime = System.currentTimeMillis()
        if (isError) {
            sessionBuyed -= 1
            sendMessage("§c落札したアイテムにエラーが発生 カウントを取り消しました")
            return
        }
        else {
            if (!userConfig.Reconnect) {
                stopSnipe()
            }
            return
        }
    }

    /** Step: 12345 */
    private fun backToIsland() {
        ChatLib.command("is")
        lastTime = System.currentTimeMillis() + worldMovingCooldown

        currentStep = 3
        return
    }

    @SubscribeEvent
    fun onClientTick(event: TickEvent.ClientTickEvent) {
        if (!userConfig.Active) return

        try {
            // タイムアウト処理
            if (System.currentTimeMillis() - lastTime > userConfig.Timeout) {
                mc.thePlayer.inventory.openInventory(mc.thePlayer)
                if (!userConfig.Reconnect) {
                    stopSnipe()
                    return
                }
                ChatLib.command("ah")
                lastTime = System.currentTimeMillis()
                sendMessage("§b動作のタイムアウト オークションの復帰を試みます")

                // ここではdelay待機を行わない
                openAuctionBrowser()
                return
            }

            // なぜかメニューを開いていない場合
            if (!isGuiChestOpened()) {
                if (isWorldChanged) {
                    isWorldChanged = false
                    lastTime = System.currentTimeMillis() + worldMovingCooldown // TODO 5000L を設定できるように

                    currentStep = 12345
                    return
                }
            }

            // チェストを開いていないならパス
            if (!isContainerOpened()) return

            // TODO
            mainLogic()
        }
        catch (e: Exception) {
            e.printStackTrace()
            logger.error(e)
        }
    }

    /** ワールド変更検知 */
    @SubscribeEvent
    fun onWorldChange(event: WorldEvent.Unload) {
        if (mc.isGamePaused) return
        if (!userConfig.Active) return
        if (!userConfig.Reconnect) return

        isWorldChanged = true
    }

    /** 買った際にエラーが起きていないかを確認
     * また、購入通知を送信する
     */
    @SubscribeEvent
    fun onChat(event: ClientChatReceivedEvent) {
        val chat = EnumChatFormatting.getTextWithoutFormattingCodes(event.message.unformattedText)

        if (chat.startsWith("You purchased")) {
            Thread({
                try {
                    Thread.sleep(2000)
                }
                catch (e: InterruptedException) {
                    println("Exception in sending webhook onPurchased: ")
                    e.printStackTrace()
                }
                var content: String
                val username = mc.session.username
                try {
                    val itemForPrice: String = chat.replace("You purchased", "");
                    val onlyPrices: Array<String?> =
                        itemForPrice.split(" for ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    val onlyPrice = onlyPrices[onlyPrices.size - 1]!!.replace(" coins!", "")
                    val onlyItem = itemForPrice.replace(" for $onlyPrice coins!", "")
                    val remainPurse: Int = getPurse()
                    val numberInstance = NumberFormat.getNumberInstance()

                    content =
                        "``` $username: Purchased \"" + onlyItem.trim { it <= ' ' } + "\" for " + onlyPrice + " coins!\n- (Purse Remaining: " + formatCoinToString(
                            remainPurse.toDouble()
                        ) + ")```"
                }
                catch (e: Exception) {
                    e.printStackTrace();
                    content = "**Error in Parsing text.**\n```$e```\n```rawText: $chat```";
                }
                /*
                    ```username Purchased item for price coins!```
                     */
                val jsonObj: String = Analytics.setJsonObj(content, username, null)
                Analytics.requestWeb(jsonObj, WebHookUrls.purchasedItemNotification)
            }).start()
        }
        else if (chat.contains("There was an error with the auction house!")) {
            isError = true
        }
        return
    }

    /** 開始処理 */
    @SubscribeEvent
    fun onKey(event: GuiScreenEvent.KeyboardInputEvent) {
        if (!isGuiChestOpened()) return
        if (mc.thePlayer.openContainer == null) return
        if (Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.keyCode) ||
            Keyboard.isKeyDown(1) ) {
            if (userConfig.Active) {
                stopSnipe()
                return
            }
        }
        else if (Keyboard.isKeyDown(keyBinSniper.keyCode)) {
            if (keyBinSniper.keyCode == 0) return
            if (userConfig.Active) {
                stopSnipe()
                return
            }
            // スタート処理
            val inventoryContainer: ContainerChest = getContainerChest()
            if (containerUtil.isAnvil(inventoryContainer)) {
                ChatLib.chat("§a合成モードの開始")
                currentStep = -10
                startSnipe()
                return
            }
            else if (containerUtil.isPending(inventoryContainer)) {
                ChatLib.chat("§a回収モードの開始")
                currentStep = -1
                startSnipe()
                return
            }
            else {
                val numberInstance: NumberFormat = NumberFormat.getNumberInstance()
                Util.sendAir()
                Util.send("§aスナイプの開始")
                Util.sendAir()
                Util.send("§7現在の設定一覧:")
                Util.send("§7- Coin: §6" + numberInstance.format(configUtil.getConfig("Cost")) + "/コイン")
                Util.send("§7- Category: §6" + configUtil.getConfig("Category") + "/番")
                Util.send("§7- Amount: §6∞/個")
                Util.send("§7- Name: §6" + configUtil.getConfig("Name"))
                Util.send("§7- Mode: §6" + configUtil.getConfig("Mode"))
                Util.sendAir()

                startSnipe()
                Thread({
                    try {
                        val username = mc.session.username.toString()
                        val content = ("```${username} has Started sniping! (" +
                                numberInstance.format(userConfig.Cost) +
                                " coins)\n(Delay: " + userConfig.Delay + ") | (Start Purse: " + formatCoinToString(
                                getPurse().toDouble()) + ")```");
                        Analytics.requestWeb(
                            Analytics.setJsonObj(content, null, null), WebHookUrls.purchasedItemNotification
                        )
                    }
                    catch (e: Exception) {
                        e.printStackTrace()
                        autoErrorReportingService(e)
                    }
                }).start()
            }

        }
    }
}