package luna724.iloveichika.binsniper.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.EnumChatFormatting;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static luna724.iloveichika.binsniper.BinSniper.mc;

public class ScoreboardUtil {
    static {
        ScoreboardUtil scoreboardUtil;
    }

    public static List<String> getScoreboardLines() {
        Scoreboard scoreboard = mc.theWorld.getScoreboard();

        if (scoreboard == null) {
            System.out.println("There are no active scoreboards in this world.");
            return null;
        }
        // 数を取得
        List<String> sidebarItems = ScoreboardUtils.getSidebarScores(scoreboard);
        return sidebarItems;
    }

    public static int getPurse() {
        // TODO: Piggy: への対応
        Scoreboard scoreboard = Minecraft.getMinecraft().theWorld.getScoreboard();
        List<String> ScoreboardLines = ScoreboardUtils.getSidebarScores(scoreboard);

        String purseString;
        Pattern pattern = Pattern.compile("Purse: ([\\d,]+)");

        for (String elem : ScoreboardLines) {
            elem = EnumChatFormatting.getTextWithoutFormattingCodes(elem);
            System.out.println("Elem: " + elem);

            System.out.println("Processing line: " + elem); // 行を出力して確認
            if (elem.contains("Purse: ")) {
                purseString = elem;
                Matcher matcher = pattern.matcher(purseString);

                if (matcher.find()) {
                    System.out.println("Regex match found: " + matcher.group(1)); // 正規表現にマッチした部分を表示
                    String result = matcher.group(1);
                    String purseStr = result.replace(",", "");

                    if (!NumberUtils.isDigits(purseStr)) {
                        continue;
                    }
                    return Integer.parseInt(purseStr);
                }
            }
        }
        return -1;
    }

    public static void debugScoreboardLines() {
        List<String> lines = getScoreboardLines();

        // スコアボードの行をすべて表示
//        for (String line : lines) {
//            System.out.println("Scoreboard Line: " + line);
    }
}
