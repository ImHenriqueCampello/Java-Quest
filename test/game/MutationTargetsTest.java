package game;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("4.0 - MutationTargetsTest: Validação da Matriz de Testes de Mutação (MUT-01 a MUT-10)")
public class MutationTargetsTest {

    @Test
    @DisplayName("MUT-01: Mitigação do escudo é estritamente metade do dano (Math.max(1, damage / 2))")
    void mut01_shieldDamageMitigation() {
        Player p = new Player("Hero");
        p.setShieldActive(true);
        // Dano 40 -> 20 de dano com escudo. Se mutar para damage / 3 (13) ou damage (40), falha.
        int applied = p.takeDamage(40);
        assertEquals(20, applied, "MUT-01: Dano com escudo deve ser rigorosamente damage / 2 (20)");
        assertEquals(80, p.getHp());
    }

    @Test
    @DisplayName("MUT-02: Escudo deve ser consumido imediatamente após o primeiro golpe (shieldActive = false)")
    void mut02_shieldConsumedAfterHit() {
        Player p = new Player("Hero");
        p.setShieldActive(true);
        p.takeDamage(10);
        assertFalse(p.isShieldActive(), "MUT-02: shieldActive deve virar false após golpe");
        // Segundo golpe consecutivo toma 100% do dano
        int secondHit = p.takeDamage(10);
        assertEquals(10, secondHit, "MUT-02: Segundo golpe sem escudo deve aplicar dano integral");
    }

    @Test
    @DisplayName("MUT-03: Overheal é limitado pelo MaxHP (Math.min(maxHp, hp + val))")
    void mut03_overhealCapped() {
        Player p = new Player("Hero");
        p.takeDamage(10); // 90 HP
        p.heal(50);
        assertEquals(100, p.getHp(), "MUT-03: Cura nunca pode exceder maxHp");
    }

    @Test
    @DisplayName("MUT-04: Gastar exatamente toda a mana deve retornar true (mana >= cost)")
    void mut04_spendExactMana() {
        Player p = new Player("Hero"); // 60 MP
        boolean spent = p.spendMana(60);
        assertTrue(spent, "MUT-04: Gastar mana exata ao saldo deve ser permitido (>=)");
        assertEquals(0, p.getMana());
        // Com 0, gastar 1 deve falhar
        assertFalse(p.spendMana(1));
    }

    @Test
    @DisplayName("MUT-05: Resposta incorreta zera rigorosamente o comboStreak para 0")
    void mut05_wrongAnswerResetsCombo() {
        Player p = new Player("Hero");
        p.registerAnswer(true);
        p.registerAnswer(true);
        assertEquals(2, p.getComboStreak());
        p.registerAnswer(false);
        assertEquals(0, p.getComboStreak(), "MUT-05: Erro de resposta deve zerar comboStreak");
    }

    @Test
    @DisplayName("MUT-06: Bônus de magia no streak >= 3 deve ser estritamente 20 (não 15)")
    void mut06_spellBonusAtStreak3() {
        Player p = new Player("Hero");
        p.registerAnswer(true);
        p.registerAnswer(true);
        p.registerAnswer(true); // streak 3
        assertEquals(20, p.getSpellDamageBonus(), "MUT-06: Bônus de magia no streak 3 deve ser rigorosamente 20");
    }

    @Test
    @DisplayName("MUT-07: withRandomizedOptions preserva estritamente a alternativa correta")
    void mut07_randomizationPreservesAnswer() {
        Question q = new Question(
            "Topic", "Prompt", List.of(),
            List.of("Option A", "Option B", "Option C", "Option D"),
            2, "Expl", 25
        );
        String originalCorrect = q.getOptions().get(q.getCorrectOptionIndex()); // "Option C"

        for (int i = 0; i < 20; i++) {
            Question r = q.withRandomizedOptions();
            String randomizedCorrect = r.getOptions().get(r.getCorrectOptionIndex());
            assertEquals(originalCorrect, randomizedCorrect,
                "MUT-07: A alternativa correta nunca pode ser desvinculada durante o embaralhamento");
        }
    }

    @Test
    @DisplayName("MUT-08: Overkill no monstro trava HP em 0 (Math.max(0, currentHp - amount))")
    void mut08_overkillClampsToZero() {
        Enemy e = Enemy.createForLevel(1); // 60 HP
        e.takeDamage(9999);
        assertEquals(0, e.getCurrentHp(), "MUT-08: HP do monstro nunca pode ser negativo");
        assertTrue(e.isDefeated());
    }

    @Test
    @DisplayName("MUT-09: stripAnsi remove todos os escapes de cores e estilos ANSI")
    void mut09_stripAnsiRemovesColor() {
        String colored = "\u001B[1m\u001B[93mALERTA\u001B[0m";
        assertEquals("ALERTA", Ansi.stripAnsi(colored), "MUT-09: stripAnsi deve remover todas as cores");
    }

    @Test
    @DisplayName("MUT-10: BattleRenderer.CellRow restringe estritamente o buffer a 26 colunas")
    void mut10_cellRowClipping() {
        BattleRenderer.CellRow row = new BattleRenderer.CellRow();
        row.put(20, "1234567890", null); // Começa em 20, 10 chars -> atinge 30, deve cortar em 26
        String rendered = row.render();
        assertEquals(26, rendered.length(), "MUT-10: CellRow render deve ter estritamente 26 colunas");
    }
}
