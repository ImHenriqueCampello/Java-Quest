package game;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("2.6 - CharacterSpriteTest: Integridade e Dimensões dos Sprites ASCII")
public class CharacterSpriteTest {

    private void assertSpriteGrid(List<String> sprite, String spriteName) {
        assertNotNull(sprite, spriteName + " não pode ser nulo");
        assertEquals(CharacterSprite.SPRITE_HEIGHT, sprite.size(),
            spriteName + " deve ter rigorosamente 8 linhas de altura");

        for (int i = 0; i < sprite.size(); i++) {
            String line = sprite.get(i);
            assertNotNull(line, spriteName + " linha [" + i + "] não pode ser nula");
            assertEquals(CharacterSprite.SPRITE_WIDTH, line.length(),
                spriteName + " linha [" + i + "] deve ter exatamente 24 colunas");
            assertFalse(line.contains("\n"), spriteName + " linha [" + i + "] não pode conter \\n");
            assertFalse(line.contains("\r"), spriteName + " linha [" + i + "] não pode conter \\r");
            assertFalse(line.contains("\t"), spriteName + " linha [" + i + "] não pode conter \\t");
        }
    }

    @Nested
    @DisplayName("2.6.1: Dimensões Canônicas")
    class ConstantsTests {

        @Test
        @DisplayName("2.6.1 - Dimensões canônicas da matriz de renderização")
        void canonicalDimensions() {
            assertEquals(8, CharacterSprite.SPRITE_HEIGHT);
            assertEquals(24, CharacterSprite.SPRITE_WIDTH);
        }
    }

    @Nested
    @DisplayName("2.6.2: Sprites do Herói (Player)")
    class PlayerSpritesTests {

        @Test
        @DisplayName("2.6.2 - Todos os quadros de animação do Jogador")
        void allPlayerSprites() {
            assertSpriteGrid(CharacterSprite.PLAYER_IDLE_1, "PLAYER_IDLE_1");
            assertSpriteGrid(CharacterSprite.PLAYER_IDLE_2, "PLAYER_IDLE_2");
            assertSpriteGrid(CharacterSprite.PLAYER_CHARGE, "PLAYER_CHARGE");
            assertSpriteGrid(CharacterSprite.PLAYER_ATTACK, "PLAYER_ATTACK");
            assertSpriteGrid(CharacterSprite.PLAYER_HURT, "PLAYER_HURT");
            assertSpriteGrid(CharacterSprite.PLAYER_VICTORY, "PLAYER_VICTORY");
        }
    }

    @Nested
    @DisplayName("2.6.3 - 2.6.8: Sprites de Todos os Chefes (Inimigos)")
    class BossSpritesTests {

        @Test
        @DisplayName("2.6.3 - NullPointer Imp (Nível 1)")
        void impSprites() {
            assertSpriteGrid(CharacterSprite.IMP_IDLE_1, "IMP_IDLE_1");
            assertSpriteGrid(CharacterSprite.IMP_IDLE_2, "IMP_IDLE_2");
            assertSpriteGrid(CharacterSprite.IMP_ATTACK, "IMP_ATTACK");
            assertSpriteGrid(CharacterSprite.IMP_HURT, "IMP_HURT");
        }

        @Test
        @DisplayName("2.6.4 - Infinite Loop Serpent (Nível 2)")
        void serpentSprites() {
            assertSpriteGrid(CharacterSprite.SERPENT_IDLE_1, "SERPENT_IDLE_1");
            assertSpriteGrid(CharacterSprite.SERPENT_IDLE_2, "SERPENT_IDLE_2");
            assertSpriteGrid(CharacterSprite.SERPENT_ATTACK, "SERPENT_ATTACK");
            assertSpriteGrid(CharacterSprite.SERPENT_HURT, "SERPENT_HURT");
        }

        @Test
        @DisplayName("2.6.5 - IndexOutOfBounds Golem (Nível 3)")
        void golemSprites() {
            assertSpriteGrid(CharacterSprite.GOLEM_IDLE_1, "GOLEM_IDLE_1");
            assertSpriteGrid(CharacterSprite.GOLEM_IDLE_2, "GOLEM_IDLE_2");
            assertSpriteGrid(CharacterSprite.GOLEM_ATTACK, "GOLEM_ATTACK");
            assertSpriteGrid(CharacterSprite.GOLEM_HURT, "GOLEM_HURT");
        }

        @Test
        @DisplayName("2.6.6 - Spaghetti Code Chimera (Nível 4)")
        void chimeraSprites() {
            assertSpriteGrid(CharacterSprite.CHIMERA_IDLE_1, "CHIMERA_IDLE_1");
            assertSpriteGrid(CharacterSprite.CHIMERA_IDLE_2, "CHIMERA_IDLE_2");
            assertSpriteGrid(CharacterSprite.CHIMERA_ATTACK, "CHIMERA_ATTACK");
            assertSpriteGrid(CharacterSprite.CHIMERA_HURT, "CHIMERA_HURT");
        }

        @Test
        @DisplayName("2.6.7 - Memory Leak Hydra (Nível 5)")
        void hydraSprites() {
            assertSpriteGrid(CharacterSprite.HYDRA_IDLE_1, "HYDRA_IDLE_1");
            assertSpriteGrid(CharacterSprite.HYDRA_IDLE_2, "HYDRA_IDLE_2");
            assertSpriteGrid(CharacterSprite.HYDRA_ATTACK, "HYDRA_ATTACK");
            assertSpriteGrid(CharacterSprite.HYDRA_HURT, "HYDRA_HURT");
        }

