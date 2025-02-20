package luna724.iloveichika.binsniper

import luna724.iloveichika.binsniper.utils._ChatLib
import luna724.iloveichika.binsniper.commands.CmdBinSniper
import luna724.iloveichika.lunaclient.config.ConfigManager
import net.minecraft.client.Minecraft
import net.minecraft.client.settings.KeyBinding
import net.minecraftforge.client.ClientCommandHandler
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.client.registry.ClientRegistry
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent

@Mod(modid="lunas_binsniper", version="2.1")
class BinSniper {
    @Mod.EventHandler
    fun preInit(event: FMLPreInitializationEvent) {
        configManager = ConfigManager()
    }

    @Mod.EventHandler
    fun onInit(event: FMLInitializationEvent) {
        keyBinSniper = KeyBinding("Snipe", 25, "key.categories.binsniper") // デフォは P キー
        ClientRegistry.registerKeyBinding(keyBinSniper)

        ClientCommandHandler.instance.registerCommand(CmdBinSniper())
        MinecraftForge.EVENT_BUS.register(BinSniper())
        MinecraftForge.EVENT_BUS.register(BinSnipeLogic())
    }


    companion object {
        const val MODID: String = "lunas_binsniper"
        const val VERSION: String = "2.1"
        const val HEADER: String = "§7[§dB§bi§9n§ap§ee§cr§7]§f:§r "

        lateinit var keyBinSniper: KeyBinding
        lateinit var configManager: ConfigManager
        lateinit var ChatLib: _ChatLib

        val mc = Minecraft.getMinecraft()
    }
}