package game;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class SaveManager {

    private static Path customSavePath = null;

    public static record SaveData(
        String playerName,
        int currentLevel,
        int hp,
        int maxHp,
        int mana,
        int maxMana,
        int xp,
        int highestStreak,
        int questionsAnswered,
        int correctAnswers,
        Map<Item, Integer> inventory,
        Set<Talent> talents,
        GameDifficulty difficulty,
        long timestamp,
        Theme theme,
        I18n.Language language
    ) {
        public SaveData(String playerName, int currentLevel, int hp, int maxHp, int mana, int maxMana,
                        int xp, int highestStreak, int questionsAnswered, int correctAnswers,
                        Map<Item, Integer> inventory, Set<Talent> talents, GameDifficulty difficulty, long timestamp, Theme theme) {
            this(playerName, currentLevel, hp, maxHp, mana, maxMana, xp, highestStreak, questionsAnswered,
                 correctAnswers, inventory, talents, difficulty, timestamp, theme, I18n.Language.PT_BR);
        }

        public SaveData(String playerName, int currentLevel, int hp, int maxHp, int mana, int maxMana,
                        int xp, int highestStreak, int questionsAnswered, int correctAnswers,
                        Map<Item, Integer> inventory, Set<Talent> talents, GameDifficulty difficulty, long timestamp) {
            this(playerName, currentLevel, hp, maxHp, mana, maxMana, xp, highestStreak, questionsAnswered,
                 correctAnswers, inventory, talents, difficulty, timestamp, Theme.CYBERPUNK, I18n.Language.PT_BR);
        }

        public SaveData(String playerName, int currentLevel, int hp, int maxHp, int mana, int maxMana,
                        int xp, int highestStreak, int questionsAnswered, int correctAnswers,
                        Map<Item, Integer> inventory, long timestamp) {
            this(playerName, currentLevel, hp, maxHp, mana, maxMana, xp, highestStreak, questionsAnswered,
                 correctAnswers, inventory, Collections.emptySet(), GameDifficulty.MID_LEVEL, timestamp, Theme.CYBERPUNK, I18n.Language.PT_BR);
        }

        public void applyTo(Player player) {
            if (player == null) return;
            if (theme != null) {
                Ansi.setTheme(theme);
            }
            if (language != null) {
                I18n.setLanguage(language);
            }
            player.setName(playerName);
            player.setMaxHp(maxHp);
            player.setHp(hp);
            player.setMaxMana(maxMana);
            player.setMana(mana);
            player.setLevel(currentLevel);
            player.setXp(xp);
            player.setHighestStreak(highestStreak);
            player.setQuestionsAnswered(questionsAnswered);
            player.setCorrectAnswers(correctAnswers);

            player.getInventory().clear();
            if (inventory != null) {
                for (Map.Entry<Item, Integer> entry : inventory.entrySet()) {
                    player.getInventory().addItem(entry.getKey(), entry.getValue());
                }
            }

            player.clearTalents();
            if (talents != null) {
                for (Talent t : talents) {
                    player.addTalent(t);
                }
            }
        }
    }

    public static Path getSavePath() {
        if (customSavePath != null) {
            return customSavePath;
        }
        return Path.of(System.getProperty("user.home"), ".javaquest_save.properties");
    }

    public static void setCustomSavePath(Path path) {
        customSavePath = path;
    }

    public static void resetDefaultSavePath() {
        customSavePath = null;
    }

    public static boolean hasSaveGame() {
        return Files.isRegularFile(getSavePath());
    }

    public static boolean saveGame(Player player, int currentLevel) {
        return saveGame(player, currentLevel, GameDifficulty.MID_LEVEL);
    }

    public static boolean saveGame(Player player, int currentLevel, GameDifficulty difficulty) {
        if (player == null) return false;
        Path path = getSavePath();
        try {
            if (path.getParent() != null) {
                Files.createDirectories(path.getParent());
            }

            Properties props = new Properties();
            props.setProperty("version", "2");
            props.setProperty("player.name", player.getName());
            props.setProperty("player.level", String.valueOf(currentLevel));
            props.setProperty("player.hp", String.valueOf(player.getHp()));
            props.setProperty("player.maxHp", String.valueOf(player.getMaxHp()));
            props.setProperty("player.mana", String.valueOf(player.getMana()));
            props.setProperty("player.maxMana", String.valueOf(player.getMaxMana()));
            props.setProperty("player.xp", String.valueOf(player.getXp()));
            props.setProperty("player.highestStreak", String.valueOf(player.getHighestStreak()));
            props.setProperty("player.questionsAnswered", String.valueOf(player.getQuestionsAnswered()));
            props.setProperty("player.correctAnswers", String.valueOf(player.getCorrectAnswers()));
            props.setProperty("game.difficulty", (difficulty != null) ? difficulty.name() : GameDifficulty.MID_LEVEL.name());
            props.setProperty("game.theme", Ansi.getTheme().name());
            props.setProperty("game.language", I18n.getLanguage().name());
            props.setProperty("timestamp", String.valueOf(System.currentTimeMillis()));

            Map<Item, Integer> inv = player.getInventory().getItems();
            for (Map.Entry<Item, Integer> entry : inv.entrySet()) {
                props.setProperty("inventory." + entry.getKey().name(), String.valueOf(entry.getValue()));
            }

            for (Talent t : player.getTalents()) {
                props.setProperty("talent." + t.name(), "true");
            }

            try (OutputStream os = Files.newOutputStream(path)) {
                props.store(os, "Java Quest Save File");
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static SaveData loadGame() {
        Path path = getSavePath();
        if (!Files.isRegularFile(path)) return null;

        try (InputStream is = Files.newInputStream(path)) {
            Properties props = new Properties();
            props.load(is);

            String name = props.getProperty("player.name", "Byte Paladin");
            int level = Integer.parseInt(props.getProperty("player.level", "1"));
            int hp = Integer.parseInt(props.getProperty("player.hp", "100"));
            int maxHp = Integer.parseInt(props.getProperty("player.maxHp", "100"));
            int mana = Integer.parseInt(props.getProperty("player.mana", "60"));
            int maxMana = Integer.parseInt(props.getProperty("player.maxMana", "60"));
            int xp = Integer.parseInt(props.getProperty("player.xp", "0"));
            int highestStreak = Integer.parseInt(props.getProperty("player.highestStreak", "0"));
            int questionsAnswered = Integer.parseInt(props.getProperty("player.questionsAnswered", "0"));
            int correctAnswers = Integer.parseInt(props.getProperty("player.correctAnswers", "0"));
            long timestamp = Long.parseLong(props.getProperty("timestamp", "0"));
            GameDifficulty difficulty = GameDifficulty.fromChoice(props.getProperty("game.difficulty", "MID_LEVEL"));

            Map<Item, Integer> inventory = new EnumMap<>(Item.class);
            for (Item item : Item.values()) {
                String val = props.getProperty("inventory." + item.name());
                if (val != null) {
                    try {
                        int count = Integer.parseInt(val.trim());
                        if (count > 0) {
                            inventory.put(item, count);
                        }
                    } catch (NumberFormatException ignored) {}
                }
            }

            Set<Talent> talents = EnumSet.noneOf(Talent.class);
            for (Talent t : Talent.values()) {
                if ("true".equalsIgnoreCase(props.getProperty("talent." + t.name()))) {
                    talents.add(t);
                }
            }

            Theme theme = Theme.fromCode(props.getProperty("game.theme", "CYBERPUNK"));
            I18n.Language language = I18n.Language.fromCode(props.getProperty("game.language", "PT_BR"));

            return new SaveData(
                name, level, hp, maxHp, mana, maxMana, xp,
                highestStreak, questionsAnswered, correctAnswers,
                inventory, talents, difficulty, timestamp, theme, language
            );
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean deleteSaveGame() {
        try {
            Path path = getSavePath();
            return Files.deleteIfExists(path);
        } catch (Exception e) {
            return false;
        }
    }
}
