package game;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("2.1 - PlayerTest: Mecânicas e Estado do Jogador")
public class PlayerTest {

    private Player player;

    @BeforeEach
    void setUp() {
        player = new Player("Byte Paladin");
    }

    @Nested
    @DisplayName("2.1.1 - 2.1.6: Inicialização e Sanitização do Nome")
    class ConstructorNameTests {

        @Test
        @DisplayName("2.1.1 - Construtor com nome válido")
        void validName() {
            Player p = new Player("Linus Torvalds");
            assertEquals("Linus Torvalds", p.getName());
        }

        @Test
        @DisplayName("2.1.2 - Construtor com nome vazio assume Byte Paladin")
        void emptyNameDefaults() {
            Player p = new Player("");
            assertEquals("Byte Paladin", p.getName());
        }

        @Test
        @DisplayName("2.1.3 - Construtor com nome de apenas espaços assume Byte Paladin")
        void whitespaceNameDefaults() {
            Player p = new Player("    ");
            assertEquals("Byte Paladin", p.getName());
        }

        @Test
        @DisplayName("2.1.4 - Construtor com nome nulo assume Byte Paladin")
        void nullNameDefaults() {
            Player p = new Player(null);
            assertEquals("Byte Paladin", p.getName());
        }

        @Test
        @DisplayName("2.1.5 - Construtor remove espaços nas pontas (trim)")
        void trimmedName() {
            Player p = new Player("  Byte Warrior  ");
            assertEquals("Byte Warrior", p.getName());
        }

        @Test
        @DisplayName("2.1.6 - Construtor preserva caracteres especiais e emojis")
        void specialCharactersAndEmojis() {
            Player p = new Player("🧙 Dev_#01");
            assertEquals("🧙 Dev_#01", p.getName());
        }
    }

    @Nested
    @DisplayName("2.1.7 - 2.1.11: Estado Inicial do Jogador")
    class InitialStateTests {

        @Test
        @DisplayName("2.1.7 - HP inicial é 100/100")
        void initialHp() {
            assertEquals(100, player.getHp());
            assertEquals(100, player.getMaxHp());
        }

        @Test
        @DisplayName("2.1.8 - Mana inicial é 60/60")
        void initialMana() {
            assertEquals(60, player.getMana());
            assertEquals(60, player.getMaxMana());
        }

        @Test
        @DisplayName("2.1.9 - Nível inicial é 1 e XP é 0")
        void initialLevelAndXp() {
            assertEquals(1, player.getLevel());
            assertEquals(0, player.getXp());
        }

        @Test
        @DisplayName("2.1.10 - Estatísticas de combo e respostas iniciam zeradas")
        void initialStats() {
            assertEquals(0, player.getComboStreak());
            assertEquals(0, player.getHighestStreak());
            assertEquals(0, player.getQuestionsAnswered());
            assertEquals(0, player.getCorrectAnswers());
        }

        @Test
        @DisplayName("2.1.11 - Escudo inativo e jogador vivo")
        void initialShieldAndDefeat() {
            assertFalse(player.isShieldActive());
            assertFalse(player.isDefeated());
        }
    }

    @Nested
    @DisplayName("2.1.12 - 2.1.17: Dano sem Escudo e Limites de HP")
    class DamageTests {

        @Test
        @DisplayName("2.1.12 - Dano normal reduz HP e retorna o dano aplicado")
        void takeDamageNormal() {
            int dealt = player.takeDamage(25);
            assertEquals(25, dealt);
            assertEquals(75, player.getHp());
            assertFalse(player.isDefeated());
        }

        @Test
        @DisplayName("2.1.13 - Dano que deixa com 1 de vida")
        void leaveOneHp() {
            int dealt = player.takeDamage(99);
            assertEquals(99, dealt);
            assertEquals(1, player.getHp());
            assertFalse(player.isDefeated());
        }

        @Test
        @DisplayName("2.1.14 - Dano letal exato zera vida e ativa derrota")
        void exactLethalDamage() {
            int dealt = player.takeDamage(100);
            assertEquals(100, dealt);
            assertEquals(0, player.getHp());
            assertTrue(player.isDefeated());
        }

        @Test
        @DisplayName("2.1.15 - Dano massivo / Overkill nunca deixa HP negativo")
        void overkillDamage() {
            int dealt = player.takeDamage(9999);
            assertEquals(9999, dealt);
            assertEquals(0, player.getHp());
            assertTrue(player.isDefeated());
        }

