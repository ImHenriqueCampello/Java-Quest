package game;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("2.2 - EnemyTest: Fábrica, Chefes e Estado dos Inimigos")
public class EnemyTest {

    @Nested
    @DisplayName("2.2.1 - 2.2.6: Fábrica de Chefes para Níveis 1 a 6")
    class BossFactoryTests {

        @Test
        @DisplayName("2.2.1 - Nível 1: NullPointer Imp")
        void createLevelOneBoss() {
            Enemy enemy = Enemy.createForLevel(1);
            assertEquals(1, enemy.getLevel());
            assertEquals("NullPointer Imp", enemy.getName());
            assertEquals("Scourge of Uninitialized References", enemy.getTitle());
            assertEquals(60, enemy.getMaxHp());
            assertEquals(60, enemy.getCurrentHp());
            assertEquals(15, enemy.getAttackDamage());
            assertEquals("Null Pointer Dereference", enemy.getAttackName());
            assertEquals(Ansi.BRIGHT_MAGENTA, enemy.getColor());
            assertFalse(enemy.isDefeated());
        }

        @Test
        @DisplayName("2.2.2 - Nível 2: Infinite Loop Serpent")
        void createLevelTwoBoss() {
            Enemy enemy = Enemy.createForLevel(2);
            assertEquals(2, enemy.getLevel());
            assertEquals("Infinite Loop Serpent", enemy.getName());
            assertEquals("Devourer of CPU Cycles", enemy.getTitle());
            assertEquals(80, enemy.getMaxHp());
            assertEquals(80, enemy.getCurrentHp());
            assertEquals(20, enemy.getAttackDamage());
            assertEquals("Eternal While Loop", enemy.getAttackName());
            assertEquals(Ansi.BRIGHT_GREEN, enemy.getColor());
        }

        @Test
        @DisplayName("2.2.3 - Nível 3: IndexOutOfBounds Golem")
        void createLevelThreeBoss() {
            Enemy enemy = Enemy.createForLevel(3);
            assertEquals(3, enemy.getLevel());
            assertEquals("IndexOutOfBounds Golem", enemy.getName());
            assertEquals("Crusher of Array Boundaries", enemy.getTitle());
            assertEquals(100, enemy.getMaxHp());
            assertEquals(100, enemy.getCurrentHp());
            assertEquals(25, enemy.getAttackDamage());
            assertEquals("Off-By-One Slam", enemy.getAttackName());
            assertEquals(Ansi.BRIGHT_YELLOW, enemy.getColor());
        }

        @Test
        @DisplayName("2.2.4 - Nível 4: Spaghetti Code Chimera")
        void createLevelFourBoss() {
            Enemy enemy = Enemy.createForLevel(4);
            assertEquals(4, enemy.getLevel());
            assertEquals("Spaghetti Code Chimera", enemy.getName());
            assertEquals("Abomination of Anti-Patterns", enemy.getTitle());
            assertEquals(120, enemy.getMaxHp());
            assertEquals(120, enemy.getCurrentHp());
            assertEquals(30, enemy.getAttackDamage());
            assertEquals("Spaghetti Entanglement", enemy.getAttackName());
            assertEquals(Ansi.BRIGHT_CYAN, enemy.getColor());
        }

        @Test
        @DisplayName("2.2.5 - Nível 5: Memory Leak Hydra")
        void createLevelFiveBoss() {
            Enemy enemy = Enemy.createForLevel(5);
            assertEquals(5, enemy.getLevel());
            assertEquals("Memory Leak Hydra", enemy.getName());
            assertEquals("Siphon of the JVM Heap", enemy.getTitle());
            assertEquals(140, enemy.getMaxHp());
            assertEquals(140, enemy.getCurrentHp());
            assertEquals(35, enemy.getAttackDamage());
            assertEquals("OutOfMemory Error", enemy.getAttackName());
            assertEquals(Ansi.BRIGHT_RED, enemy.getColor());
        }

