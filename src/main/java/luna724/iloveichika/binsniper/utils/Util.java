/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.util.ChatComponentText
 *  net.minecraft.util.EnumChatFormatting
 *  net.minecraft.util.IChatComponent
 */
package luna724.iloveichika.binsniper.utils;

import java.io.File;
import java.io.IOException;

import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

public class Util {
    private static /* synthetic */ Configuration mainConfig;
    private static /* synthetic */ String oldStr;
    private static /* synthetic */ File file;
    private static /* synthetic */ Configuration config;
    private static /* synthetic */ File mainFile;
    private static /* synthetic */ Minecraft mc;

    public static void init() {
        try {
            String playerUUID = Wrapper.mc.getSession().getProfile().getId().toString();
            if (!mainFile.exists()) {
                mainFile.createNewFile();
            }
            mainConfig = ConfigurationProvider.getProvider(YamlConfiguration.class).load(mainFile);
            Util.saveMain();
            if (!file.exists()) {
                file.createNewFile();
            }
            if (!(config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file)).contains(playerUUID + ".Cost")) {
                config.set(playerUUID + ".Cost", -1);
            }
            if (!(config.contains(playerUUID + ".Mode"))) {
                config.set(playerUUID + ".Mode", "FASTMODE");
            }
            if (!(config.contains(playerUUID + ".Delay"))) {
                config.set(playerUUID + ".Delay", 1000L);
            }
            if (!(config.contains(playerUUID + ".Reconnect"))) {
                config.set(playerUUID + ".Reconnect", true);
            }
            if (!(config.contains(playerUUID + ".Name"))) {
                config.set(playerUUID + ".Name", "None");
            }
            if (!(config.contains(playerUUID + ".Category"))) {
                config.set(playerUUID + ".Category", 0);
            }
            if (!(config.contains(playerUUID + ".Amount"))) {
                config.set(playerUUID + ".Amount", 0);
            }
            if (!(config.contains(playerUUID + ".Timeout"))) {
                config.set(playerUUID + ".Timeout", 10000);
            }
            if (!(config.contains(playerUUID + ".Message"))) {
                config.set(playerUUID + ".Message", true);
            }
            if (!(config.contains(playerUUID + ".uuidMode"))) {
                config.set(playerUUID + ".uuidMode", true);
            }
            if (!(config.contains(playerUUID + ".sleepOptimization"))) {
                config.set(playerUUID + ".sleepOptimization", true);
            }
            if (!(config.contains(playerUUID + ".antiantimacro"))) {
                config.set(playerUUID + ".antiantimacro", true);
            }
            if (!(config.contains(playerUUID + ".onTickNPECatcher"))) {
                config.set(playerUUID + ".onTickNPECatcher", true);
            }
            if (!(config.contains(playerUUID + ".BackCompatibility"))) {
                config.set(playerUUID + ".BackCompatibility", false);
            }
            config.set(playerUUID + ".Active", false);
            Util.save();
            
        }
        catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    static {
        mc = Minecraft.getMinecraft();
        mainFile = new File("config/BinSniper.yml");
        file = new File("config/BinSniper-" + Wrapper.mc.getSession().getProfile().getId().toString() + ".yml");
        oldStr = "";
    }

    public static Configuration configMain() {
        return mainConfig;
    }

    public static Configuration config() {
        return config;
    }

    public static void save() {
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(config, file);
            
        }
        catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public static void sendAir() {
        Util.mc.thePlayer.addChatComponentMessage(new ChatComponentText(" "));
    }

    public static void saveMain() {
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(mainConfig, mainFile);
        }
        catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public static EnumChatFormatting convertColor(String gotString) {
        int compareIdentifier = -2;
        switch (gotString) {
            case "COMMON":
                compareIdentifier = -1;
                break;
            case "UNCOMMON":
                compareIdentifier = 0;
                break;
            case "RARE":
                compareIdentifier = 1;
                break;
            case "EPIC":
                compareIdentifier = 2;
                break;
            case "LEGENDARY":
                compareIdentifier = 3;
                break;
            case "MYTHIC":
                compareIdentifier = 4;
                break;
            case "DIVINE":
                compareIdentifier = 5;
                break;
            case "SPECIAL":
                compareIdentifier = 6;
                break;
            case "VERY_SPECIAL":
                compareIdentifier = 7;
                break;
            case "ULTIMATE":
                compareIdentifier = 8;
                break;

        }

        switch (compareIdentifier) {
            case -1: {
                return EnumChatFormatting.WHITE;
            }
            case 0: {
                return EnumChatFormatting.GREEN;
            }
            case 1: {
                return EnumChatFormatting.BLUE;
            }
            case 2: {
                return EnumChatFormatting.DARK_PURPLE;
            }
            case 3: {
                return EnumChatFormatting.GOLD;
            }
            case 4: {
                return EnumChatFormatting.LIGHT_PURPLE;
            }
            case 5: {
                return EnumChatFormatting.AQUA;
            }
            case 6:
            case 7: {
                return EnumChatFormatting.RED;
            }
            case 8: {
                return EnumChatFormatting.DARK_RED;
            }

            case -2: {
                // 例外を出さずにエラーログを出力
                Exception exception = new IllegalArgumentException("Invalid rarity: " + gotString);
                exception.printStackTrace();  // スタックトレースを出力
                break;
            }
        }
        throw new IllegalArgumentException("compareIdentifier Initializing Failed! please report this error to Github!");
    }

    public static void send(String sendString) {
        if (oldStr.equalsIgnoreCase(sendString)) {
            return;
        }
        Util.mc.thePlayer.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.GRAY + "> " + sendString));
        oldStr = sendString;
    }

    public Util() {
    }
}
