///*
// * Decompiled with CFR 0.151.
// *
// * Could not load the following classes:
// *  net.minecraft.client.gui.GuiMainMenu
// *  net.minecraft.client.gui.inventory.GuiChest
// *  net.minecraft.entity.player.EntityPlayer
// *  net.minecraft.inventory.ContainerChest
// *  net.minecraft.inventory.Slot
// *  net.minecraft.item.Item
// *  net.minecraft.item.ItemStack
// *  net.minecraft.nbt.NBTTagList
// *  net.minecraft.util.EnumChatFormatting
// *  net.minecraftforge.client.event.ClientChatReceivedEvent
// *  net.minecraftforge.client.event.GuiScreenEvent
// *  net.minecraftforge.client.event.GuiScreenEvent$KeyboardInputEvent
// *  net.minecraftforge.event.world.WorldEvent$Unload
// *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
// *  net.minecraftforge.fml.common.gameevent.TickEvent$ClientTickEvent
// *  org.lwjgl.input.Keyboard
// */
//package luna724.iloveichika.binsniper.logics;
//
//import java.text.NumberFormat;
//import java.util.*;
//
//import net.minecraft.client.Minecraft;
//import net.minecraft.client.entity.EntityPlayerSP;
//import net.minecraft.client.gui.GuiMainMenu;
//import net.minecraft.client.gui.inventory.GuiChest;
//import net.minecraft.entity.player.EntityPlayer;
//import net.minecraft.inventory.ContainerChest;
//import net.minecraft.inventory.Slot;
//import net.minecraft.item.Item;
//import net.minecraft.item.ItemStack;
//import net.minecraft.nbt.NBTTagCompound;
//import net.minecraft.nbt.NBTTagList;
//import net.minecraft.util.EnumChatFormatting;
//import net.minecraftforge.client.event.ClientChatReceivedEvent;
//import net.minecraftforge.client.event.GuiScreenEvent;
//import net.minecraftforge.event.world.WorldEvent;
//import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
//import net.minecraftforge.fml.common.gameevent.TickEvent;
//
//public class BinSnipeLogic {
//    private /* synthetic */ int currentStep;
//    private /* synthetic */ int slotB;
//
//    public BinSnipeLogic() {
//        this.currentStep = 0;
//    }
//
//    @SubscribeEvent
//    public void onClientTick(TickEvent.ClientTickEvent clientTickEvent) {
//        String playerId = Wrapper.mc.getSession().getProfile().getId().toString();
//
//        try {
//            if (this.currentStep == -1) {
//                Wrapper.mc.thePlayer.sendChatMessage("/ah");
//                this.currentStep = -2;
//                this.timer = System.currentTimeMillis();
//                return;
//            }
//            if (this.currentStep == -2) {
//                if (!(Wrapper.mc.thePlayer.openContainer instanceof ContainerChest)) {
//                    this.stopSnipe();
//                    return;
//                }
//                ContainerChest containerChest = (ContainerChest) Wrapper.mc.thePlayer.openContainer;
//                if (this.isPending(containerChest)) {
//                    this.clickSlot(13, 0);
//                    this.currentStep = -3;
//                    this.timer = System.currentTimeMillis();
//                    return;
//                }
//                this.stopSnipe();
//                return;
//            }
//            if (this.currentStep == -3) {
//                if (!(Wrapper.mc.thePlayer.openContainer instanceof ContainerChest)) {
//                    this.stopSnipe();
//                    return;
//                }
//                ContainerChest openContainer = (ContainerChest) Wrapper.mc.thePlayer.openContainer;
//                int i = 0;
//                while (i < 54) {
//                    ItemStack itemStack = openContainer.getSlot(i).getStack();
//                    if (!(Item.getIdFromItem(itemStack.getItem()) == 160) // ガラス板以外
//                            && !(Item.getIdFromItem(itemStack.getItem()) == 262) // 矢 以外
//                    ) {
//                        this.clickSlot(i, 0); // ガラス板、矢以外をクリック
//                        this.currentStep = -4;
//                        this.timer = System.currentTimeMillis();
//                        return;
//                    }
//                    ++i;
//                }
//                this.stopSnipe();
//                return;
//            }
//            if (this.currentStep == -4) {
//                this.clickSlot(31, 0);
//                this.currentStep = -1;
//                this.timer = System.currentTimeMillis();
//                return;
//            }
//            if (this.currentStep == -10) {
//                // エンチャ本の時の処理
//                int i = 0;
//                while (i <= 35) {
//                    ItemStack itemStack = Wrapper.mc.thePlayer.inventory.getStackInSlot(i);
//                    if (itemStack != null && itemStack.getDisplayName().contains("Enchanted Book") && itemStack.hasDisplayName() && Item.getIdFromItem((Item) itemStack.getItem()) == 403) {
//                        NBTTagList LoreList = itemStack.getTagCompound().getCompoundTag("display").getTagList("Lore", 8);
//                        String lore = EnumChatFormatting.getTextWithoutFormattingCodes((String) LoreList.getStringTagAt(0));
//                        int i2 = 0;
//                        while (i2 <= 35) {
//                            if (i2 != i) {
//                                ItemStack itemStack2 = Wrapper.mc.thePlayer.inventory.getStackInSlot(i2);
//                                if (itemStack2 != null && itemStack2.getDisplayName().contains("Enchanted Book") && itemStack2.hasDisplayName() && Item.getIdFromItem((Item) itemStack2.getItem()) == 403) {
//                                    NBTTagList LoreList2 = itemStack2.getTagCompound().getCompoundTag("display").getTagList("Lore", 8);
//                                    String lore2 = EnumChatFormatting.getTextWithoutFormattingCodes((String) LoreList2.getStringTagAt(0));
//                                    if (lore.equalsIgnoreCase(lore2)) {
//                                        this.slotB = i2;
//                                        this.currentStep = -11;
//                                        if (i >= 9) {
//                                            this.clickSlot(i + 45, 0);
//                                        }
//                                        if (i < 9) {
//                                            this.clickSlot(i + 81, 0);
//                                        }
//                                        this.timer = System.currentTimeMillis();
//                                        return;
//                                    }
//                                }
//                            }
//                            ++i2;
//                        }
//                    }
//                    ++i;
//                }
//                this.stopSnipe();
//                return;
//            }
//            if (this.currentStep == -11) {
//                this.currentStep = -12;
//                this.clickSlot(29, 0);
//                this.timer = System.currentTimeMillis();
//                return;
//            }
//            if (this.currentStep == -12) {
//                this.currentStep = -13;
//                if (this.slotB >= 9) {
//                    this.clickSlot(this.slotB + 45, 0);
//                }
//                if (this.slotB < 9) {
//                    this.clickSlot(this.slotB + 81, 0);
//                }
//                this.timer = System.currentTimeMillis();
//                return;
//            }
//            if (this.currentStep == -13) {
//                this.currentStep = -14;
//                this.clickSlot(33, 0);
//                this.timer = System.currentTimeMillis();
//                return;
//            }
//            if (this.currentStep == -14) {
//                this.currentStep = -15;
//                this.clickSlot(22, 0);
//                this.timer = System.currentTimeMillis();
//                return;
//            }
//            if (this.currentStep == -15) {
//                this.currentStep = -16;
//                this.clickSlot(22, 0);
//                this.timer = System.currentTimeMillis();
//                return;
//            }
//            if (this.currentStep == -16) {
//                this.currentStep = -10;
//                this.timer = System.currentTimeMillis();
//                return;
//            }
//            if (Util.config().getInt(playerId + ".Cost") == -1) {
//                Util.config().set(playerId + ".Active", false);
//                Util.save();
//                Util.send("§c金額を /binsniper coin 10000 などで設定して下さい");
//                this.stopSnipe();
//                return;
//            }
//        }
//        catch (NullPointerException NPE) {
//            NPE.printStackTrace();
////            autoErrorReportingService(NPE.toString());
//            Util.send("§c実行中にNPEが発生しました。");
//
//            if (getConfigValueBoolean("onTickNPECatcher")) { stopSnipe(); }
//
//            autoErrorReportingService(NPE);
//            sendMessageToDiscord(Wrapper.mc.getSession().getUsername() + ": NPEによるスナイプの停止", WebHookUrls.purchasedItemNotification);
//            return;
//        }
//        catch (Exception e) {
//            e.printStackTrace();
//            autoErrorReportingService(e);
//            Util.send("§c実行中にエラーが発生しました。 詳しくはエラーログを確認してください");
//            stopSnipe();
//            return;
//        }
//    }
//}