        @Test
        @DisplayName("2.2.6 - Nível 6: THE STACKOVERFLOW DRAGON")
        void createLevelSixBoss() {
            Enemy enemy = Enemy.createForLevel(6);
            assertEquals(6, enemy.getLevel());
            assertEquals("THE STACKOVERFLOW DRAGON", enemy.getName());
            assertEquals("Apex Predator of the Call Stack", enemy.getTitle());
            assertEquals(180, enemy.getMaxHp());
            assertEquals(180, enemy.getCurrentHp());
            assertEquals(40, enemy.getAttackDamage());
            assertEquals("Deep Recursion Inferno", enemy.getAttackName());
            assertEquals(Ansi.RED, enemy.getColor());
        }
    }

    @Nested
    @DisplayName("2.2.7 - 2.2.9: Níveis Inválidos e Fallback Seguro")
    class BossFallbackTests {

        @ParameterizedTest(name = "Nível inválido {0} retorna chefe nível 1 (Imp)")
        @ValueSource(ints = {0, -1, -100, 7, 8, 99})
        @DisplayName("2.2.7 - 2.2.9 - Fallback seguro para nível 1")
        void invalidLevelFallbacksToLevelOne(int invalidLevel) {
            Enemy enemy = Enemy.createForLevel(invalidLevel);
            assertEquals(1, enemy.getLevel());
            assertEquals("NullPointer Imp", enemy.getName());
            assertEquals(60, enemy.getMaxHp());
        }
    }

    @Nested
    @DisplayName("2.2.10 - 2.2.15: Integridade e Dimensões dos Sprites dos Chefes")
    class BossSpriteIntegrityTests {

        private void assertSpriteDimensions(List<String> sprite, String spriteName) {
            assertNotNull(sprite, spriteName + " não pode ser nulo");
            assertEquals(8, sprite.size(), spriteName + " deve ter exatamente 8 linhas");
            for (int i = 0; i < sprite.size(); i++) {
                assertEquals(24, sprite.get(i).length(),
                    spriteName + " linha " + i + " deve ter largura 24");
            }
        }

        @Test
        @DisplayName("2.2.10 - Sprites do NullPointer Imp")
        void impSprites() {
            Enemy imp = Enemy.createForLevel(1);
            assertSpriteDimensions(imp.getIdle1(), "Imp Idle1");
            assertSpriteDimensions(imp.getIdle2(), "Imp Idle2");
            assertSpriteDimensions(imp.getAttackFrame(), "Imp Attack");
            assertSpriteDimensions(imp.getHurtFrame(), "Imp Hurt");
        }

        @Test
        @DisplayName("2.2.11 - Sprites da Serpent")
        void serpentSprites() {
            Enemy serpent = Enemy.createForLevel(2);
            assertSpriteDimensions(serpent.getIdle1(), "Serpent Idle1");
            assertSpriteDimensions(serpent.getIdle2(), "Serpent Idle2");
            assertSpriteDimensions(serpent.getAttackFrame(), "Serpent Attack");
            assertSpriteDimensions(serpent.getHurtFrame(), "Serpent Hurt");
        }

        @Test
        @DisplayName("2.2.12 - Sprites do Golem")
        void golemSprites() {
            Enemy golem = Enemy.createForLevel(3);
            assertSpriteDimensions(golem.getIdle1(), "Golem Idle1");
            assertSpriteDimensions(golem.getIdle2(), "Golem Idle2");
            assertSpriteDimensions(golem.getAttackFrame(), "Golem Attack");
            assertSpriteDimensions(golem.getHurtFrame(), "Golem Hurt");
        }

        @Test
        @DisplayName("2.2.13 - Sprites da Chimera")
        void chimeraSprites() {
            Enemy chimera = Enemy.createForLevel(4);
            assertSpriteDimensions(chimera.getIdle1(), "Chimera Idle1");
            assertSpriteDimensions(chimera.getIdle2(), "Chimera Idle2");
            assertSpriteDimensions(chimera.getAttackFrame(), "Chimera Attack");
            assertSpriteDimensions(chimera.getHurtFrame(), "Chimera Hurt");
        }

