package game;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;

/**
 * Gerenciador de Conquistas e Medalhas (Bytecode Badges) com persistência local.
 */
public class AchievementManager {

    private static Path customSavePath = null;
    private static final Map<Achievement, Long> unlockedBadges = new EnumMap<>(Achievement.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
            .withZone(ZoneId.systemDefault());

    static {
        loadAchievements();
    }

    public static Path getSavePath() {
        if (customSavePath != null) {
            return customSavePath;
        }
        return Path.of(System.getProperty("user.home"), ".javaquest_achievements.properties");
    }

    public static void setCustomSavePath(Path path) {
        customSavePath = path;
        unlockedBadges.clear();
        loadAchievements();
    }

    public static void resetDefaultSavePath() {
        customSavePath = null;
        unlockedBadges.clear();
        loadAchievements();
    }

    public static synchronized boolean isUnlocked(Achievement achievement) {
        if (achievement == null) return false;
        return unlockedBadges.containsKey(achievement);
    }

    public static synchronized boolean unlock(Achievement achievement) {
        if (achievement == null || isUnlocked(achievement)) {
            return false;
        }

        long timestamp = System.currentTimeMillis();
        unlockedBadges.put(achievement, timestamp);
        saveAchievements();

        // Audio & visual unlock notification
        AudioEngine.playCriticalHit();
        System.out.println();
        Ansi.holoBadgeLine("🏆 CONQUISTA DESBLOQUEADA!",
                achievement.getIcon() + " " + achievement.getTitle() + " — " + achievement.getDescription(),
                Ansi.BRIGHT_YELLOW, Ansi.BRIGHT_WHITE + Ansi.BOLD);
        System.out.println();
        return true;
    }

    public static synchronized int getUnlockedCount() {
        return unlockedBadges.size();
    }

    public static int getTotalCount() {
        return Achievement.values().length;
    }

    public static synchronized Map<Achievement, Long> getUnlockedAchievements() {
        return Collections.unmodifiableMap(new EnumMap<>(unlockedBadges));
    }

    public static synchronized void resetAll() {
        unlockedBadges.clear();
        try {
            Files.deleteIfExists(getSavePath());
        } catch (Exception ignored) {}
    }

    public static synchronized void loadAchievements() {
        unlockedBadges.clear();
        Path path = getSavePath();
        if (!Files.isRegularFile(path)) return;

        try (InputStream is = Files.newInputStream(path)) {
            Properties props = new Properties();
            props.load(is);
            for (Achievement a : Achievement.values()) {
                String val = props.getProperty("badge." + a.getId());
                if (val != null) {
                    try {
                        unlockedBadges.put(a, Long.parseLong(val.trim()));
                    } catch (NumberFormatException e) {
                        unlockedBadges.put(a, System.currentTimeMillis());
                    }
                }
            }
        } catch (Exception ignored) {}
    }

    public static synchronized void saveAchievements() {
        Path path = getSavePath();
        try {
            if (path.getParent() != null) {
                Files.createDirectories(path.getParent());
            }
            Properties props = new Properties();
            for (Map.Entry<Achievement, Long> entry : unlockedBadges.entrySet()) {
                props.setProperty("badge." + entry.getKey().getId(), String.valueOf(entry.getValue()));
            }
            try (OutputStream os = Files.newOutputStream(path)) {
                props.store(os, "Java Quest Bytecode Badges");
            }
        } catch (Exception ignored) {}
    }

    public static void renderAchievementsScreen(Scanner scanner) {
        Ansi.clearScreen();
        Ansi.holoBanner("🏆", "BYTECODE BADGES & ACHIEVEMENTS",
                "Registro de Conquistas e Proezas no Ecossistema JVM", Ansi.BRIGHT_YELLOW);
        System.out.println();

        int unlocked = getUnlockedCount();
        int total = getTotalCount();
        double percent = (total > 0) ? (unlocked * 100.0 / total) : 0.0;

        int barWidth = 30;
        int filled = (int) Math.round((percent / 100.0) * barWidth);
        String bar = "█".repeat(filled) + "░".repeat(barWidth - filled);

        Ansi.holoLine("  " + Ansi.colorize("Progresso Global: ", Ansi.BRIGHT_WHITE + Ansi.BOLD) +
                Ansi.colorize("[" + bar + "]", Ansi.BRIGHT_YELLOW) +
                Ansi.colorize(String.format(" %d/%d (%4.1f%%)", unlocked, total, percent), Ansi.BRIGHT_GREEN + Ansi.BOLD));
        System.out.println();

        for (Achievement a : Achievement.values()) {
            boolean done = isUnlocked(a);
            String statusBadge = done
                    ? Ansi.colorize(" [✓ CONCLUÍDO] ", Ansi.BRIGHT_GREEN + Ansi.BOLD)
                    : Ansi.colorize(" [✕ BLOQUEADO] ", Ansi.DIM + Ansi.BRIGHT_BLACK);

            String titleColor = done ? Ansi.BRIGHT_YELLOW + Ansi.BOLD : Ansi.WHITE;
            String descColor = done ? Ansi.BRIGHT_WHITE : Ansi.DIM + Ansi.WHITE;

            String dateStr = "";
            if (done) {
                Long ts = unlockedBadges.get(a);
                if (ts != null && ts > 0) {
                    dateStr = Ansi.colorize(" (" + DATE_FORMATTER.format(Instant.ofEpochMilli(ts)) + ")", Ansi.DIM + Ansi.CYAN);
                }
            }

            System.out.println(statusBadge + " " + a.getIcon() + " " +
                    Ansi.colorize(a.getTitle(), titleColor) + dateStr);
            System.out.println("      " + Ansi.colorize(a.getDescription(), descColor));
            System.out.println();
        }

        System.out.print(Ansi.colorize("  Pressione [ENTER] para retornar ao menu principal...", Ansi.BRIGHT_CYAN));
        if (scanner != null && scanner.hasNextLine()) {
            scanner.nextLine();
        }
    }
}
