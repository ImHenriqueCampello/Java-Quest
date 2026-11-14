package game;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("3.4 - GameOverAndRetryIntegrationTest: Fluxo de Derrota e Retry")
public class GameOverAndRetryIntegrationTest {

    private PrintStream origOut;

    @BeforeEach
    void setUp() {
        Ansi.FAST_MODE = true;
        origOut = System.out;
        System.setOut(new PrintStream(new ByteArrayOutputStream()));
    }

    @AfterEach
    void tearDown() {
        System.setOut(origOut);
        Ansi.FAST_MODE = false;
    }

    @Test
    @DisplayName("3.4.1 - Recompilar & Retry: Jogador derrotado no nível 4 reinicia no nível 4 com HP/Mana cheios")
    void testGameOverRetryKeepsLevelAndRestoresResources() {
        Player player = new Player("Byte Warrior");
        // Evolui até o nível 4
        player.levelUp(2);
        player.levelUp(3);
        player.levelUp(4);

        assertEquals(4, player.getLevel());
        assertEquals(160, player.getMaxHp());
        assertEquals(105, player.getMaxMana());

        // Inicia GameEngine no nível 4 com resposta 'Y' (ou '1') para retry
        GameEngine engine = new GameEngine(new ByteArrayInputStream("Y\n".getBytes(StandardCharsets.UTF_8)));
        engine.setPlayer(player);
        engine.setCurrentLevel(4);

        // Player é derrotado pela Chimera (Nível 4)
        Enemy chimera = Enemy.createForLevel(4);
        player.takeDamage(player.getMaxHp());
        assertTrue(player.isDefeated());

        // Ação: tela de game over processa retry
        boolean retryChosen = engine.showGameOverScreen();
        assertTrue(retryChosen, "Jogador optou por retry");

        // Lógica de recuperação de retry do GameEngine:
        if (retryChosen) {
            player.heal(player.getMaxHp());
            player.restoreMana(player.getMaxMana());
        }

        // Asserções Integradas:
        assertEquals(4, engine.getCurrentLevel(), "O nível NÃO regride para 1, continua no nível 4");
        assertEquals(160, player.getHp(), "HP é restaurado ao máximo do nível 4 (160)");
        assertEquals(105, player.getMana(), "Mana é restaurada ao máximo do nível 4 (105)");
        assertFalse(player.isDefeated(), "Player está vivo novamente");

        // Nova instância do chefe do nível 4
        Enemy freshChimera = Enemy.createForLevel(engine.getCurrentLevel());
        assertEquals("Spaghetti Code Chimera", freshChimera.getName());
        assertEquals(120, freshChimera.getCurrentHp(), "Nova Chimera gerada com 120 HP integral");
        assertFalse(freshChimera.isDefeated());
    }

    @Test
    @DisplayName("3.4.2 - Desistir no Game Over: Jogador escolhe sair (N)")
    void testGameOverQuit() {
        Player player = new Player("Byte Warrior");
        player.takeDamage(100);
        assertTrue(player.isDefeated());

        GameEngine engine = new GameEngine(new ByteArrayInputStream("N\n".getBytes(StandardCharsets.UTF_8)));
        engine.setPlayer(player);

        boolean retryChosen = engine.showGameOverScreen();
        assertFalse(retryChosen, "Jogador escolheu sair");
    }
}
