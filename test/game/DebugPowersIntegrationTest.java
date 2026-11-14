package game;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("3.2 - DebugPowersIntegrationTest: Integração de Poderes de Debug e Estado")
public class DebugPowersIntegrationTest {

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
    @DisplayName("3.2.1 - Poder Refactor Heal Integrado: gasta 20 MP e cura 35 HP")
    void testRefactorHealIntegration() {
        Player player = new Player("Byte Paladin");
        player.takeDamage(70); // HP 30 / 100
        player.spendMana(10);  // MP 50 / 60
        assertEquals(30, player.getHp());
        assertEquals(50, player.getMana());

        Enemy enemy = Enemy.createForLevel(1);
        int enemyStartHp = enemy.getCurrentHp();

        // Ação: invocar Heal (Custo 20 MP, Cura +35 HP)
        boolean spent = player.spendMana(20);
        assertTrue(spent);
        player.heal(35);

        // Asserções:
        assertEquals(30, player.getMana(), "Mana cai de 50 para 30");
        assertEquals(65, player.getHp(), "HP sobe de 30 para 65");
        assertEquals(enemyStartHp, enemy.getCurrentHp(), "Inimigo não sofreu alteração");
    }

    @Test
    @DisplayName("3.2.2 - PrintStackTrace Hint Integrado: descarta 2 opções incorretas e desarma flag")
    void testPrintStackTraceHintIntegration() {
        Player player = new Player("Byte Paladin");
        assertEquals(60, player.getMana());

        GameEngine engine = new GameEngine(new ByteArrayInputStream("A\n\n".getBytes(StandardCharsets.UTF_8)));
        engine.setPlayer(player);
        assertFalse(engine.isHintActiveNextQuestion());

        // Ação 1: Ativar hint
        boolean spent = player.spendMana(15);
        assertTrue(spent);
        engine.setHintActiveNextQuestion(true);
        assertEquals(45, player.getMana());
        assertTrue(engine.isHintActiveNextQuestion());

        // Simulação da lógica de eliminação do quiz
        Question q = new Question(
            "Topic", "Prompt", List.of(),
            List.of("Option A", "Option B", "Option C", "Option D"),
            2, // Option C is correct
            "Explanation", 25
        );

        List<Integer> eliminated = new ArrayList<>();
        if (engine.isHintActiveNextQuestion()) {
            engine.setHintActiveNextQuestion(false); // Desarmada imediatamente
            List<Integer> wrongIndices = new ArrayList<>();
            for (int i = 0; i < q.getOptions().size(); i++) {
                if (i != q.getCorrectOptionIndex()) {
                    wrongIndices.add(i);
                }
            }
            Collections.shuffle(wrongIndices);
            for (int i = 0; i < Math.min(2, wrongIndices.size()); i++) {
                eliminated.add(wrongIndices.get(i));
            }
        }

        // Asserções 2:
        assertEquals(2, eliminated.size(), "Exatamente 2 alternativas incorretas devem ser eliminadas");
        assertFalse(eliminated.contains(q.getCorrectOptionIndex()), "A resposta correta NUNCA pode ser eliminada");
        assertFalse(engine.isHintActiveNextQuestion(), "A flag deve estar desarmada para as próximas perguntas");
    }

    @Test
    @DisplayName("3.2.3 - Try-Catch Shield Integrado: reduz dano à metade, consome escudo e próximo golpe é pleno")
    void testTryCatchShieldIntegration() {
        Player player = new Player("Byte Paladin");
        assertEquals(100, player.getHp());
        assertEquals(60, player.getMana());
        assertFalse(player.isShieldActive());

        // Ação 1: Ativar Try-Catch Shield (Custo 15 MP)
        assertTrue(player.spendMana(15));
        player.setShieldActive(true);
        assertEquals(45, player.getMana());
        assertTrue(player.isShieldActive());

        // Ação 2: Player erra pergunta; Golem ataca com 25 de dano
        Enemy golem = Enemy.createForLevel(3); // ATK: 25
        int firstHit = player.takeDamage(golem.getAttackDamage());

        assertEquals(12, firstHit, "Dano mitigado deve ser 25 / 2 = 12");
        assertEquals(88, player.getHp(), "HP cai de 100 para 88");
        assertFalse(player.isShieldActive(), "Escudo deve ser desativado após o primeiro golpe");

        // Ação 3: Segundo golpe do Golem sem escudo
        int secondHit = player.takeDamage(golem.getAttackDamage());
        assertEquals(25, secondHit, "Sem escudo, dano integral de 25 é aplicado");
        assertEquals(63, player.getHp(), "HP cai de 88 para 63");
    }

    @Test
    @DisplayName("3.2.4 - Tentativa de Uso de Poderes sem Mana Suficiente é recusada sem efeitos colaterais")
    void testInsufficientManaRejected() {
        Player player = new Player("Byte Paladin");
        player.spendMana(50); // MP restante: 10
        assertEquals(10, player.getMana());
        int startHp = player.getHp();

        // Tenta Heal (20 MP)
        assertFalse(player.spendMana(20), "Não deve conseguir gastar 20 MP com apenas 10");
        assertEquals(10, player.getMana());
        assertEquals(startHp, player.getHp(), "HP não deve ser alterado");

        // Tenta Hint (15 MP)
        assertFalse(player.spendMana(15));
        assertEquals(10, player.getMana());

        // Tenta Shield (15 MP)
        assertFalse(player.spendMana(15));
        assertEquals(10, player.getMana());
        assertFalse(player.isShieldActive(), "Escudo não deve ser ativado sem mana");
    }
}
