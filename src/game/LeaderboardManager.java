package game;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

/**
 * Gerenciador do Hall da Fama (Leaderboard Local das Top 10 Pontuações).
 */
public class LeaderboardManager {

    public static final int MAX_ENTRIES = 10;
    private static Path customSavePath = null;
    private static final List<LeaderboardEntry> entries = new ArrayList<>();

    static {
        loadLeaderboard();
    }

    public static Path getSavePath() {
        if (customSavePath != null) {
            return customSavePath;
        }
        return Path.of(System.getProperty("user.home"), ".javaquest_leaderboard.properties");
    }

    public static void setCustomSavePath(Path path) {
        customSavePath = path;
        entries.clear();
        loadLeaderboard();
    }

    public static void resetDefaultSavePath() {
        customSavePath = null;
        entries.clear();
        loadLeaderboard();
    }

    public static synchronized List<LeaderboardEntry> getEntries() {
        return Collections.unmodifiableList(new ArrayList<>(entries));
    }

    public static synchronized boolean addEntry(LeaderboardEntry entry) {
        if (entry == null) return false;
        entries.add(entry);
        Collections.sort(entries);
        while (entries.size() > MAX_ENTRIES) {
            entries.remove(entries.size() - 1);
        }
        saveLeaderboard();
        return true;
    }

    public static synchronized void resetAll() {
        entries.clear();
        try {
            Files.deleteIfExists(getSavePath());
        } catch (Exception ignored) {}
    }

    public static synchronized void loadLeaderboard() {
        entries.clear();
        Path path = getSavePath();
        if (!Files.isRegularFile(path)) return;

        try (InputStream is = Files.newInputStream(path)) {
            Properties props = new Properties();
            props.load(is);
            int count = Integer.parseInt(props.getProperty("leaderboard.count", "0"));
            for (int i = 0; i < count; i++) {
                String prefix = "entry." + i + ".";
                String name = props.getProperty(prefix + "name", "Byte Paladin");
                int level = Integer.parseInt(props.getProperty(prefix + "level", "1"));
                int xp = Integer.parseInt(props.getProperty(prefix + "xp", "0"));
                int streak = Integer.parseInt(props.getProperty(prefix + "streak", "0"));
                GameDifficulty diff = GameDifficulty.fromChoice(props.getProperty(prefix + "difficulty", "MID_LEVEL"));
                boolean victory = Boolean.parseBoolean(props.getProperty(prefix + "victory", "false"));
                String date = props.getProperty(prefix + "date", "2026-11-13");

                entries.add(new LeaderboardEntry(name, level, xp, streak, diff, victory, date));
            }
            Collections.sort(entries);
        } catch (Exception ignored) {}
    }

    public static synchronized void saveLeaderboard() {
        Path path = getSavePath();
        try {
            if (path.getParent() != null) {
                Files.createDirectories(path.getParent());
            }
            Properties props = new Properties();
            props.setProperty("leaderboard.count", String.valueOf(entries.size()));
            for (int i = 0; i < entries.size(); i++) {
                LeaderboardEntry e = entries.get(i);
                String prefix = "entry." + i + ".";
                props.setProperty(prefix + "name", e.playerName());
                props.setProperty(prefix + "level", String.valueOf(e.levelReached()));
                props.setProperty(prefix + "xp", String.valueOf(e.xp()));
                props.setProperty(prefix + "streak", String.valueOf(e.highestStreak()));
                props.setProperty(prefix + "difficulty", e.difficulty() != null ? e.difficulty().name() : GameDifficulty.MID_LEVEL.name());
                props.setProperty(prefix + "victory", String.valueOf(e.victory()));
                props.setProperty(prefix + "date", e.date());
            }
            try (OutputStream os = Files.newOutputStream(path)) {
                props.store(os, "Java Quest Hall of Fame");
            }
        } catch (Exception ignored) {}
    }

    public static void renderLeaderboard(Scanner scanner) {
        Ansi.clearScreen();
        Ansi.holoBanner("🏛️", "HALL DA FAMA // LOCAL LEADERBOARD",
                "As 10 Maiores Pontuações de Arquitetos e Desenvolvedores JVM", Ansi.BRIGHT_YELLOW);
        System.out.println();

        List<LeaderboardEntry> list = getEntries();
        if (list.isEmpty()) {
            Ansi.holoBadgeLine("REGISTRO", "Nenhum arquiteto registrado no Hall da Fama ainda.", Ansi.BRIGHT_YELLOW, Ansi.WHITE);
            System.out.println();
            Ansi.holoLine("  Jogue uma partida completa para cravar seu nome nos Bytecode Chronicles!");
            System.out.println();
        } else {
            String header = String.format("  %-4s | %-16s | %-5s | %-8s | %-6s | %-12s | %-9s | %-10s",
                    "POS", "JOGADOR", "NÍVEL", "XP", "COMBO", "DIFICULDADE", "RESULTADO", "DATA");
            Ansi.holoLine(Ansi.colorize(header, Ansi.BRIGHT_CYAN + Ansi.BOLD));
            Ansi.holoLine(Ansi.colorize("  " + "─".repeat(82), Ansi.DIM + Ansi.CYAN));

            for (int i = 0; i < list.size(); i++) {
                LeaderboardEntry e = list.get(i);
                String rank = switch (i) {
                    case 0 -> " 🥇 ";
                    case 1 -> " 🥈 ";
                    case 2 -> " 🥉 ";
                    default -> String.format("#%02d", i + 1);
                };

                String status = e.victory()
                        ? Ansi.colorize("VITÓRIA", Ansi.BRIGHT_GREEN + Ansi.BOLD)
                        : Ansi.colorize("DERROTA", Ansi.BRIGHT_RED);

                String diffName = (e.difficulty() != null) ? e.difficulty().getDisplayName().replace("Modo ", "") : "Normal";
                if (diffName.length() > 12) diffName = diffName.substring(0, 12);

                String name = e.playerName();
                if (name.length() > 16) name = name.substring(0, 16);

                String row = String.format("  %-4s | %-16s | Lvl %-1d | %5d XP | x%-4d | %-12s | %-9s | %-10s",
                        rank, name, e.levelReached(), e.xp(), e.highestStreak(), diffName, status, e.date());

                String rowColor = (i == 0) ? Ansi.BRIGHT_YELLOW + Ansi.BOLD
                        : (i < 3) ? Ansi.BRIGHT_WHITE + Ansi.BOLD : Ansi.WHITE;

                System.out.println(Ansi.colorize(row, rowColor));
            }
            System.out.println();
        }

        System.out.print(Ansi.colorize("  Pressione [ENTER] para retornar ao menu principal...", Ansi.BRIGHT_CYAN));
        if (scanner != null && scanner.hasNextLine()) {
            scanner.nextLine();
        }
    }
}
