package game;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("3.1 - CombatTurnIntegrationTest: Ciclo de Turno de Combate Completo")
public class CombatTurnIntegrationTest {

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
    @DisplayName("3.1.1 - Turno Completo com Acerto (Player Ataca Monstro)")
    void testTurnWithCorrectAnswer() {
        // Estado Inicial:
        Player player = new Player("Byte Paladin");
        player.spendMana(20); // MP: 40/60
        assertEquals(100, player.getHp());
        assertEquals(40, player.getMana());
        assertEquals(0, player.getComboStreak());
        assertEquals(0, player.getXp());

        Enemy enemy = Enemy.createForLevel(1); // NullPointer Imp (60 HP)
        assertEquals(60, enemy.getCurrentHp());

        Question q = new Question(
            "Syntax", "Is Java statically typed?",
            List.of(), List.of("Yes", "No", "Maybe", "Never"), 0, "Java is statically typed.", 25
        );

        // Turn Execution: Player answers correctly
        int baseDamage = q.getDamage(); // 25
        int comboBonus = player.getSpellDamageBonus(); // 0
        int totalDamage = GameEngine.calculateTotalDamage(baseDamage, comboBonus); // 25
        assertEquals(25, totalDamage);

        enemy.takeDamage(totalDamage);
        player.registerAnswer(true);

        // Asserções Integradas:
        assertEquals(35, enemy.getCurrentHp(), "Imp HP cai de 60 para 35");
        assertEquals(1, player.getComboStreak(), "Combo vira 1");
        assertEquals(60, player.getXp(), "XP vai para 60 (50 + 10)");
        assertEquals(55, player.getMana(), "Mana sobe de 40 para 55 (+15 MP)");
        assertFalse(enemy.isDefeated(), "Inimigo ainda vivo");
        assertFalse(player.isDefeated(), "Player ainda vivo");
    }

    @Test
    @DisplayName("3.1.2 - Sequência de Acertos com Ativação de Combo e Morte do Chefe")
    void testConsecutiveHitsWithComboKill() {
        Player player = new Player("Byte Paladin");
        player.spendMana(20); // 40 MP
        Enemy enemy = Enemy.createForLevel(1); // 60 HP

        // Turno 1
        Question q1 = new Question("T", "P1", List.of(), List.of("A", "B", "C", "D"), 0, "Exp", 25);
        int dmg1 = GameEngine.calculateTotalDamage(q1.getDamage(), player.getSpellDamageBonus());
        enemy.takeDamage(dmg1);
        player.registerAnswer(true);

        assertEquals(35, enemy.getCurrentHp());
        assertEquals(1, player.getComboStreak());
        assertEquals(55, player.getMana());

        // Turno 2: Segunda pergunta de dano 25
        Question q2 = new Question("T", "P2", List.of(), List.of("A", "B", "C", "D"), 1, "Exp", 25);
        // Player combo é 1 agora, ao calcular spell damage bonus para este ataque ainda é 0
        // Após acertar, combo vai para 2
        // Mas se o turno anterior já gerou combo 2:
        player.registerAnswer(true); // combo 2 agora!
        assertEquals(2, player.getComboStreak());
        assertEquals(10, player.getSpellDamageBonus(), "Bônus de magia de +10 ativo!");

        int dmg2 = GameEngine.calculateTotalDamage(q2.getDamage(), player.getSpellDamageBonus());
        assertEquals(35, dmg2, "Dano total é 25 base + 10 bônus = 35");

        enemy.takeDamage(dmg2);
        assertEquals(0, enemy.getCurrentHp(), "HP do Imp cai para 0");
        assertTrue(enemy.isDefeated(), "Inimigo derrotado com sucesso!");
        assertEquals(60, player.getMana(), "Mana atinge o teto máximo de 60");
    }

    @Test
    @DisplayName("3.1.3 - Turno Completo com Erro (Contra-Ataque do Inimigo)")
    void testTurnWithWrongAnswerCounterAttack() {
        Player player = new Player("Byte Paladin");
        // Ativa combo 2
        player.registerAnswer(true);
        player.registerAnswer(true);
        assertEquals(2, player.getComboStreak());
        assertEquals(2, player.getHighestStreak());
        assertEquals(100, player.getHp());

        Enemy enemy = Enemy.createForLevel(1); // Imp 35 HP, ATK 15
        enemy.takeDamage(25);
        assertEquals(35, enemy.getCurrentHp());

        // Player erra a pergunta
        player.registerAnswer(false);

        // Asserções Integradas:
        assertEquals(35, enemy.getCurrentHp(), "Inimigo não sofre dano");
        assertEquals(0, player.getComboStreak(), "Combo resetado para 0");
        assertEquals(2, player.getHighestStreak(), "Highest streak preservado em 2");

        // Monstro contra-ataca aplicando 15 de dano
        int damageTaken = player.takeDamage(enemy.getAttackDamage());
        assertEquals(15, damageTaken);
        assertEquals(85, player.getHp(), "HP cai de 100 para 85");
        assertFalse(player.isDefeated());
    }

    @Test
    @DisplayName("3.1.4 - Turno com Erro Causando Morte do Jogador (Game Over)")
    void testTurnWithPlayerDefeat() {
        Player player = new Player("Byte Paladin");
        player.takeDamage(90); // 10 HP restante
        assertEquals(10, player.getHp());
        assertFalse(player.isDefeated());

        Enemy chimera = Enemy.createForLevel(4); // Chimera: 30 ATK
        assertEquals(30, chimera.getAttackDamage());

        // Player erra a pergunta e Chimera ataca
        player.registerAnswer(false);
        player.takeDamage(chimera.getAttackDamage());

        assertEquals(0, player.getHp(), "HP crava em 0");
        assertTrue(player.isDefeated(), "Player derrotado, sinalização de Game Over");
    }
}
