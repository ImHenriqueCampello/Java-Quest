package game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class GameEngine {
    private final Scanner scanner;
    private Player player;
    private int currentLevel;
    private static final int MAX_LEVEL = 6;
    private boolean hintActiveNextQuestion = false;
    private GameDifficulty difficulty = GameDifficulty.MID_LEVEL;
    private final CombatConfig combatConfig = CombatConfig.defaultBalance();
    private int hintUsesInCampaign = 0;
    private int coffeeUsesInCampaign = 0;
    private int consecutiveHardQuestions = 0;
    private boolean healUsedInCurrentBattle = false;

    public static void configureCliArgs(String[] args) {
        if (args != null) {
            for (String arg : args) {
                if (arg == null) continue;
                String lower = arg.trim().toLowerCase();
                if (lower.equals("--no-color") || lower.equals("--plain") || lower.equals("-nocolor")) {
                    Ansi.setColorEnabled(false);
                } else if (lower.equals("--high-contrast") || lower.equals("--contrast")) {
                    Ansi.setHighContrast(true);
                } else if (lower.startsWith("--theme=")) {
                    String themeCode = lower.substring("--theme=".length());
                    Ansi.setTheme(Theme.fromCode(themeCode));
                } else if (lower.startsWith("--lang=") || lower.startsWith("--language=")) {
                    String code = lower.substring(lower.indexOf('=') + 1);
                    I18n.setLanguage(I18n.Language.fromCode(code));
                } else if (lower.equals("--en") || lower.equals("--english")) {
                    I18n.setLanguage(I18n.Language.EN_US);
                } else if (lower.equals("--pt") || lower.equals("--portugues") || lower.equals("--portuguese")) {
                    I18n.setLanguage(I18n.Language.PT_BR);
                }
            }
        }
    }

    public static class MissedQuestion {
        private final Question question;
        private final int selectedIndex;
        private final int level;

        public MissedQuestion(Question question, int selectedIndex, int level) {
            this.question = question;
            this.selectedIndex = selectedIndex;
            this.level = level;
        }

        public Question getQuestion() { return question; }
        public int getSelectedIndex() { return selectedIndex; }
        public int getLevel() { return level; }
    }

    private final List<MissedQuestion> missedQuestions = new ArrayList<>();

    public List<MissedQuestion> getMissedQuestions() {
        return Collections.unmodifiableList(missedQuestions);
    }

    public GameEngine() {
        this(new Scanner(System.in));
    }

    public GameEngine(Scanner scanner) {
        this.scanner = scanner;
        this.currentLevel = 1;
    }

    public GameEngine(java.io.InputStream in) {
        this(new Scanner(in));
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public void setCurrentLevel(int currentLevel) {
        this.currentLevel = currentLevel;
    }

    public boolean isHintActiveNextQuestion() {
        return hintActiveNextQuestion;
    }

    public void setHintActiveNextQuestion(boolean hintActiveNextQuestion) {
        this.hintActiveNextQuestion = hintActiveNextQuestion;
    }

    public GameDifficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(GameDifficulty difficulty) {
        this.difficulty = (difficulty != null) ? difficulty : GameDifficulty.MID_LEVEL;
    }

    public int getSpellCost(int baseCost) {
        if (player != null && player.hasTalent(Talent.ZERO_ALLOCATION)) {
            return (int) Math.round(baseCost * 0.75);
        }
        return baseCost;
    }

    public CombatConfig getCombatConfig() {
        return combatConfig;
    }

    public BattleAction resolveBattleAction(String choice, int optionIndex, Item item) {
        return BattleAction.parse(choice, optionIndex, item);
    }

    public static int parseQuizChoice(String input) {
        if (input == null) return -1;
        String clean = input.trim().toUpperCase();
        return switch (clean) {
            case "A", "1" -> 0;
            case "B", "2" -> 1;
            case "C", "3" -> 2;
            case "D", "4" -> 3;
            default -> -1;
        };
    }

    public static String parseMainMenuChoice(String input) {
        if (input == null) return "";
        String clean = input.trim().toUpperCase();
        return switch (clean) {
            case "1", "S", "N", "START", "NEW" -> "1";
            case "2", "H", "T", "HOW", "HELP" -> "2";
            case "3", "C", "L", "CHEAT", "LORE" -> "3";
            case "4", "E", "Q", "EXIT", "QUIT" -> "4";
            case "5", "R", "RESUME", "CONTINUE", "LOAD", "LOADGAME" -> "5";
            case "6", "D", "DIFF", "DIFFICULTY" -> "6";
            case "7", "B", "LAB", "STUDY", "TREINO" -> "7";
            case "8", "A", "BADGE", "BADGES", "ACHIEVEMENT", "ACHIEVEMENTS", "CONQUISTAS" -> "8";
            case "9", "LEADERBOARD", "HALL", "FAMA", "RANK", "SCORES" -> "9";
            case "10", "THEME", "THEMES", "COLOR", "ACCESSIBILITY", "PALETTE" -> "10";
            case "M", "MODES", "SPECIAL", "RUSH", "ENDLESS" -> "M";
            default -> clean;
        };
    }

    public static String parseCombatAction(String input) {
        if (input == null) return "";
        String clean = input.trim().toUpperCase();
        return switch (clean) {
            case "1", "C", "A", "S", "CAST", "ATTACK", "SPELL" -> "1";
            case "2", "D", "P", "DEBUG", "POWER", "POWERS" -> "2";
            case "3", "I", "L", "INSPECT", "LORE", "INFO" -> "3";
            case "4", "R", "Q", "F", "RETREAT", "SURRENDER", "QUIT", "FORFEIT" -> "4";
            case "5", "B", "BAG", "ITEM", "ITEMS", "BACKPACK", "INV", "INVENTORY" -> "5";
            default -> clean;
        };
    }

    public static String parseDebugPowerChoice(String input) {
        if (input == null) return "";
        String clean = input.trim().toUpperCase();
        return switch (clean) {
            case "A", "1", "H", "R", "HEAL", "REFACTOR" -> "A";
            case "B", "2", "P", "HINT", "STACK", "PRINT" -> "B";
            case "C", "3", "S", "T", "SHIELD", "TRY", "CATCH" -> "C";
            case "D", "4", "Q", "RETURN", "BACK", "EXIT" -> "D";
            default -> clean;
        };
    }

    public static boolean parseGameOverChoice(String input) {
        if (input == null) return false;
        String clean = input.trim().toUpperCase();
        if (clean.equals("SAIR") || clean.equals("QUIT") || clean.equals("EXIT") || clean.startsWith("N") || clean.equals("0") || clean.equals("Q")) {
            return false;
        }
        return clean.startsWith("Y") || clean.equals("S") || clean.equals("SIM") || clean.equals("1") || clean.equals("R") || clean.equals("RETRY");
    }

    public static int calculateTotalDamage(int baseDamage, int comboBonus) {
        return baseDamage + comboBonus;
    }

    public static int calculateTotalDamage(int baseDamage, int comboBonus, Player player) {
        int total = baseDamage + comboBonus;
        if (player != null && player.hasTalent(Talent.HOTSPOT_INLINING)) {
            total += 10;
        }
        return total;
    }

    public void start() {
        while (true) {
            showTitleScreen();
            String promptMsg = SaveManager.hasSaveGame()
                ? "Enter your choice (1-4, 5/R to resume): "
                : "Enter your choice (1-4 | S/H/C/Q): ";
            String rawChoice = prompt(promptMsg);
            String choice = parseMainMenuChoice(rawChoice);
            switch (choice) {
                case "1" -> startNewGame();
                case "2" -> showHowToPlay();
                case "3" -> showJavaCheatSheet();
                case "4" -> {
                    Ansi.holoBadgeLine("SYSTEM", "Thanks for playing Java Quest! Keep coding!\n", Ansi.BRIGHT_GREEN, Ansi.BRIGHT_WHITE + Ansi.BOLD);
                    return;
                }
                case "5" -> continueSavedGame();
                case "6" -> promptDifficulty();
                case "7" -> new BytecodeLab(scanner).start();
                case "8" -> AchievementManager.renderAchievementsScreen(scanner);
                case "9" -> LeaderboardManager.renderLeaderboard(scanner);
                case "10" -> promptThemeAndAccessibility();
                case "M" -> promptSpecialModes();
                default -> {
                    Ansi.holoBadgeLine("ERROR", "Invalid choice. Press Enter to retry.", Ansi.RED, Ansi.BRIGHT_RED);
                    scanner.nextLine();
                }
            }
        }
    }

    void showTitleScreen() {
        Ansi.clearScreen();
        String titleLogo = """
   ██╗ █████╗ ██╗   ██╗ █████╗      ██████╗ ██╗   ██╗███████╗███████╗████████╗
   ██║██╔══██╗██║   ██║██╔══██╗    ██╔═══██╗██║   ██║██╔════╝██╔════╝╚══██╔══╝
   ██║███████║██║   ██║███████║    ██║   ██║██║   ██║█████╗  ███████╗   ██║   
██   ██║██╔══██║╚██╗ ██╔╝██╔══██║    ██║▄▄ ██║██║   ██║██╔══╝  ╚════██║   ██║   
╚█████╔╝██║  ██║ ╚████╔╝ ██║  ██║    ╚██████╔╝╚██████╔╝███████╗███████║   ██║   
 ╚════╝ ╚═╝  ╚═╝  ╚═══╝  ╚═╝  ╚═╝     ╚══▀▀═╝  ╚═════╝ ╚══════╝╚══════╝   ╚═╝   
        """;
        for (String line : titleLogo.split("\n")) {
            if (!line.trim().isEmpty()) {
                Ansi.holoLine(Ansi.colorize(line, Ansi.BRIGHT_CYAN));
            }
        }

        Ansi.holoLine(Ansi.center(Ansi.colorize("⚔️  " + I18n.get("menu.title") + "  ⚔️", Ansi.BRIGHT_YELLOW + Ansi.BOLD), 78));
        Ansi.holoLine(Ansi.center(Ansi.colorize(I18n.get("menu.subtitle"), Ansi.WHITE), 78));
        System.out.println();
        
        Ansi.holoOption("1 | S", I18n.get("menu.start") + " (" + difficulty.getDisplayName() + ")", Ansi.BRIGHT_GREEN, Ansi.BRIGHT_WHITE + Ansi.BOLD);
        if (SaveManager.hasSaveGame()) {
            SaveManager.SaveData save = SaveManager.loadGame();
            String saveDesc = (save != null) 
                ? I18n.get("menu.resume.details", save.currentLevel(), save.playerName(), save.hp(), save.maxHp())
                : I18n.get("menu.resume");
            Ansi.holoOption("5 | R", "💾 " + saveDesc, Ansi.BRIGHT_MAGENTA, Ansi.BRIGHT_WHITE + Ansi.BOLD);
        }
        Ansi.holoOption("2 | H", I18n.get("menu.howtoplay"), Ansi.BRIGHT_CYAN, Ansi.WHITE);
        Ansi.holoOption("3 | C", I18n.get("menu.cheatsheet"), Ansi.BRIGHT_CYAN, Ansi.WHITE);
        Ansi.holoOption("6 | D", I18n.get("menu.difficulty") + " (" + difficulty.getDisplayName() + ")", Ansi.BRIGHT_YELLOW, Ansi.WHITE);
        Ansi.holoOption("7 | LAB", "🔬 " + I18n.get("menu.lab"), Ansi.BRIGHT_MAGENTA, Ansi.BRIGHT_WHITE + Ansi.BOLD);
        Ansi.holoOption("8 | A", "🏆 " + I18n.get("menu.achievements") + " (" + AchievementManager.getUnlockedCount() + "/" + AchievementManager.getTotalCount() + ")", Ansi.BRIGHT_YELLOW, Ansi.BRIGHT_WHITE + Ansi.BOLD);
        Ansi.holoOption("9 | RANK", "🏛️ " + I18n.get("menu.leaderboard"), Ansi.BRIGHT_CYAN, Ansi.WHITE);
        String themeLabel = Ansi.getTheme().getDisplayName() + (Ansi.isHighContrast() ? " [" + I18n.get("theme.highcontrast") + "]" : "") + (!Ansi.isColorEnabled() ? " [" + I18n.get("theme.nocolor") + "]" : "") + " [" + I18n.getLanguage().getDisplayName() + "]";
        Ansi.holoOption("10 | THEME", "🎨 " + I18n.get("menu.themes") + " (" + themeLabel + ")", Ansi.BRIGHT_GREEN, Ansi.WHITE);
        Ansi.holoOption("M | SPECIAL", "⚔️ " + I18n.get("menu.special"), Ansi.BRIGHT_MAGENTA, Ansi.WHITE);
        Ansi.holoOption("4 | Q", I18n.get("menu.exit"), Ansi.BRIGHT_RED, Ansi.DIM + Ansi.WHITE);
        System.out.println();
    }

    void promptThemeAndAccessibility() {
        Ansi.clearScreen();
        Ansi.holoBanner("🎨", I18n.get("theme.title"), I18n.get("theme.subtitle"), Ansi.BRIGHT_MAGENTA);
        System.out.println();
        Ansi.holoOption("1", "Cyberpunk Bytecode (Ciano, Magenta e Amarelo Neon) [Padrão]", Ansi.BRIGHT_CYAN, Ansi.WHITE);
        Ansi.holoOption("2", "The Matrix Phosphor (Verde CRT retro monocromático)", Ansi.GREEN, Ansi.WHITE);
        Ansi.holoOption("3", "Dracula / Dark IDE (Púrpura, Laranja e Cinza moderno)", Ansi.MAGENTA, Ansi.WHITE);
        Ansi.holoOption("4", "Classic Monokai (Amarelo, Verde e Vermelho clássicos)", Ansi.YELLOW, Ansi.WHITE);
        Ansi.holoOption("5 | C", I18n.get("theme.toggle.contrast") + " (" + (Ansi.isHighContrast() ? I18n.get("theme.active") : I18n.get("theme.disabled")) + ")", Ansi.BRIGHT_YELLOW, Ansi.WHITE);
        Ansi.holoOption("6 | N", I18n.get("theme.toggle.nocolor") + " (" + (Ansi.isColorEnabled() ? I18n.get("theme.color.on") : I18n.get("theme.color.off")) + ")", Ansi.BRIGHT_WHITE, Ansi.WHITE);
        Ansi.holoOption("7 | A", I18n.get("theme.toggle.audio") + " (" + (AudioEngine.isSoundEnabled() ? I18n.get("theme.audio.on") : I18n.get("theme.audio.off")) + ")", Ansi.BRIGHT_CYAN, Ansi.WHITE);
        Ansi.holoOption("8 | L", I18n.get("theme.toggle.language") + " (" + I18n.getLanguage().getDisplayName() + ")", Ansi.BRIGHT_GREEN, Ansi.WHITE);
        Ansi.holoOption("0 | R", I18n.get("theme.back"), Ansi.BRIGHT_RED, Ansi.WHITE);
        System.out.println();
        String choice = prompt("  Escolha uma opção (1-4 temas, 5-8 toggles, 0 voltar): ").trim().toUpperCase();
        switch (choice) {
            case "1" -> Ansi.setTheme(Theme.CYBERPUNK);
            case "2" -> Ansi.setTheme(Theme.MATRIX);
            case "3" -> Ansi.setTheme(Theme.DRACULA);
            case "4" -> Ansi.setTheme(Theme.MONOKAI);
            case "5", "C" -> Ansi.setHighContrast(!Ansi.isHighContrast());
            case "6", "N" -> Ansi.setColorEnabled(!Ansi.isColorEnabled());
            case "7", "A" -> AudioEngine.setSoundEnabled(!AudioEngine.isSoundEnabled());
            case "8", "L" -> {
                I18n.toggleLanguage();
                Ansi.holoBadgeLine("IDIOMA", I18n.get("theme.language.switched", I18n.getLanguage().getDisplayName()), Ansi.BRIGHT_GREEN, Ansi.BRIGHT_WHITE);
            }
            default -> {}
        }
        Ansi.holoBadgeLine("CONFIG", I18n.get("theme.updated"), Ansi.BRIGHT_GREEN, Ansi.BRIGHT_WHITE);
        Ansi.sleep(250);
    }

    void promptSpecialModes() {
        Ansi.clearScreen();
        Ansi.holoBanner("⚔️", I18n.get("modes.title"), I18n.get("modes.subtitle"), Ansi.BRIGHT_MAGENTA);
        System.out.println();
        Ansi.holoOption("1 | R", "⚡ " + I18n.get("modes.rush"), Ansi.BRIGHT_RED, Ansi.WHITE);
        Ansi.holoOption("2 | E", "♾️  " + I18n.get("modes.endless"), Ansi.BRIGHT_CYAN, Ansi.WHITE);
        Ansi.holoOption("0 | Q", "↩️  " + I18n.get("modes.back"), Ansi.DIM + Ansi.WHITE, Ansi.DIM + Ansi.WHITE);
        System.out.println();
        String choice = prompt("  Escolha o modo (1-2 | 0 voltar): ").trim().toUpperCase();
        switch (choice) {
            case "1", "R", "RUSH" -> runBossRushMode();
            case "2", "E", "ENDLESS" -> runEndlessDungeonMode();
            default -> {}
        }
    }

    void promptDifficulty() {
        Ansi.clearScreen();
        Ansi.holoBanner("⚙️", "GAME DIFFICULTY SETTINGS", "Configure JVM Challenge Profile", Ansi.BRIGHT_CYAN);
        System.out.println();
        Ansi.holoOption("1 | I", "🌱 Modo Estagiário (Casual: -35% dano inimigo, Dica custa 5 MP)", Ansi.BRIGHT_GREEN, Ansi.WHITE);
        Ansi.holoOption("2 | M", "⚙️ Modo Desenvolvedor Pleno (Normal: Balanceamento clássico)", Ansi.BRIGHT_CYAN, Ansi.WHITE);
        Ansi.holoOption("3 | S", "🔥 Modo Arquiteto Sênior (Hardcore: +30% dano inimigo, Permadeath!)", Ansi.BRIGHT_RED, Ansi.WHITE);
        System.out.println();
        String diffChoice = prompt("  Choice (1-3 | I/M/S) [Current: " + this.difficulty.getOptionIndex() + "]: ");
        this.difficulty = GameDifficulty.fromChoice(diffChoice);
        System.out.println();
        Ansi.holoBadgeLine("PROFILE", "Deployment difficulty set to: " + this.difficulty.getDisplayName(), Ansi.BRIGHT_YELLOW, Ansi.BRIGHT_WHITE + Ansi.BOLD);
        Ansi.sleep(200);
    }

    void runBossRushMode() {
        Ansi.clearScreen();
        Ansi.holoBanner("⚡", "BOSS RUSH MODE: GAUNTLET DA JVM", "Enfrente os 6 Chefes Consecutivos com Cura Parcial!", Ansi.BRIGHT_RED);
        System.out.println();
        String name = prompt("Nome do Desenvolvedor (ou Enter para 'Rush Paladin'): ");
        if (name.trim().isEmpty()) name = "Rush Paladin";
        player = new Player(name);
        player.getInventory().addItem(Item.COFFEE_BREW, 2);
        player.getInventory().addItem(Item.CLEAN_ARCH_SHIELD, 1);
        player.getInventory().addItem(Item.BREAKPOINT_TOKEN, 1);

        for (int lvl = 1; lvl <= MAX_LEVEL; lvl++) {
            currentLevel = lvl;
            Ansi.clearScreen();
            Ansi.holoBadgeLine("BOSS RUSH", String.format("ROUND %d/6 — Chefe Nível %d!", lvl, lvl), Ansi.BRIGHT_YELLOW, Ansi.BRIGHT_WHITE + Ansi.BOLD);
            Ansi.sleep(250);
            boolean cleared = runLevel(lvl);
            if (!cleared) {
                Ansi.holoBanner("💀", "BOSS RUSH FALHOU!", "Você foi derrotado no Chefe " + lvl + "!", Ansi.BRIGHT_RED);
                LeaderboardManager.addEntry(new LeaderboardEntry(player.getName() + " [Rush]", lvl, player.getXp(), player.getHighestStreak(), difficulty, false));
                pressEnterToContinue();
                return;
            }
            if (lvl < MAX_LEVEL) {
                Ansi.clearScreen();
                Ansi.holoBanner("⚡", "CHEFE DERROTADO!", "Recuperação Parcial de Recursos (+30 HP, +25 MP)", Ansi.BRIGHT_GREEN);
                player.heal(30);
                player.restoreMana(25);
                player.getInventory().addItem(Item.COFFEE_BREW);
                Ansi.holoBadgeLine("HP/MANA", String.format("%d/%d HP | %d/%d MP", player.getHp(), player.getMaxHp(), player.getMana(), player.getMaxMana()), Ansi.BRIGHT_CYAN, Ansi.WHITE);
                pressEnterToContinue("  [Pressione Enter para enfrentar o próximo chefe...]");
            }
        }

        Ansi.clearScreen();
        Ansi.holoBanner("🏆", "BOSS RUSH VENCIDO COM GLÓRIA!", "Todos os 6 Chefes da JVM Foram Conquistados!", Ansi.BRIGHT_YELLOW);
        LeaderboardManager.addEntry(new LeaderboardEntry(player.getName() + " [Rush]", MAX_LEVEL, player.getXp() + 500, player.getHighestStreak(), difficulty, true));
        AchievementManager.unlock(Achievement.GARBAGE_COLLECTOR);
        pressEnterToContinue();
    }

    void runEndlessDungeonMode() {
        Ansi.clearScreen();
        Ansi.holoBanner("♾️", "ENDLESS BYTECODE DUNGEON", "Desça aos Andares Infinitos da JVM! Até onde você sobrevive?", Ansi.BRIGHT_MAGENTA);
        System.out.println();
        String name = prompt("Nome do Desenvolvedor (ou Enter para 'Dungeon Hacker'): ");
        if (name.trim().isEmpty()) name = "Dungeon Hacker";
        player = new Player(name);
        player.getInventory().addItem(Item.COFFEE_BREW, 2);
        player.getInventory().addItem(Item.CLEAN_ARCH_SHIELD, 1);
        player.getInventory().addItem(Item.JIT_BOOSTER, 1);

        int floor = 1;
        while (!player.isDefeated()) {
            currentLevel = 1 + ((floor - 1) % MAX_LEVEL);
            Enemy enemy = createEndlessEnemy(floor);
            Ansi.clearScreen();
            Ansi.holoBanner("🏰", "DUNGEON ANDAR " + floor, "Inimigo: " + enemy.getName() + " (" + enemy.getCurrentHp() + " HP)", Ansi.BRIGHT_CYAN);
            Ansi.sleep(250);

            boolean floorCleared = runEndlessBattle(enemy, floor);
            if (!floorCleared || player.isDefeated()) {
                Ansi.clearScreen();
                Ansi.holoBanner("💀", "FIM DA EXPEDIÇÃO", "Você tombou no Andar " + floor + " da Endless Dungeon!", Ansi.BRIGHT_RED);
                LeaderboardManager.addEntry(new LeaderboardEntry(player.getName() + " [Floor " + floor + "]", floor, player.getXp(), player.getHighestStreak(), difficulty, false));
                pressEnterToContinue();
                return;
            }

            floor++;
            player.heal(25);
            player.restoreMana(20);
            if (floor % 2 == 0) {
                player.getInventory().addItem(Item.COFFEE_BREW);
            }
            if (floor % 3 == 0) {
                player.getInventory().addItem(Item.CLEAN_ARCH_SHIELD);
            }
            Ansi.holoBadgeLine("ANDAR CONCLUÍDO", "Descendo para o Andar " + floor + "! Recursos restaurados.", Ansi.BRIGHT_GREEN, Ansi.WHITE);
            Ansi.sleep(200);
        }
    }

    private Enemy createEndlessEnemy(int floor) {
        String[] titles = {
            "Deadlock Specter", "ConcurrentModification Phantom", "ClassLoader Leaker",
            "Bytecode Anomaly", "Thread Starvation Fiend", "Off-By-One Gremlin"
        };
        String name = titles[(floor - 1) % titles.length] + " [F-" + floor + "]";
        int hp = 40 + (floor * 10);
        int attack = 12 + (floor * 2);
        int simulatedLvl = 1 + ((floor - 1) % MAX_LEVEL);
        Enemy template = Enemy.createForLevel(simulatedLvl);
        return new Enemy(simulatedLvl, name, "Dungeon Anomaly", "Uma anomalia de bytecode gerada nas profundezas da JVM.",
                hp, attack, "Bytecode Glitch", "executa uma instrução corrompida de bytecode!",
                Ansi.BRIGHT_MAGENTA,
                template.getIdle1(), template.getIdle2(),
                template.getAttackFrame(), template.getHurtFrame());
    }

    private boolean runEndlessBattle(Enemy enemy, int floor) {
        int simulatedLvl = 1 + ((floor - 1) % MAX_LEVEL);
        List<Question> questions = QuestionBank.getRandomizedQuestionsForLevel(simulatedLvl);
        int qIdx = 0;

        while (!enemy.isDefeated() && !player.isDefeated()) {
            enemy.incrementTurnCount();
            BattleRenderer.renderFrame(player, enemy, CharacterSprite.PLAYER_IDLE_1, enemy.getIdle1(), null,
                    "Endless Dungeon Andar " + floor + ": Escolha sua ação:", false, false);

            Ansi.holoOption("1 | C", "⚔️  Responder Quiz de Java", Ansi.BRIGHT_YELLOW, Ansi.BRIGHT_WHITE + Ansi.BOLD);
            Ansi.holoOption("2 | D", "🧪  Usar Poderes Debug", Ansi.BRIGHT_CYAN, Ansi.BRIGHT_WHITE + Ansi.BOLD);
            Ansi.holoOption("3 | B", "🎒  Dev Backpack (" + player.getInventory().getTotalItemCount() + " itens)", Ansi.BRIGHT_MAGENTA, Ansi.BRIGHT_WHITE + Ansi.BOLD);
            Ansi.holoOption("4 | Q", "🏃  Desistir da Dungeon", Ansi.BRIGHT_RED, Ansi.DIM + Ansi.WHITE);

            String choice;
            try (BattleRenderer.IdleSession anim = BattleRenderer.startIdleAnimation(player, enemy, "Endless Dungeon Andar " + floor + ":")) {
                choice = prompt("  Ação (1-4 | C/D/B/Q): ");
            }
            String act = parseCombatAction(choice);
            switch (act) {
                case "1" -> {
                    if (qIdx >= questions.size()) {
                        questions = QuestionBank.getRandomizedQuestionsForLevel(simulatedLvl);
                        qIdx = 0;
                    }
                    Question q = questions.get(qIdx++);
                    BattleRenderer.animateQuizFaceOff(player, enemy);
                    boolean correct = handleQuizQuestion(q, enemy);
                    if (correct) {
                        int dmg = calculateTotalDamage(q.getDamage(), player.getSpellDamageBonus(), player);
                        if (player.isJitBoosterActive()) {
                            dmg = (int) Math.round(dmg * 2.5);
                            player.setJitBoosterActive(false);
                            AchievementManager.unlock(Achievement.OVERCLOCKED_JIT);
                        }
                        BattleRenderer.animatePlayerAttack(player, enemy, dmg, player.getComboStreak() > 0);
                        if (enemy.isDefeated()) {
                            BattleRenderer.animateEnemyDefeated(player, enemy);
                            return true;
                        }
                    } else {
                        BattleRenderer.animateEnemyAttack(player, enemy, difficulty.getEnemyDamageMultiplier());
                        if (player.isDefeated()) {
                            BattleRenderer.animatePlayerDefeated(player, enemy);
                            return false;
                        }
                    }
                }
                case "2" -> handleDebugPowers(enemy);
                case "3", "5" -> handleInventory(enemy);
                case "4" -> {
                    return false;
                }
                default -> {}
            }
        }
        return !player.isDefeated();
    }

    void showHowToPlay() {
        Ansi.clearScreen();
        Ansi.holoBanner("🎮", "HOW TO PLAY & SPELLS GUIDE", "Tactical Manual for JVM Bug Eradication", Ansi.BRIGHT_YELLOW);
        System.out.println();
        
        Ansi.holoBadgeLine("01: COMBAT", "Choose [1] Cast Code Spell to answer Java quizzes and attack!", Ansi.BRIGHT_YELLOW, Ansi.WHITE);
        Ansi.holoLine("     • Correct answers deal heavy damage, animate your hero, and increase COMBO STREAK.");
        Ansi.holoLine("     • Wrong answers trigger enemy evasion, analysis debrief, and counter-attacks.");
        System.out.println();

        Ansi.holoBadgeLine("02: POWERS", "Spend Mana (MP) to manipulate runtime conditions:", Ansi.BRIGHT_CYAN, Ansi.WHITE);
        Ansi.holoLine("     • Refactor Heal (20 MP): Cleans up code and heals 35 HP.");
        Ansi.holoLine("     • PrintStackTrace Hint (15 MP): Discards 2 wrong options via breakpoint.");
        Ansi.holoLine("     • Try-Catch Shield (15 MP): Deploys an exception barrier halving next incoming damage.");
        System.out.println();

        Ansi.holoBadgeLine("03: PROGRESS", "Ascend through 6 distinct layers of the Java Virtual Machine:", Ansi.BRIGHT_GREEN, Ansi.WHITE);
        Ansi.holoLine("     • Level 1: Syntax, Primitives & Immutability (NullPointer Imp)");
        Ansi.holoLine("     • Level 2: Loops, Branching & Logic (Infinite Loop Serpent)");
        Ansi.holoLine("     • Level 3: Arrays, Memory & Collections (IndexOutOfBounds Golem)");
        Ansi.holoLine("     • Level 4: OOP, Inheritance & Polymorphism (Spaghetti Code Chimera)");
        Ansi.holoLine("     • Level 5: Exceptions, GC & Memory Leaks (Memory Leak Hydra)");
        Ansi.holoLine("     • Level 6: Recursion, Streams & Generics (The StackOverflow Dragon)");
        System.out.println();
        pressEnterToContinue();
    }

    void showJavaCheatSheet() {
        Ansi.clearScreen();
        Ansi.holoBanner("📖", "JAVA QUICK STUDY GUIDE & LORE", "Key Architectural Principles for the Battleground", Ansi.BRIGHT_CYAN);
        System.out.println();

        Ansi.holoBadgeLine("PRIMITIVES", "byte, short, int, long, float, double, boolean, char stored on stack.", Ansi.BRIGHT_CYAN, Ansi.WHITE);
        Ansi.holoLine("     • Objects and Arrays are dynamically allocated on the Heap.");
        Ansi.holoLine("     • Strings are immutable; modifying creates a new object in the String Pool.");
        System.out.println();

        Ansi.holoBadgeLine("EQUALITY", "'==' checks reference address identity; '.equals()' compares logical values.", Ansi.BRIGHT_YELLOW, Ansi.WHITE);
        Ansi.holoLine("     • Always override 'hashCode()' whenever overriding 'equals()'.");
        System.out.println();

        Ansi.holoBadgeLine("CONTROL", "'break' exits loops immediately; 'continue' jumps to the next cycle.", Ansi.BRIGHT_CYAN, Ansi.WHITE);
        Ansi.holoLine("     • '&&' and '||' short-circuit: right operands are skipped when outcome is guaranteed.");
        System.out.println();

        Ansi.holoBadgeLine("OOP", "Overloading is compile-time (same name, different arguments).", Ansi.BRIGHT_YELLOW, Ansi.WHITE);
        Ansi.holoLine("     • Overriding is runtime polymorphism (subclass replaces inherited behavior).");
        System.out.println();

        Ansi.holoBadgeLine("EXCEPTIONS", "'try' encloses risky code, 'catch' intercepts specific throwables.", Ansi.BRIGHT_RED, Ansi.WHITE);
        Ansi.holoLine("     • 'finally' always executes, ensuring vital resources are deallocated.");
        System.out.println();
        pressEnterToContinue();
    }

    void continueSavedGame() {
        SaveManager.SaveData save = SaveManager.loadGame();
        if (save == null) {
            Ansi.holoBadgeLine("ERROR", "No saved campaign found.", Ansi.RED, Ansi.BRIGHT_RED);
            Ansi.sleep(250);
            return;
        }
        player = new Player(save.playerName());
        save.applyTo(player);
        currentLevel = save.currentLevel();
        if (save.difficulty() != null) {
            this.difficulty = save.difficulty();
        }

        Ansi.clearScreen();
        Ansi.holoBanner("💾", "CAMPAIGN CHECKPOINT RESTORED", "Resuming mission from disk storage", Ansi.BRIGHT_MAGENTA);
        System.out.println();
        Ansi.holoBadgeLine("HERO", player.getName() + " (Level " + player.getLevel() + ")", Ansi.BRIGHT_CYAN, Ansi.WHITE);
        Ansi.holoBadgeLine("HP/MANA", player.getHp() + "/" + player.getMaxHp() + " HP | " + player.getMana() + "/" + player.getMaxMana() + " MP", Ansi.BRIGHT_GREEN, Ansi.WHITE);
        Ansi.holoBadgeLine("STAGE", "Resuming at JVM Layer: Level " + currentLevel, Ansi.BRIGHT_YELLOW, Ansi.BRIGHT_WHITE + Ansi.BOLD);
        Ansi.holoBadgeLine("DIFFICULTY", this.difficulty.getDisplayName(), Ansi.BRIGHT_CYAN, Ansi.WHITE);
        Ansi.holoBadgeLine("BACKPACK", player.getInventory().getTotalItemCount() + " items ready in Dev Backpack", Ansi.BRIGHT_MAGENTA, Ansi.WHITE);
        System.out.println();
        pressEnterToContinue();

        runCampaignLoop();
    }

    void startNewGame() {
        Ansi.clearScreen();
        Ansi.holoBanner("⚔️", "WELCOME TO THE BYTECODE REALM", "Kernel initialized. Prepare for deployment.", Ansi.BRIGHT_YELLOW);
        System.out.println();
        String name = prompt("Enter your Programmer Name (or press Enter for 'Byte Paladin'): ");
        if (name.trim().isEmpty()) {
            name = "Byte Paladin";
        }
        player = new Player(name);
        player.getInventory().addItem(Item.COFFEE_BREW);
        player.getInventory().addItem(Item.CLEAN_ARCH_SHIELD);
        currentLevel = 1;
        hintUsesInCampaign = 0;
        coffeeUsesInCampaign = 0;
        consecutiveHardQuestions = 0;
        healUsedInCurrentBattle = false;

        System.out.println();
        Ansi.holoBadgeLine("INITIALIZATION", "Hero registered: " + player.getName() + " | Level 1 Byte Paladin", Ansi.BRIGHT_GREEN, Ansi.BRIGHT_WHITE + Ansi.BOLD);
        Ansi.holoBadgeLine("PROFILE", "Difficulty: " + this.difficulty.getDisplayName(), Ansi.BRIGHT_YELLOW, Ansi.BRIGHT_WHITE + Ansi.BOLD);
        Ansi.holoBadgeLine("INVENTORY", "Starter pack deployed: 1x Coffee Brew ☕, 1x Clean Arch Shield 🛡️", Ansi.BRIGHT_MAGENTA, Ansi.WHITE);
        Ansi.holoBadgeLine("STATUS", "Bytecode engine running. Entering the Syntax Swamp...", Ansi.BRIGHT_CYAN, Ansi.WHITE);
        Ansi.sleep(250);

        runCampaignLoop();
    }

    void runCampaignLoop() {
        while (currentLevel <= MAX_LEVEL) {
            boolean levelCleared = runLevel(currentLevel);
            if (levelCleared) {
                if (currentLevel < MAX_LEVEL) {
                    showLevelTransitionScreen(currentLevel);
                    currentLevel++;
                    player.levelUp(currentLevel);
                    promptTalentSelection();
                    SaveManager.saveGame(player, currentLevel, difficulty);
                } else {
                    SaveManager.deleteSaveGame();
                    showGameWonScreen();
                    return;
                }
            } else {
                boolean retry = showGameOverScreen();
                if (retry) {
                    player.heal(player.getMaxHp());
                    player.restoreMana(player.getMaxMana());
                    System.out.println();
                    Ansi.holoBadgeLine("RECOMPILE", "Recompiling codebase... Retrying Level " + currentLevel + "!", Ansi.BRIGHT_GREEN, Ansi.BRIGHT_WHITE + Ansi.BOLD);
                    Ansi.sleep(250);
                } else {
                    return;
                }
            }
        }
    }

    boolean runLevel(int level) {
        healUsedInCurrentBattle = false;
        Enemy enemy = Enemy.createForLevel(level);
        List<Question> questions = QuestionBank.getRandomizedQuestionsForLevel(level);
        int questionIndex = 0;
        Question lastQuestion = null;

        showLevelIntro(enemy);
        BattleRenderer.playIdleAnimation(player, enemy, 1);

        while (!enemy.isDefeated() && !player.isDefeated()) {
            enemy.incrementTurnCount();
            if (enemy.getMechanic() == Enemy.BossMechanic.CPU_THROTTLE && player.getMana() > 0) {
                int drain = Math.min(5, player.getMana());
                player.spendMana(drain);
                Ansi.holoBadgeLine("CPU THROTTLE", "Infinite Loop Serpent drained " + drain + " MP in an endless loop!", Ansi.BRIGHT_GREEN, Ansi.WHITE);
            }
            if (enemy.getMechanic() == Enemy.BossMechanic.HEAP_OVERFLOW && enemy.getCurrentHp() < enemy.getMaxHp()) {
                enemy.heal(5);
                Ansi.holoBadgeLine("HEAP OVERFLOW", "Memory Leak Hydra expands JVM Heap, regenerating 5 HP! (" + enemy.getCurrentHp() + "/" + enemy.getMaxHp() + " HP)", Ansi.BRIGHT_RED, Ansi.WHITE);
            }
            if (enemy.getMechanic() == Enemy.BossMechanic.DEEP_RECURSION) {
                enemy.incrementMechanicCounter();
                Ansi.holoBadgeLine("DEEP RECURSION", "StackOverflow Dragon recurses deeper (+3 DMG)! Attack power is now " + enemy.getEffectiveAttackDamage() + " DMG!", Ansi.RED, Ansi.BRIGHT_WHITE + Ansi.BOLD);
            }
            enemy.decrementCouplingLockTurns();

            BattleRenderer.renderFrame(player, enemy, CharacterSprite.PLAYER_IDLE_1, enemy.getIdle1(), null,
                I18n.get("combat.action.prompt"), false, false);

            Ansi.holoOption("1 | C", "⚔️  " + I18n.get("combat.cast"), Ansi.BRIGHT_YELLOW, Ansi.BRIGHT_WHITE + Ansi.BOLD);
            Ansi.holoOption("2 | D", "🧪  " + I18n.get("combat.debug"), Ansi.BRIGHT_CYAN, Ansi.BRIGHT_WHITE + Ansi.BOLD);
            Ansi.holoOption("3 | I", "📖  " + I18n.get("combat.inspect"), Ansi.BRIGHT_CYAN, Ansi.BRIGHT_WHITE + Ansi.BOLD);
            Ansi.holoOption("4 | Q", "🏃  " + I18n.get("combat.surrender"), Ansi.BRIGHT_RED, Ansi.DIM + Ansi.WHITE);
            Ansi.holoOption("5 | B", "🎒  " + I18n.get("combat.backpack") + " (" + player.getInventory().getTotalItemCount() + " items)", Ansi.BRIGHT_MAGENTA, Ansi.BRIGHT_WHITE + Ansi.BOLD);

            String choice;
            try (BattleRenderer.IdleSession anim = BattleRenderer.startIdleAnimation(player, enemy, I18n.get("combat.action.prompt"))) {
                choice = prompt("  Action (1-5 | C/D/I/Q/B): ");
            }
            switch (parseCombatAction(choice)) {
                case "1" -> {
                    // Answer Quiz
                    if (questionIndex >= questions.size()) {
                        // Recycle questions dynamically without immediate repeating question
                        List<Question> nextBatch = QuestionBank.getRandomizedQuestionsForLevel(level);
                        if (nextBatch.size() > 1 && lastQuestion != null && nextBatch.get(0).getPrompt().equals(lastQuestion.getPrompt())) {
                            Collections.swap(nextBatch, 0, 1);
                        }
                        questions = nextBatch;
                        questionIndex = 0;
                    }
                    Question q = questions.get(questionIndex++);
                    lastQuestion = q;

                    // 1. Duel face-off animation of player vs enemy in the arena!
                    BattleRenderer.animateQuizFaceOff(player, enemy);

                    // 2. Handle quiz question with cyber cards and unified speed
                    boolean answeredCorrect = handleQuizQuestion(q, enemy);

                    if (answeredCorrect) {
                        int baseDamage = q.getDamage();
                        int comboBonus = player.getSpellDamageBonus();
                        int totalDamage = calculateTotalDamage(baseDamage, comboBonus, player);
                        boolean isCritical = comboBonus > 0;
                        if (player.isJitBoosterActive()) {
                            totalDamage = (int) Math.round(totalDamage * 2.5);
                            isCritical = true;
                            player.setJitBoosterActive(false);
                            AchievementManager.unlock(Achievement.OVERCLOCKED_JIT);
                            Ansi.holoBadgeLine("JIT COMPILER", "⚡ JIT BOOSTER ACTIVATED! 2.5x Overclock Damage (" + totalDamage + " DMG)!", Ansi.BRIGHT_YELLOW, Ansi.BRIGHT_WHITE + Ansi.BOLD);
                        }

                        BattleRenderer.animatePlayerAttack(player, enemy, totalDamage, isCritical);

                        if (enemy.isDefeated()) {
                            BattleRenderer.animateEnemyDefeated(player, enemy);
                            if (level == 1) {
                                AchievementManager.unlock(Achievement.FIRST_COMMIT);
                            }
                            if (!healUsedInCurrentBattle) {
                                AchievementManager.unlock(Achievement.ZERO_ALLOCATION);
                            }
                            if (player.getHp() <= 10 && player.getHp() > 0) {
                                AchievementManager.unlock(Achievement.STACK_TRACE_SURVIVOR);
                            }
                            return true;
                        }
                    } else {
                        // Enemy counter attack!
                        BattleRenderer.animateEnemyAttack(player, enemy, difficulty.getEnemyDamageMultiplier());
                        if (player.isDefeated()) {
                            BattleRenderer.animatePlayerDefeated(player, enemy);
                            return false;
                        }
                    }
                }

                case "2" -> handleDebugPowers(enemy);

                case "3" -> showEnemyInspect(enemy);

                case "4" -> {
                    Ansi.holoBadgeLine("RETREAT", "You retreated from the battle!", Ansi.YELLOW, Ansi.BRIGHT_YELLOW);
                    return false;
                }

                case "5" -> handleInventory(enemy);

                default -> {
                    Ansi.holoBadgeLine("ERROR", "Invalid option! Press Enter to retry.", Ansi.RED, Ansi.BRIGHT_RED);
                    scanner.nextLine();
                }
            }
        }

        return !player.isDefeated();
    }

    private static String formatCompactOption(char label, String text, boolean isEliminated, int width) {
        if (isEliminated) {
            String tag = "◈ [" + label + "] ✖ ";
            int maxLen = Math.max(10, width - tag.length() - 2);
            String truncated = (text.length() > maxLen) ? text.substring(0, maxLen - 3) + "..." : text;
            return Ansi.padVisibleRight(Ansi.colorize("  " + tag + truncated, Ansi.DIM + Ansi.RED), width);
        } else {
            String tag = "◈ [" + label + "] ";
            int maxLen = Math.max(10, width - tag.length() - 2);
            String truncated = (text.length() > maxLen) ? text.substring(0, maxLen - 3) + "..." : text;
            String content = Ansi.colorize("  " + tag, Ansi.BRIGHT_YELLOW + Ansi.BOLD) + Ansi.colorize(truncated, Ansi.BRIGHT_WHITE);
            return Ansi.padVisibleRight(content, width);
        }
    }

    boolean handleQuizQuestion(Question q, Enemy enemy) {
        Question activeQ = q;
        if (enemy.getMechanic() == Enemy.BossMechanic.ARRAY_SLICE) {
            activeQ = q.withRandomizedOptions();
            Ansi.holoBadgeLine("ARRAY SLICE", "IndexOutOfBounds Golem sliced and scrambled the option indices!", Ansi.BRIGHT_YELLOW, Ansi.WHITE);
        }

        String statusMsg = "⚔️ [Tópico: " + activeQ.getTopic() + " | Dificuldade: " + activeQ.getDifficultyStars() + "] | Resolva a query!";
        BattleRenderer.renderFrame(player, enemy, CharacterSprite.PLAYER_CHARGE, enemy.getIdle1(), null,
            statusMsg, false, false);

        // Cyber Question Card (placed directly below the Battle Arena)
        if (!Ansi.isCompactMode()) System.out.println();
        int cardWidth = 74;
        String qHeader = "── ❓ [BYTECODE QUERY] ";
        int qVis = Ansi.getVisibleLength(qHeader) + 1; // emoji ❓ occupies 2 terminal cells
        int padQ = Math.max(0, cardWidth - qVis - 2);
        Ansi.holoLine(Ansi.BRIGHT_YELLOW + "  ┌" + qHeader + "─".repeat(padQ) + "┐" + Ansi.RESET);
        Ansi.holoLine(Ansi.BRIGHT_YELLOW + "  │  " + Ansi.colorize(Ansi.padVisibleRight(activeQ.getPrompt(), cardWidth - 6), Ansi.BOLD + Ansi.BRIGHT_WHITE) + Ansi.BRIGHT_YELLOW + "  │" + Ansi.RESET);
        Ansi.holoLine(Ansi.BRIGHT_YELLOW + "  └" + "─".repeat(cardWidth - 2) + "┘" + Ansi.RESET);

        // Display code snippet in cyber source block with line numbers
        if (activeQ.getCodeSnippet() != null && !activeQ.getCodeSnippet().isEmpty()) {
            if (!Ansi.isCompactMode()) System.out.println();
            int codeWidth = 74;
            String cHeader = "── [ Java Source Monitor ] ";
            int padC = Math.max(0, codeWidth - Ansi.getVisibleLength(cHeader) - 2);
            Ansi.holoLine(Ansi.BRIGHT_BLACK + "  ┌" + cHeader + "─".repeat(padC) + "┐" + Ansi.RESET);
            for (int lineIdx = 0; lineIdx < activeQ.getCodeSnippet().size(); lineIdx++) {
                String line = activeQ.getCodeSnippet().get(lineIdx);
                String lineNum = String.format("%02d", lineIdx + 1);
                String prefix = Ansi.colorize("  │  " + lineNum + " │ ", Ansi.BRIGHT_BLACK);
                String codeText = Ansi.colorize(line, Ansi.BRIGHT_CYAN);
                String padded = Ansi.padVisibleRight(prefix + codeText, codeWidth - 1) + Ansi.colorize("│", Ansi.BRIGHT_BLACK);
                Ansi.holoLine(padded);
            }
            Ansi.holoLine(Ansi.BRIGHT_BLACK + "  └" + "─".repeat(codeWidth - 2) + "┘" + Ansi.RESET);
        }

        if (!Ansi.isCompactMode()) System.out.println();

        // If hint is active, eliminate 2 random wrong options!
        List<Integer> eliminated = new ArrayList<>();
        if (hintActiveNextQuestion) {
            hintActiveNextQuestion = false;
            List<Integer> wrongIndices = new ArrayList<>();
            for (int i = 0; i < activeQ.getOptions().size(); i++) {
                if (i != activeQ.getCorrectOptionIndex()) {
                    wrongIndices.add(i);
                }
            }
            Collections.shuffle(wrongIndices);
            for (int i = 0; i < Math.min(2, wrongIndices.size()); i++) {
                eliminated.add(wrongIndices.get(i));
            }
            Ansi.holoBadgeLine("BREAKPOINT", "2 incorrect options discarded by debugger!", Ansi.BRIGHT_YELLOW, Ansi.BRIGHT_WHITE + Ansi.BOLD);
            System.out.println();
        }

        // Staggered holographic reveal for choices (2-column in compact mode, single column in standard)
        char[] labels = {'A', 'B', 'C', 'D'};
        if (Ansi.isCompactMode()) {
            for (int i = 0; i < activeQ.getOptions().size(); i += 2) {
                String opt1 = formatCompactOption(labels[i], activeQ.getOptions().get(i), eliminated.contains(i), 36);
                String opt2 = (i + 1 < activeQ.getOptions().size()) 
                    ? formatCompactOption(labels[i + 1], activeQ.getOptions().get(i + 1), eliminated.contains(i + 1), 36)
                    : "";
                Ansi.holoLine(opt1 + " " + opt2);
            }
        } else {
            for (int i = 0; i < activeQ.getOptions().size(); i++) {
                Ansi.holoChoice(labels[i], activeQ.getOptions().get(i), eliminated.contains(i));
            }
        }

        if (!Ansi.isCompactMode()) System.out.println();
        int userChoice = -1;
        try (BattleRenderer.IdleSession anim = BattleRenderer.startIdleAnimation(player, enemy,
                "⚔️ DUEL IN PROGRESS: Select A, B, C, or D to cast your spell!")) {
            while (userChoice == -1) {
                String input = prompt("  Select your answer (A, B, C, or D): ");
                userChoice = parseQuizChoice(input);
                if (userChoice == -1) {
                    System.out.println(Ansi.colorize("  Please enter A, B, C, or D.", Ansi.RED));
                }
            }
        }

        boolean correct = (userChoice == activeQ.getCorrectOptionIndex());
        player.registerAnswer(correct);
        if (correct) {
            enemy.recordPlayerHit();
            if (activeQ.getDifficulty() >= 4) {
                consecutiveHardQuestions++;
                if (consecutiveHardQuestions >= 3) {
                    AchievementManager.unlock(Achievement.CONCURRENCY_WIZARD);
                }
            } else {
                consecutiveHardQuestions = 0;
            }
        } else {
            consecutiveHardQuestions = 0;
            missedQuestions.add(new MissedQuestion(activeQ, userChoice, enemy.getLevel()));
            enemy.recordPlayerMiss();
            if (enemy.getMechanic() == Enemy.BossMechanic.COUPLING_LOCK) {
                enemy.setCouplingLockTurns(2);
                Ansi.holoBadgeLine("COUPLING LOCK", "Spaghetti Chimera tangled your architecture! Refactor Heal is locked for 2 turns!", Ansi.BRIGHT_CYAN, Ansi.WHITE);
            }
        }

        System.out.println();
        if (correct) {
            Ansi.holoBanner("✅", "BYTECODE PASS: COMPILER PASSED WITH 0 ERRORS!", null, Ansi.BRIGHT_GREEN);
            Ansi.holoBadgeLine("ANALYSIS", activeQ.getExplanation(), Ansi.BRIGHT_YELLOW, Ansi.WHITE);
            if (activeQ.getJlsReference() != null && !activeQ.getJlsReference().isEmpty()) {
                Ansi.holoBadgeLine("JLS REF", activeQ.getJlsReference(), Ansi.BRIGHT_CYAN, Ansi.WHITE);
            }
            if (player.getComboStreak() > 1) {
                Ansi.holoBadgeLine("COMBO STREAK", "🔥 x" + player.getComboStreak() + " ACTIVE! (+BONUS SPELL DAMAGE)", Ansi.BRIGHT_YELLOW, Ansi.BRIGHT_WHITE + Ansi.BOLD);
            }
            try (BattleRenderer.IdleSession anim = BattleRenderer.startIdleAnimation(player, enemy,
                    "✨ COMPILER PASSED: Press Enter to cast Bytecode Spell!")) {
                pressEnterToContinue("\n  [Press Enter to cast bytecode spell!]");
            }
        } else {
            Ansi.holoBanner("❌", "RUNTIME ERROR: COMPILATION / EXECUTION EXCEPTION!", null, Ansi.BRIGHT_RED);
            Ansi.holoBadgeLine("CORRECT SOLUTION", "[" + labels[activeQ.getCorrectOptionIndex()] + "] " + activeQ.getOptions().get(activeQ.getCorrectOptionIndex()), Ansi.BRIGHT_GREEN, Ansi.BRIGHT_WHITE + Ansi.BOLD);
            Ansi.holoBadgeLine("ANALYSIS", activeQ.getExplanation(), Ansi.BRIGHT_YELLOW, Ansi.WHITE);
            if (activeQ.getJlsReference() != null && !activeQ.getJlsReference().isEmpty()) {
                Ansi.holoBadgeLine("JLS REF", activeQ.getJlsReference(), Ansi.BRIGHT_CYAN, Ansi.WHITE);
            }
            Ansi.holoBadgeLine("COUNTER-ATTACK", enemy.getName() + " exploits your bug and strikes!", Ansi.BRIGHT_RED, Ansi.RED + Ansi.BOLD);
            try (BattleRenderer.IdleSession anim = BattleRenderer.startIdleAnimation(player, enemy,
                    "💥 RUNTIME ERROR: Press Enter to brace for impact!")) {
                pressEnterToContinue("\n  [Press Enter to brace for impact!]");
            }
        }

        return correct;
    }

    void handleDebugPowers(Enemy enemy) {
        while (true) {
            BattleRenderer.renderFrame(player, enemy, CharacterSprite.PLAYER_IDLE_1, enemy.getIdle1(), null,
                "Debug Powers Menu: Spend Mana to manipulate the runtime!", false, false);

            int healCost = getSpellCost(20);
            int hintCost = getSpellCost(difficulty.getHintManaCost());
            int shieldCost = getSpellCost(15);

            Ansi.holoOption("A | 1", String.format("💚 Refactor Code (Cost: %d MP) -> Restores 35 HP (Current HP: %d/%d)",
                healCost, player.getHp(), player.getMaxHp()), Ansi.BRIGHT_GREEN, Ansi.WHITE);
            Ansi.holoOption("B | 2", String.format("🔍 PrintStackTrace Hint (Cost: %d MP) -> Eliminates 2 wrong answers on next quiz",
                hintCost), Ansi.BRIGHT_YELLOW, Ansi.WHITE);
            Ansi.holoOption("C | 3", String.format("🛡️ Try-Catch Shield (Cost: %d MP) -> Halves next enemy hit (Active: %s)",
                shieldCost, player.isShieldActive() ? "YES" : "NO"), Ansi.BRIGHT_CYAN, Ansi.WHITE);
            Ansi.holoOption("D | 4", "↩️ Return to Battle Menu", Ansi.DIM + Ansi.WHITE, Ansi.DIM + Ansi.WHITE);

            String choice;
            try (BattleRenderer.IdleSession anim = BattleRenderer.startIdleAnimation(player, enemy,
                    "Debug Powers Menu: Spend Mana to manipulate the runtime!")) {
                choice = prompt("  Choose spell (A-D | 1-4): ");
            }
            switch (parseDebugPowerChoice(choice)) {
                case "A" -> {
                    if (enemy.getCouplingLockTurns() > 0) {
                        Ansi.holoBadgeLine("COUPLING LOCK", "Refactor Heal is locked by Spaghetti Chimera (" + enemy.getCouplingLockTurns() + " turns remaining)! Use a GC Scroll to purge.", Ansi.RED, Ansi.BRIGHT_RED);
                        Ansi.sleep(250);
                    } else if (player.spendMana(healCost)) {
                        healUsedInCurrentBattle = true;
                        BattleRenderer.renderHealAnimation(player, enemy, 35);
                        return;
                    } else {
                        Ansi.holoBadgeLine("MANA ERROR", "Not enough Mana! Need " + healCost + " MP.", Ansi.RED, Ansi.BRIGHT_RED);
                        Ansi.sleep(250);
                    }
                }
                case "B" -> {
                    if (hintActiveNextQuestion) {
                        Ansi.holoBadgeLine("WARN", "Hint is already primed for the next quiz!", Ansi.YELLOW, Ansi.BRIGHT_YELLOW);
                        Ansi.sleep(250);
                    } else if (player.spendMana(hintCost)) {
                        hintActiveNextQuestion = true;
                        hintUsesInCampaign++;
                        if (hintUsesInCampaign >= 5) {
                            AchievementManager.unlock(Achievement.BUG_HUNTER);
                        }
                        Ansi.holoBadgeLine("BREAKPOINT", "Debugger breakpoint set! 2 wrong answers will be discarded next quiz.", Ansi.BRIGHT_YELLOW, Ansi.BRIGHT_WHITE + Ansi.BOLD);
                        Ansi.sleep(250);
                        return;
                    } else {
                        Ansi.holoBadgeLine("MANA ERROR", "Not enough Mana! Need " + hintCost + " MP.", Ansi.RED, Ansi.BRIGHT_RED);
                        Ansi.sleep(250);
                    }
                }
                case "C" -> {
                    if (player.isShieldActive()) {
                        Ansi.holoBadgeLine("WARN", "Try-Catch Shield is already active!", Ansi.YELLOW, Ansi.BRIGHT_YELLOW);
                        Ansi.sleep(250);
                    } else if (player.spendMana(shieldCost)) {
                        BattleRenderer.renderShieldAnimation(player, enemy);
                        return;
                    } else {
                        Ansi.holoBadgeLine("MANA ERROR", "Not enough Mana! Need " + shieldCost + " MP.", Ansi.RED, Ansi.BRIGHT_RED);
                        Ansi.sleep(250);
                    }
                }
                case "D" -> {
                    return;
                }
                default -> {
                    Ansi.holoBadgeLine("ERROR", "Invalid option.", Ansi.RED, Ansi.BRIGHT_RED);
                    Ansi.sleep(250);
                }
            }
        }
    }

    public static List<Item> getDropsForLevel(int level) {
        return switch (level) {
            case 1 -> List.of(Item.COFFEE_BREW, Item.CLEAN_ARCH_SHIELD);
            case 2 -> List.of(Item.JIT_BOOSTER, Item.COFFEE_BREW);
            case 3 -> List.of(Item.GC_SCROLL, Item.COFFEE_BREW);
            case 4 -> List.of(Item.CLEAN_ARCH_SHIELD, Item.JIT_BOOSTER);
            case 5 -> List.of(Item.BREAKPOINT_TOKEN, Item.COFFEE_BREW);
            default -> List.of(Item.COFFEE_BREW);
        };
    }

    void handleInventory(Enemy enemy) {
        while (true) {
            BattleRenderer.renderFrame(player, enemy, CharacterSprite.PLAYER_IDLE_1, enemy.getIdle1(), null,
                "Dev Backpack: Select an item to use or 0 to return.", false, false);

            Inventory inv = player.getInventory();
            List<Item> availableItems = new ArrayList<>();
            for (Item item : Item.values()) {
                if (inv.getCount(item) > 0) {
                    availableItems.add(item);
                }
            }

            if (availableItems.isEmpty()) {
                System.out.println();
                Ansi.holoBadgeLine("BACKPACK EMPTY", "Your Dev Backpack contains no items! Defeat bosses to earn loot drops.", Ansi.YELLOW, Ansi.WHITE);
                System.out.println();
                pressEnterToContinue("  [Press Enter to return to battle menu...]");
                return;
            }

            System.out.println();
            for (int i = 0; i < availableItems.size(); i++) {
                Item item = availableItems.get(i);
                int count = inv.getCount(item);
                String tag = (i + 1) + " | " + item.getCode();
                String desc = item.getIcon() + " " + item.getDisplayName() + " [x" + count + "] - " + item.getDescription();
                Ansi.holoOption(tag, desc, Ansi.BRIGHT_MAGENTA, Ansi.WHITE);
            }
            Ansi.holoOption("0 | Q", "↩️ Return to Battle Menu", Ansi.DIM + Ansi.WHITE, Ansi.DIM + Ansi.WHITE);

            String choice;
            try (BattleRenderer.IdleSession anim = BattleRenderer.startIdleAnimation(player, enemy,
                    "Dev Backpack: Choose item (1-" + availableItems.size() + " | 0 to return):")) {
                choice = prompt("  Select item: ");
            }
            if (choice == null || choice.trim().equalsIgnoreCase("0") || choice.trim().equalsIgnoreCase("Q") ||
                choice.trim().equalsIgnoreCase("BACK") || choice.trim().equalsIgnoreCase("RETURN")) {
                return;
            }

            Item selectedItem = null;
            try {
                int idx = Integer.parseInt(choice.trim()) - 1;
                if (idx >= 0 && idx < availableItems.size()) {
                    selectedItem = availableItems.get(idx);
                }
            } catch (NumberFormatException e) {
                selectedItem = Item.fromCode(choice.trim());
                if (selectedItem != null && !inv.hasItem(selectedItem)) {
                    selectedItem = null;
                }
            }

            if (selectedItem == null) {
                Ansi.holoBadgeLine("ERROR", "Invalid item selection.", Ansi.RED, Ansi.BRIGHT_RED);
                Ansi.sleep(250);
                continue;
            }

            switch (selectedItem) {
                case COFFEE_BREW -> {
                    inv.consumeItem(Item.COFFEE_BREW);
                    player.restoreMana(40);
                    coffeeUsesInCampaign++;
                    if (coffeeUsesInCampaign >= 3) {
                        AchievementManager.unlock(Achievement.COFFEE_ADDICT);
                    }
                    AudioEngine.playHeal();
                    Ansi.holoBadgeLine("COFFEE BREW", "☕ Drank hot Java Brew! Restored 40 MP without losing your turn!", Ansi.BRIGHT_GREEN, Ansi.BRIGHT_WHITE + Ansi.BOLD);
                    Ansi.sleep(300);
                    return;
                }
                case GC_SCROLL -> {
                    inv.consumeItem(Item.GC_SCROLL);
                    hintActiveNextQuestion = true;
                    enemy.setCouplingLockTurns(0);
                    AudioEngine.playShield();
                    Ansi.holoBadgeLine("GC SCROLL", "📜 Garbage Collection run! 2 wrong answers eliminated next quiz & debuffs cleared!", Ansi.BRIGHT_CYAN, Ansi.BRIGHT_WHITE + Ansi.BOLD);
                    Ansi.sleep(300);
                    return;
                }
                case JIT_BOOSTER -> {
                    if (player.isJitBoosterActive()) {
                        Ansi.holoBadgeLine("WARN", "JIT Booster is already overclocked!", Ansi.YELLOW, Ansi.BRIGHT_YELLOW);
                        Ansi.sleep(250);
                    } else {
                        inv.consumeItem(Item.JIT_BOOSTER);
                        player.setJitBoosterActive(true);
                        AudioEngine.playCriticalHit();
                        Ansi.holoBadgeLine("JIT BOOSTER", "⚡ JIT Compiler primed! Next correct bytecode query deals 2.5x critical damage!", Ansi.BRIGHT_YELLOW, Ansi.BRIGHT_WHITE + Ansi.BOLD);
                        Ansi.sleep(300);
                        return;
                    }
                }
                case CLEAN_ARCH_SHIELD -> {
                    if (player.isCleanArchShieldActive()) {
                        Ansi.holoBadgeLine("WARN", "Clean Architecture Shield is already active!", Ansi.YELLOW, Ansi.BRIGHT_YELLOW);
                        Ansi.sleep(250);
                    } else {
                        inv.consumeItem(Item.CLEAN_ARCH_SHIELD);
                        player.setCleanArchShieldActive(true);
                        AudioEngine.playShield();
                        Ansi.holoBadgeLine("CLEAN SHIELD", "🛡️ Clean Architecture deployed! 100% absorption on next incoming bug attack!", Ansi.BRIGHT_GREEN, Ansi.BRIGHT_WHITE + Ansi.BOLD);
                        Ansi.sleep(300);
                        return;
                    }
                }
                case BREAKPOINT_TOKEN -> {
                    Ansi.holoBadgeLine("PASSIVE ITEM", "🛑 Breakpoint Token is a passive safety net held in inventory. It will automatically intercept fatal damage and revive you with 25% HP!", Ansi.BRIGHT_YELLOW, Ansi.WHITE);
                    Ansi.sleep(400);
                }
            }
        }
    }

    void showEnemyInspect(Enemy enemy) {
        Ansi.clearScreen();
        Ansi.holoLine(Ansi.colorize("╔══════════════════════════════════════════════════════════════════════╗", enemy.getColor()));
        Ansi.holoLine(Ansi.colorize(String.format("║ MONSTER DOSSIER: %-51s ║", enemy.getName()), enemy.getColor() + Ansi.BOLD));
        Ansi.holoLine("╠══════════════════════════════════════════════════════════════════════╣");
        Ansi.holoLine(String.format("║ Title:       %-55s ║", enemy.getTitle()));
        Ansi.holoLine(String.format("║ Level:       %-55s ║", String.valueOf(enemy.getLevel())));
        Ansi.holoLine(String.format("║ Health:      %-55s ║", enemy.getCurrentHp() + " / " + enemy.getMaxHp() + " HP"));
        Ansi.holoLine(String.format("║ Attack:      %-55s ║", enemy.getAttackName() + " (" + enemy.getAttackDamage() + " DMG)"));
        Ansi.holoLine("╠══════════════════════════════════════════════════════════════════════╣");
        Ansi.holoLine(String.format("║ Lore:        %-55s ║", enemy.getDescription()));
        Ansi.holoLine(Ansi.colorize("╚══════════════════════════════════════════════════════════════════════╝", enemy.getColor()));
        System.out.println();
        pressEnterToContinue();
        Ansi.clearScreen();
    }

    void showLevelIntro(Enemy enemy) {
        // 1. Stage Appearing animation (runs at unified 25ms speed)
        BattleRenderer.animateStageIntroEnter(enemy.getLevel(), enemy);

        // 2. Holographic stage dossier with cyber badges (25ms each, NOT typing!)
        System.out.println();
        Ansi.holoBadgeLine("KERNEL ALERT", "Hostile bug process detected on thread 'main'!", Ansi.BRIGHT_YELLOW, Ansi.WHITE);
        Ansi.holoBadgeLine("STAGE BOSS", enemy.getName() + " (" + enemy.getTitle() + ")", enemy.getColor(), enemy.getColor() + Ansi.BOLD);
        Ansi.holoBadgeLine("THREAT STATS", enemy.getCurrentHp() + " HP | Special Attack: " + enemy.getAttackName() + " (" + enemy.getAttackDamage() + " DMG)", Ansi.BRIGHT_RED, Ansi.BRIGHT_WHITE);
        Ansi.holoBadgeLine("TACTICAL INTEL", enemy.getDescription(), Ansi.BRIGHT_CYAN, Ansi.WHITE);
        Ansi.holoBadgeLine("MISSION", "Prepare your syntax and compile bytecode to defeat the bug!", Ansi.BRIGHT_GREEN, Ansi.BRIGHT_WHITE + Ansi.BOLD);
        System.out.println();
        pressEnterToContinue();

        // 3. Stage Exiting animation (runs at unified 25ms speed)
        BattleRenderer.animateStageIntroExit(enemy.getLevel(), enemy);
        Ansi.clearScreen();
    }

    void showLevelTransitionScreen(int completedLevel) {
        Ansi.clearScreen();
        Ansi.beep();
        Ansi.holoBanner("🏆", "STAGE CLEAR! LEVEL UP!", "Layer of the JVM Purged", Ansi.BRIGHT_GREEN);
        System.out.println();
        Ansi.holoBadgeLine("VICTOR", player.getName() + " conquered Level " + completedLevel + "!", Ansi.BRIGHT_GREEN, Ansi.BRIGHT_WHITE + Ansi.BOLD);
        Ansi.holoBadgeLine("HP UPGRADE", "Max HP increased by +20! (Full Heal)", Ansi.BRIGHT_GREEN, Ansi.WHITE);
        Ansi.holoBadgeLine("MANA UPGRADE", "Max Mana increased by +15! (Full Mana Recharge)", Ansi.BRIGHT_CYAN, Ansi.WHITE);
        Ansi.holoBadgeLine("TOTAL XP", String.valueOf(player.getXp()), Ansi.BRIGHT_YELLOW, Ansi.BRIGHT_YELLOW + Ansi.BOLD);
        Ansi.holoBadgeLine("MAX STREAK", String.valueOf(player.getHighestStreak()), Ansi.BRIGHT_YELLOW, Ansi.WHITE);
        System.out.println();
        List<Item> drops = getDropsForLevel(completedLevel);
        for (Item item : drops) {
            player.getInventory().addItem(item);
            Ansi.holoBadgeLine("LOOT DROP", item.getIcon() + " Acquired: " + item.getDisplayName() + "!", Ansi.BRIGHT_MAGENTA, Ansi.BRIGHT_WHITE + Ansi.BOLD);
        }
        System.out.println();
        Ansi.holoLine("  " + Ansi.colorize("Advancing to the next layer of the JVM...", Ansi.BRIGHT_CYAN));
        System.out.println();
        pressEnterToContinue();
        Ansi.clearScreen();
    }

    void promptTalentSelection() {
        if (player == null) return;
        Set<Talent> currentTalents = player.getTalents();
        List<Talent> available = new ArrayList<>();
        for (Talent t : Talent.values()) {
            if (!currentTalents.contains(t)) {
                available.add(t);
            }
        }

        if (available.isEmpty()) {
            player.setMaxHp(player.getMaxHp() + 15);
            player.setHp(player.getMaxHp());
            player.setMaxMana(player.getMaxMana() + 15);
            player.setMana(player.getMaxMana());
            Ansi.holoBadgeLine("PERK OVERCLOCK", "All JVM perks unlocked! Gained +15 Max HP and +15 Max MP overload!", Ansi.BRIGHT_MAGENTA, Ansi.BRIGHT_WHITE + Ansi.BOLD);
            pressEnterToContinue();
            return;
        }

        Collections.shuffle(available);
        List<Talent> choices = available.subList(0, Math.min(3, available.size()));

        Ansi.holoBanner("🧬", "CHOOSE A RUNTIME TALENT PERK", "Select a permanent JVM enhancement", Ansi.BRIGHT_MAGENTA);
        System.out.println();
        for (int i = 0; i < choices.size(); i++) {
            Talent t = choices.get(i);
            String tag = (i + 1) + " | " + t.getCode();
            String desc = t.getIcon() + " " + t.getDisplayName() + ": " + t.getDescription();
            Ansi.holoOption(tag, desc, Ansi.BRIGHT_CYAN, Ansi.WHITE);
        }
        System.out.println();

        String input = prompt("  Select Talent Perk (1-" + choices.size() + "): ");
        int selectedIndex = 0;
        try {
            int parsed = Integer.parseInt(input.trim()) - 1;
            if (parsed >= 0 && parsed < choices.size()) {
                selectedIndex = parsed;
            }
        } catch (Exception e) {
            Talent fromCode = Talent.fromCode(input.trim());
            if (fromCode != null && choices.contains(fromCode)) {
                selectedIndex = choices.indexOf(fromCode);
            }
        }

        Talent chosen = choices.get(selectedIndex);
        player.addTalent(chosen);
        AudioEngine.playCriticalHit();
        System.out.println();
        Ansi.holoBadgeLine("TALENT UNLOCKED", chosen.getIcon() + " " + chosen.getDisplayName() + " permanently acquired!", Ansi.BRIGHT_GREEN, Ansi.BRIGHT_WHITE + Ansi.BOLD);
        Ansi.holoBadgeLine("EFFECT", chosen.getDescription(), Ansi.BRIGHT_YELLOW, Ansi.WHITE);
        System.out.println();
        pressEnterToContinue();
    }

    boolean showGameOverScreen() {
        Ansi.clearScreen();
        String banner = """
                ██████╗ ██╗   ██╗██╗██╗     ██████╗ 
                ██╔══██╗██║   ██║██║██║     ██╔══██╗
                ██████╔╝██║   ██║██║██║     ██║  ██║
                ██╔══██╗██║   ██║██║██║     ██║  ██║
                ██████╔╝╚██████╔╝██║███████╗██████╔╝
                ╚═════╝  ╚═════╝ ╚═╝╚══════╝╚═════╝ 

              ███████╗ █████╗ ██╗██╗     ███████╗██████╗ 
              ██╔════╝██╔══██╗██║██║     ██╔════╝██╔══██╗
              █████╗  ███████║██║██║     █████╗  ██║  ██║
              ██╔══╝  ██╔══██║██║██║     ██╔══╝  ██║  ██║
              ██║     ██║  ██║██║███████╗███████╗██████╔╝
              ╚═╝     ╚═╝  ╚═╝╚═╝╚══════╝╚══════╝╚═════╝ 
        """;
        for (String line : banner.split("\n")) {
            if (!line.trim().isEmpty()) {
                Ansi.holoLine(Ansi.colorize(line, Ansi.BRIGHT_RED));
            }
        }

        Ansi.holoLine(Ansi.center(Ansi.colorize("Process exited with code 1. A bug overwhelmed your defenses.", Ansi.BRIGHT_RED + Ansi.BOLD), 78));
        System.out.println();
        Ansi.holoBadgeLine("QUESTIONS", String.valueOf(player.getQuestionsAnswered()), Ansi.BRIGHT_CYAN, Ansi.WHITE);
        Ansi.holoBadgeLine("SOLUTIONS", String.valueOf(player.getCorrectAnswers()), Ansi.BRIGHT_GREEN, Ansi.WHITE);
        double accuracy = player.getQuestionsAnswered() > 0 
            ? ((double) player.getCorrectAnswers() / player.getQuestionsAnswered()) * 100 
            : 0;
        Ansi.holoBadgeLine("ACCURACY", String.format("%.1f%%", accuracy), Ansi.BRIGHT_YELLOW, Ansi.WHITE);
        Ansi.holoBadgeLine("TOTAL XP", String.valueOf(player.getXp()), Ansi.BRIGHT_YELLOW, Ansi.WHITE);
        System.out.println();
        Ansi.holoBadgeLine("ADVICE", "Don't give up! Debugging is an iterative process.", Ansi.BRIGHT_YELLOW, Ansi.BRIGHT_WHITE);

        showPostMortemReport();
        if (exportStudyReport("estudo_revisao.txt")) {
            System.out.println();
            Ansi.holoBadgeLine("STUDY GUIDE", "Dossiê de revisão salvo em 'estudo_revisao.txt' para estudo no IntelliJ!", Ansi.BRIGHT_GREEN, Ansi.BRIGHT_WHITE);
        }

        if (difficulty.isPermadeath()) {
            SaveManager.deleteSaveGame();
            LeaderboardManager.addEntry(new LeaderboardEntry(
                player.getName(), currentLevel, player.getXp(), player.getHighestStreak(), difficulty, false
            ));
            System.out.println();
            Ansi.holoBadgeLine("PERMADEATH", "💀 Hardcore mode: Process terminated with fatal fault. Save game wiped!", Ansi.RED, Ansi.BRIGHT_WHITE + Ansi.BOLD);
            pressEnterToContinue("  [Press Enter to return to main menu...]");
            return false;
        }

        LeaderboardManager.addEntry(new LeaderboardEntry(
            player.getName(), currentLevel, player.getXp(), player.getHighestStreak(), difficulty, false
        ));
        
        String input = prompt("\nWould you like to retry this level? (Y/N | 1/0): ");
        return parseGameOverChoice(input);
    }

    void showGameWonScreen() {
        Ansi.clearScreen();
        Ansi.beep();
        Ansi.holoBanner("🏆", "GRAND VICTORY: JVM MASTER ACHIEVED!", "The Monolith Core has been purified", Ansi.BRIGHT_YELLOW);

        AchievementManager.unlock(Achievement.GARBAGE_COLLECTOR);
        if (difficulty == GameDifficulty.SENIOR_ARCHITECT) {
            AchievementManager.unlock(Achievement.ARCHITECT);
        }
        LeaderboardManager.addEntry(new LeaderboardEntry(
            player.getName(), MAX_LEVEL, player.getXp(), player.getHighestStreak(), difficulty, true
        ));

        String[] trophyLines = {
            "                       ___________",
            "                      '._==_==_=_.'",
            "                      .-\\\\:      /-.",
            "                     | (|:.     |) |",
            "                      '-|:.     |-'",
            "                        \\\\::.    /",
            "                         '::. .'",
            "                           ) (",
            "                         _.' '._",
            "                        `\"\"\"\"\"\"\"`"
        };
        for (String line : trophyLines) {
            Ansi.holoLine(Ansi.colorize(line, Ansi.BRIGHT_CYAN));
        }

        Ansi.holoBadgeLine("VICTORY", "All bugs have been purged. The StackOverflow Dragon has been subdued!", Ansi.BRIGHT_GREEN, Ansi.BRIGHT_WHITE + Ansi.BOLD);
        System.out.println();
        Ansi.holoBadgeLine("HERO", player.getName(), Ansi.BRIGHT_CYAN, Ansi.BRIGHT_WHITE + Ansi.BOLD);
        Ansi.holoBadgeLine("TITLE", "LEGENDARY PRINCIPAL JAVA ARCHITECT", Ansi.BRIGHT_YELLOW, Ansi.BRIGHT_YELLOW + Ansi.BOLD);
        Ansi.holoBadgeLine("TOTAL XP", String.valueOf(player.getXp()), Ansi.BRIGHT_YELLOW, Ansi.WHITE);
        Ansi.holoBadgeLine("QUIZZES", player.getCorrectAnswers() + " / " + player.getQuestionsAnswered(), Ansi.BRIGHT_CYAN, Ansi.WHITE);
        double accuracy = player.getQuestionsAnswered() > 0 
            ? ((double) player.getCorrectAnswers() / player.getQuestionsAnswered()) * 100 
            : 0;
        Ansi.holoBadgeLine("ACCURACY", String.format("%.1f%%", accuracy), Ansi.BRIGHT_YELLOW, Ansi.WHITE);
        Ansi.holoBadgeLine("STREAK RECORD", String.valueOf(player.getHighestStreak()), Ansi.BRIGHT_YELLOW, Ansi.WHITE);
        System.out.println();
        Ansi.holoLine(Ansi.colorize("  \"There are only two kinds of languages: the ones folks complain about and the ones nobody uses.\"", Ansi.DIM + Ansi.WHITE));
        Ansi.holoBadgeLine("CONGRATULATIONS", "You have mastered the Bytecode Chronicles!", Ansi.BRIGHT_GREEN, Ansi.BRIGHT_WHITE + Ansi.BOLD);

        if (!missedQuestions.isEmpty()) {
            showPostMortemReport();
            if (exportStudyReport("estudo_revisao.txt")) {
                System.out.println();
                Ansi.holoBadgeLine("STUDY GUIDE", "Dossiê de revisão salvo em 'estudo_revisao.txt' para estudo no IntelliJ!", Ansi.BRIGHT_GREEN, Ansi.BRIGHT_WHITE);
            }
        }

        System.out.println();
        pressEnterToContinue();
    }

    void showPostMortemReport() {
        if (missedQuestions.isEmpty()) return;
        System.out.println();
        Ansi.holoBanner("🔍", "POST-MORTEM DEBUG REPORT", "Analysis of Runtime Exceptions Encountered", Ansi.BRIGHT_RED);
        char[] labels = {'A', 'B', 'C', 'D'};
        for (int i = 0; i < missedQuestions.size(); i++) {
            MissedQuestion mq = missedQuestions.get(i);
            Question q = mq.getQuestion();
            String marked = (mq.getSelectedIndex() >= 0 && mq.getSelectedIndex() < q.getOptions().size()) 
                ? labels[mq.getSelectedIndex()] + ": " + q.getOptions().get(mq.getSelectedIndex())
                : "Invalid / None";
            String correct = labels[q.getCorrectOptionIndex()] + ": " + q.getOptions().get(q.getCorrectOptionIndex());

            System.out.println();
            Ansi.holoBadgeLine("BUG #" + (i + 1) + " (LVL " + mq.getLevel() + ")", q.getTopic() + " [" + q.getDifficultyStars() + "] - " + q.getPrompt(), Ansi.BRIGHT_YELLOW, Ansi.BRIGHT_WHITE);
            Ansi.holoLine("     ❌ Your Choice:    " + Ansi.colorize(marked, Ansi.RED));
            Ansi.holoLine("     ✅ Correct Syntax:  " + Ansi.colorize(correct, Ansi.BRIGHT_GREEN + Ansi.BOLD));
            Ansi.holoLine("     📖 Explanation:     " + Ansi.colorize(q.getExplanation(), Ansi.WHITE));
            if (q.getJlsReference() != null && !q.getJlsReference().isEmpty()) {
                Ansi.holoLine("     📜 JLS Reference:   " + Ansi.colorize(q.getJlsReference(), Ansi.BRIGHT_CYAN));
            }
        }
    }

    public boolean exportStudyReport(String filename) {
        if (missedQuestions.isEmpty()) return false;
        try (java.io.PrintWriter pw = new java.io.PrintWriter(new java.io.FileWriter(filename, java.nio.charset.StandardCharsets.UTF_8))) {
            pw.println("================================================================================");
            pw.println("             JAVA QUEST: RELATÓRIO PÓS-JOGO DE ESTUDO E REVISÃO                 ");
            pw.println("================================================================================");
            pw.println("Hero: " + (player != null ? player.getName() : "Byte Paladin"));
            pw.println("Data: " + java.time.LocalDateTime.now());
            pw.println("Total de Erros a Revisar: " + missedQuestions.size());
            pw.println("================================================================================\n");

            char[] labels = {'A', 'B', 'C', 'D'};
            for (int i = 0; i < missedQuestions.size(); i++) {
                MissedQuestion mq = missedQuestions.get(i);
                Question q = mq.getQuestion();
                pw.println("--------------------------------------------------------------------------------");
                pw.println("QUESTÃO " + (i + 1) + " | Nível " + mq.getLevel() + " | Tópico: " + q.getTopic() + " | Dificuldade: " + q.getDifficultyStars() + " | Tags: " + String.join(", ", q.getTags()));
                pw.println("--------------------------------------------------------------------------------");
                pw.println("Pergunta: " + q.getPrompt());
                if (q.getCodeSnippet() != null && !q.getCodeSnippet().isEmpty()) {
                    pw.println("\nCódigo-Fonte:");
                    for (int l = 0; l < q.getCodeSnippet().size(); l++) {
                        pw.printf("  %02d | %s%n", l + 1, q.getCodeSnippet().get(l));
                    }
                }
                pw.println("\nAlternativas:");
                for (int o = 0; o < q.getOptions().size(); o++) {
                    String marker = (o == q.getCorrectOptionIndex()) ? " [CORRETA]" : (o == mq.getSelectedIndex() ? " [SUA ESCOLHA]" : "");
                    pw.println("  [" + labels[o] + "] " + q.getOptions().get(o) + marker);
                }
                pw.println("\nExplicação Oficial:");
                pw.println("  " + q.getExplanation());
                if (q.getJlsReference() != null && !q.getJlsReference().isEmpty()) {
                    pw.println("Referência da Especificação Oficial:");
                    pw.println("  " + q.getJlsReference());
                }
                pw.println();
            }
            pw.println("================================================================================");
            pw.println("Continue praticando! A maestria em Java se constrói na depuração.");
            pw.println("================================================================================");
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private String prompt(String message) {
        Ansi.showCursor();
        Ansi.type(Ansi.colorize(message, Ansi.BRIGHT_YELLOW + Ansi.BOLD));
        if (scanner.hasNextLine()) {
            return scanner.nextLine();
        }
        return "4";
    }

    private void pressEnterToContinue(String customMessage) {
        Ansi.showCursor();
        String msg = (customMessage != null) ? customMessage : "\n[Press Enter to continue...]";
        Ansi.type(Ansi.colorize(msg, Ansi.DIM + Ansi.BRIGHT_WHITE));
        if (scanner.hasNextLine()) {
            scanner.nextLine();
        }
    }

    private void pressEnterToContinue() {
        pressEnterToContinue(null);
    }
}
