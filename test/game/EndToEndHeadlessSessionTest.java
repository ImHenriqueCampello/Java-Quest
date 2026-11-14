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

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("3.6 - EndToEndHeadlessSessionTest: Simulação Integral via Stream de Entrada")
public class EndToEndHeadlessSessionTest {

    private InputStream origIn;
    private PrintStream origOut;
    private ByteArrayOutputStream outContent;

    @BeforeEach
    void setUp() {
        Ansi.FAST_MODE = true;
        origIn = System.in;
        origOut = System.out;
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void tearDown() {
        System.setIn(origIn);
        System.setOut(origOut);
        Ansi.FAST_MODE = false;
    }

    @Test
    @DisplayName("3.6.1 - Partida Completa Simulada via Stream de Entrada (Headless E2E)")
    void testEndToEndHeadlessSession() {
        // Sequência de entrada completa:
        // 1. Menu Principal: "1" (Start New Adventure)
        // 2. Nome: "Byte Paladin"
        // 3. Dismiss Intro do Chefe: "\n"
        // 4. Ação do Turno: "1" (Cast Code Spell / Quiz)
        // 5. Resposta do Quiz: "A"
        // 6. Dismiss Debrief pós-quiz: "\n"
        // 7. Ação do Turno 2: "4" (Surrender / Retreat)
        // 8. Decisão de Game Over: "N" (Não retentar)
        // 9. Menu Principal: "2" (Guia How to Play)
        // 10. Dismiss Guia: "\n"
        // 11. Menu Principal: "4" (Exit Game)
        String simulatedInputs = String.join("\n",
            "1",
            "Byte Paladin",
            "",
            "1",
            "A",
            "",
            "4",
            "N",
            "2",
            "",
            "4"
        ) + "\n";

        ByteArrayInputStream in = new ByteArrayInputStream(simulatedInputs.getBytes(StandardCharsets.UTF_8));
        GameEngine engine = new GameEngine(in);

        assertDoesNotThrow(engine::start, "A execução integral de ponta a ponta não deve lançar exceções");

        String fullOutput = outContent.toString();

        // Asserções Integradas:
        assertTrue(fullOutput.contains("THE BYTECODE CHRONICLES"), "Deve conter o logo de título");
        assertTrue(fullOutput.contains("Byte Paladin"), "Deve registrar e exibir o nome do jogador");
        assertTrue(fullOutput.contains("NullPointer Imp"), "Deve carregar o chefe do nível 1");
        assertTrue(fullOutput.contains("HOW TO PLAY & SPELLS GUIDE"), "Deve exibir tela do guia");
        assertTrue(fullOutput.contains("Thanks for playing Java Quest"), "Deve conter mensagem de encerramento");
    }
}
