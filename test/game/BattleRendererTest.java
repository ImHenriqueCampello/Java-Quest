package game;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("2.7 - BattleRendererTest: Buffers Gráficos, Canvas e Barras de HUD")
public class BattleRendererTest {

    @Nested
    @DisplayName("2.7.1 - 2.7.8: Buffer Gráfico de Linha (CellRow)")
    class CellRowTests {

        @Test
        @DisplayName("2.7.1 - Inicialização vazia gera exatamente 26 espaços")
        void emptyRowInitializesToSpaces() {
            BattleRenderer.CellRow row = new BattleRenderer.CellRow();
            String rendered = row.render();
            assertEquals(" ".repeat(26), rendered);
            assertEquals(26, rendered.length());
        }

        @Test
        @DisplayName("2.7.2 - Inserção no início (col 0)")
        void putAtStart() {
            BattleRenderer.CellRow row = new BattleRenderer.CellRow();
            row.put(0, "ABC", null);
            String rendered = row.render();
            assertTrue(rendered.startsWith("ABC"));
            assertEquals(26, rendered.length());
        }

        @Test
        @DisplayName("2.7.3 - Inserção no meio (col 10)")
        void putInMiddle() {
            BattleRenderer.CellRow row = new BattleRenderer.CellRow();
            row.put(10, "XYZ", null);
            String rendered = row.render();
            assertEquals(" ".repeat(10) + "XYZ" + " ".repeat(13), rendered);
        }

        @Test
        @DisplayName("2.7.4 - Inserção que excede o limite superior é truncada em 26 colunas")
        void putClippingAtEnd() {
            BattleRenderer.CellRow row = new BattleRenderer.CellRow();
            row.put(24, "WXYZ", null); // Deve colocar 'W' em 24, 'X' em 25, 'Y' e 'Z' ignorados
            String rendered = row.render();
            assertEquals(26, rendered.length());
            assertTrue(rendered.endsWith("WX"));
        }

        @Test
        @DisplayName("2.7.5 - Inserção com coluna negativa posiciona apenas caracteres dentro do buffer")
        void putClippingNegativeCol() {
            BattleRenderer.CellRow row = new BattleRenderer.CellRow();
            row.put(-2, "ABCD", null); // -2='A', -1='B', 0='C', 1='D'
            String rendered = row.render();
            assertTrue(rendered.startsWith("CD"));
            assertEquals(26, rendered.length());
        }

        @Test
        @DisplayName("2.7.6 - Inserção totalmente fora de limites não altera o buffer")
        void putEntirelyOutOfBounds() {
            BattleRenderer.CellRow row = new BattleRenderer.CellRow();
            row.put(30, "FORA", null);
            row.put(-50, "FORA", null);
            assertEquals(" ".repeat(26), row.render());
        }

        @Test
        @DisplayName("2.7.7 - Inserção de texto nulo é ignorada com segurança")
        void putNullText() {
            BattleRenderer.CellRow row = new BattleRenderer.CellRow();
            row.put(5, null, Ansi.RED);
            assertEquals(" ".repeat(26), row.render());
        }

        @Test
        @DisplayName("2.7.8 - Renderização fecha cor com Ansi.RESET se o último caractere era colorido")
        void renderAppendsReset() {
            BattleRenderer.CellRow row = new BattleRenderer.CellRow();
            row.put(23, "END", Ansi.RED);
            String rendered = row.render();
            assertTrue(rendered.endsWith(Ansi.RESET));
        }
    }

    @Nested
    @DisplayName("2.7.9 - 2.7.11: Canvas Central da Arena (MidCanvas)")
    class MidCanvasTests {

        @Test
        @DisplayName("2.7.9 - MidCanvas inicializado possui 8 linhas de 26 colunas cada")
        void midCanvasInitialization() {
            BattleRenderer.MidCanvas canvas = new BattleRenderer.MidCanvas();
            List<String> rendered = canvas.render();
            assertEquals(CharacterSprite.SPRITE_HEIGHT, rendered.size());
            for (String line : rendered) {
                assertEquals(26, line.length());
                assertEquals(" ".repeat(26), line);
            }
        }

