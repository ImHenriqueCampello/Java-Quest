package game;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("5.0 - SystemInvariantsTest: Contratos e Invariantes Imutáveis do Sistema")
public class SystemInvariantsTest {

    @Test
    @DisplayName("Invariante 1: Invariante de HP (0 <= HP <= MaxHP para Player e Inimigo, nunca negativo)")
    void invariantOne_hpBoundaries() {
        Player p = new Player("Hero");
        assertTrue(p.getHp() >= 0 && p.getHp() <= p.getMaxHp());

        p.takeDamage(9999);
        assertEquals(0, p.getHp(), "Player HP nunca pode ser negativo");

        p.heal(9999);
        assertEquals(p.getMaxHp(), p.getHp(), "Player HP nunca pode ultrapassar MaxHP");

        Enemy e = Enemy.createForLevel(1);
        assertTrue(e.getCurrentHp() >= 0 && e.getCurrentHp() <= e.getMaxHp());

        e.takeDamage(9999);
        assertEquals(0, e.getCurrentHp(), "Enemy HP nunca pode ser negativo");
    }

    @Test
    @DisplayName("Invariante 2: Invariante do Escudo (Mitigação Math.max(1, damage/2), consumo único e dano mín 1)")
    void invariantTwo_shieldMechanics() {
        Player p = new Player("Hero");
        p.setShieldActive(true);

        // Golpe 1 com dano 1: dano mínimo é 1
        int hit = p.takeDamage(1);
        assertEquals(1, hit);
        assertFalse(p.isShieldActive(), "Escudo é de uso único e deve desativar após 1 golpe");

        // Golpe seguinte: dano 100%
        int secondHit = p.takeDamage(10);
        assertEquals(10, secondHit);
    }

    @Test
    @DisplayName("Invariante 3: Invariante de Mana (0 <= Mana <= MaxMana, bloqueio sem saldo)")
    void invariantThree_manaBoundaries() {
        Player p = new Player("Hero");
        assertTrue(p.getMana() >= 0 && p.getMana() <= p.getMaxMana());

        p.spendMana(50); // Restam 10
        boolean allowed = p.spendMana(15);
        assertFalse(allowed, "Gasto sem saldo deve ser bloqueado");
        assertEquals(10, p.getMana(), "Zero de mana deve ser deduzida na recusa");

        p.restoreMana(9999);
        assertEquals(p.getMaxMana(), p.getMana(), "Mana nunca pode ultrapassar MaxMana");
    }

    @Test
    @DisplayName("Invariante 4: Invariante de Combo, Bônus de Magia e Recompensas de XP")
    void invariantFour_comboAndXpRules() {
        Player p = new Player("Hero");
        p.spendMana(40); // 20 MP

        // Streak 0 -> Spell Bonus 0
        assertEquals(0, p.getSpellDamageBonus());

        // Acerto 1: + (50 + 10) = 60 XP, +15 MP
        p.registerAnswer(true);
        assertEquals(1, p.getComboStreak());
        assertEquals(60, p.getXp());
        assertEquals(35, p.getMana());
        assertEquals(0, p.getSpellDamageBonus());

        // Acerto 2: + (50 + 20) = 130 XP, +15 MP -> 50 MP, Bonus +10
        p.registerAnswer(true);
        assertEquals(2, p.getComboStreak());
        assertEquals(130, p.getXp());
        assertEquals(50, p.getMana());
        assertEquals(10, p.getSpellDamageBonus());

        // Acerto 3: + (50 + 30) = 210 XP, +15 MP -> 60 MP (cap), Bonus +20
        p.registerAnswer(true);
        assertEquals(3, p.getComboStreak());
        assertEquals(210, p.getXp());
        assertEquals(60, p.getMana());
        assertEquals(20, p.getSpellDamageBonus());

        // Erro: ZERA comboStreak obrigatoriamente
        p.registerAnswer(false);
        assertEquals(0, p.getComboStreak());
        assertEquals(0, p.getSpellDamageBonus());
        assertEquals(210, p.getXp(), "Erro não altera XP");
    }

    @Test
    @DisplayName("Invariante 5: Invariante do Quiz (4 alternativas, índice 0..3, preservação na randomização)")
    void invariantFive_quizIntegrity() {
        for (int lvl = 1; lvl <= 6; lvl++) {
            List<Question> questions = QuestionBank.getQuestionsForLevel(lvl);
            for (Question q : questions) {
                assertEquals(4, q.getOptions().size(), "Toda pergunta DEVE conter 4 alternativas");
                assertTrue(q.getCorrectOptionIndex() >= 0 && q.getCorrectOptionIndex() < 4);

                String origAnswer = q.getOptions().get(q.getCorrectOptionIndex());
                Question r = q.withRandomizedOptions();
                String randAnswer = r.getOptions().get(r.getCorrectOptionIndex());
                assertEquals(origAnswer, randAnswer, "withRandomizedOptions deve preservar a resposta correta");
            }
        }
    }

    @Test
    @DisplayName("Invariante 6: Invariante da Arena (Largura interna 74, total 78, altura 17 linhas)")
    void invariantSix_arenaDimensions() {
        Player p = new Player("Hero");
        Enemy e = Enemy.createForLevel(1);

        PrintStream origOut = System.out;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(baos));
        try {
            BattleRenderer.renderFrame(p, e, CharacterSprite.PLAYER_IDLE_1, e.getIdle1(), null, "Invariant Test", false, false);
        } finally {
            System.setOut(origOut);
        }

        String plain = Ansi.stripAnsi(baos.toString()).replace("\r", "");
        String[] lines = plain.split("\n");

        assertEquals(17, lines.length, "Altura total da arena deve ser exatamente 17 linhas");
        for (int i = 0; i < lines.length; i++) {
            assertEquals(78, lines[i].length(), "Linha " + i + " da arena deve ter exatamente 78 colunas");
        }
    }
}
