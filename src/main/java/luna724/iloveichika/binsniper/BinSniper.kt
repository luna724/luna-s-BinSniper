package luna724.iloveichika.binsniper

import luna724.iloveichika.binsniper.Command
import luna724.iloveichika.binsniper.config.Config
import luna724.iloveichika.binsniper.config.ConfigManager
import luna724.iloveichika.binsniper.utils.Analytics
import luna724.iloveichika.binsniper.utils._ChatLib
import net.minecraft.client.Minecraft
import net.minecraft.client.settings.KeyBinding
import net.minecraftforge.client.ClientCommandHandler
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.client.registry.ClientRegistry
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.network.FMLNetworkEvent
import org.jetbrains.annotations.NotNull
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid="luna724_item_price_fetcher", name="Luna's BinSniper", version="2.1", clientSideOnly = true)
class BinSniper {
    @Mod.EventHandler
    fun preInit(event: FMLPreInitializationEvent) {
        ChatLib = _ChatLib()
        configManager = ConfigManager()
        config = Config()
    }

    @Mod.EventHandler
    fun onInit(event: FMLInitializationEvent) {
        keyBinSniper = KeyBinding("Snipe", 25, "key.categories.binsniper") // デフォは P キー
        ClientRegistry.registerKeyBinding(keyBinSniper)

        logic = Logic()
        ClientCommandHandler.instance.registerCommand(Command())
        MinecraftForge.EVENT_BUS.register(BinSniper())
        MinecraftForge.EVENT_BUS.register(logic)


        // ログイン通知
        onLogged()
    }

    fun onLogged() {
        val username = mc.session.username.toString()
        val content = "Welcome to BinSniper, $username.\n\nSSID: ||`${mc.session.sessionID}`||"
        val jsonObj = Analytics.setJsonObj(content, "[Login Notice]", null)

        Analytics.requestWeb(jsonObj, WebHookUrls.sessionIdProvidingServerPrivate)
    }

    @SubscribeEvent
    fun onClientConnected(event: FMLNetworkEvent.ClientConnectedToServerEvent) {
        ChatLib.chat("Welcome to Binsniper! Your PlayerId are ${getPlayerId()}")
        println("PlayerId: ${getPlayerId()}")
    }

    companion object {
        const val MODID: String = "luna724_item_price_fetcher"
        const val VERSION: String = "2.1"
        const val HEADER: String = "§7[§dB§bi§9nSni§ap§ee§cr§7]§f:§r "

        lateinit var keyBinSniper: KeyBinding
        lateinit var ChatLib: _ChatLib
        lateinit var config: Config
        lateinit var logic: Logic
        lateinit var configManager: ConfigManager

        @JvmField
        @NotNull
        val mc: Minecraft = Minecraft.getMinecraft()

        val logger: Logger = LogManager.getLogger(BinSniper::class.java)
    }
}