        @Test
        @DisplayName("2.7.10 - Inserção em linha válida atualiza apenas a linha correspondente")
        void putValidRow() {
            BattleRenderer.MidCanvas canvas = new BattleRenderer.MidCanvas();
            canvas.put(3, 5, "TEST", null);
            List<String> rendered = canvas.render();
            assertEquals(" ".repeat(26), rendered.get(0));
            assertTrue(rendered.get(3).contains("TEST"));
            assertEquals(" ".repeat(26), rendered.get(7));
        }

        @Test
        @DisplayName("2.7.11 - Inserção em linhas fora de limite (-1 ou 8) é segura")
        void putOutOfBoundsRow() {
            BattleRenderer.MidCanvas canvas = new BattleRenderer.MidCanvas();
            assertDoesNotThrow(() -> {
                canvas.put(-1, 0, "TEST", null);
                canvas.put(8, 0, "TEST", null);
                canvas.put(100, 0, "TEST", null);
            });
            List<String> rendered = canvas.render();
            assertEquals(8, rendered.size());
        }
    }

    @Nested
    @DisplayName("2.7.12: Nomes de Arenas por Nível")
    class ArenaNameTests {

        @ParameterizedTest(name = "Nível {0} tem nome de arena \"{1}\"")
        @CsvSource({
            "1, Syntax Swamp",
            "2, Recursion Ravine",
            "3, Array Archipelago",
            "4, OOP Citadel",
            "5, The Heap Tomb",
            "6, THE MONOLITH CORE",
            "0, Virtual Machine",
            "-1, Virtual Machine",
            "7, Virtual Machine",
            "99, Virtual Machine"
        })
        @DisplayName("2.7.12 - Mapeamento canônico de nome de arenas")
        void arenaNameMapping(int level, String expectedName) {
            assertEquals(expectedName, BattleRenderer.getArenaName(level));
        }
    }

    @Nested
    @DisplayName("2.7.13 - 2.7.18: Barra de Vida Dinâmica (getHpBar)")
    class HpBarTests {

        @Test
        @DisplayName("2.7.13 - Barra cheia (100%) contém todos os blocos cheios")
        void hpBarFull() {
            String bar = BattleRenderer.getHpBar(100, 100, 10, Ansi.BRIGHT_GREEN);
            String stripped = Ansi.stripAnsi(bar);
            assertEquals("█".repeat(10), stripped);
            assertTrue(bar.contains(Ansi.BRIGHT_GREEN));
        }

        @Test
        @DisplayName("2.7.14 - Barra vazia (0%) contém todos os blocos vazios")
        void hpBarEmpty() {
            String bar = BattleRenderer.getHpBar(0, 100, 10, Ansi.BRIGHT_GREEN);
            String stripped = Ansi.stripAnsi(bar);
            assertEquals("░".repeat(10), stripped);
        }

        @Test
        @DisplayName("2.7.15 - Barra meia vida (50%) contém 5 cheios e 5 vazios")
        void hpBarHalf() {
            String bar = BattleRenderer.getHpBar(50, 100, 10, Ansi.BRIGHT_GREEN);
            String stripped = Ansi.stripAnsi(bar);
            assertEquals("█".repeat(5) + "░".repeat(5), stripped);
        }

        @Test
        @DisplayName("2.7.16 - Cores dinâmicas de feedback de dano (Verde > 50%, Amarelo 25..50%, Vermelho <= 25%)")
        void hpBarDynamicColors() {
            String greenBar = BattleRenderer.getHpBar(60, 100, 10, Ansi.BRIGHT_GREEN);
            assertTrue(greenBar.contains(Ansi.BRIGHT_GREEN));

            String yellowBar = BattleRenderer.getHpBar(40, 100, 10, Ansi.BRIGHT_GREEN);
            assertTrue(yellowBar.contains(Ansi.BRIGHT_YELLOW));

            String redBar = BattleRenderer.getHpBar(20, 100, 10, Ansi.BRIGHT_GREEN);
            assertTrue(redBar.contains(Ansi.BRIGHT_RED));
        }

        @Test
        @DisplayName("2.7.17 - Proteção contra divisão por zero quando maxHp <= 0")
        void hpBarZeroMaxProtection() {
            assertDoesNotThrow(() -> {
                String bar = BattleRenderer.getHpBar(10, 0, 10, Ansi.BRIGHT_GREEN);
                assertNotNull(bar);
                String barNeg = BattleRenderer.getHpBar(10, -5, 10, Ansi.BRIGHT_GREEN);
                assertNotNull(barNeg);
            });
        }

