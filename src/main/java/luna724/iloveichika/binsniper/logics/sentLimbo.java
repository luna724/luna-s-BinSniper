package net.luna724.iloveichika.binsniper.logics;

import net.luna724.iloveichika.binsniper.utils.Wrapper;

public class sentLimbo {
    public static void sentToLimbo() {
        int i = 0;
        while (i < 13) {
            // 12回で disconnect.spam をトリガーする
            Wrapper.mc.thePlayer.sendChatMessage("/");
            i++;
        }
    }


}
