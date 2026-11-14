package game;

import game.state.BattleState;
import game.state.GameContext;
import game.state.GameOverState;
import game.state.GameState;
import game.state.LevelTransitionState;
import game.state.MainMenuState;
import game.state.StateMachine;
import game.state.VictoryState;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("8.0 - ComprehensivePolishingFeaturesTest: Validação das Fases 3 e 4 do Polimento")
public class ComprehensivePolishingFeaturesTest {

    private Path tempAchievementPath;
    private Path tempLeaderboardPath;

    @BeforeEach
    void setUp() throws Exception {
        Ansi.FAST_MODE = true;
        tempAchievementPath = Files.createTempFile("javaquest_test_achievements", ".properties");
        AchievementManager.setCustomSavePath(tempAchievementPath);

        tempLeaderboardPath = Files.createTempFile("javaquest_test_leaderboard", ".properties");
        LeaderboardManager.setCustomSavePath(tempLeaderboardPath);
    }

    @AfterEach
    void tearDown() throws Exception {
        AchievementManager.resetDefaultSavePath();
        LeaderboardManager.resetDefaultSavePath();
        Files.deleteIfExists(tempAchievementPath);
        Files.deleteIfExists(tempLeaderboardPath);
        Ansi.setColorEnabled(true);
        Ansi.setHighContrast(false);
        Ansi.setTheme(Theme.CYBERPUNK);
        I18n.setLanguage(I18n.Language.EN_US);
    }

    @Nested
    @DisplayName("8.1: Paletas e Temas Visuais (4.5)")
    class ThemeTests {

        @Test
        @DisplayName("8.1.1 - Enum Theme possui os 4 temas planejados")
        void themeEnumValues() {
            assertEquals(4, Theme.values().length);
            assertNotNull(Theme.CYBERPUNK);
            assertNotNull(Theme.MATRIX);
            assertNotNull(Theme.DRACULA);
            assertNotNull(Theme.MONOKAI);
        }

        @Test
        @DisplayName("8.1.2 - Busca de tema por código e fallback seguro")
        void themeFromCode() {
            assertEquals(Theme.CYBERPUNK, Theme.fromCode("1"));
            assertEquals(Theme.CYBERPUNK, Theme.fromCode("cyberpunk"));
            assertEquals(Theme.MATRIX, Theme.fromCode("2"));
            assertEquals(Theme.MATRIX, Theme.fromCode("matrix"));
            assertEquals(Theme.DRACULA, Theme.fromCode("3"));
            assertEquals(Theme.DRACULA, Theme.fromCode("dracula"));
            assertEquals(Theme.MONOKAI, Theme.fromCode("4"));
            assertEquals(Theme.MONOKAI, Theme.fromCode("monokai"));
            assertEquals(Theme.CYBERPUNK, Theme.fromCode("invalid_theme"));
            assertEquals(Theme.CYBERPUNK, Theme.fromCode(null));
        }

        @Test
        @DisplayName("8.1.3 - Ansi aplica e recupera temas dinamicamente")
        void ansiThemeApplication() {
            Ansi.setTheme(Theme.MATRIX);
            assertEquals(Theme.MATRIX, Ansi.getTheme());
            assertTrue(Ansi.themePrimary().contains("32")); // ANSI green

            Ansi.setTheme(Theme.DRACULA);
            assertEquals(Theme.DRACULA, Ansi.getTheme());
            assertTrue(Ansi.themePrimary().contains("35")); // ANSI magenta
        }

        @Test
        @DisplayName("8.1.4 - SaveManager persiste e restaura tema escolhido")
        void saveManagerThemePersistence() throws Exception {
            Path saveFile = Files.createTempFile("save_theme_test", ".properties");
            try {
                SaveManager.setCustomSavePath(saveFile);
                Ansi.setTheme(Theme.MONOKAI);

                Player p = new Player("ThemeTester");
                SaveManager.saveGame(p, 2);

                Ansi.setTheme(Theme.CYBERPUNK);
                SaveManager.SaveData loaded = SaveManager.loadGame();
                assertNotNull(loaded);
                assertEquals(Theme.MONOKAI, loaded.theme());

                loaded.applyTo(p);
                assertEquals(Theme.MONOKAI, Ansi.getTheme());
            } finally {
                SaveManager.resetDefaultSavePath();
                Files.deleteIfExists(saveFile);
            }
        }
    }