        @Test
        @DisplayName("2.1.16 - Dano zero não altera HP")
        void zeroDamage() {
            int dealt = player.takeDamage(0);
            assertEquals(0, dealt);
            assertEquals(100, player.getHp());
            assertFalse(player.isDefeated());
        }

        @Test
        @DisplayName("2.1.17 - Dano negativo não cura o jogador")
        void negativeDamage() {
            int dealt = player.takeDamage(-20);
            assertEquals(0, dealt);
            assertEquals(100, player.getHp());
        }
    }

    @Nested
    @DisplayName("2.1.18 - 2.1.23: Escudo Try-Catch Shield")
    class ShieldTests {

        @Test
        @DisplayName("2.1.18 - Escudo ativo com dano par reduz pela metade")
        void shieldWithEvenDamage() {
            player.setShieldActive(true);
            int dealt = player.takeDamage(40);
            assertEquals(20, dealt);
            assertEquals(80, player.getHp());
        }

        @Test
        @DisplayName("2.1.19 - Escudo ativo com dano ímpar trunca divisão inteira")
        void shieldWithOddDamage() {
            player.setShieldActive(true);
            int dealt = player.takeDamage(15);
            assertEquals(7, dealt);
            assertEquals(93, player.getHp());
        }

        @Test
        @DisplayName("2.1.20 - Escudo tem consumo único: desativa após primeiro golpe")
        void shieldSingleUse() {
            player.setShieldActive(true);
            player.takeDamage(20);
            assertFalse(player.isShieldActive());

            // Segundo golpe entra cheio
            int dealtSecond = player.takeDamage(20);
            assertEquals(20, dealtSecond);
            assertEquals(70, player.getHp()); // 100 - 10 - 20 = 70
        }

        @Test
        @DisplayName("2.1.21 - Escudo garante dano mínimo de 1 quando dano for 1")
        void shieldMinimumDamageOne() {
            player.setShieldActive(true);
            int dealt = player.takeDamage(1);
            assertEquals(1, dealt);
            assertEquals(99, player.getHp());
        }

        @Test
        @DisplayName("2.1.22 - Escudo com dano zero é protegido")
        void shieldWithZeroDamage() {
            player.setShieldActive(true);
            int dealt = player.takeDamage(0);
            assertEquals(0, dealt);
            assertEquals(100, player.getHp());
        }

        @Test
        @DisplayName("2.1.23 - Escudo com dano massivo mitiga mas pode derrotar")
        void shieldWithLethalDamage() {
            player.takeDamage(60); // HP vai para 40
            player.setShieldActive(true);
            int dealt = player.takeDamage(200); // Mitigado para 100
            assertEquals(100, dealt);
            assertEquals(0, player.getHp());
            assertTrue(player.isDefeated());
        }
    }

    @Nested
    @DisplayName("2.1.24 - 2.1.28: Mecânica de Cura (heal)")
    class HealTests {

        @Test
        @DisplayName("2.1.24 - Cura parcial restaura HP")
        void partialHeal() {
            player.takeDamage(50); // HP 50
            player.heal(30);
            assertEquals(80, player.getHp());
        }

        @Test
        @DisplayName("2.1.25 - Cura exata atinge MaxHP")
        void exactMaxHeal() {
            player.takeDamage(30); // HP 70
            player.heal(30);
            assertEquals(100, player.getHp());
        }

        @Test
        @DisplayName("2.1.26 - Overheal é limitado pelo MaxHP")
        void overhealCappedAtMax() {
            player.takeDamage(10); // HP 90
            player.heal(50);
            assertEquals(100, player.getHp());
        }

        @Test
        @DisplayName("2.1.27 - Cura com HP já cheio não ultrapassa MaxHP")
        void healAlreadyFull() {
            player.heal(35);
            assertEquals(100, player.getHp());
        }

        @Test
        @DisplayName("2.1.28 - Cura com valor zero ou negativo não altera HP")
        void healNegativeOrZero() {
            player.takeDamage(40); // HP 60
            player.heal(0);
            assertEquals(60, player.getHp());
            player.heal(-20);
            assertEquals(60, player.getHp());
        }
    }

    @Nested
    @DisplayName("2.1.29 - 2.1.37: Gastar e Restaurar Mana")
    class ManaTests {

