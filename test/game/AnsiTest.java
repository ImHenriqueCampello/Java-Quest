package game;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("2.5 - AnsiTest: Utilitários de Escape ANSI, Medição e Formatação de Texto")
public class AnsiTest {

    @Nested
    @DisplayName("2.5.1 - 2.5.7: Remoção de Códigos ANSI (stripAnsi)")
    class StripAnsiTests {

        @Test
        @DisplayName("2.5.1 - Texto puro permanece idêntico")
        void plainText() {
            assertEquals("Olá Mundo", Ansi.stripAnsi("Olá Mundo"));
        }

        @Test
        @DisplayName("2.5.2 - Remove cores padrão ANSI")
        void standardColors() {
            assertEquals("Erro", Ansi.stripAnsi("\u001B[31mErro\u001B[0m"));
        }

        @Test
        @DisplayName("2.5.3 - Remove cores bright ANSI")
        void brightColors() {
            assertEquals("Sucesso", Ansi.stripAnsi("\u001B[92mSucesso\u001B[0m"));
        }

        @Test
        @DisplayName("2.5.4 - Remove múltiplos estilos combinados (Bold, Underline, Cor)")
        void combinedStyles() {
            assertEquals("ALERTA", Ansi.stripAnsi("\u001B[1m\u001B[93m\u001B[4mALERTA\u001B[0m"));
        }

        @Test
        @DisplayName("2.5.5 - Remove comandos de tela e cursor")
        void cursorAndScreenCodes() {
            assertEquals("Texto", Ansi.stripAnsi("\033[H\033[KTexto\033[J"));
        }

        @Test
        @DisplayName("2.5.6 - String nula retorna string vazia com segurança")
        void nullReturnsEmpty() {
            assertEquals("", Ansi.stripAnsi(null));
        }

        @Test
        @DisplayName("2.5.7 - String vazia retorna string vazia")
        void emptyReturnsEmpty() {
            assertEquals("", Ansi.stripAnsi(""));
        }
    }

    @Nested
    @DisplayName("2.5.8 - 2.5.11: Medição de Comprimento Visível (getVisibleLength)")
    class VisibleLengthTests {

        @Test
        @DisplayName("2.5.8 - Texto puro retorna comprimento de caracteres")
        void plainTextLength() {
            assertEquals(4, Ansi.getVisibleLength("Java"));
        }

        @Test
        @DisplayName("2.5.9 - Texto com sequências ANSI ignora bytes de escape")
        void ansiFormattedLength() {
            String text = "\u001B[1m\u001B[91mPerigo\u001B[0m";
            assertEquals(6, Ansi.getVisibleLength(text)); // "Perigo" tem 6 caracteres
        }

        @Test
        @DisplayName("2.5.10 - String nula retorna comprimento 0")
        void nullStringLength() {
            assertEquals(0, Ansi.getVisibleLength(null));
        }

        @Test
        @DisplayName("2.5.11 - String vazia retorna comprimento 0")
        void emptyStringLength() {
            assertEquals(0, Ansi.getVisibleLength(""));
        }
    }

    @Nested
    @DisplayName("2.5.12 - 2.5.16: Preenchimento à Direita (padVisibleRight)")
    class PadRightTests {

        @Test
        @DisplayName("2.5.12 - Texto menor preenche com espaços à direita")
        void padRightShorter() {
            assertEquals("A    ", Ansi.padVisibleRight("A", 5));
        }

        @Test
        @DisplayName("2.5.13 - Texto com tamanho exato não adiciona espaços")
        void padRightExact() {
            assertEquals("Teste", Ansi.padVisibleRight("Teste", 5));
        }

        @Test
        @DisplayName("2.5.14 - Texto maior não é truncado")
        void padRightLonger() {
            assertEquals("Extremamente Longo", Ansi.padVisibleRight("Extremamente Longo", 5));
        }

        @Test
        @DisplayName("2.5.15 - Texto com ANSI calcula espaços baseado no tamanho visível")
        void padRightAnsi() {
            String colored = "\u001B[32mOK\u001B[0m"; // Visível: 2
            String padded = Ansi.padVisibleRight(colored, 5);
            assertEquals(5, Ansi.getVisibleLength(padded));
            assertTrue(padded.endsWith("   "));
        }

