package net.luna724.iloveichika.binsniper.external;

import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.List;

public class ScoreboardUtils {

    public static List<String> getSidebarScores(Scoreboard scoreboard) {
        List<String> found = new ArrayList<>();

        ScoreObjective sidebar = scoreboard.getObjectiveInDisplaySlot(1);
        if (sidebar != null) {
            List<Score> scores = new ArrayList<>(scoreboard.getSortedScores(sidebar));

            // スコアをソートしてから取得
            scores.sort((a, b) -> Integer.compare(b.getScorePoints(), a.getScorePoints()));

            for (Score score : scores) {
                String playerName = score.getPlayerName();
                ScorePlayerTeam team = scoreboard.getPlayersTeam(playerName);
                String scoreText;

                if (team != null) {
                    // プレイヤー名の前後にチームのプレフィックス・サフィックスを付ける
                    scoreText = team.getColorPrefix() + playerName + team.getColorSuffix();
                } else {
                    // チームが無い場合はそのまま表示
                    scoreText = playerName;
                }

                // チャットメッセージの長さが16文字を超える場合にサフィックスを結合
                if (scoreText.length() > 16) {
                    scoreText = scoreText.substring(0, 16) + scoreboard.getPlayersTeam(score.getPlayerName()).getColorSuffix();
                }

                found.add(scoreText);
            }
        }

        return found;
    }
}