        @Test
        @DisplayName("2.1.29 - Gastar mana com saldo suficiente")
        void spendManaSuccess() {
            assertTrue(player.spendMana(20));
            assertEquals(40, player.getMana());
        }

        @Test
        @DisplayName("2.1.30 - Gastar exatamente toda a mana disponível")
        void spendExactAllMana() {
            player.spendMana(40); // Resta 20
            assertTrue(player.spendMana(20));
            assertEquals(0, player.getMana());
        }

        @Test
        @DisplayName("2.1.31 - Gastar mana insuficiente é recusado e preserva saldo")
        void spendManaInsufficient() {
            player.spendMana(50); // Resta 10
            assertFalse(player.spendMana(15));
            assertEquals(10, player.getMana());
        }

        @Test
        @DisplayName("2.1.32 - Gastar mana com saldo zerado")
        void spendManaZero() {
            player.spendMana(60);
            assertFalse(player.spendMana(15));
            assertEquals(0, player.getMana());
        }

        @Test
        @DisplayName("2.1.33 - Gastar zero de mana é sempre permitido")
        void spendZeroMana() {
            assertTrue(player.spendMana(0));
            assertEquals(60, player.getMana());
        }

        @Test
        @DisplayName("2.1.34 - Restaurar mana parcial")
        void restoreManaPartial() {
            player.spendMana(40); // Mana 20
            player.restoreMana(15);
            assertEquals(35, player.getMana());
        }

        @Test
        @DisplayName("2.1.35 - Restaurar mana exata ao máximo")
        void restoreManaExactMax() {
            player.spendMana(15); // Mana 45
            player.restoreMana(15);
            assertEquals(60, player.getMana());
        }

        @Test
        @DisplayName("2.1.36 - Overflow de mana é limitado por MaxMana")
        void restoreManaOverflowCapped() {
            player.spendMana(5); // Mana 55
            player.restoreMana(15);
            assertEquals(60, player.getMana());
        }

        @Test
        @DisplayName("2.1.37 - Restaurar mana quando já cheio não ultrapassa MaxMana")
        void restoreManaAlreadyFull() {
            player.restoreMana(15);
            assertEquals(60, player.getMana());
            player.restoreMana(-10);
            assertEquals(60, player.getMana());
        }
    }

    @Nested
    @DisplayName("2.1.38 - 2.1.46: Registro de Respostas, Combos e XP")
    class AnswerAndComboTests {

        @Test
        @DisplayName("2.1.38 - Resposta correta incrementa contadores de acerto")
        void correctAnswerIncrementsCounters() {
            player.registerAnswer(true);
            assertEquals(1, player.getQuestionsAnswered());
            assertEquals(1, player.getCorrectAnswers());
        }

        @Test
        @DisplayName("2.1.39 - Respostas consecutivas incrementam combo streak")
        void consecutiveCorrectIncrementsStreak() {
            player.registerAnswer(true);
            assertEquals(1, player.getComboStreak());
            player.registerAnswer(true);
            assertEquals(2, player.getComboStreak());
        }

        @Test
        @DisplayName("2.1.40 - Cálculo cumulativo de XP por streak")
        void xpCalculationWithStreak() {
            player.registerAnswer(true); // streak 1: 50 + 10 = 60 XP
            assertEquals(60, player.getXp());

            player.registerAnswer(true); // streak 2: + (50 + 20) = 60 + 70 = 130 XP
            assertEquals(130, player.getXp());

            player.registerAnswer(true); // streak 3: + (50 + 30) = 130 + 80 = 210 XP
            assertEquals(210, player.getXp());
        }

        @Test
        @DisplayName("2.1.41 - Resposta correta restaura 15 de mana")
        void correctAnswerRestoresMana() {
            player.spendMana(40); // Mana 20
            player.registerAnswer(true);
            assertEquals(35, player.getMana());
        }

        @Test
        @DisplayName("2.1.42 - Resposta correta atualiza highest streak se superar o anterior")
        void highestStreakTracksMax() {
            player.registerAnswer(true);
            player.registerAnswer(true);
            player.registerAnswer(true);
            assertEquals(3, player.getHighestStreak());
            assertEquals(3, player.getComboStreak());
        }

        @Test
        @DisplayName("2.1.43 - Resposta incorreta zera combo streak imediatamente")
        void incorrectAnswerResetsStreak() {
            player.registerAnswer(true);
            player.registerAnswer(true);
            assertEquals(2, player.getComboStreak());

            player.registerAnswer(false);
            assertEquals(0, player.getComboStreak());
        }