        @Test
        @DisplayName("2.5.16 - String nula preenche com N espaços")
        void padRightNull() {
            assertEquals("    ", Ansi.padVisibleRight(null, 4));
        }
    }

    @Nested
    @DisplayName("2.5.17 - 2.5.19: Preenchimento à Esquerda (padVisibleLeft)")
    class PadLeftTests {

        @Test
        @DisplayName("2.5.17 - Texto menor preenche com espaços à esquerda")
        void padLeftShorter() {
            assertEquals("    B", Ansi.padVisibleLeft("B", 5));
        }

        @Test
        @DisplayName("2.5.18 - Texto colorido ANSI preenche à esquerda corretamente")
        void padLeftAnsi() {
            String colored = "\u001B[31mFIM\u001B[0m"; // Visível: 3
            String padded = Ansi.padVisibleLeft(colored, 6);
            assertEquals(6, Ansi.getVisibleLength(padded));
            assertTrue(padded.startsWith("   "));
        }

        @Test
        @DisplayName("2.5.19 - String nula preenche com N espaços")
        void padLeftNull() {
            assertEquals("   ", Ansi.padVisibleLeft(null, 3));
        }
    }

    @Nested
    @DisplayName("2.5.20 - 2.5.24: Centralização de Texto (center)")
    class CenterTests {

        @Test
        @DisplayName("2.5.20 - Centralização com diferença par distribui espaços igualmente")
        void centerEvenDifference() {
            assertEquals("  A  ", Ansi.center("A", 5)); // 5 - 1 = 4 -> 2 esquerda, 2 direita
        }

        @Test
        @DisplayName("2.5.21 - Centralização com diferença ímpar")
        void centerOddDifference() {
            assertEquals(" AB  ", Ansi.center("AB", 5)); // 5 - 2 = 3 -> 1 esquerda, 2 direita
        }

        @Test
        @DisplayName("2.5.22 - Texto maior ou igual não é alterado")
        void centerEqualOrLonger() {
            assertEquals("Grande", Ansi.center("Grande", 4));
            assertEquals("Cinco", Ansi.center("Cinco", 5));
        }

        @Test
        @DisplayName("2.5.23 - Centralização com ANSI baseia-se no tamanho visível")
        void centerAnsi() {
            String colored = "\u001B[33mX\u001B[0m";
            String centered = Ansi.center(colored, 5);
            assertEquals(5, Ansi.getVisibleLength(centered));
            assertTrue(centered.startsWith("  "));
            assertTrue(centered.endsWith("  "));
        }

        @Test
        @DisplayName("2.5.24 - String nula centralizada gera N espaços")
        void centerNull() {
            assertEquals("    ", Ansi.center(null, 4));
        }
    }

    @Nested
    @DisplayName("2.5.25 - 2.5.40: Formatação de Cor e Constantes")
    class ColorizeAndConstantsTests {

        @Test
        @DisplayName("2.5.25 - colorize envolve o texto no código de cor e finaliza com RESET")
        void colorizeWrapsWithReset() {
            String colored = Ansi.colorize("Byte", Ansi.CYAN);
            assertEquals(Ansi.CYAN + "Byte" + Ansi.RESET, colored);
        }

        @Test
        @DisplayName("2.5.26 - Constante RESET retorna código de reset ANSI")
        void resetConstant() {
            assertEquals("\u001B[0m", Ansi.RESET);
        }

        @Test
        @DisplayName("2.5.27 - Constantes BOLD e DIM")
        void boldAndDimConstants() {
            assertEquals("\u001B[1m", Ansi.BOLD);
            assertEquals("\u001B[2m", Ansi.DIM);
        }

        @Test
        @DisplayName("2.5.28 - Constantes ITALIC e UNDERLINE")
        void italicAndUnderlineConstants() {
            assertEquals("\u001B[3m", Ansi.ITALIC);
            assertEquals("\u001B[4m", Ansi.UNDERLINE);
        }

        @Test
        @DisplayName("2.5.29 - Cores primárias de primeiro plano (RED, GREEN, YELLOW, BLUE)")
        void primaryColors() {
            assertEquals("\u001B[31m", Ansi.RED);
            assertEquals("\u001B[32m", Ansi.GREEN);
            assertEquals("\u001B[33m", Ansi.YELLOW);
            assertEquals("\u001B[34m", Ansi.BLUE);
        }

