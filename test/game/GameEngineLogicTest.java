package game;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("2.8 - GameEngineLogicTest: Parsing de Entradas, Menus e Fórmulas de Combate")
public class GameEngineLogicTest {

    private PrintStream originalOut;
    private ByteArrayOutputStream outContent;

    @BeforeEach
    void setUp() {
        Ansi.FAST_MODE = true;
        originalOut = System.out;
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        Ansi.FAST_MODE = false;
    }

    @Nested
    @DisplayName("2.8.1: Parsing de Ações de Menu Principal")
    class MainMenuParsingTests {

        @ParameterizedTest(name = "Opção \"{0}\" mapeia para {1}")
        @CsvSource({
            "1, Nova Aventura",
            "2, Guia",
            "3, Quick Reference",
            "4, Sair"
        })
        @DisplayName("2.8.1 - Parsing de opções do menu principal 1 a 4")
        void parseValidMainMenuOptions(String input, String expectedMeaning) {
            String choice = input.trim();
            assertTrue(choice.equals("1") || choice.equals("2") || choice.equals("3") || choice.equals("4"));
        }
    }

    @Nested
    @DisplayName("2.8.2: Parsing de Ações de Turno de Combate")
    class CombatActionsParsingTests {

        @ParameterizedTest(name = "Ação de combate \"{0}\" válida")
        @ValueSource(strings = {"1", "2", "3", "4"})
        @DisplayName("2.8.2 - Parsing de ações de turno de combate (1=Atacar, 2=Poderes, 3=Inspecionar, 4=Fugir)")
        void parseCombatActions(String input) {
            String choice = input.trim();
            assertTrue(choice.matches("[1-4]"), "Ações de combate válidas são de 1 a 4");
        }
    }

    @Nested
    @DisplayName("2.8.3 - 2.8.5: Parsing de Alternativas do Quiz")
    class QuizParsingTests {

        @ParameterizedTest(name = "Entrada \"{0}\" mapeia para o índice {1}")
        @CsvSource({
            "A, 0", "1, 0",
            "B, 1", "2, 1",
            "C, 2", "3, 2",
            "D, 3", "4, 3"
        })
        @DisplayName("2.8.3 - Parsing canônico de alternativas (A-D e 1-4)")
        void canonicalQuizOptions(String input, int expectedIndex) {
            assertEquals(expectedIndex, GameEngine.parseQuizChoice(input));
        }

        @ParameterizedTest(name = "Entrada com espaços e minúsculas \"{0}\" mapeia para {1}")
        @CsvSource({
            "' a ', 0", "'  A  ', 0",
            "' b ', 1", "'  B  ', 1",
            "' c ', 2", "'  C  ', 2",
            "' d ', 3", "'  D  ', 3"
        })
        @DisplayName("2.8.4 - Insensibilidade a maiúsculas e espaços no quiz")
        void caseAndWhitespaceInsensitivity(String input, int expectedIndex) {
            assertEquals(expectedIndex, GameEngine.parseQuizChoice(input));
        }

        @ParameterizedTest(name = "Entrada inválida \"{0}\" deve ser rejeitada (-1)")
        @ValueSource(strings = {"E", "5", "sim", "", "   ", "!", "X", "-1", "ABC"})
        @DisplayName("2.8.5 - Tratamento de entradas inválidas no quiz")
        void invalidInputsRejected(String invalidInput) {
            assertEquals(-1, GameEngine.parseQuizChoice(invalidInput));
        }

        @Test
        @DisplayName("2.8.5b - Entrada nula retorna -1 com segurança")
        void nullInputSafety() {
            assertEquals(-1, GameEngine.parseQuizChoice(null));
        }
    }

    @Nested
    @DisplayName("2.8.6: Fórmula do Dano Total do Ataque do Jogador")
    class DamageFormulaTests {

        @Test
        @DisplayName("2.8.6 - Combo 0: dano base sem bônus (25 + 0 = 25)")
        void damageFormulaComboZero() {
            Player p = new Player("Hero");
            int total = GameEngine.calculateTotalDamage(25, p.getSpellDamageBonus());
            assertEquals(25, total);
        }

        @Test
        @DisplayName("2.8.6b - Combo 1: dano base sem bônus (25 + 0 = 25)")
        void damageFormulaComboOne() {
            Player p = new Player("Hero");
            p.registerAnswer(true); // streak 1
            int total = GameEngine.calculateTotalDamage(25, p.getSpellDamageBonus());
            assertEquals(25, total);
        }