        @Test
        @DisplayName("2.6.8 - THE STACKOVERFLOW DRAGON (Nível 6)")
        void dragonSprites() {
            assertSpriteGrid(CharacterSprite.DRAGON_IDLE_1, "DRAGON_IDLE_1");
            assertSpriteGrid(CharacterSprite.DRAGON_IDLE_2, "DRAGON_IDLE_2");
            assertSpriteGrid(CharacterSprite.DRAGON_ATTACK, "DRAGON_ATTACK");
            assertSpriteGrid(CharacterSprite.DRAGON_HURT, "DRAGON_HURT");
        }
    }

    @Nested
    @DisplayName("2.6.9 - 2.6.32: Integridade de Conteúdo Visual e Ausência de Caracteres Inválidos")
    class VisualContentIntegrityTests {

        private void assertVisualCleanliness(List<String> sprite, String spriteName) {
            for (int i = 0; i < sprite.size(); i++) {
                String line = sprite.get(i);
                assertFalse(line.contains("\n"), spriteName + " não pode conter \\n");
                assertFalse(line.contains("\r"), spriteName + " não pode conter \\r");
                assertFalse(line.contains("\t"), spriteName + " não pode conter \\t");
                assertEquals(24, line.length(), spriteName + " linha " + i + " deve ter exatamente 24 colunas");
            }
        }

        @Test
        @DisplayName("2.6.9 - 2.6.14 - Todos os sprites do Herói possuem formatação limpa")
        void heroVisualCleanliness() {
            assertVisualCleanliness(CharacterSprite.PLAYER_IDLE_1, "PLAYER_IDLE_1");
            assertVisualCleanliness(CharacterSprite.PLAYER_IDLE_2, "PLAYER_IDLE_2");
            assertVisualCleanliness(CharacterSprite.PLAYER_CHARGE, "PLAYER_CHARGE");
            assertVisualCleanliness(CharacterSprite.PLAYER_ATTACK, "PLAYER_ATTACK");
            assertVisualCleanliness(CharacterSprite.PLAYER_HURT, "PLAYER_HURT");
            assertVisualCleanliness(CharacterSprite.PLAYER_VICTORY, "PLAYER_VICTORY");
        }

        @Test
        @DisplayName("2.6.15 - 2.6.17 - Sprites do Imp possuem formatação limpa")
        void impVisualCleanliness() {
            assertVisualCleanliness(CharacterSprite.IMP_IDLE_1, "IMP_IDLE_1");
            assertVisualCleanliness(CharacterSprite.IMP_IDLE_2, "IMP_IDLE_2");
            assertVisualCleanliness(CharacterSprite.IMP_ATTACK, "IMP_ATTACK");
            assertVisualCleanliness(CharacterSprite.IMP_HURT, "IMP_HURT");
        }

        @Test
        @DisplayName("2.6.18 - 2.6.20 - Sprites da Serpent possuem formatação limpa")
        void serpentVisualCleanliness() {
            assertVisualCleanliness(CharacterSprite.SERPENT_IDLE_1, "SERPENT_IDLE_1");
            assertVisualCleanliness(CharacterSprite.SERPENT_IDLE_2, "SERPENT_IDLE_2");
            assertVisualCleanliness(CharacterSprite.SERPENT_ATTACK, "SERPENT_ATTACK");
            assertVisualCleanliness(CharacterSprite.SERPENT_HURT, "SERPENT_HURT");
        }

        @Test
        @DisplayName("2.6.21 - 2.6.23 - Sprites do Golem possuem formatação limpa")
        void golemVisualCleanliness() {
            assertVisualCleanliness(CharacterSprite.GOLEM_IDLE_1, "GOLEM_IDLE_1");
            assertVisualCleanliness(CharacterSprite.GOLEM_IDLE_2, "GOLEM_IDLE_2");
            assertVisualCleanliness(CharacterSprite.GOLEM_ATTACK, "GOLEM_ATTACK");
            assertVisualCleanliness(CharacterSprite.GOLEM_HURT, "GOLEM_HURT");
        }

        @Test
        @DisplayName("2.6.24 - 2.6.26 - Sprites da Chimera possuem formatação limpa")
        void chimeraVisualCleanliness() {
            assertVisualCleanliness(CharacterSprite.CHIMERA_IDLE_1, "CHIMERA_IDLE_1");
            assertVisualCleanliness(CharacterSprite.CHIMERA_IDLE_2, "CHIMERA_IDLE_2");
            assertVisualCleanliness(CharacterSprite.CHIMERA_ATTACK, "CHIMERA_ATTACK");
            assertVisualCleanliness(CharacterSprite.CHIMERA_HURT, "CHIMERA_HURT");
        }

        @Test
        @DisplayName("2.6.27 - 2.6.29 - Sprites da Hydra possuem formatação limpa")
        void hydraVisualCleanliness() {
            assertVisualCleanliness(CharacterSprite.HYDRA_IDLE_1, "HYDRA_IDLE_1");
            assertVisualCleanliness(CharacterSprite.HYDRA_IDLE_2, "HYDRA_IDLE_2");
            assertVisualCleanliness(CharacterSprite.HYDRA_ATTACK, "HYDRA_ATTACK");
            assertVisualCleanliness(CharacterSprite.HYDRA_HURT, "HYDRA_HURT");
        }

        @Test
        @DisplayName("2.6.30 - 2.6.32 - Sprites do Dragon possuem formatação limpa")
        void dragonVisualCleanliness() {
            assertVisualCleanliness(CharacterSprite.DRAGON_IDLE_1, "DRAGON_IDLE_1");
            assertVisualCleanliness(CharacterSprite.DRAGON_IDLE_2, "DRAGON_IDLE_2");
            assertVisualCleanliness(CharacterSprite.DRAGON_ATTACK, "DRAGON_ATTACK");
            assertVisualCleanliness(CharacterSprite.DRAGON_HURT, "DRAGON_HURT");
        }
    }
}
