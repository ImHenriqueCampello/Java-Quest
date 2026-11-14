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
import java.io.File;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("6.0 - PolishingFeaturesTest: Validação das Melhorias das Fases 1 e 2")
public class PolishingFeaturesTest {

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
        Ansi.clearCompactModeOverride();
    }

    @Nested
    @DisplayName("6.1: Atalhos Híbridos no Teclado (7.1)")
    class HybridShortcutsTests {

        @ParameterizedTest(name = "Entrada \"{0}\" mapeia para menu principal \"{1}\"")
        @CsvSource({
            "1, 1", "s, 1", "S, 1", "n, 1", "N, 1", "start, 1", "START, 1", "new, 1",
            "2, 2", "h, 2", "H, 2", "t, 2", "how, 2", "help, 2",
            "3, 3", "c, 3", "C, 3", "l, 3", "cheat, 3", "lore, 3",
            "4, 4", "e, 4", "q, 4", "Q, 4", "exit, 4", "quit, 4"
        })
        @DisplayName("6.1.1 - Parsing de opções híbridas no menu principal")
        void mainMenuHybridParsing(String input, String expected) {
            assertEquals(expected, GameEngine.parseMainMenuChoice(input));
        }

        @ParameterizedTest(name = "Entrada \"{0}\" mapeia para ação de combate \"{1}\"")
        @CsvSource({
            "1, 1", "c, 1", "C, 1", "a, 1", "A, 1", "s, 1", "cast, 1", "attack, 1", "spell, 1",
            "2, 2", "d, 2", "D, 2", "p, 2", "P, 2", "debug, 2", "power, 2", "powers, 2",
            "3, 3", "i, 3", "I, 3", "l, 3", "inspect, 3", "lore, 3", "info, 3",
            "4, 4", "r, 4", "R, 4", "q, 4", "Q, 4", "f, 4", "retreat, 4", "surrender, 4", "quit, 4"
        })
        @DisplayName("6.1.2 - Parsing de atalhos híbridos de turno de combate (1/C, 2/D, 3/I, 4/Q)")
        void combatActionHybridParsing(String input, String expected) {
            assertEquals(expected, GameEngine.parseCombatAction(input));
        }

        @ParameterizedTest(name = "Entrada \"{0}\" mapeia para poder debug \"{1}\"")
        @CsvSource({
            "A, A", "a, A", "1, A", "h, A", "H, A", "r, A", "heal, A", "refactor, A",
            "B, B", "b, B", "2, B", "p, B", "P, B", "hint, B", "print, B",
            "C, C", "c, C", "3, C", "s, C", "t, C", "shield, C", "try, C", "catch, C",
            "D, D", "d, D", "4, D", "q, D", "return, D", "back, D", "exit, D"
        })
        @DisplayName("6.1.3 - Parsing de poderes debug híbridos (A/1, B/2, C/3, D/4)")
        void debugPowersHybridParsing(String input, String expected) {
            assertEquals(expected, GameEngine.parseDebugPowerChoice(input));
        }

        @ParameterizedTest(name = "Entrada de retry \"{0}\" deve ser aceita")
        @ValueSource(strings = {"Y", "y", "yes", "YES", "S", "s", "sim", "1", "r", "R", "retry"})
        @DisplayName("6.1.4 - Parsing flexível de confirmação de retry no Game Over")
        void gameOverRetryAccept(String input) {
            assertTrue(GameEngine.parseGameOverChoice(input));
        }

        @ParameterizedTest(name = "Entrada de recusa \"{0}\" deve retornar false")
        @ValueSource(strings = {"N", "n", "no", "nao", "0", "q", "quit", "sair", ""})
        @DisplayName("6.1.5 - Parsing de encerramento no Game Over")
        void gameOverQuitAccept(String input) {
            assertFalse(GameEngine.parseGameOverChoice(input));
        }

        @Test
        @DisplayName("6.1.6 - Parsing seguro com valores nulos")
        void nullInputsSafety() {
            assertEquals("", GameEngine.parseMainMenuChoice(null));
            assertEquals("", GameEngine.parseCombatAction(null));
            assertEquals("", GameEngine.parseDebugPowerChoice(null));
            assertFalse(GameEngine.parseGameOverChoice(null));
        }
    }

    @Nested
    @DisplayName("6.2: Banco de Questões e Pool Dinâmico Não-Repetitivo (3.5)")
    class DynamicPoolTests {

        @Test
        @DisplayName("6.2.1 - getRandomizedQuestionsForLevel retorna perguntas com alternativas válidas")
        void randomizedQuestionsStructure() {
            for (int lvl = 1; lvl <= 6; lvl++) {
                List<Question> list = QuestionBank.getRandomizedQuestionsForLevel(lvl);
                assertNotNull(list);
                assertFalse(list.isEmpty());
                for (Question q : list) {
                    assertEquals(4, q.getOptions().size());
                    assertTrue(q.getCorrectOptionIndex() >= 0 && q.getCorrectOptionIndex() < 4);
                }
            }
        }

        @Test
        @DisplayName("6.2.2 - Múltiplas chamadas geram instâncias com ordem de alternativas funcionais")
        void randomizedDistribution() {
            Set<Integer> correctIndices = new HashSet<>();
            for (int i = 0; i < 30; i++) {
                List<Question> list = QuestionBank.getRandomizedQuestionsForLevel(1);
                for (Question q : list) {
                    correctIndices.add(q.getCorrectOptionIndex());
                }
            }
            // Verifica que alternativas A(0), B(1), C(2) e D(3) são todas contempladas
            assertTrue(correctIndices.contains(0));
            assertTrue(correctIndices.contains(1));
            assertTrue(correctIndices.contains(2));
            assertTrue(correctIndices.contains(3));
        }
    }

    @Nested
    @DisplayName("6.3: Terminal Compacto e Efeitos Visuais (4.1, 4.2, 4.4)")
    class TerminalCompactAndVisualTests {

        @Test
        @DisplayName("6.3.1 - Override de modo compacto funciona conforme configurado")
        void compactModeOverride() {
            Ansi.setCompactMode(true);
            assertTrue(Ansi.isCompactMode());

            Ansi.setCompactMode(false);
            assertFalse(Ansi.isCompactMode());

            Ansi.clearCompactModeOverride();
            assertNotNull(Ansi.isCompactMode());
        }

        @Test
        @DisplayName("6.3.2 - getTerminalHeight retorna valor positivo e seguro")
        void terminalHeightSafe() {
            int h = Ansi.getTerminalHeight();
            assertTrue(h > 0);
        }

        @Test
        @DisplayName("6.3.3 - screenShake executa com segurança sem lançar exceções")
        void screenShakeExecution() {
            assertDoesNotThrow(() -> Ansi.screenShake(3, 20));
        }

        @Test
        @DisplayName("6.3.4 - flashSprite estiliza todas as linhas com estilo de flash de dano")
        void flashSpriteStunEffect() {
            List<String> rawSprite = List.of("  [HERO]  ", "  (o_o)   ");
            List<String> flashed = BattleRenderer.flashSprite(rawSprite);
            assertEquals(2, flashed.size());
            for (String line : flashed) {
                assertTrue(line.contains("\033[97;41m"), "Linha deve conter cor de flash stun");
                assertTrue(line.endsWith(Ansi.RESET), "Linha deve encerrar com RESET");
            }
        }
    }

    @Nested
    @DisplayName("6.4: Relatório Pós-Jogo de Depuração e Exportação (3.4)")
    class PostMortemAnalysisTests {

        @Test
        @DisplayName("6.4.1 - Registro de questões erradas e integridade do relatório")
        void missedQuestionsTracking() {
            GameEngine engine = new GameEngine();
            assertTrue(engine.getMissedQuestions().isEmpty());

            Question q = new Question("Syntax", "What is an int?", List.of(), List.of("A", "B", "C", "D"), 0, "Explanation", 25);
            GameEngine.MissedQuestion mq = new GameEngine.MissedQuestion(q, 2, 1);
            assertEquals(q, mq.getQuestion());
            assertEquals(2, mq.getSelectedIndex());
            assertEquals(1, mq.getLevel());
        }

        @Test
        @DisplayName("6.4.2 - exportStudyReport gera arquivo físico com formato correto")
        void exportStudyReportFileCreation() throws Exception {
            GameEngine engine = new GameEngine(new ByteArrayInputStream("N\n".getBytes()));
            Player p = new Player("Debugger");
            engine.setPlayer(p);

            // Sem erros: export retorna false e não cria arquivo
            Path tempFile = Files.createTempFile("estudo_test_", ".txt");
            Files.deleteIfExists(tempFile);
            assertFalse(engine.exportStudyReport(tempFile.toString()));
            assertFalse(Files.exists(tempFile));

            // Simula execução onde uma pergunta é errada
            Enemy enemy = Enemy.createForLevel(1);
            Question q = new Question("OOP", "Explain Polymorphism", List.of("class A {}", "class B extends A {}"),
                List.of("Option A", "Option B", "Option C", "Option D"), 1, "Polymorphism allows dynamic dispatch.", 30);

            // Responde errado (escolhe 0 em vez de 1)
            ByteArrayInputStream in = new ByteArrayInputStream("A\n\n".getBytes());
            GameEngine customEngine = new GameEngine(in);
            customEngine.setPlayer(p);
            customEngine.handleQuizQuestion(q, enemy);

            assertEquals(1, customEngine.getMissedQuestions().size());

            // Exporta arquivo
            assertTrue(customEngine.exportStudyReport(tempFile.toString()));
            assertTrue(Files.exists(tempFile));

            String content = Files.readString(tempFile);
            assertTrue(content.contains("RELATÓRIO PÓS-JOGO DE ESTUDO E REVISÃO"));
            assertTrue(content.contains("Explain Polymorphism"));
            assertTrue(content.contains("Polymorphism allows dynamic dispatch"));
            assertTrue(content.contains("Debugger"));

            Files.deleteIfExists(tempFile);
        }
    }

    @Nested
    @DisplayName("6.5: Sistema de Áudio 8-Bit Procedural (5.1)")
    class AudioEngineTests {

        @Test
        @DisplayName("6.5.1 - Configuração de áudio ativado/desativado")
        void soundToggle() {
            boolean initial = AudioEngine.isSoundEnabled();
            AudioEngine.setSoundEnabled(false);
            assertFalse(AudioEngine.isSoundEnabled());
            AudioEngine.setSoundEnabled(true);
            assertTrue(AudioEngine.isSoundEnabled());
            AudioEngine.setSoundEnabled(initial);
        }

        @Test
        @DisplayName("6.5.2 - Invocação de efeitos sonoros sem lançar exceções")
        void soundEffectsInvocationSafety() {
            assertDoesNotThrow(() -> {
                AudioEngine.playAttack();
                AudioEngine.playHeal();
                AudioEngine.playShield();
                AudioEngine.playCriticalHit();
                AudioEngine.playHurt();
                AudioEngine.playVictory();
            });
        }

        @Test
        @DisplayName("6.5.3 - Enum de formas de onda e record de Tone")
        void audioToneRecord() {
            AudioEngine.Tone tone = new AudioEngine.Tone(440.0, 50, AudioEngine.Waveform.SQUARE);
            assertEquals(440.0, tone.frequency());
            assertEquals(50, tone.durationMs());
            assertEquals(AudioEngine.Waveform.SQUARE, tone.waveform());
        }
    }
}