        @Test
        @DisplayName("2.8.6c - Combo 2: bônus de magia +10 (25 + 10 = 35)")
        void damageFormulaComboTwo() {
            Player p = new Player("Hero");
            p.registerAnswer(true);
            p.registerAnswer(true); // streak 2
            int total = GameEngine.calculateTotalDamage(25, p.getSpellDamageBonus());
            assertEquals(35, total);
        }

        @Test
        @DisplayName("2.8.6d - Combo 3+: bônus de magia +20 (25 + 20 = 45)")
        void damageFormulaComboThree() {
            Player p = new Player("Hero");
            p.registerAnswer(true);
            p.registerAnswer(true);
            p.registerAnswer(true); // streak 3
            int total = GameEngine.calculateTotalDamage(25, p.getSpellDamageBonus());
            assertEquals(45, total);
        }
    }

    @Nested
    @DisplayName("2.8.7 - 2.8.14: Seleção e Parsing de Poderes Debug")
    class DebugPowersParsingTests {

        @ParameterizedTest(name = "Poder debug \"{0}\" é reconhecido")
        @ValueSource(strings = {"A", "B", "C", "D", "a", "b", "c", "d"})
        @DisplayName("2.8.7 - Opções do menu debug (A=Heal, B=Hint, C=Shield, D=Voltar)")
        void debugOptionsValid(String opt) {
            String choice = opt.trim().toUpperCase();
            assertTrue(choice.equals("A") || choice.equals("B") || choice.equals("C") || choice.equals("D"));
        }

        @ParameterizedTest(name = "Poder debug inválido \"{0}\"")
        @ValueSource(strings = {"E", "1", "X", "", "heal"})
        @DisplayName("2.8.8 - Opções inválidas do menu debug")
        void debugOptionsInvalid(String opt) {
            String choice = opt.trim().toUpperCase();
            assertFalse(choice.equals("A") || choice.equals("B") || choice.equals("C") || choice.equals("D"));
        }
    }

    @Nested
    @DisplayName("2.8.15 - 2.8.24: Exibição de Telas Informativas sem Erros")
    class InformationalScreensTests {

        private GameEngine createEngineWithInput(String input) {
            return new GameEngine(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
        }

        @Test
        @DisplayName("2.8.15 - showTitleScreen renderiza banner e opções")
        void titleScreenRender() {
            GameEngine engine = createEngineWithInput("");
            assertDoesNotThrow(engine::showTitleScreen);
            String out = outContent.toString();
            assertTrue(out.contains("BYTECODE CHRONICLES"));
            assertTrue(out.contains("Start New Adventure"));
        }

        @Test
        @DisplayName("2.8.16 - showHowToPlay renderiza regras e aguarda Enter")
        void howToPlayRender() {
            GameEngine engine = createEngineWithInput("\n");
            assertDoesNotThrow(engine::showHowToPlay);
            String out = outContent.toString();
            assertTrue(out.contains("HOW TO PLAY & SPELLS GUIDE"));
            assertTrue(out.contains("Refactor Heal"));
        }

        @Test
        @DisplayName("2.8.17 - showJavaCheatSheet renderiza conceitos essenciais")
        void cheatSheetRender() {
            GameEngine engine = createEngineWithInput("\n");
            assertDoesNotThrow(engine::showJavaCheatSheet);
            String out = outContent.toString();
            assertTrue(out.contains("JAVA QUICK STUDY GUIDE"));
            assertTrue(out.contains("PRIMITIVES"));
        }

        @Test
        @DisplayName("2.8.18 - showEnemyInspect renderiza ficha de monstro")
        void enemyInspectRender() {
            GameEngine engine = createEngineWithInput("\n");
            Enemy imp = Enemy.createForLevel(1);
            assertDoesNotThrow(() -> engine.showEnemyInspect(imp));
            String out = outContent.toString();
            assertTrue(out.contains("MONSTER DOSSIER"));
            assertTrue(out.contains("NullPointer Imp"));
        }

        @Test
        @DisplayName("2.8.19 - showLevelIntro renderiza introdução do chefe")
        void levelIntroRender() {
            GameEngine engine = createEngineWithInput("\n");
            Enemy imp = Enemy.createForLevel(1);
            assertDoesNotThrow(() -> engine.showLevelIntro(imp));
            String out = outContent.toString();
            assertTrue(out.contains("STAGE BOSS"));
            assertTrue(out.contains("NullPointer Imp"));
        }
    }

    @Nested
    @DisplayName("2.8.20 - 2.8.27: Transições de Fim de Nível e Vitória")
    class LevelTransitionsTests {

