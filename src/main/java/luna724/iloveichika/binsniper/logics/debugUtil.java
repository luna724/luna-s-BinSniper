package net.luna724.iloveichika.binsniper.logics;

import net.luna724.iloveichika.binsniper.utils.Analytics;
import net.luna724.iloveichika.binsniper.utils.Wrapper;
import net.minecraft.util.ChatComponentText;

import java.io.PrintWriter;
import java.io.StringWriter;

import static net.luna724.iloveichika.binsniper.WebHookUrls.autoErrorReporter;
import static net.luna724.iloveichika.binsniper.utils.Wrapper.mc;

public class debugUtil {
    public static void autoErrorReportingService(Exception e) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String username = Wrapper.mc.getSession().getUsername();
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
                    mc.thePlayer.addChatMessage(new ChatComponentText(
                            "§4エラーの自動送信時にエラーが発生しました。 (at debugUtil:autoErrorReportingService())"
                    ));
                }
            }
        }).start();
    }

    public static void sendMessageToDiscord(String message, String URL) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String username = Wrapper.mc.getSession().getUsername();
                    String jsonObj = Analytics.setJsonObj(
                            message, username, null
                    );
                    Analytics.requestWeb(
                            jsonObj, URL
                    );
                } catch (Exception e) {
                    e.printStackTrace();
                    mc.thePlayer.addChatMessage(new ChatComponentText(
                            "§4メッセージの送信中にエラーが発生しました。"
                    ));
                }
            }
        }).start();
    }
}
