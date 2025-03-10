package luna724.iloveichika.binsniper

import gg.skytils.skytilsmod.utils.ItemUtil
import luna724.iloveichika.binsniper.BinSniper.Companion.ChatLib
import luna724.iloveichika.binsniper.BinSniper.Companion.config
import luna724.iloveichika.binsniper.BinSniper.Companion.keyBinSniper
import luna724.iloveichika.binsniper.BinSniper.Companion.logger
import luna724.iloveichika.binsniper.BinSniper.Companion.mc
import luna724.iloveichika.binsniper.config.SessionConfig
import luna724.iloveichika.binsniper.utils.debugUtil.autoErrorReportingService
import luna724.iloveichika.binsniper.utils.Analytics
import luna724.iloveichika.binsniper.utils.ItemChecker
import luna724.iloveichika.binsniper.utils.ScoreboardUtil.getPurse
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
        private var currentStep: Int = 0
        private var isError: Boolean = false
    }


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

        if (userConfig.BackCompatibility) {
            reforges.addAll(accessoryReforges)
        }

        userConfig = config.loadPlayer()
    }
    // Checker
    private fun isAuctionBrowserOpened(): Boolean = containerUtil.isAuctionBrowser(mc.thePlayer.openContainer)
    private fun isUUIDCheckerEnable(): Boolean = userConfig.UUIDMode
    private fun isBuyedUUID(targetUUID: String): Boolean = sessionUUID.contains(targetUUID)

    /** */
    fun stopSnipe(reason: String = "不明") {
        userConfig.Active = false
        config.savePlayer(userConfig)
        ChatLib.chat("§c動作の停止 (${reason})")
        return
    }

    /** */
    fun startSnipe(reason: String = "") {
        resetSession()
        // コストのチェック
        if (userConfig.Cost == -1) {
            ChatLib.chat("§c金額を /binsniper coin 10000 などで設定して下さい")
            stopSnipe("コストが -1")
            return
        }

        userConfig.Active = true
        config.savePlayer(userConfig)
        ChatLib.chat("§a動作の開始 (${reason})")
        return
    }

    /** 設定に基づいてメッセージを送る */
    private fun sendMessage(msg: String) {
        if (userConfig.Message) {
            ChatLib.chat(msg)
        }
    }
    private fun sendDebug(msg: String, currentStep: Int? = null) {
        if (userConfig.Debug) {
            ChatLib.chat("§f[§6Debug§f]: §r$msg ${if (currentStep != null) "§6(Step: ${currentStep})" else ""}")
        }
        println("$msg (step: $currentStep)")
    }

    /** 受け取った値をそのまま返すだけ */
    private fun swapStep(new: Int): Int {
        val old = currentStep

        sendDebug("§7ステップの変更.. (§6$old §r-> §6$new§r)")
        return new
    }

    /** 指定されたスロットをクリックする */
    private fun clickSlot(windowSlot1: Int, windowSlot2: Int) {
        val player = mc.thePlayer
        mc.playerController.windowClick(
            player.openContainer.windowId, windowSlot1, windowSlot2, 3, player
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

        // sendMessage("検索中.. (${sessionCheckCount}回目のチェック) (購入確定: ${sessionBuyed})")

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

    /**  */
    fun showCurrentSettings(userConfig: SessionConfig) {
        ChatLib.chat(" ")
        ChatLib.chat("§7現在の設定一覧:")
        ChatLib.chat("§7- Coin: §6 ${formatCoinToString(userConfig.Cost.toDouble())} コイン")
        ChatLib.chat("§7- Category: §6 ${userConfig.Category} 番")
        ChatLib.chat("§7- Amount: §6 ${userConfig.Amount} 個")
        ChatLib.chat("§7- Name: §6 ${userConfig.Name}")
        ChatLib.chat("§7- Mode: §6 ${userConfig.Mode}")
        ChatLib.chat(" ")
    }

    /** Step: 0 */
    private fun mainLogic() {
        if (!isAuctionBrowserOpened()) {
            sendDebug("mainLogic: isAuctionBrowserOpenedがFalseです")
            return
        }
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
                        sendDebug("mainLogic: ItemID 166 が販売されています 購入対象がバリアブロックの場合は AntiAntiMacro を無効化してください")
                        ChatLib.command("limbo")
                        stopSnipe("AntiAntiMacro バリアブロックチェック")
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
                                sendDebug("§6mainLogic: itemUUIDの重複 ($itemUUID)")
                                ChatLib.chat("§c購入のスキップ.. (購入済みのUUID)")
                            }
                            else {
                                // 購入開始処理
                                lastSeller = getSeller(itemStack)
                                registerUUID(itemUUID)
                                clickSlot(slotIndex, 0)
                                lastTime = System.currentTimeMillis()

                                sendDebug("mainLogic: 購入対象のアイテムが見つかりました (seller: $lastSeller | UUID: $itemUUID)")
                                currentStep = swapStep(1)
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
            sendDebug("tryToBuyItem: アイテムの購入を決定しています..")

            currentStep = swapStep(2)
            return
        }

        // ベッドの場合
        else if (itemId == 355) {
            if (userConfig.SleepOptimization) {
                sendDebug("tryToBuyItem: SleepOptimizationは現在実装されていません この機能を無効化してください")
                // TODO()
            }
            else { // Optimization がオフなら戻って購入しようとした履歴を消す
                clickSlot(49, 0)
                lastSeller = ""
                sessionUUID.removeAt(sessionUUID.size - 1)
                lastTime = System.currentTimeMillis()
                sendMessage("§c購入のキャンセル (アイテムがまだ購入可能でありません)")
                sendDebug("tryToBuyItem: SleepOptimizationがオフです アイテムを再度処理します")

                currentStep = swapStep(0)
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
            sendDebug("purchaseItem: スロット12の値が期待した値と違います (itemId: ${slot?.stack?.item?.registryName ?: "不明"})")

            currentStep = swapStep(3)
            return
        }

        // 購入処理
        sendDebug("purchaseItem: 未実装のため購入数のチェックはスキップされました")
        val purchaseAmount: Int = 0 // TODO
        clickSlot(11, 0)
        sendMessage("§a§lスナイプを実行しました")
        sendMessage("§7- チェック回数: $sessionCheckCount")

        if (!userConfig.Reconnect) {
            stopSnipe("再接続設定がオフ")
            sendDebug("purchaseItem: Reconnectがオフです")
            return
        }

        currentStep = 100
        sessionBuyed += 1
        sessionCheckCount = 0
        sendDebug("purchaseItem: sessionBuyed: -> $sessionBuyed")
        sendDebug("purchaseItem: sessionCheckCount: -> $sessionCheckCount")
        lastTime = System.currentTimeMillis()
        return
    }

    /** Step: 3 */
    private fun reconnectToAuctionHouse() {
        if (!userConfig.Reconnect) {
            stopSnipe("再接続設定がオフ")
            sendDebug("reconnectToAuctionHouse: Reconnectがオフです")
            return
        }
        ChatLib.command("ah")
        lastTime = System.currentTimeMillis()

        currentStep = swapStep(4)
        return
    }

    /** Step: 4 */
    private fun openAuctionBrowser() {
        if (!isContainerOpened()) {
            sendDebug("openAuctionBrowser: isContainerOpened()がFalseです")
            return
        }
        clickSlot(11, 0)
        lastTime = System.currentTimeMillis()

        currentStep = swapStep(5)
        return
    }

    /** Step: 5 */
    private fun isAuctionBrowser() {
        if (isAuctionBrowserOpened()) {
            lastTime = System.currentTimeMillis()
            currentStep = swapStep(6)
            return
        } else {
            sendDebug("isAuctionBrowser: isAuctionBrowserOpened()がFalseです")
            return
        }
    }

    /** Step: 6 */
    private fun moveCategory() {
        clickSlot(
            9 * (userConfig.Category - 1), 0
        )
        lastTime = System.currentTimeMillis()
        currentStep = swapStep(0)

        return
    }

    /** Step: 100 */
    private fun confirmPurchase() {
        currentStep = swapStep(3)
        isError = false
        lastTime = System.currentTimeMillis()
        if (isError) {
            sessionBuyed -= 1
            sendMessage("§c落札したアイテムにエラーが発生 カウントを取り消しました")
            return
        }
        else {
            if (!userConfig.Reconnect) {
                sendDebug("confirmPurchase: アイテムの購入に成功しました Reconnectがオフのため終了します")
                stopSnipe("再接続がオフ")
            }
            return
        }
    }

    /** Step: 12345 */
    private fun backToIsland() {
        ChatLib.command("is")
        sendDebug("backToIsland: 自島に戻りました 5秒後に動作を再開します")
        lastTime = System.currentTimeMillis() + worldMovingCooldown

        currentStep = swapStep(3)
        return
    }

    @SubscribeEvent
    fun onClientTick(event: TickEvent.ClientTickEvent) {
        if (!userConfig.Active) return

        try {
            // タイムアウト処理
            if (System.currentTimeMillis() - lastTime > userConfig.Timeout) {
                sendDebug("タイムアウト: ${System.currentTimeMillis() - lastTime}ms")
                mc.thePlayer.inventory.openInventory(mc.thePlayer)
                if (!userConfig.Reconnect) {
                    stopSnipe("動作のタイムアウト Reconnectがオフ")
                    return
                }
                ChatLib.command("ah")
                lastTime = System.currentTimeMillis()
                sendMessage("§b動作のタイムアウト オークションの復帰を試みます")

                currentStep = swapStep(4)
                return
            }
            // Delay を上回っていないならパス
            if (System.currentTimeMillis() - lastTime < userConfig.Delay) return

            if (currentStep == 100) {
                // アイテム購入完了処理
                sendDebug("confirmPurchase()を呼び出し中", currentStep)
                confirmPurchase()
                return
            }

            // なぜかメニューを開いていない場合
            if (!isGuiChestOpened()) {
                if (isWorldChanged) {
                    sendDebug("ワールド移動が検出されました", currentStep)
                    isWorldChanged = false
                    lastTime = System.currentTimeMillis() + worldMovingCooldown // TODO 5000L を設定できるように

                    currentStep = swapStep(12345)
                    return
                }
                if (currentStep == 12345) {
                    // is に戻る
                    sendDebug("backToIsland()を呼び出し中", currentStep)
                    lastTime = System.currentTimeMillis() + worldMovingCooldown

                    backToIsland()
                    return
                }
                if (currentStep == 3) {
                    // ah を開く
                    sendDebug("reconnectToAuctionHouse()を呼び出し中", currentStep)
                    lastTime = System.currentTimeMillis()

                    reconnectToAuctionHouse()
                    return
                }
            }
            // チェストを開いていないならパス
            if (!isContainerOpened()) {
                sendDebug("ContainerOpenedのチェックに失敗しました ステップ0, 1, 2, 4, 5, 6は処理されません", currentStep)
                return
            }

            if (currentStep == 1) {
                sendDebug("tryToBuyItem()を呼び出し中", currentStep)
                tryToBuyItem()
            }
            else if (currentStep == 2) {
                sendDebug("purchaseItem()を呼び出し中", currentStep)
                purchaseItem()
            }
            else if (currentStep == 4) {
                sendDebug("openAuctionBrowser()を呼び出し中", currentStep)
                openAuctionBrowser()
            }
            else if (currentStep == 5) {
                sendDebug("isAuctionBrowser()を呼び出し中", currentStep)
                isAuctionBrowser()
            }
            else if (currentStep == 6) {
                sendDebug("moveCategory()を呼び出し中", currentStep)
                moveCategory()
            }
            else if (currentStep == 0) {
                sendDebug("mainLogic()を呼び出し中", currentStep)
                mainLogic()
            }
        }
        catch (e: Exception) {
            e.printStackTrace()
            sendDebug("§c実行中にエラーが発生しました。($e)", currentStep)
            ChatLib.chat("§c実行中にエラーが発生しました。 詳しくはエラーログを確認してください (${e})")
            logger.error(e)
            return
        }
    }

    /** ワールド変更検知 */
    @SubscribeEvent
    fun onWorldChange(event: WorldEvent.Unload) {
        if (mc.isGamePaused) return
        if (!userConfig.Active) return
        if (!userConfig.Reconnect) return

        sendDebug("onWorldChange: BinSniperがオンの際にワールドの変更が検知されました")
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
                    logger.error(e)
                }
                var content: String
                val username = mc.session.username
                try { // You purchased God Potion for 2,250,000 coins!  < 例
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
        else if (chat.contains("There was an error with the auction house!") && userConfig.Active) {
            sendDebug("アイテム購入時のエラーを検知しました 購入カウントを撤回します")
            isError = true
        }
        return
    }

    /** 開始処理 */
    @SubscribeEvent
    fun onKey(event: GuiScreenEvent.KeyboardInputEvent) {
        if (!isGuiChestOpened()) return
        if (mc.thePlayer.openContainer == null) return
        resetSession()
        if (Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.keyCode) ||
            Keyboard.isKeyDown(1) ) {
            if (userConfig.Active) {
                stopSnipe("動作中にシフトまたはESCが押された")
                return
            }
        }
        else if (Keyboard.isKeyDown(keyBinSniper.keyCode)) {
            if (keyBinSniper.keyCode == 0) return
            if (userConfig.Active) {
                stopSnipe("動作中にキーが押された")
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
                currentStep = 0
                showCurrentSettings(userConfig)

                Thread({
                    try {
                        Thread.sleep(50)
                        startSnipe()
                        val username = mc.session.username.toString()
                        val content = ("```${username} has Started sniping ${userConfig.Name}! (${formatCoinToString(userConfig.Cost.toDouble())} coins)\n" +
                                "(Delay: " + userConfig.Delay + ") | (Start Purse: ${formatCoinToString(getPurse().toDouble())})```");
                        Analytics.requestWeb(
                            Analytics.setJsonObj(content, null, null), WebHookUrls.purchasedItemNotification
                        )
                    }
                    catch (e: Exception) {
                        logger.error(e)
                        autoErrorReportingService(e)
                    }
                }).start()
            }
        }
    }
}