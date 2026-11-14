package game;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Registro de pontuação do Hall da Fama (Leaderboard).
 */
public record LeaderboardEntry(
    String playerName,
    int levelReached,
    int xp,
    int highestStreak,
    GameDifficulty difficulty,
    boolean victory,
    String date
) implements Comparable<LeaderboardEntry> {

    public LeaderboardEntry(String playerName, int levelReached, int xp,
                            int highestStreak, GameDifficulty difficulty, boolean victory) {
        this(playerName, levelReached, xp, highestStreak, difficulty, victory,
             LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
    }

    @Override
    public int compareTo(LeaderboardEntry other) {
        if (other == null) return -1;
        int cmp = Integer.compare(other.xp, this.xp);
        if (cmp != 0) return cmp;
        cmp = Integer.compare(other.levelReached, this.levelReached);
        if (cmp != 0) return cmp;
        return Integer.compare(other.highestStreak, this.highestStreak);
    }
}