    @Nested
    @DisplayName("8.2: Modo Alto Contraste & --no-color (7.3)")
    class AccessibilityTests {

        @Test
        @DisplayName("8.2.1 - Ansi.setColorEnabled(false) desativa cores em colorize")
        void noColorColorize() {
            Ansi.setColorEnabled(false);
            assertFalse(Ansi.isColorEnabled());
            assertEquals("Texto Puro", Ansi.colorize("Texto Puro", Ansi.RED));
            assertEquals("", Ansi.themePrimary());
        }

        @Test
        @DisplayName("8.2.2 - Alto Contraste substitui cores escuras por texto de alto contraste")
        void highContrastMode() {
            Ansi.setColorEnabled(true);
            Ansi.setHighContrast(true);
            assertTrue(Ansi.isHighContrast());

            String dimmed = Ansi.colorize("Info", Ansi.DIM + Ansi.BRIGHT_BLACK);
            // In high contrast mode, dim colors are converted to WHITE
            assertTrue(dimmed.contains("\u001B[37m"));
        }

        @Test
        @DisplayName("8.2.3 - stripAnsi remove escapes ANSI, cursor e caracteres de controle")
        void stripAnsiCompleteness() {
            String dirty = "\033[31;1mAlerta\033[0m\033[H\033[2J\u0007";
            String clean = Ansi.stripAnsi(dirty);
            assertEquals("Alerta", clean);
        }

        @Test
        @DisplayName("8.2.4 - screenShake é ignorado com no-color ativo")
        void screenShakeDisabledUnderNoColor() {
            Ansi.FAST_MODE = false;
            Ansi.setColorEnabled(false);
            assertDoesNotThrow(() -> Ansi.screenShake(5, 50));
        }
    }

    @Nested
    @DisplayName("8.3: Sistema de Conquistas e Medalhas (6.2)")
    class AchievementTests {

        @Test
        @DisplayName("8.3.1 - Total de 15 Bytecode Badges com metadados válidos")
        void achievementCountAndMetadata() {
            assertEquals(15, Achievement.values().length);
            for (Achievement a : Achievement.values()) {
                assertNotNull(a.getId());
                assertNotNull(a.getTitle());
                assertNotNull(a.getDescription());
                assertNotNull(a.getIcon());
                assertFalse(a.getId().isEmpty());
                assertFalse(a.getTitle().isEmpty());
            }
        }

        @Test
        @DisplayName("8.3.2 - Desbloqueio e persistência em arquivo")
        void unlockAndPersistence() {
            assertEquals(0, AchievementManager.getUnlockedCount());
            assertFalse(AchievementManager.isUnlocked(Achievement.FIRST_COMMIT));

            boolean unlocked = AchievementManager.unlock(Achievement.FIRST_COMMIT);
            assertTrue(unlocked);
            assertTrue(AchievementManager.isUnlocked(Achievement.FIRST_COMMIT));
            assertEquals(1, AchievementManager.getUnlockedCount());

            // Tentativa de desbloqueio duplicado é idempotente
            boolean again = AchievementManager.unlock(Achievement.FIRST_COMMIT);
            assertFalse(again);
            assertEquals(1, AchievementManager.getUnlockedCount());

            // Recarregar do arquivo preserva o desbloqueio
            AchievementManager.loadAchievements();
            assertTrue(AchievementManager.isUnlocked(Achievement.FIRST_COMMIT));
        }

