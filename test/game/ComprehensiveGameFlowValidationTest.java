package game;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("9.0 - ComprehensiveGameFlowValidationTest: Validação Abrangente de Fluxos e Recursos")
public class ComprehensiveGameFlowValidationTest {

    private InputStream origIn;
    private PrintStream origOut;
    private ByteArrayOutputStream outContent;
    private Path tempSavePath;
    private Path tempAchievementsPath;
    private Path tempLeaderboardPath;

    @BeforeEach
    void setUp() throws Exception {
        Ansi.FAST_MODE = true;
        origIn = System.in;
        origOut = System.out;
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        tempSavePath = Files.createTempFile("flow_test_save", ".properties");
        SaveManager.setCustomSavePath(tempSavePath);

        tempAchievementsPath = Files.createTempFile("flow_test_achievements", ".properties");
        AchievementManager.setCustomSavePath(tempAchievementsPath);

        tempLeaderboardPath = Files.createTempFile("flow_test_leaderboard", ".properties");
        LeaderboardManager.setCustomSavePath(tempLeaderboardPath);
    }

    @AfterEach
    void tearDown() throws Exception {
        System.setIn(origIn);
        System.setOut(origOut);
        Ansi.FAST_MODE = false;
        Ansi.setColorEnabled(true);
        Ansi.setHighContrast(false);
        Ansi.setTheme(Theme.CYBERPUNK);
        I18n.setLanguage(I18n.Language.EN_US);

        SaveManager.resetDefaultSavePath();
        AchievementManager.resetDefaultSavePath();
        LeaderboardManager.resetDefaultSavePath();

        Files.deleteIfExists(tempSavePath);
        Files.deleteIfExists(tempAchievementsPath);
        Files.deleteIfExists(tempLeaderboardPath);
    }

    @Test
    @DisplayName("9.1 - Bytecode Lab: Sessão de estudo interativa com relatório final")
    void testBytecodeLabInteractiveSession() {
        // Sequência de entrada:
        // 1. Tópico 1 (ALL)
        // 2. Resposta 'A' para a primeira pergunta
        // 3. Enter após feedback
        // 4. '0' para encerrar a sessão
        String input = "1\nA\n\n0\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
        BytecodeLab lab = new BytecodeLab(scanner);

        assertDoesNotThrow(lab::start);

        String out = outContent.toString();
        assertTrue(out.contains("BYTECODE LAB") || out.contains("LAB QUESTION"), "Deve conter cabeçalho do Lab");
        assertTrue(out.contains("SCORECARD") || out.contains("RELATÓRIO") || out.contains("Aproveitamento"), "Deve exibir scorecard de aproveitamento");
    }

    @Test
    @DisplayName("9.2 - Save and Load: Ciclo completo com inventário, talentos, tema e idioma")
    void testFullSaveLoadCycle() {
        Player player = new Player("MasterCoder");
        player.setLevel(4);
        player.setHp(85);
        player.setMaxHp(120);
        player.setMana(50);
        player.setMaxMana(90);
        player.setXp(420);
        player.setHighestStreak(7);

        // Inventário variado
        player.getInventory().addItem(Item.COFFEE_BREW, 3);
        player.getInventory().addItem(Item.CLEAN_ARCH_SHIELD, 1);
        player.getInventory().addItem(Item.BREAKPOINT_TOKEN, 1);

        // Talentos adquiridos
        player.addTalent(Talent.GRAALVM_COMPILER);
        player.addTalent(Talent.ZERO_ALLOCATION);

        // Configuração de ambiente
        Ansi.setTheme(Theme.DRACULA);
        I18n.setLanguage(I18n.Language.PT_BR);

        // Salvar
        boolean saved = SaveManager.saveGame(player, 4, GameDifficulty.SENIOR_ARCHITECT);
        assertTrue(saved, "O jogo deve ser salvo com sucesso");
        assertTrue(SaveManager.hasSaveGame(), "hasSaveGame deve ser verdadeiro");

        // Resetar ambiente em memória
        Ansi.setTheme(Theme.CYBERPUNK);
        I18n.setLanguage(I18n.Language.EN_US);
        Player restoredPlayer = new Player("Newborn");

        // Carregar
        SaveManager.SaveData data = SaveManager.loadGame();
        assertNotNull(data, "Dados carregados não devem ser nulos");
        assertEquals("MasterCoder", data.playerName());
        assertEquals(4, data.currentLevel());
        assertEquals(85, data.hp());
        assertEquals(120, data.maxHp());
        assertEquals(420, data.xp());
        assertEquals(GameDifficulty.SENIOR_ARCHITECT, data.difficulty());
        assertEquals(Theme.DRACULA, data.theme());
        assertEquals(I18n.Language.PT_BR, data.language());

        // Aplicar
        data.applyTo(restoredPlayer);
        assertEquals("MasterCoder", restoredPlayer.getName());
        assertEquals(85, restoredPlayer.getHp());
        assertEquals(3, restoredPlayer.getInventory().getCount(Item.COFFEE_BREW));
        assertEquals(1, restoredPlayer.getInventory().getCount(Item.CLEAN_ARCH_SHIELD));
        assertTrue(restoredPlayer.hasTalent(Talent.GRAALVM_COMPILER));
        assertTrue(restoredPlayer.hasTalent(Talent.ZERO_ALLOCATION));
        assertEquals(Theme.DRACULA, Ansi.getTheme());
        assertEquals(I18n.Language.PT_BR, I18n.getLanguage());
    }

