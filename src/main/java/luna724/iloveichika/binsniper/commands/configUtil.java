package luna724.iloveichika.binsniper.commands;

import luna724.iloveichika.binsniper.utils.Util;
import net.minecraft.util.ChatComponentText;

import static net.luna724.iloveichika.binsniper.logics.debugUtil.autoErrorReportingService;
import static net.luna724.iloveichika.binsniper.utils.Wrapper.mc;

public class configUtil {
    private static String getPlayerId() { return mc.getSession().getProfile().getId().toString(); }

    /**
    設定をトグルする
     targetConfig に playerId および . は不要
     */
    public static boolean   toggleConfig(String targetConfig) {
        try {
            String playerId = getPlayerId();
            Util.config().set(
                    playerId + "." + targetConfig,
                    !Util.config().getBoolean(playerId + "." + targetConfig)
            );
            Util.save();
            return Util.config().getBoolean(playerId + "." + targetConfig);
        }
        catch (Exception e) {
            e.printStackTrace();
            mc.thePlayer.addChatMessage(new ChatComponentText(
                    "§4設定の変更時にエラーが発生しました。 (at configUtil:toggleConfig())"
            ));
            autoErrorReportingService(e);
            return false;
        }
    }

    public static boolean getConfigValueBoolean(String targetConfig) {
        try {
            String playerId = getPlayerId();
            return Util.config().getBoolean(playerId + "." + targetConfig, false);
        }
        catch (Exception e) {
            e.printStackTrace();
            mc.thePlayer.addChatMessage(new ChatComponentText(
                    "§4設定の取得時にエラーが発生しました。 (at configUtil:getConfigValueBoolean())"
            ));
            autoErrorReportingService(e);
            return false;
        }
    }
}
