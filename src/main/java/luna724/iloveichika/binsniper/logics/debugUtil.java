package luna724.iloveichika.binsniper.logics;

import luna724.iloveichika.binsniper.utils.Analytics;
import net.minecraft.util.ChatComponentText;

import java.io.PrintWriter;
import java.io.StringWriter;

import static luna724.iloveichika.binsniper.BinSniper.ChatLib;
import static luna724.iloveichika.binsniper.BinSniper.mc;
import static luna724.iloveichika.binsniper.WebHookUrls.autoErrorReporter;

public class debugUtil {
    public static void autoErrorReportingService(Exception e) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String username = mc.getSession().getUsername();
                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    e.printStackTrace(pw);
                    String message = sw.toString();

                    Analytics.requestWeb(
                            Analytics.setJsonObj(
                                    "```"+message+"```",
                                    "[AutoErrorReporter]("+username+")",
                                    "https://avatars.githubusercontent.com/u/111692896?v=4"
                            ), autoErrorReporter
                    );
                } catch (Exception e) {
                    e.printStackTrace();
                    ChatLib.chat("§4エラーの自動送信時にエラーが発生しました。 (at debugUtil:autoErrorReportingService())");
                }
            }
        }).start();
    }

    public static void sendMessageToDiscord(String message, String URL) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String username = mc.getSession().getUsername();
                    String jsonObj = Analytics.setJsonObj(
                            message, username, null
                    );
                    Analytics.requestWeb(
                            jsonObj, URL
                    );
                } catch (Exception e) {
                    e.printStackTrace();
                    ChatLib.chat("§4メッセージの送信中にエラーが発生しました。");
                }
            }
        }).start();
    }
}
