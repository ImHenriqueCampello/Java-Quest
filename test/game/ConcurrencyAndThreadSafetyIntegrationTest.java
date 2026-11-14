package game;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("3.5 - ConcurrencyAndThreadSafetyIntegrationTest: IdleSession vs Scanner I/O")
public class ConcurrencyAndThreadSafetyIntegrationTest {

    private PrintStream origOut;

    @BeforeEach
    void setUp() {
        origOut = System.out;
        System.setOut(new PrintStream(new ByteArrayOutputStream()));
    }

    @AfterEach
    void tearDown() {
        System.setOut(origOut);
        Ansi.FAST_MODE = false;
    }

    @Test
    @DisplayName("3.5.1 - Inicialização e Encerramento sem Vazamento de Threads")
    void testIdleSessionThreadLifecycle() throws InterruptedException {
        Player player = new Player("Byte Paladin");
        Enemy enemy = Enemy.createForLevel(1);

        BattleRenderer.IdleSession session = BattleRenderer.startIdleAnimation(player, enemy, "Status Testing");
        assertNotNull(session);
        Thread thread = session.getThread();

        assertNotNull(thread, "Thread de animação deve ser criada");
        assertTrue(thread.isDaemon(), "Thread de animação DEVE ser daemon");
        assertTrue(thread.isAlive(), "Thread de animação deve estar viva após inicialização");

        // Atualiza mensagem de status concorrentemente
        session.setStatusMessage("Updated Status");

        // Fecha a sessão
        session.close();

        // Aguarda brevemente para garantia de término
        thread.join(500);
        assertFalse(thread.isAlive(), "Thread deve estar finalizada após session.close(), sem vazamentos");
    }

    @Test
    @DisplayName("3.5.2 - Digitação Concorrente no Terminal sem Perda de Dados")
    void testConcurrentTypingWithIdleAnimation() {
        Player player = new Player("Hero");
        Enemy enemy = Enemy.createForLevel(1);

        ByteArrayInputStream simulatedIn = new ByteArrayInputStream("B\n".getBytes(StandardCharsets.UTF_8));
        Scanner scanner = new Scanner(simulatedIn);

        String readChoice;
        try (BattleRenderer.IdleSession session = BattleRenderer.startIdleAnimation(player, enemy, "Waiting for user input")) {
            // Emite frames enquanto o usuário 'digita'
            for (int i = 0; i < 3; i++) {
                BattleRenderer.renderCombatIdleFrame(player, enemy, i, "Idle Frame " + i);
            }
            readChoice = scanner.nextLine();
        }

        assertEquals("B", readChoice, "A entrada do scanner não pode sofrer perda ou corrupção de caracteres");
    }

    @Test
    @DisplayName("3.5.3 - Preservação da Região Inferior do Terminal (inPlace = true e clearBelow = false)")
    void testLowerScreenRegionPreserved() {
        Player player = new Player("Hero");
        Enemy enemy = Enemy.createForLevel(1);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(baos));

        BattleRenderer.renderFrame(
            player, enemy,
            CharacterSprite.PLAYER_IDLE_1, enemy.getIdle1(),
            null, "Quiz Prompt Active",
            false, // clearBelow = false
            true   // inPlace = true
        );

        String output = baos.toString();

        // Deve conter save e restore de cursor
        assertTrue(output.contains("\0337\033[s"), "Deve salvar posição do cursor");
        assertTrue(output.contains("\033[u\0338"), "Deve restaurar posição do cursor");

        // NÃO pode conter código de limpeza de tela CLEAR_TO_END_OF_SCREEN (\033[J)
        assertFalse(output.contains(Ansi.CLEAR_TO_END_OF_SCREEN),
            "NÃO pode conter CLEAR_TO_END_OF_SCREEN para preservar o question card abaixo");
    }
}