        @Test
        @DisplayName("2.5.30 - Cores secundárias de primeiro plano (MAGENTA, CYAN, WHITE)")
        void secondaryColors() {
            assertEquals("\u001B[35m", Ansi.MAGENTA);
            assertEquals("\u001B[36m", Ansi.CYAN);
            assertEquals("\u001B[37m", Ansi.WHITE);
        }

        @Test
        @DisplayName("2.5.31 - Cores bright (BRIGHT_BLACK, BRIGHT_RED, BRIGHT_GREEN, BRIGHT_YELLOW)")
        void brightColorsPartOne() {
            assertEquals("\u001B[90m", Ansi.BRIGHT_BLACK);
            assertEquals("\u001B[91m", Ansi.BRIGHT_RED);
            assertEquals("\u001B[92m", Ansi.BRIGHT_GREEN);
            assertEquals("\u001B[93m", Ansi.BRIGHT_YELLOW);
        }

        @Test
        @DisplayName("2.5.32 - Cores bright adicionais (BRIGHT_BLUE, BRIGHT_MAGENTA, BRIGHT_CYAN, BRIGHT_WHITE)")
        void brightColorsPartTwo() {
            assertEquals("\u001B[94m", Ansi.BRIGHT_BLUE);
            assertEquals("\u001B[95m", Ansi.BRIGHT_MAGENTA);
            assertEquals("\u001B[96m", Ansi.BRIGHT_CYAN);
            assertEquals("\u001B[97m", Ansi.BRIGHT_WHITE);
        }

        @Test
        @DisplayName("2.5.33 - Controle de cursor CURSOR_HOME")
        void cursorHomeConstant() {
            assertEquals("\033[H", Ansi.CURSOR_HOME);
        }

        @Test
        @DisplayName("2.5.34 - Controle de cursor HIDE_CURSOR e SHOW_CURSOR")
        void cursorVisibilityConstants() {
            assertEquals("\033[?25l", Ansi.HIDE_CURSOR);
            assertEquals("\033[?25h", Ansi.SHOW_CURSOR);
        }

        @Test
        @DisplayName("2.5.35 - Limpeza CLEAR_TO_END_OF_LINE e CLEAR_TO_END_OF_SCREEN")
        void clearConstants() {
            assertEquals("\033[K", Ansi.CLEAR_TO_END_OF_LINE);
            assertEquals("\033[J", Ansi.CLEAR_TO_END_OF_SCREEN);
        }

        @Test
        @DisplayName("2.5.36 - Alternância de tela ENTER_ALT_SCREEN e EXIT_ALT_SCREEN")
        void altScreenConstants() {
            assertEquals("\033[?1049h", Ansi.ENTER_ALT_SCREEN);
            assertEquals("\033[?1049l", Ansi.EXIT_ALT_SCREEN);
        }

        @Test
        @DisplayName("2.5.37 - Sincronização de terminal SYNC_START e SYNC_END")
        void syncConstants() {
            assertEquals("\033[?2026h", Ansi.SYNC_START);
            assertEquals("\033[?2026l", Ansi.SYNC_END);
        }

        @Test
        @DisplayName("2.5.38 - TARGET_FPS e FRAME_MS")
        void fpsConstants() {
            assertEquals(30, Ansi.TARGET_FPS);
            assertEquals(1000 / 30, Ansi.FRAME_MS);
        }

        @Test
        @DisplayName("2.5.39 - UNIFIED_ANIM_SPEED_MS")
        void animSpeedConstant() {
            assertEquals(25, Ansi.UNIFIED_ANIM_SPEED_MS);
        }

        @Test
        @DisplayName("2.5.40 - FAST_MODE flag de bypass de sleep para testes rápidos")
        void fastModeFlag() {
            boolean original = Ansi.FAST_MODE;
            try {
                Ansi.FAST_MODE = true;
                long start = System.currentTimeMillis();
                Ansi.sleep(500); // Não deve dormir
                long elapsed = System.currentTimeMillis() - start;
                assertTrue(elapsed < 100, "Com FAST_MODE ativo, Ansi.sleep retorna imediatamente");
            } finally {
                Ansi.FAST_MODE = original;
            }
        }
    }
}