        @Test
        @DisplayName("2.1.44 - Resposta incorreta preserva highest streak recorde")
        void incorrectAnswerPreservesHighestStreak() {
            for (int i = 0; i < 5; i++) {
                player.registerAnswer(true);
            }
            assertEquals(5, player.getHighestStreak());
            assertEquals(5, player.getComboStreak());

            player.registerAnswer(false);
            assertEquals(0, player.getComboStreak());
            assertEquals(5, player.getHighestStreak());
        }

        @Test
        @DisplayName("2.1.45 - Resposta incorreta não concede XP, nem mana, nem acertos")
        void incorrectAnswerGrantsNoReward() {
            player.spendMana(30); // Mana 30
            int prevXp = player.getXp();
            int prevCorrect = player.getCorrectAnswers();

            player.registerAnswer(false);

            assertEquals(prevXp, player.getXp());
            assertEquals(30, player.getMana());
            assertEquals(prevCorrect, player.getCorrectAnswers());
            assertEquals(1, player.getQuestionsAnswered());
        }

        @Test
        @DisplayName("2.1.46 - Bônus de magia por combo streak")
        void spellDamageBonusThresholds() {
            assertEquals(0, player.getSpellDamageBonus()); // Streak 0 -> 0

            player.registerAnswer(true); // Streak 1
            assertEquals(0, player.getSpellDamageBonus());

            player.registerAnswer(true); // Streak 2
            assertEquals(10, player.getSpellDamageBonus());

            player.registerAnswer(true); // Streak 3
            assertEquals(20, player.getSpellDamageBonus());

            player.registerAnswer(true); // Streak 4
            assertEquals(20, player.getSpellDamageBonus());
        }
    }

    @Nested
    @DisplayName("2.1.47 - 2.1.49: Level Up e Progressão de Status")
    class LevelUpTests {

        @Test
        @DisplayName("2.1.47 - Subir do nível 1 para o nível 2 aumenta MaxHP/MaxMana e restaura tudo")
        void levelUpToLevelTwo() {
            player.takeDamage(50);
            player.spendMana(40);

            player.levelUp(2);

            assertEquals(2, player.getLevel());
            assertEquals(120, player.getMaxHp());
            assertEquals(120, player.getHp()); // Restaurado totalmente
            assertEquals(75, player.getMaxMana());
            assertEquals(75, player.getMana()); // Restaurado totalmente
        }

        @Test
        @DisplayName("2.1.48 - Progressão consecutiva até nível 6 acumula status")
        void progressionToLevelSix() {
            for (int lvl = 2; lvl <= 6; lvl++) {
                player.levelUp(lvl);
            }
            assertEquals(6, player.getLevel());
            assertEquals(200, player.getMaxHp()); // 100 + (5 * 20)
            assertEquals(200, player.getHp());
            assertEquals(135, player.getMaxMana()); // 60 + (5 * 15)
            assertEquals(135, player.getMana());
        }

        @Test
        @DisplayName("2.1.49 - Level Up cura jogador à beira da morte")
        void levelUpNearDeathFullRestore() {
            player.takeDamage(99); // 1 HP
            player.levelUp(2);
            assertEquals(120, player.getHp());
        }
    }

    @Nested
    @DisplayName("2.1.50 - 2.1.52: Setters e Verificação de Derrota")
    class SettersAndDefeatTests {

        @Test
        @DisplayName("2.1.50 - Setter e Getter de nome")
        void nameSetterAndGetter() {
            player.setName("Code Sorcerer");
            assertEquals("Code Sorcerer", player.getName());
        }

        @Test
        @DisplayName("2.1.51 - Setter e Getter de escudo ativo")
        void shieldSetterAndGetter() {
            assertFalse(player.isShieldActive());
            player.setShieldActive(true);
            assertTrue(player.isShieldActive());
            player.setShieldActive(false);
            assertFalse(player.isShieldActive());
        }

        @Test
        @DisplayName("2.1.52 - Condição isDefeated baseada em HP")
        void defeatCondition() {
            assertFalse(player.isDefeated());
            player.takeDamage(99);
            assertFalse(player.isDefeated());
            player.takeDamage(1);
            assertTrue(player.isDefeated());
        }
    }
}