        @Test
        @DisplayName("8.3.3 - Clean Code desbloqueia ao atingir streak 10 no Player")
        void cleanCodeAchievementTrigger() {
            Player p = new Player("CleanCoder");
            for (int i = 0; i < 9; i++) {
                p.registerAnswer(true);
            }
            assertFalse(AchievementManager.isUnlocked(Achievement.CLEAN_CODE));

            p.registerAnswer(true); // 10 acertos seguidos!
            assertTrue(AchievementManager.isUnlocked(Achievement.CLEAN_CODE));
        }

        @Test
        @DisplayName("8.3.4 - HotSpot Optimizer desbloqueia ao acumular 500 XP")
        void hotspotOptimizerAchievementTrigger() {
            Player p = new Player("Optimizer");
            p.setXp(490);
            p.registerAnswer(true); // +50 XP
            assertTrue(AchievementManager.isUnlocked(Achievement.HOTSPOT_OPTIMIZER));
        }

        @Test
        @DisplayName("8.3.5 - Polyglot Programmer desbloqueia ao acumular 3 talentos")
        void polyglotProgrammerTrigger() {
            Player p = new Player("Polyglot");
            p.addTalent(Talent.GRAALVM_COMPILER);
            p.addTalent(Talent.HOTSPOT_INLINING);
            assertFalse(AchievementManager.isUnlocked(Achievement.POLYGLOT_PROGRAMMER));

            p.addTalent(Talent.ZERO_ALLOCATION);
            assertTrue(AchievementManager.isUnlocked(Achievement.POLYGLOT_PROGRAMMER));
        }

        @Test
        @DisplayName("8.3.6 - Clean Architecture Shield desbloqueia ao absorver 100% do golpe")
        void cleanArchAchievementTrigger() {
            Player p = new Player("Architect");
            p.setCleanArchShieldActive(true);
            p.takeDamage(40);
            assertTrue(AchievementManager.isUnlocked(Achievement.CLEAN_ARCHITECTURE));
        }

        @Test
        @DisplayName("8.3.7 - Breakpoint Savior desbloqueia ao salvar de morte letal")
        void breakpointSaviorTrigger() {
            Player p = new Player("Survivor");
            p.setHp(10);
            p.getInventory().addItem(Item.BREAKPOINT_TOKEN);
            p.takeDamage(50); // dano letal interceptado!
            assertTrue(AchievementManager.isUnlocked(Achievement.BREAKPOINT_SAVIOR));
        }
    }

    @Nested
    @DisplayName("8.4: Hall da Fama / Leaderboard Local (6.4)")
    class LeaderboardTests {

        @Test
        @DisplayName("8.4.1 - Inserção ordenada e limite de 10 entradas")
        void leaderboardSortingAndLimit() {
            LeaderboardManager.resetAll();
            for (int i = 1; i <= 15; i++) {
                LeaderboardManager.addEntry(new LeaderboardEntry("Player " + i, i % 6 + 1, i * 100, i, GameDifficulty.MID_LEVEL, true));
            }

            List<LeaderboardEntry> entries = LeaderboardManager.getEntries();
            assertEquals(10, entries.size(), "Deve conter no máximo 10 entradas");

            // Primeira entrada deve ser a de maior pontuação (1500 XP)
            assertEquals("Player 15", entries.get(0).playerName());
            assertEquals(1500, entries.get(0).xp());
        }

        @Test
        @DisplayName("8.4.2 - Persistência em disco do Leaderboard")
        void leaderboardPersistence() {
            LeaderboardManager.resetAll();
            LeaderboardManager.addEntry(new LeaderboardEntry("Ace", 6, 850, 12, GameDifficulty.SENIOR_ARCHITECT, true));

            LeaderboardManager.loadLeaderboard();
            List<LeaderboardEntry> loaded = LeaderboardManager.getEntries();
            assertEquals(1, loaded.size());
            assertEquals("Ace", loaded.get(0).playerName());
            assertEquals(850, loaded.get(0).xp());
            assertEquals(GameDifficulty.SENIOR_ARCHITECT, loaded.get(0).difficulty());
        }