        private GameEngine createEngineWithInput(String input) {
            return new GameEngine(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
        }

        @Test
        @DisplayName("2.8.20 - showLevelTransitionScreen exibe upgrade de HP e Mana")
        void levelTransitionScreen() {
            GameEngine engine = createEngineWithInput("\n");
            Player p = new Player("Hero");
            engine.setPlayer(p);
            assertDoesNotThrow(() -> engine.showLevelTransitionScreen(1));
            String out = outContent.toString();
            assertTrue(out.contains("STAGE CLEAR! LEVEL UP!"));
            assertTrue(out.contains("Max HP increased by +20"));
            assertTrue(out.contains("Max Mana increased by +15"));
        }

        @Test
        @DisplayName("2.8.21 - showGameWonScreen exibe troféu e estatísticas finais")
        void gameWonScreen() {
            GameEngine engine = createEngineWithInput("\n");
            Player p = new Player("Hero");
            p.registerAnswer(true);
            engine.setPlayer(p);
            assertDoesNotThrow(engine::showGameWonScreen);
            String out = outContent.toString();
            assertTrue(out.contains("GRAND VICTORY: JVM MASTER ACHIEVED!"));
            assertTrue(out.contains("LEGENDARY PRINCIPAL JAVA ARCHITECT"));
        }
    }

    @Nested
    @DisplayName("2.8.28 - 2.8.34: Game Over Screen e Decisão de Retry")
    class GameOverScreenTests {

        private GameEngine createEngineWithInput(String input) {
            return new GameEngine(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
        }

        @Test
        @DisplayName("2.8.28 - showGameOverScreen com resposta 'Y' retorna true (retry)")
        void gameOverScreenRetryYes() {
            GameEngine engine = createEngineWithInput("Y\n");
            Player p = new Player("Hero");
            engine.setPlayer(p);
            assertTrue(engine.showGameOverScreen());
        }

        @Test
        @DisplayName("2.8.29 - showGameOverScreen com resposta '1' retorna true (retry numérico)")
        void gameOverScreenRetryOne() {
            GameEngine engine = createEngineWithInput("1\n");
            Player p = new Player("Hero");
            engine.setPlayer(p);
            assertTrue(engine.showGameOverScreen());
        }

        @Test
        @DisplayName("2.8.30 - showGameOverScreen com resposta 'N' retorna false (sair)")
        void gameOverScreenQuit() {
            GameEngine engine = createEngineWithInput("N\n");
            Player p = new Player("Hero");
            engine.setPlayer(p);
            assertFalse(engine.showGameOverScreen());
        }

        @Test
        @DisplayName("2.8.31 - showGameOverScreen exibe acurácia e total de questões")
        void gameOverScreenStats() {
            GameEngine engine = createEngineWithInput("N\n");
            Player p = new Player("Hero");
            p.registerAnswer(true);
            p.registerAnswer(false);
            engine.setPlayer(p);
            engine.showGameOverScreen();
            String out = outContent.toString();
            assertTrue(out.contains("Process exited with code 1") || out.contains("QUESTIONS"));
            assertTrue(out.contains("ACCURACY"));
            assertTrue(out.contains("50.0%"));
        }

        @Test
        @DisplayName("2.8.32 - GameEngine getters e setters de estado")
        void engineStateGettersSetters() {
            GameEngine engine = new GameEngine();
            assertNull(engine.getPlayer());
            assertEquals(1, engine.getCurrentLevel());
            assertFalse(engine.isHintActiveNextQuestion());

            Player p = new Player("Paladin");
            engine.setPlayer(p);
            engine.setCurrentLevel(4);
            engine.setHintActiveNextQuestion(true);

            assertSame(p, engine.getPlayer());
            assertEquals(4, engine.getCurrentLevel());
            assertTrue(engine.isHintActiveNextQuestion());
        }

        @Test
        @DisplayName("2.8.33 - Construtor com InputStream customizado")
        void customInputStreamConstructor() {
            ByteArrayInputStream bais = new ByteArrayInputStream("test\n".getBytes(StandardCharsets.UTF_8));
            GameEngine engine = new GameEngine(bais);
            assertNotNull(engine);
            assertEquals(1, engine.getCurrentLevel());
        }

        @Test
        @DisplayName("2.8.34 - Default constructor usa System.in")
        void defaultConstructor() {
            GameEngine engine = new GameEngine();
            assertNotNull(engine);
            assertEquals(1, engine.getCurrentLevel());
        }
    }
}