        @Test
        @DisplayName("2.7.18 - Valores fora de limite são travados em 0% e 100%")
        void hpBarOutOfRangeClamped() {
            String overflow = BattleRenderer.getHpBar(150, 100, 10, Ansi.BRIGHT_GREEN);
            assertEquals("█".repeat(10), Ansi.stripAnsi(overflow));

            String underflow = BattleRenderer.getHpBar(-20, 100, 10, Ansi.BRIGHT_GREEN);
            assertEquals("░".repeat(10), Ansi.stripAnsi(underflow));
        }
    }

    @Nested
    @DisplayName("2.7.19 - 2.7.20: Barra de Mana (getManaBar)")
    class ManaBarTests {

        @Test
        @DisplayName("2.7.19 - Barra de mana proporcional com cor ciano fixa")
        void manaBarHalf() {
            String bar = BattleRenderer.getManaBar(30, 60, 8);
            String stripped = Ansi.stripAnsi(bar);
            assertEquals("█".repeat(4) + "░".repeat(4), stripped);
            assertTrue(bar.contains(Ansi.BRIGHT_CYAN));
        }

        @Test
        @DisplayName("2.7.20 - Limites e proteção de zero na mana")
        void manaBarBoundaries() {
            assertEquals("█".repeat(8), Ansi.stripAnsi(BattleRenderer.getManaBar(60, 60, 8)));
            assertEquals("░".repeat(8), Ansi.stripAnsi(BattleRenderer.getManaBar(0, 60, 8)));
            assertDoesNotThrow(() -> BattleRenderer.getManaBar(20, 0, 8));
        }
    }

    @Nested
    @DisplayName("2.7.21 - 2.7.25: Deslocamento de Sprites (shiftSpriteRight / Left)")
    class SpriteShiftTests {

        @Test
        @DisplayName("2.7.21 - shiftSpriteRight adiciona espaços à esquerda e preserva largura 24")
        void shiftRight() {
            List<String> original = List.of("123456789012345678901234");
            List<String> shifted = BattleRenderer.shiftSpriteRight(original, 2);
            assertEquals(1, shifted.size());
            assertEquals(24, shifted.get(0).length());
            assertTrue(shifted.get(0).startsWith("  1234"));
        }

        @Test
        @DisplayName("2.7.22 & 2.7.23 - shiftSpriteRight com zero, negativo ou nulo")
        void shiftRightEdgeCases() {
            List<String> original = List.of("123456789012345678901234");
            assertSame(original, BattleRenderer.shiftSpriteRight(original, 0));
            assertSame(original, BattleRenderer.shiftSpriteRight(original, -1));
            assertNull(BattleRenderer.shiftSpriteRight(null, 2));
        }

        @Test
        @DisplayName("2.7.24 - shiftSpriteLeft desloca caracteres à esquerda e preenche espaços no final")
        void shiftLeft() {
            List<String> original = List.of("123456789012345678901234");
            List<String> shifted = BattleRenderer.shiftSpriteLeft(original, 2);
            assertEquals(1, shifted.size());
            assertEquals(24, shifted.get(0).length());
            assertTrue(shifted.get(0).startsWith("3456"));
            assertTrue(shifted.get(0).endsWith("  "));
        }

        @Test
        @DisplayName("2.7.25 - shiftSpriteLeft com zero ou negativo retorna lista original")
        void shiftLeftEdgeCases() {
            List<String> original = List.of("123456789012345678901234");
            assertSame(original, BattleRenderer.shiftSpriteLeft(original, 0));
            assertSame(original, BattleRenderer.shiftSpriteLeft(original, -2));
            assertNull(BattleRenderer.shiftSpriteLeft(null, 2));
        }
    }

    @Nested
    @DisplayName("2.7.26 - 2.7.27: Sessão Idle e Renderização de Frame sem Erros")
    class IdleSessionTests {