        @Test
        @DisplayName("8.4.3 - Renderização do Leaderboard executa sem exceções")
        void leaderboardRendering() {
            LeaderboardManager.addEntry(new LeaderboardEntry("Hero", 6, 900, 15, GameDifficulty.MID_LEVEL, true));
            assertDoesNotThrow(() -> LeaderboardManager.renderLeaderboard(null));
        }
    }

    @Nested
    @DisplayName("8.5: Citações JLS & Explicações Aprofundadas (3.1)")
    class JlsReferenceTests {

        @Test
        @DisplayName("8.5.1 - Inferência automática de referência JLS para primitivos e operadores")
        void primitiveJlsInference() {
            Question q = new Question("Primitive Types", "Pergunta", List.of(), List.of("A", "B"), 0, "Explicação", 30);
            assertNotNull(q.getJlsReference());
            assertTrue(q.getJlsReference().contains("JLS §4.2"));
            assertTrue(q.getFullExplanation().contains("JLS §4.2"));
        }

        @Test
        @DisplayName("8.5.2 - Inferência de JLS para Imutabilidade de String")
        void stringJlsInference() {
            Question q = new Question("String Immutability", "Pergunta", List.of(), List.of("A", "B"), 0, "Explicação", 30);
            assertTrue(q.getJlsReference().contains("JLS §4.3.3"));
        }

        @Test
        @DisplayName("8.5.3 - Construtor com citação JLS explícita")
        void explicitJlsConstructor() {
            Question q = new Question("Custom Topic", "Prompt", List.of(), List.of("A"), 0,
                    "Explicação conceitual", 35, 4, List.of("custom"), "JEP 440 (Record Patterns)");
            assertEquals("JEP 440 (Record Patterns)", q.getJlsReference());
            assertTrue(q.getFullExplanation().contains("JEP 440"));
        }
    }

    @Nested
    @DisplayName("8.6: Modos Especiais de Rejogabilidade (6.3)")
    class SpecialModesTests {

        @Test
        @DisplayName("8.6.1 - Parsing de atalho para Modos Especiais")
        void specialModesParsing() {
            assertEquals("M", GameEngine.parseMainMenuChoice("M"));
            assertEquals("M", GameEngine.parseMainMenuChoice("m"));
            assertEquals("M", GameEngine.parseMainMenuChoice("modes"));
            assertEquals("M", GameEngine.parseMainMenuChoice("rush"));
            assertEquals("M", GameEngine.parseMainMenuChoice("endless"));
        }

        @Test
        @DisplayName("8.6.2 - GameEngine possui CombatConfig imutável")
        void gameEngineCombatConfig() {
            GameEngine engine = new GameEngine();
            assertNotNull(engine.getCombatConfig());
            assertEquals(35, engine.getCombatConfig().baseHealAmount());
            assertEquals(20, engine.getCombatConfig().healManaCost());
        }
    }

    @Nested
    @DisplayName("8.7: Arquitetura & Engenharia de Código (Fase 4: 1.1, 1.2, 1.3, 1.4, 7.2)")
    class ArchitectureFase4Tests {

        @Test
        @DisplayName("8.7.1 - CombatConfig centraliza balanceamento de combate sem números mágicos")
        void combatConfigValidation() {
            CombatConfig cfg = CombatConfig.defaultBalance();
            assertEquals(35, cfg.baseHealAmount());
            assertEquals(20, cfg.healManaCost());
            assertEquals(15, cfg.shieldManaCost());
            assertEquals(0.50, cfg.shieldDamageReduction());
            assertEquals(15, cfg.hintManaCost());
            assertEquals(15, cfg.baseTurnManaRecovery());
            assertEquals(10, cfg.comboStreakBonusDamageTier1());
            assertEquals(20, cfg.comboStreakBonusDamageTier2());
        }