        @Test
        @DisplayName("2.2.14 - Sprites da Hydra")
        void hydraSprites() {
            Enemy hydra = Enemy.createForLevel(5);
            assertSpriteDimensions(hydra.getIdle1(), "Hydra Idle1");
            assertSpriteDimensions(hydra.getIdle2(), "Hydra Idle2");
            assertSpriteDimensions(hydra.getAttackFrame(), "Hydra Attack");
            assertSpriteDimensions(hydra.getHurtFrame(), "Hydra Hurt");
        }

        @Test
        @DisplayName("2.2.15 - Sprites do Dragon")
        void dragonSprites() {
            Enemy dragon = Enemy.createForLevel(6);
            assertSpriteDimensions(dragon.getIdle1(), "Dragon Idle1");
            assertSpriteDimensions(dragon.getIdle2(), "Dragon Idle2");
            assertSpriteDimensions(dragon.getAttackFrame(), "Dragon Attack");
            assertSpriteDimensions(dragon.getHurtFrame(), "Dragon Hurt");
        }
    }

    @Nested
    @DisplayName("2.2.16 - 2.2.20: Dano e Derrota do Inimigo")
    class EnemyDamageTests {

        @Test
        @DisplayName("2.2.16 - Dano parcial reduz HP do inimigo")
        void partialDamage() {
            Enemy enemy = Enemy.createForLevel(1); // 60 HP
            enemy.takeDamage(25);
            assertEquals(35, enemy.getCurrentHp());
            assertFalse(enemy.isDefeated());
        }

        @Test
        @DisplayName("2.2.17 - Dano exato que zera vida do inimigo")
        void exactLethalDamage() {
            Enemy enemy = Enemy.createForLevel(1);
            enemy.takeDamage(60);
            assertEquals(0, enemy.getCurrentHp());
            assertTrue(enemy.isDefeated());
        }

        @Test
        @DisplayName("2.2.18 - Dano massivo / Overkill nunca deixa HP negativo")
        void overkillDamage() {
            Enemy enemy = Enemy.createForLevel(1);
            enemy.takeDamage(999);
            assertEquals(0, enemy.getCurrentHp());
            assertTrue(enemy.isDefeated());
        }

        @Test
        @DisplayName("2.2.19 - Dano zero ou negativo não altera HP do inimigo")
        void zeroOrNegativeDamage() {
            Enemy enemy = Enemy.createForLevel(1);
            enemy.takeDamage(0);
            assertEquals(60, enemy.getCurrentHp());
            enemy.takeDamage(-25);
            assertEquals(60, enemy.getCurrentHp());
        }

        @Test
        @DisplayName("2.2.20 - isDefeated reflete status de HP")
        void isDefeatedBehavior() {
            Enemy enemy = Enemy.createForLevel(1);
            assertFalse(enemy.isDefeated());
            enemy.takeDamage(59);
            assertFalse(enemy.isDefeated());
            enemy.takeDamage(1);
            assertTrue(enemy.isDefeated());
        }
    }

    @Nested
    @DisplayName("2.2.21 - 2.2.30: Metadados Textuais e Mensagens de Ataque")
    class EnemyMetadataTests {

        @ParameterizedTest(name = "Chefe nível {0} possui descrição e mensagem de ataque válidas")
        @ValueSource(ints = {1, 2, 3, 4, 5, 6})
        @DisplayName("2.2.21 - 2.2.30 - Metadados de cada chefe são não-vazios")
        void bossMetadataValid(int level) {
            Enemy enemy = Enemy.createForLevel(level);
            assertNotNull(enemy.getDescription());
            assertFalse(enemy.getDescription().trim().isEmpty());
            assertNotNull(enemy.getAttackMessage());
            assertFalse(enemy.getAttackMessage().trim().isEmpty());
            assertNotNull(enemy.getAttackName());
            assertFalse(enemy.getAttackName().trim().isEmpty());
            assertNotNull(enemy.getTitle());
            assertFalse(enemy.getTitle().trim().isEmpty());
        }
    }
}
