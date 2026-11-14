package game;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("3.3 - FullCampaignProgressionTest: Níveis 1 ao 6, Level-Up e Vitória")
public class FullCampaignProgressionTest {

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
    @DisplayName("3.3.1 - Vitória no Nível 1 e Transição para Nível 2 com Level Up")
    void testLevelOneVictoryAndTransitionToLevelTwo() {
        Player player = new Player("Byte Paladin");
        Enemy imp = Enemy.createForLevel(1);

        // Danifica o jogador durante a luta
        player.takeDamage(40);
        player.spendMana(30);
        assertEquals(60, player.getHp());
        assertEquals(30, player.getMana());

        // Imp é derrotado
        imp.takeDamage(60);
        assertTrue(imp.isDefeated());

        // Transição para Nível 2
        int currentLevel = 1;
        currentLevel++;
        player.levelUp(currentLevel);

        assertEquals(2, currentLevel);
        assertEquals(2, player.getLevel());
        assertEquals(120, player.getMaxHp());
        assertEquals(120, player.getHp(), "HP totalmente restaurado no Level Up");
        assertEquals(75, player.getMaxMana());
        assertEquals(75, player.getMana(), "Mana totalmente restaurada no Level Up");

        Enemy serpent = Enemy.createForLevel(currentLevel);
        assertEquals("Infinite Loop Serpent", serpent.getName());
        assertEquals(80, serpent.getMaxHp());
        assertEquals(20, serpent.getAttackDamage());

        List<Question> levelTwoQuestions = QuestionBank.getQuestionsForLevel(2);
        assertNotNull(levelTwoQuestions);
        assertTrue(levelTwoQuestions.size() >= 5);
    }

    @Test
    @DisplayName("3.3.2 - Campanha Completa: Nível 1 até Vitória Final no Nível 6")
    void testFullCampaignProgressionLevelsOneToSix() {
        Player player = new Player("Ada Lovelace");
        GameEngine engine = new GameEngine(new ByteArrayInputStream("\n\n\n\n\n\n\n".getBytes(StandardCharsets.UTF_8)));
        engine.setPlayer(player);

        String[] bossNames = {
            "NullPointer Imp",
            "Infinite Loop Serpent",
            "IndexOutOfBounds Golem",
            "Spaghetti Code Chimera",
            "Memory Leak Hydra",
            "THE STACKOVERFLOW DRAGON"
        };

        int[] bossHps = {60, 80, 100, 120, 140, 180};

        for (int lvl = 1; lvl <= 6; lvl++) {
            engine.setCurrentLevel(lvl);
            Enemy boss = Enemy.createForLevel(lvl);
            assertEquals(bossNames[lvl - 1], boss.getName());
            assertEquals(bossHps[lvl - 1], boss.getMaxHp());

            // Simula combate: acerta perguntas consecutivas até derrotar o monstro
            while (!boss.isDefeated()) {
                player.registerAnswer(true);
                int dmg = GameEngine.calculateTotalDamage(35, player.getSpellDamageBonus());
                boss.takeDamage(dmg);
            }
            assertTrue(boss.isDefeated(), "Chefe do nível " + lvl + " deve ser derrotado");

            if (lvl < 6) {
                player.levelUp(lvl + 1);
                assertEquals(lvl + 1, player.getLevel());
                assertEquals(100 + (lvl * 20), player.getMaxHp());
                assertEquals(60 + (lvl * 15), player.getMaxMana());
            } else {
                // Nível 6 concluído: vitória do jogo!
                assertDoesNotThrow(engine::showGameWonScreen);
            }
        }

        // Asserções Integradas ao final da campanha:
        assertEquals(6, engine.getCurrentLevel());
        assertEquals(6, player.getLevel());
        assertEquals(200, player.getMaxHp());
        assertEquals(135, player.getMaxMana());
        assertTrue(player.getQuestionsAnswered() > 0);
        assertEquals(player.getQuestionsAnswered(), player.getCorrectAnswers(), "100% de precisão");
        assertEquals(player.getQuestionsAnswered(), player.getHighestStreak(), "Streak contínuo preservado");
        assertTrue(player.getXp() > 0, "XP substancial acumulado");
    }
}