        @Test
        @DisplayName("8.7.2 - VirtualTerminalIO captura I/O e permite asserts determinísticos")
        void virtualTerminalIO() {
            VirtualTerminalIO vterm = new VirtualTerminalIO("opcao1", "opcao2");
            vterm.println("Linha 1");
            vterm.print("Texto ");
            vterm.println("Composto");

            assertEquals("opcao1", vterm.readLine());
            assertEquals("opcao2", vterm.readLine());
            assertEquals("", vterm.readLine());

            assertTrue(vterm.getOutputLog().contains("Linha 1"));
            assertTrue(vterm.getOutputLog().contains("Texto Composto"));
            assertEquals(2, vterm.getPrintedLines().size());
        }

        @Test
        @DisplayName("8.7.3 - StandardTerminalIO opera sobre streams padrão")
        void standardTerminalIO() {
            StandardTerminalIO sio = new StandardTerminalIO();
            assertEquals(80, sio.getTerminalWidth());
            assertTrue(sio.getTerminalHeight() > 0);
        }

        @Test
        @DisplayName("8.7.4 - BattleAction sealed interface modela ações estritas de combate")
        void battleActionHierarchy() {
            BattleAction a1 = new BattleAction.AnswerQuestion(2);
            BattleAction a2 = new BattleAction.CastDebugHeal();
            BattleAction a3 = new BattleAction.CastVariableShield();
            BattleAction a4 = new BattleAction.CastCompilerHint();
            BattleAction a5 = new BattleAction.UseInventoryItem(Item.COFFEE_BREW);
            BattleAction a6 = new BattleAction.ForfeitBattle();

            assertInstanceOf(BattleAction.class, a1);
            assertInstanceOf(BattleAction.class, a2);
            assertInstanceOf(BattleAction.class, a3);
            assertInstanceOf(BattleAction.class, a4);
            assertInstanceOf(BattleAction.class, a5);
            assertInstanceOf(BattleAction.class, a6);

            // Parser estático
            assertInstanceOf(BattleAction.AnswerQuestion.class, BattleAction.parse("1", 0, null));
            assertInstanceOf(BattleAction.CastDebugHeal.class, BattleAction.parse("A", 0, null));
            assertInstanceOf(BattleAction.CastCompilerHint.class, BattleAction.parse("B", 0, null));
            assertInstanceOf(BattleAction.CastVariableShield.class, BattleAction.parse("3", 0, null));
            assertInstanceOf(BattleAction.UseInventoryItem.class, BattleAction.parse("5", 0, Item.COFFEE_BREW));
            assertInstanceOf(BattleAction.ForfeitBattle.class, BattleAction.parse("4", 0, null));
        }

        @Test
        @DisplayName("8.7.5 - State Pattern desacopla GameState e StateMachine")
        void stateMachineTransitions() {
            VirtualTerminalIO io = new VirtualTerminalIO();
            StateMachine sm = new StateMachine();
            GameContext ctx = new GameContext(io, CombatConfig.defaultBalance(), sm);

            MainMenuState menu = new MainMenuState();
            sm.changeState(menu, ctx);
            assertTrue(menu.isEntered());
            assertEquals(menu, sm.getCurrentState());

            sm.render(ctx);
            assertTrue(io.getOutputLog().contains("MAIN MENU STATE"));

            BattleState battle = new BattleState();
            sm.changeState(battle, ctx);
            assertFalse(menu.isEntered());
            assertTrue(battle.isEntered());
            assertEquals(battle, sm.getCurrentState());

            LevelTransitionState trans = new LevelTransitionState();
            sm.changeState(trans, ctx);
            assertTrue(trans.isEntered());

            GameOverState gameOver = new GameOverState();
            sm.changeState(gameOver, ctx);
            assertTrue(gameOver.isEntered());

            VictoryState victory = new VictoryState();
            sm.changeState(victory, ctx);
            assertTrue(victory.isEntered());
        }