    @Test
    @DisplayName("9.3 - Paletas Visuais e Temas: Aplicação dinâmica de todos os 4 temas")
    void testAllThemesApplication() {
        for (Theme theme : Theme.values()) {
            Ansi.setTheme(theme);
            assertEquals(theme, Ansi.getTheme());
            assertNotNull(Ansi.themePrimary());
            assertNotNull(Ansi.themeSecondary());
            assertNotNull(Ansi.themeAccent());
            assertNotNull(Ansi.themeText());
            assertNotNull(Ansi.themeSuccess());
            assertNotNull(Ansi.themeDanger());
        }
    }

    @Test
    @DisplayName("9.4 - Acessibilidade: Modo Alto Contraste e No-Color operam sem falhas")
    void testAccessibilityCombinedModes() {
        Ansi.setColorEnabled(false);
        Ansi.setHighContrast(true);

        assertFalse(Ansi.isColorEnabled());
        assertTrue(Ansi.isHighContrast());

        assertEquals("Texto Puro", Ansi.colorize("Texto Puro", Ansi.RED));
        assertEquals("", Ansi.themePrimary());
        assertEquals("", Ansi.themeSecondary());

        // Sanitização de ANSI
        String stripped = Ansi.stripAnsi("\033[31;1mAlerta\033[0m\033[H\033[2J\u0007");
        assertEquals("Alerta", stripped);

        // Chamadas aos métodos holográficos não lançam exceção
        assertDoesNotThrow(() -> {
            Ansi.holoBanner("🛡️", "ACESSIBILIDADE", "Modo Seguro Ativo", Ansi.CYAN);
            Ansi.holoOption("1", "Opção de Texto Simples");
            Ansi.holoChoice('A', "Alternativa A", false);
            Ansi.holoBadgeLine("TESTE", "Texto sem sequências ANSI", Ansi.GREEN, Ansi.WHITE);
        });
    }

    @Test
    @DisplayName("9.5 - Conquistas e Leaderboard: Telas holográficas renderizam sem erros")
    void testAchievementsAndLeaderboardScreens() {
        // Desbloquear algumas conquistas
        AchievementManager.unlock(Achievement.FIRST_COMMIT);
        AchievementManager.unlock(Achievement.CLEAN_CODE);
        assertEquals(2, AchievementManager.getUnlockedCount());

        // Inserir registros no leaderboard
        LeaderboardManager.addEntry(new LeaderboardEntry("Alice", 6, 950, 15, GameDifficulty.SENIOR_ARCHITECT, true));
        LeaderboardManager.addEntry(new LeaderboardEntry("Bob", 3, 300, 5, GameDifficulty.MID_LEVEL, false));

        // Renderizar Leaderboard
        assertDoesNotThrow(() -> LeaderboardManager.renderLeaderboard(new Scanner(new ByteArrayInputStream("\n".getBytes(StandardCharsets.UTF_8)))));

        String out = outContent.toString();
        assertTrue(out.contains("Alice"));
        assertTrue(out.contains("Bob"));
        assertTrue(out.contains("HALL DA FAMA") || out.contains("LEADERBOARD"));
    }

    @Test
    @DisplayName("9.6 - Post-Mortem e Exportação de Dossiê de Estudo com JLS")
    void testPostMortemReportAndExport() throws Exception {
        Path reportFile = Files.createTempFile("test_estudo_revisao", ".txt");
        try {
            Files.deleteIfExists(reportFile);

            Player player = new Player("Scholar");
            Enemy enemy = Enemy.createForLevel(1);
            Question q = QuestionBank.getAllQuestions().get(0);

            // Responde incorreto para simular registro no missedQuestions
            // Índice diferente do correto
            int wrongIndex = (q.getCorrectOptionIndex() + 1) % q.getOptions().size();
            char wrongChar = (char) ('A' + wrongIndex);
            ByteArrayInputStream in = new ByteArrayInputStream((wrongChar + "\n\n").getBytes(StandardCharsets.UTF_8));
            GameEngine engine = new GameEngine(in);
            engine.setPlayer(player);
            engine.handleQuizQuestion(q, enemy);

            assertEquals(1, engine.getMissedQuestions().size());

            // Exportar relatório
            boolean exported = engine.exportStudyReport(reportFile.toString());
            assertTrue(exported, "Arquivo de relatório deve ser criado");
            assertTrue(Files.exists(reportFile), "Arquivo deve existir fisicamente");

            String content = Files.readString(reportFile);
            assertTrue(content.contains("RELATÓRIO PÓS-JOGO DE ESTUDO E REVISÃO"), "Deve conter cabeçalho do dossiê");
            assertTrue(content.contains("Scholar"), "Deve conter o nome do jogador");
            assertTrue(content.contains(q.getPrompt()), "Deve conter a pergunta errada");
        } finally {
            Files.deleteIfExists(reportFile);
        }
    }

    @Test
    @DisplayName("9.7 - CLI Configuração Integral: no-color, theme, high-contrast e idioma")
    void testCliFullConfiguration() {
        String[] args = {
            "--theme=matrix",
            "--high-contrast",
            "--lang=pt"
        };
        GameEngine.configureCliArgs(args);

        assertEquals(Theme.MATRIX, Ansi.getTheme());
        assertTrue(Ansi.isHighContrast());
        assertEquals(I18n.Language.PT_BR, I18n.getLanguage());

        String[] args2 = {
            "--no-color",
            "--theme=dracula",
            "--en"
        };
        GameEngine.configureCliArgs(args2);

        assertFalse(Ansi.isColorEnabled());
        assertEquals(Theme.DRACULA, Ansi.getTheme());
        assertEquals(I18n.Language.EN_US, I18n.getLanguage());
    }
}