        @Test
        @DisplayName("2.7.26 - IdleSession ciclo de vida: inicializa thread daemon e fecha rapidamente")
        void idleSessionLifecycle() {
            Player p = new Player("Hero");
            Enemy e = Enemy.createForLevel(1);

            // Redireciona System.out para evitar poluição visual nos testes
            PrintStream origOut = System.out;
            System.setOut(new PrintStream(new ByteArrayOutputStream()));
            try {
                BattleRenderer.IdleSession session = BattleRenderer.startIdleAnimation(p, e, "Testing");
                assertNotNull(session);
                session.setStatusMessage("Updated Status");
                session.close();
            } finally {
                System.setOut(origOut);
            }
        }

        @ParameterizedTest(name = "Frame {0} do ciclo idle renderiza sem erros")
        @ValueSource(ints = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 15})
        @DisplayName("2.7.27 - renderCombatIdleFrame executa para todas as etapas de bobbing")
        void renderCombatIdleFrameNoCrash(int frame) {
            Player p = new Player("Hero");
            Enemy e = Enemy.createForLevel(1);

            PrintStream origOut = System.out;
            System.setOut(new PrintStream(new ByteArrayOutputStream()));
            try {
                assertDoesNotThrow(() -> BattleRenderer.renderCombatIdleFrame(p, e, frame, "Idle Frame " + frame));
            } finally {
                System.setOut(origOut);
            }
        }
    }

    @Nested
    @DisplayName("2.7.28 - 2.7.48: renderFrame Overloads, Flags e Geometria da Arena")
    class RenderFrameAndArenaDimensionsTests {

        private String captureRenderOutput(Runnable action) {
            PrintStream origOut = System.out;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            System.setOut(new PrintStream(baos));
            try {
                action.run();
                return baos.toString();
            } finally {
                System.setOut(origOut);
            }
        }

        @Test
        @DisplayName("2.7.28 - renderFrame com clearBelow = true emite CLEAR_TO_END_OF_SCREEN")
        void renderFrameClearBelowTrue() {
            Player p = new Player("Hero");
            Enemy e = Enemy.createForLevel(1);
            String out = captureRenderOutput(() ->
                BattleRenderer.renderFrame(p, e, CharacterSprite.PLAYER_IDLE_1, e.getIdle1(), null, "Status", true, false)
            );
            assertTrue(out.contains(Ansi.CLEAR_TO_END_OF_SCREEN), "Deve conter código de limpeza até o fim da tela");
        }

        @Test
        @DisplayName("2.7.29 - renderFrame com clearBelow = false NÃO emite CLEAR_TO_END_OF_SCREEN")
        void renderFrameClearBelowFalse() {
            Player p = new Player("Hero");
            Enemy e = Enemy.createForLevel(1);
            String out = captureRenderOutput(() ->
                BattleRenderer.renderFrame(p, e, CharacterSprite.PLAYER_IDLE_1, e.getIdle1(), null, "Status", false, false)
            );
            assertFalse(out.contains(Ansi.CLEAR_TO_END_OF_SCREEN), "Não deve limpar a parte inferior da tela");
        }

        @Test
        @DisplayName("2.7.30 - renderFrame com inPlace = true emite códigos de cursor save/restore DEC e SCO")
        void renderFrameInPlaceTrue() {
            Player p = new Player("Hero");
            Enemy e = Enemy.createForLevel(1);
            String out = captureRenderOutput(() ->
                BattleRenderer.renderFrame(p, e, CharacterSprite.PLAYER_IDLE_1, e.getIdle1(), null, "Status", false, true)
            );
            assertTrue(out.contains("\0337\033[s"), "Deve conter save de cursor");
            assertTrue(out.contains("\033[u\0338"), "Deve conter restore de cursor");
        }

        @Test
        @DisplayName("2.7.31 - renderFrame com inPlace = false NÃO emite códigos de restauração de cursor")
        void renderFrameInPlaceFalse() {
            Player p = new Player("Hero");
            Enemy e = Enemy.createForLevel(1);
            String out = captureRenderOutput(() ->
                BattleRenderer.renderFrame(p, e, CharacterSprite.PLAYER_IDLE_1, e.getIdle1(), null, "Status", false, false)
            );
            assertFalse(out.contains("\0337\033[s"));
            assertFalse(out.contains("\033[u\0338"));
        }

        @Test
        @DisplayName("2.7.32 - renderFrame emite SYNC_START, SYNC_END e CURSOR_HOME")
        void renderFrameSyncAndHome() {
            Player p = new Player("Hero");
            Enemy e = Enemy.createForLevel(1);
            String out = captureRenderOutput(() ->
                BattleRenderer.renderFrame(p, e, CharacterSprite.PLAYER_IDLE_1, e.getIdle1(), null, "Status")
            );
            assertTrue(out.contains(Ansi.SYNC_START), "Deve conter terminal synchronized update start");
            assertTrue(out.contains(Ansi.SYNC_END), "Deve conter terminal synchronized update end");
            assertTrue(out.contains(Ansi.CURSOR_HOME), "Deve conter cursor home");
        }

        @Test
        @DisplayName("2.7.33 - Sobrecarga com HP customizado respeita valores passados")
        void renderFrameCustomHpOverload() {
            Player p = new Player("Hero");
            Enemy e = Enemy.createForLevel(1);
            String out = captureRenderOutput(() ->
                BattleRenderer.renderFrame(p, e, 42, 17, CharacterSprite.PLAYER_IDLE_1, e.getIdle1(), null, "Custom HP")
            );
            assertTrue(out.contains("42/"), "Deve conter o HP customizado do jogador");
            assertTrue(out.contains("17/"), "Deve conter o HP customizado do monstro");
        }

        @Test
        @DisplayName("2.7.34 - Sobrecarga padrão sem flags assume clearBelow = true e inPlace = false")
        void renderFrameDefaultOverloadFlags() {
            Player p = new Player("Hero");
            Enemy e = Enemy.createForLevel(1);
            String out = captureRenderOutput(() ->
                BattleRenderer.renderFrame(p, e, CharacterSprite.PLAYER_IDLE_1, e.getIdle1(), null, "Default Overload")
            );
            assertTrue(out.contains(Ansi.CLEAR_TO_END_OF_SCREEN));
            assertFalse(out.contains("\0337\033[s"));
        }

        @Test
        @DisplayName("2.7.35 a 2.7.48 - Invariante Geométrico da Arena: 17 linhas e 78 colunas com bordas")
        void arenaGeometryIntegrity() {
            Player p = new Player("Hero");
            Enemy e = Enemy.createForLevel(1);
            String out = captureRenderOutput(() ->
                BattleRenderer.renderFrame(p, e, CharacterSprite.PLAYER_IDLE_1, e.getIdle1(), null, "Geometry Check", false, false)
            );

            // Isola as linhas visíveis do frame removendo sequências ANSI
            String clean = Ansi.stripAnsi(out).replace("\r", "");
            String[] lines = clean.split("\n");

            // Exatamente 17 linhas formam a arena
            assertEquals(17, lines.length, "A arena de combate deve ter rigorosamente 17 linhas de altura");

            // Linha 0 (Topo): ╔ + 76 '=' + ╗ = 78 colunas
            assertEquals(78, lines[0].length(), "Borda superior deve ter exatamente 78 caracteres");
            assertTrue(lines[0].startsWith("╔") && lines[0].endsWith("╗"));

            // Linha 5 e Linha 14 (Divisórias): ╠ + 76 '=' + ╣ = 78 colunas
            assertEquals(78, lines[5].length(), "Primeira divisória deve ter 78 caracteres");
            assertTrue(lines[5].startsWith("╠") && lines[5].endsWith("╣"));
            assertEquals(78, lines[14].length(), "Segunda divisória deve ter 78 caracteres");
            assertTrue(lines[14].startsWith("╠") && lines[14].endsWith("╣"));

            // Linha 16 (Base): ╚ + 76 '=' + ╝ = 78 colunas
            assertEquals(78, lines[16].length(), "Borda inferior deve ter exatamente 78 caracteres");
            assertTrue(lines[16].startsWith("╚") && lines[16].endsWith("╝"));

            // Linhas 6 a 13 (8 linhas de campo de batalha com sprites):
            for (int row = 6; row <= 13; row++) {
                assertEquals(78, lines[row].length(), "Linha de campo de batalha " + row + " deve ter 78 colunas");
                assertTrue(lines[row].startsWith("║ ") && lines[row].endsWith(" ║"));
            }
        }
    }
}