        @Test
        @DisplayName("8.7.6 - Sistema de i18n suporta PT-BR e EN-US")
        void i18nTranslations() {
            I18n.setLanguage(I18n.Language.PT_BR);
            assertEquals("Iniciar Nova Aventura", I18n.get("menu.start"));
            assertEquals("Sair do Jogo", I18n.get("menu.exit"));
            assertEquals("Lançar Feitiço de Código (Responder Quiz)", I18n.get("combat.cast"));
            assertEquals("Dev Backpack & Consumíveis", I18n.get("combat.backpack"));

            I18n.setLanguage(I18n.Language.EN_US);
            assertEquals("Start New Adventure", I18n.get("menu.start"));
            assertEquals("Exit Game", I18n.get("menu.exit"));
            assertEquals("Cast Code Spell (Answer Java Quiz)", I18n.get("combat.cast"));
            assertEquals("Dev Backpack & Items", I18n.get("combat.backpack"));
        }

        @Test
        @DisplayName("8.7.7 - I18n toggleLanguage e fromCode parsing robusto")
        void i18nToggleAndFromCode() {
            I18n.setLanguage(I18n.Language.PT_BR);
            assertEquals(I18n.Language.EN_US, I18n.toggleLanguage());
            assertEquals(I18n.Language.PT_BR, I18n.toggleLanguage());

            assertEquals(I18n.Language.EN_US, I18n.Language.fromCode("en"));
            assertEquals(I18n.Language.EN_US, I18n.Language.fromCode("english"));
            assertEquals(I18n.Language.EN_US, I18n.Language.fromCode("en_US"));
            assertEquals(I18n.Language.PT_BR, I18n.Language.fromCode("pt"));
            assertEquals(I18n.Language.PT_BR, I18n.Language.fromCode("portugues"));
            assertEquals(I18n.Language.PT_BR, I18n.Language.fromCode("pt_BR"));
            assertEquals(I18n.Language.PT_BR, I18n.Language.fromCode(null));
        }

        @Test
        @DisplayName("8.7.8 - I18n MessageFormat com interpolação dinâmica de parâmetros")
        void i18nMessageFormatting() {
            I18n.setLanguage(I18n.Language.PT_BR);
            String pt = I18n.get("menu.resume.details", 3, "Hero", 80, 100);
            assertTrue(pt.contains("Nível 3") || pt.contains("Lvl 3"));
            assertTrue(pt.contains("Hero"));
            assertTrue(pt.contains("80/100"));

            I18n.setLanguage(I18n.Language.EN_US);
            String en = I18n.get("menu.resume.details", 3, "Hero", 80, 100);
            assertTrue(en.contains("Lvl 3") || en.contains("Level 3"));
            assertTrue(en.contains("Hero"));
            assertTrue(en.contains("80/100"));
        }

        @Test
        @DisplayName("8.7.9 - SaveManager persiste e restaura idioma selecionado")
        void saveManagerLanguagePersistence() throws Exception {
            Path saveFile = Files.createTempFile("save_lang_test", ".properties");
            try {
                SaveManager.setCustomSavePath(saveFile);
                I18n.setLanguage(I18n.Language.EN_US);

                Player p = new Player("LangTester");
                SaveManager.saveGame(p, 3);

                I18n.setLanguage(I18n.Language.PT_BR);
                SaveManager.SaveData loaded = SaveManager.loadGame();
                assertNotNull(loaded);
                assertEquals(I18n.Language.EN_US, loaded.language());

                loaded.applyTo(p);
                assertEquals(I18n.Language.EN_US, I18n.getLanguage());
            } finally {
                SaveManager.resetDefaultSavePath();
                Files.deleteIfExists(saveFile);
            }
        }

        @Test
        @DisplayName("8.7.10 - CLI processa argumentos de linha de comando para idioma (--lang=en, --pt, etc)")
        void cliLanguageFlags() {
            I18n.setLanguage(I18n.Language.PT_BR);
            GameEngine.configureCliArgs(new String[]{"--lang=en"});
            assertEquals(I18n.Language.EN_US, I18n.getLanguage());

            GameEngine.configureCliArgs(new String[]{"--pt"});
            assertEquals(I18n.Language.PT_BR, I18n.getLanguage());

            GameEngine.configureCliArgs(new String[]{"--english"});
            assertEquals(I18n.Language.EN_US, I18n.getLanguage());
        }
    }
}
