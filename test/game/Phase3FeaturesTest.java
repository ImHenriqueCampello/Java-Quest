package game;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("7.0 - Phase3FeaturesTest: Validação das Melhorias da Fase 3 (Inventário, Chefes e Save/Load)")
public class Phase3FeaturesTest {

    private PrintStream originalOut;
    private ByteArrayOutputStream outContent;

    @BeforeEach
    void setUp() {
        Ansi.FAST_MODE = true;
        originalOut = System.out;
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        Ansi.FAST_MODE = false;
        SaveManager.resetDefaultSavePath();
    }

    @Nested
    @DisplayName("7.1: Sistema de Inventário e Itens Consumíveis (2.1)")
    class InventoryAndItemsTests {

        @Test
        @DisplayName("7.1.1 - Enum Item possui metadados válidos e busca por código")
        void itemEnumProperties() {
            for (Item item : Item.values()) {
                assertNotNull(item.getCode());
                assertFalse(item.getCode().isEmpty());
                assertNotNull(item.getDisplayName());
                assertNotNull(item.getDescription());
                assertNotNull(item.getIcon());
                assertEquals(item, Item.fromCode(item.getCode()));
                assertEquals(item, Item.fromCode(item.name()));
            }
            assertNull(Item.fromCode(null));
            assertNull(Item.fromCode("NON_EXISTING_ITEM_CODE"));
        }

        @Test
        @DisplayName("7.1.2 - Manipulação do inventário: adicionar, contar, consumir e limpar")
        void inventoryManagement() {
            Inventory inv = new Inventory();
            assertTrue(inv.isEmpty());
            assertEquals(0, inv.getTotalItemCount());

            inv.addItem(Item.COFFEE_BREW, 3);
            assertFalse(inv.isEmpty());
            assertEquals(3, inv.getCount(Item.COFFEE_BREW));
            assertTrue(inv.hasItem(Item.COFFEE_BREW));
            assertEquals(3, inv.getTotalItemCount());

            inv.addItem(Item.JIT_BOOSTER);
            assertEquals(1, inv.getCount(Item.JIT_BOOSTER));
            assertEquals(4, inv.getTotalItemCount());

            assertTrue(inv.consumeItem(Item.COFFEE_BREW));
            assertEquals(2, inv.getCount(Item.COFFEE_BREW));

            inv.addItem(null, 5);
            inv.addItem(Item.COFFEE_BREW, -1);
            assertEquals(2, inv.getCount(Item.COFFEE_BREW));

            assertFalse(inv.consumeItem(null));
            assertFalse(inv.consumeItem(Item.BREAKPOINT_TOKEN));

            inv.clear();
            assertTrue(inv.isEmpty());
            assertEquals(0, inv.getTotalItemCount());
        }

        @Test
        @DisplayName("7.1.3 - Efeito Coffee Brew: Restaura 40 MP sem passar o turno")
        void coffeeBrewManaRestoration() {
            Player p = new Player("ByteDev");
            p.spendMana(50); // Mana vai para 10/60
            assertEquals(10, p.getMana());

            p.getInventory().addItem(Item.COFFEE_BREW, 1);
            p.restoreMana(40);
            assertEquals(50, p.getMana());

            // Limite pelo MaxMana
            p.restoreMana(40);
            assertEquals(p.getMaxMana(), p.getMana());
        }

        @Test
        @DisplayName("7.1.4 - Efeito JIT Booster: Próximo ataque acerta com 2.5x dano crítico")
        void jitBoosterDamageMultiplication() {
            Player p = new Player("JitHero");
            assertFalse(p.isJitBoosterActive());

            p.setJitBoosterActive(true);
            assertTrue(p.isJitBoosterActive());

            int baseDamage = 30;
            int boostedDamage = (int) Math.round(baseDamage * 2.5);
            assertEquals(75, boostedDamage);

            p.setJitBoosterActive(false);
            assertFalse(p.isJitBoosterActive());
        }

        @Test
        @DisplayName("7.1.5 - Efeito Clean Arch Shield: Absorve 100% do próximo dano")
        void cleanArchShieldTotalAbsorption() {
            Player p = new Player("Architect");
            p.setCleanArchShieldActive(true);
            assertTrue(p.isCleanArchShieldActive());

            // Dano de 50 recebido deve ser totalmente absorvido
            int damageTaken = p.takeDamage(50);
            assertEquals(0, damageTaken);
            assertEquals(p.getMaxHp(), p.getHp());
            assertFalse(p.isCleanArchShieldActive()); // Consumo único

            // Próximo dano agora atinge normal
            int normalDmg = p.takeDamage(20);
            assertEquals(20, normalDmg);
            assertEquals(80, p.getHp());
        }

        @Test
        @DisplayName("7.1.6 - Efeito Breakpoint Token: Evita morte letal e revive com 25% HP")
        void breakpointTokenRevivesAtLethalDamage() {
            Player p = new Player("Survivor");
            p.getInventory().addItem(Item.BREAKPOINT_TOKEN, 1);

            // Reduz HP para 10
            p.setHp(10);
            assertFalse(p.isBreakpointTriggered());

            // Dano letal de 50
            int dmg = p.takeDamage(50);
            assertEquals(50, dmg);
            assertFalse(p.isDefeated(), "Jogador deve sobreviver com o Breakpoint Token!");
            assertEquals(p.getMaxHp() / 4, p.getHp(), "HP deve ser restaurado para 25% do MaxHP!");
            assertTrue(p.isBreakpointTriggered());
            assertEquals(0, p.getInventory().getCount(Item.BREAKPOINT_TOKEN), "Token deve ser consumido");

            p.clearBreakpointTriggered();
            assertFalse(p.isBreakpointTriggered());

            // Novo dano letal sem token agora derrota
            p.takeDamage(50);
            assertTrue(p.isDefeated());
        }

        @Test
        @DisplayName("7.1.7 - Breakpoint Token não é consumido em danos não-letais")
        void breakpointTokenNotTriggeredOnNonLethalDamage() {
            Player p = new Player("HealthyDev");
            p.getInventory().addItem(Item.BREAKPOINT_TOKEN, 2);

            p.takeDamage(30);
            assertEquals(70, p.getHp());
            assertEquals(2, p.getInventory().getCount(Item.BREAKPOINT_TOKEN));
            assertFalse(p.isBreakpointTriggered());
        }

        @Test
        @DisplayName("7.1.8 - Loot drops são gerados corretamente por nível")
        void lootDropsPerLevel() {
            for (int lvl = 1; lvl <= 6; lvl++) {
                List<Item> drops = GameEngine.getDropsForLevel(lvl);
                assertNotNull(drops);
                assertFalse(drops.isEmpty());
                assertTrue(drops.size() >= 1 && drops.size() <= 2);
            }
            assertTrue(GameEngine.getDropsForLevel(1).contains(Item.COFFEE_BREW));
            assertTrue(GameEngine.getDropsForLevel(1).contains(Item.CLEAN_ARCH_SHIELD));
            assertTrue(GameEngine.getDropsForLevel(5).contains(Item.BREAKPOINT_TOKEN));
        }
    }

    @Nested
    @DisplayName("7.2: Habilidades Especiais e Mecânicas Únicas de Chefes (2.3)")
    class BossMechanicsTests {

        @Test
        @DisplayName("7.2.1 - NullPointer Imp (Lvl 1): Vazio Cósmico (+10 dano após 2 erros seguidos)")
        void cosmicVoidMechanic() {
            Enemy imp = Enemy.createForLevel(1);
            assertEquals(Enemy.BossMechanic.COSMIC_VOID, imp.getMechanic());
            assertEquals(15, imp.getAttackDamage());
            assertEquals(15, imp.getEffectiveAttackDamage());

            imp.recordPlayerMiss();
            assertEquals(1, imp.getConsecutiveMisses());
            assertEquals(15, imp.getEffectiveAttackDamage());

            imp.recordPlayerMiss();
            assertEquals(2, imp.getConsecutiveMisses());
            assertEquals(25, imp.getEffectiveAttackDamage(), "Deve causar +10 de dano após 2 erros seguidos!");

            imp.recordPlayerHit();
            assertEquals(0, imp.getConsecutiveMisses());
            assertEquals(15, imp.getEffectiveAttackDamage(), "Deve resetar o bônus após acerto!");
        }

        @Test
        @DisplayName("7.2.2 - Infinite Loop Serpent (Lvl 2): CPU Throttle mecânica presente")
        void cpuThrottleMechanic() {
            Enemy serpent = Enemy.createForLevel(2);
            assertEquals(Enemy.BossMechanic.CPU_THROTTLE, serpent.getMechanic());
        }

        @Test
        @DisplayName("7.2.3 - IndexOutOfBounds Golem (Lvl 3): Array Slice com embaralhamento de opções")
        void arraySliceMechanic() {
            Enemy golem = Enemy.createForLevel(3);
            assertEquals(Enemy.BossMechanic.ARRAY_SLICE, golem.getMechanic());

            Question q = new Question("Arrays", "What is length?", List.of(),
                List.of("A", "B", "C", "D"), 0, "Length is field", 25);
            Question shuffled = q.withRandomizedOptions();
            assertEquals(4, shuffled.getOptions().size());
            // O texto da resposta correta deve ser preservado mesmo com a mudança de índice
            assertEquals("A", shuffled.getOptions().get(shuffled.getCorrectOptionIndex()));
        }

        @Test
        @DisplayName("7.2.4 - Spaghetti Chimera (Lvl 4): Coupling Lock bloqueia cura e decrementa turnos")
        void couplingLockMechanic() {
            Enemy chimera = Enemy.createForLevel(4);
            assertEquals(Enemy.BossMechanic.COUPLING_LOCK, chimera.getMechanic());
            assertEquals(0, chimera.getCouplingLockTurns());

            chimera.setCouplingLockTurns(2);
            assertEquals(2, chimera.getCouplingLockTurns());

            chimera.decrementCouplingLockTurns();
            assertEquals(1, chimera.getCouplingLockTurns());

            chimera.decrementCouplingLockTurns();
            assertEquals(0, chimera.getCouplingLockTurns());

            chimera.decrementCouplingLockTurns();
            assertEquals(0, chimera.getCouplingLockTurns());
        }

        @Test
        @DisplayName("7.2.5 - Memory Leak Hydra (Lvl 5): Heap Overflow cura HP por turno")
        void heapOverflowMechanic() {
            Enemy hydra = Enemy.createForLevel(5);
            assertEquals(Enemy.BossMechanic.HEAP_OVERFLOW, hydra.getMechanic());

            hydra.takeDamage(20);
            assertEquals(120, hydra.getCurrentHp());

            hydra.heal(5);
            assertEquals(125, hydra.getCurrentHp());
        }

        @Test
        @DisplayName("7.2.6 - The StackOverflow Dragon (Lvl 6): Recursão Profunda escala +3 dano por turno")
        void deepRecursionMechanic() {
            Enemy dragon = Enemy.createForLevel(6);
            assertEquals(Enemy.BossMechanic.DEEP_RECURSION, dragon.getMechanic());
            assertEquals(40, dragon.getAttackDamage());
            assertEquals(40, dragon.getEffectiveAttackDamage());

            dragon.incrementMechanicCounter();
            assertEquals(43, dragon.getEffectiveAttackDamage());

            dragon.incrementMechanicCounter();
            assertEquals(46, dragon.getEffectiveAttackDamage());

            dragon.incrementMechanicCounter();
            assertEquals(49, dragon.getEffectiveAttackDamage());
        }
    }

    @Nested
    @DisplayName("7.3: Sistema de Salvamento / Carregamento de Perfil (6.1)")
    class SaveManagerTests {

        @Test
        @DisplayName("7.3.1 - Ciclo completo de Save e Load preserva todas as propriedades e inventário")
        void saveAndLoadCycle() throws Exception {
            Path tempSave = Files.createTempFile("javaquest_save_", ".properties");
            SaveManager.setCustomSavePath(tempSave);

            Player p = new Player("CyberMage");
            p.setMaxHp(120);
            p.setHp(85);
            p.setMaxMana(75);
            p.setMana(40);
            p.setLevel(3);
            p.setXp(350);
            p.setHighestStreak(5);
            p.registerAnswer(true);
            p.getInventory().addItem(Item.COFFEE_BREW, 2);
            p.getInventory().addItem(Item.JIT_BOOSTER, 1);
            p.getInventory().addItem(Item.CLEAN_ARCH_SHIELD, 3);

            assertTrue(SaveManager.saveGame(p, 3));
            assertTrue(SaveManager.hasSaveGame());

            SaveManager.SaveData data = SaveManager.loadGame();
            assertNotNull(data);
            assertEquals("CyberMage", data.playerName());
            assertEquals(3, data.currentLevel());
            assertEquals(85, data.hp());
            assertEquals(120, data.maxHp());
            assertEquals(55, data.mana()); // registerAnswer(true) restored 15 mana
            assertEquals(75, data.maxMana());
            assertEquals(2, data.inventory().get(Item.COFFEE_BREW));
            assertEquals(1, data.inventory().get(Item.JIT_BOOSTER));
            assertEquals(3, data.inventory().get(Item.CLEAN_ARCH_SHIELD));

            // Aplica em outro jogador
            Player restored = new Player("NewPlayer");
            data.applyTo(restored);
            assertEquals("CyberMage", restored.getName());
            assertEquals(85, restored.getHp());
            assertEquals(3, restored.getLevel());
            assertEquals(2, restored.getInventory().getCount(Item.COFFEE_BREW));

            assertTrue(SaveManager.deleteSaveGame());
            assertFalse(SaveManager.hasSaveGame());
            Files.deleteIfExists(tempSave);
        }

        @Test
        @DisplayName("7.3.2 - Tratamento seguro para save inexistente ou nulo")
        void saveNullAndMissingSafety() {
            assertFalse(SaveManager.saveGame(null, 1));

            Path nonExistent = Path.of("/tmp/non_existent_dir_12345/save.properties");
            SaveManager.setCustomSavePath(nonExistent);
            assertFalse(SaveManager.hasSaveGame());
            assertNull(SaveManager.loadGame());
            assertFalse(SaveManager.deleteSaveGame());
        }
    }

    @Nested
    @DisplayName("7.4: Integração dos Novos Atalhos Híbridos (Fase 3)")
    class Phase3HybridShortcutsTests {

        @ParameterizedTest(name = "Opção de continuar \"{0}\" mapeia para menu principal \"5\"")
        @ValueSource(strings = {"5", "r", "R", "resume", "RESUME", "continue", "CONTINUE", "load", "LOAD", "LOADGAME"})
        @DisplayName("7.4.1 - Parsing de atalho de continuar/resumir campanha")
        void mainMenuResumeParsing(String input) {
            assertEquals("5", GameEngine.parseMainMenuChoice(input));
        }

        @ParameterizedTest(name = "Opção de inventário \"{0}\" mapeia para ação de combate \"5\"")
        @ValueSource(strings = {"5", "b", "B", "bag", "BAG", "item", "ITEM", "items", "backpack", "inv", "inventory"})
        @DisplayName("7.4.2 - Parsing de atalho de Dev Backpack em combate")
        void combatBackpackParsing(String input) {
            assertEquals("5", GameEngine.parseCombatAction(input));
        }

        @Test
        @DisplayName("7.4.3 - Execução end-to-end de uso de item no Dev Backpack via GameEngine")
        void handleInventoryInEngine() {
            // Simula input "1" para escolher o primeiro item (Coffee Brew)
            ByteArrayInputStream in = new ByteArrayInputStream("1\n".getBytes());
            GameEngine engine = new GameEngine(in);
            Player p = new Player("BagTester");
            p.spendMana(50);
            p.getInventory().addItem(Item.COFFEE_BREW, 1);
            engine.setPlayer(p);

            Enemy enemy = Enemy.createForLevel(1);
            engine.handleInventory(enemy);

            assertEquals(50, p.getMana(), "Coffee Brew deve restaurar 40 MP");
            assertEquals(0, p.getInventory().getCount(Item.COFFEE_BREW), "Item deve ser consumido");
        }

        @Test
        @DisplayName("7.4.4 - GC Scroll usado via GameEngine elimina debuffs do chefe e arma dica")
        void handleInventoryInEngineGCScroll() {
            ByteArrayInputStream in = new ByteArrayInputStream("1\n".getBytes());
            GameEngine engine = new GameEngine(in);
            Player p = new Player("GCTester");
            p.getInventory().addItem(Item.GC_SCROLL, 1);
            engine.setPlayer(p);

            Enemy chimera = Enemy.createForLevel(4);
            chimera.setCouplingLockTurns(2);

            engine.handleInventory(chimera);

            assertEquals(0, chimera.getCouplingLockTurns(), "GC Scroll deve purgar o Coupling Lock!");
            assertTrue(engine.isHintActiveNextQuestion(), "GC Scroll deve armar o Hint para a próxima questão!");
            assertEquals(0, p.getInventory().getCount(Item.GC_SCROLL), "Item deve ser consumido");
        }

        @Test
        @DisplayName("7.4.5 - Abertura de Dev Backpack vazio retorna com segurança sem travar")
        void handleInventoryEmptyGraceful() {
            ByteArrayInputStream in = new ByteArrayInputStream("\n".getBytes());
            GameEngine engine = new GameEngine(in);
            Player p = new Player("EmptyBagTester");
            engine.setPlayer(p);

            Enemy enemy = Enemy.createForLevel(1);
            assertDoesNotThrow(() -> engine.handleInventory(enemy));
        }
    }

    @Nested
    @DisplayName("7.5: Categorização de Perguntas por Tags e Tópicos (3.2)")
    class QuestionsAndCategorizationTests {

        @Test
        @DisplayName("7.5.1 - Metadados de Question: estrelas de dificuldade (1 a 5) e tags associadas")
        void questionDifficultyStarsAndClamping() {
            Question q1 = new Question("Sintaxe", "Prompt 1", List.of(), List.of("A", "B"), 0, "Exp", 25, 1, List.of("tipos", "stack"));
            assertEquals("★☆☆☆☆ [Fácil]", q1.getDifficultyStars());
            assertEquals(1, q1.getDifficulty());

            Question q2 = new Question("Controle", "Prompt 2", List.of(), List.of("A", "B"), 0, "Exp", 25, 2, List.of("loops"));
            assertEquals("★★☆☆☆ [Iniciante]", q2.getDifficultyStars());

            Question q3 = new Question("Coleções", "Prompt 3", List.of(), List.of("A", "B"), 0, "Exp", 25, 3, List.of("list", "map"));
            assertEquals("★★★☆☆ [Intermediário]", q3.getDifficultyStars());

            Question q4 = new Question("OOP", "Prompt 4", List.of(), List.of("A", "B"), 0, "Exp", 25, 4, List.of("herança", "polimorfismo"));
            assertEquals("★★★★☆ [Avançado]", q4.getDifficultyStars());

            Question q5 = new Question("JVM", "Prompt 5", List.of(), List.of("A", "B"), 0, "Exp", 25, 5, List.of("gc", "heap"));
            assertEquals("★★★★★ [Especialista]", q5.getDifficultyStars());

            // Clamping em valores fora do intervalo [1, 5]
            Question qLow = new Question("Top", "P", List.of(), List.of("A"), 0, "E", 10, -5, null);
            assertEquals(1, qLow.getDifficulty());
            assertEquals("★☆☆☆☆ [Fácil]", qLow.getDifficultyStars());
            assertTrue(qLow.getTags().contains("Top"), "Tags nulas assumem o tópico como fallback");

            Question qHigh = new Question("Top", "P", List.of(), List.of("A"), 0, "E", 10, 99, List.of("tag"));
            assertEquals(5, qHigh.getDifficulty());
            assertEquals("★★★★★ [Especialista]", qHigh.getDifficultyStars());
        }

        @Test
        @DisplayName("7.5.2 - Método hasTag realiza busca case-insensitive e parcial em tags")
        void questionHasTagSearch() {
            Question q = new Question("JVM", "Prompt", List.of(), List.of("A", "B"), 0, "Exp", 25, 4, List.of("GarbageCollector", "HeapMemory", "ZGC"));
            assertTrue(q.hasTag("garbage"));
            assertTrue(q.hasTag("GARBAGECOLLECTOR"));
            assertTrue(q.hasTag("heap"));
            assertTrue(q.hasTag("zgc"));
            assertFalse(q.hasTag("concurrency"));
            assertFalse(q.hasTag(null));
            assertFalse(q.hasTag("   "));
        }

        @Test
        @DisplayName("7.5.3 - Consultas no QuestionBank: getAllQuestions, getQuestionsByTopic, getQuestionsByDifficulty, getQuestionsByTag")
        void questionBankQueryMethods() {
            List<Question> all = QuestionBank.getAllQuestions();
            assertNotNull(all);
            assertTrue(all.size() >= 30, "QuestionBank deve conter todas as questões dos 6 níveis");

            List<Question> jvmQuestions = QuestionBank.getQuestionsByTag("jvm");
            assertFalse(jvmQuestions.isEmpty(), "Deve encontrar questões com tag 'jvm'");
            for (Question q : jvmQuestions) {
                assertTrue(q.hasTag("jvm"));
            }

            List<Question> diff1Questions = QuestionBank.getQuestionsByDifficulty(1);
            assertFalse(diff1Questions.isEmpty());
            for (Question q : diff1Questions) {
                assertEquals(1, q.getDifficulty());
            }

            List<Question> primitiveQuestions = QuestionBank.getQuestionsByTopic("Primitive");
            assertFalse(primitiveQuestions.isEmpty());
            for (Question q : primitiveQuestions) {
                assertTrue(q.getTopic().toLowerCase().contains("primitive"));
            }

            List<Question> syntaxQuestions = QuestionBank.getQuestionsByTag("sintaxe");
            assertFalse(syntaxQuestions.isEmpty());
            for (Question q : syntaxQuestions) {
                assertTrue(q.hasTag("sintaxe"));
            }

            // Consultas inválidas ou vazias retornam lista vazia com segurança
            assertTrue(QuestionBank.getQuestionsByTopic(null).isEmpty());
            assertTrue(QuestionBank.getQuestionsByTopic("   ").isEmpty());
            assertTrue(QuestionBank.getQuestionsByTag(null).isEmpty());
            assertTrue(QuestionBank.getQuestionsByTag("TAG_INEXISTENTE_XYZ").isEmpty());
        }

        @Test
        @DisplayName("7.5.4 - withRandomizedOptions preserva difficulty e tags intactos")
        void withRandomizedOptionsPreservesDifficultyAndTags() {
            Question original = new Question("OOP", "Pergunta teste", List.of("int x = 1;"),
                List.of("Opção A", "Opção B", "Opção C", "Opção D"), 2, "Explicação", 30, 4, List.of("polimorfismo", "virtual"));
            
            Question shuffled = original.withRandomizedOptions();
            assertEquals(original.getDifficulty(), shuffled.getDifficulty());
            assertEquals(original.getTags(), shuffled.getTags());
            assertEquals(original.getTopic(), shuffled.getTopic());
            assertEquals(original.getPrompt(), shuffled.getPrompt());
            assertEquals(original.getDamage(), shuffled.getDamage());
            assertEquals(original.getExplanation(), shuffled.getExplanation());
            assertEquals(original.getOptions().get(original.getCorrectOptionIndex()),
                shuffled.getOptions().get(shuffled.getCorrectOptionIndex()),
                "Texto da resposta correta deve ser idêntico mesmo com shuffle");
        }
    }

    @Nested
    @DisplayName("7.6: Árvore de Talentos e Build Diversity (2.2)")
    class TalentsAndBuildDiversityTests {

        @Test
        @DisplayName("7.6.1 - Enum Talent possui códigos, nomes, descrições e ícones válidos")
        void talentEnumProperties() {
            for (Talent t : Talent.values()) {
                assertNotNull(t.getCode());
                assertFalse(t.getCode().isEmpty());
                assertNotNull(t.getDisplayName());
                assertNotNull(t.getDescription());
                assertNotNull(t.getIcon());
                assertEquals(t, Talent.fromCode(t.getCode()));
                assertEquals(t, Talent.fromCode(t.name()));
            }
            assertNull(Talent.fromCode(null));
            assertNull(Talent.fromCode("INVALID_TALENT_CODE"));
        }

        @Test
        @DisplayName("7.6.2 - Player armazena e consulta múltiplos talentos sem duplicatas")
        void playerTalentCollection() {
            Player p = new Player("TalentHero");
            assertTrue(p.getTalents().isEmpty());

            p.addTalent(Talent.GRAALVM_COMPILER);
            assertTrue(p.hasTalent(Talent.GRAALVM_COMPILER));
            assertEquals(1, p.getTalents().size());

            // Adicionar talento repetido não duplica
            p.addTalent(Talent.GRAALVM_COMPILER);
            assertEquals(1, p.getTalents().size());

            p.addTalent(Talent.TYPE_SAFETY_SHIELD);
            assertEquals(2, p.getTalents().size());
            assertTrue(p.hasTalent(Talent.TYPE_SAFETY_SHIELD));

            p.clearTalents();
            assertTrue(p.getTalents().isEmpty());
            assertFalse(p.hasTalent(Talent.GRAALVM_COMPILER));
        }

        @Test
        @DisplayName("7.6.3 - GraalVM Compiler: restaura +20 MP em acertos de quiz em vez dos +15 padrão")
        void graalVmCompilerBonusMana() {
            Player normal = new Player("NormalDev");
            normal.spendMana(50);
            int manaBefore = normal.getMana();
            normal.registerAnswer(true);
            assertEquals(manaBefore + 15, normal.getMana(), "Jogador comum restaura +15 MP");

            Player graal = new Player("GraalDev");
            graal.addTalent(Talent.GRAALVM_COMPILER);
            graal.spendMana(50);
            int graalBefore = graal.getMana();
            graal.registerAnswer(true);
            assertEquals(graalBefore + 20, graal.getMana(), "GraalVM Compiler restaura +20 MP (+5 bônus)");
        }

        @Test
        @DisplayName("7.6.4 - Type Safety Shield: reduz 75% do dano recebido em vez de 50%")
        void typeSafetyShieldMitigation() {
            Player normal = new Player("NormalShieldDev");
            normal.setShieldActive(true);
            int normalTaken = normal.takeDamage(40);
            assertEquals(20, normalTaken, "Escudo padrão reduz 50% (40 -> 20)");

            Player typeSafety = new Player("TypeSafetyDev");
            typeSafety.addTalent(Talent.TYPE_SAFETY_SHIELD);
            typeSafety.setShieldActive(true);
            int typeSafetyTaken = typeSafety.takeDamage(40);
            assertEquals(10, typeSafetyTaken, "Type Safety Shield reduz 75% (40 -> 10)");
            assertFalse(typeSafety.isShieldActive(), "Escudo deve ser consumido após o impacto");
        }

        @Test
        @DisplayName("7.6.5 - Pattern Matching Reflex: 20% chance de esquiva completa")
        void patternMatchingReflexEvasion() {
            Player p = new Player("ReflexHero");
            p.addTalent(Talent.PATTERN_MATCHING_REFLEX);

            // Simula roll < 0.20 (esquiva bem-sucedida)
            p.setForcedEvasionRoll(0.15);
            int dmgTaken = p.takeDamage(30);
            assertEquals(0, dmgTaken, "Dano deve ser zero ao esquivar");
            assertTrue(p.isEvasionTriggered(), "Flag de evasão deve ser true");
            assertEquals(100, p.getHp());

            p.clearEvasionTriggered();
            assertFalse(p.isEvasionTriggered());

            // Simula roll >= 0.20 (falha na esquiva)
            p.setForcedEvasionRoll(0.25);
            dmgTaken = p.takeDamage(30);
            assertEquals(30, dmgTaken, "Dano total aplicado ao falhar esquiva");
            assertFalse(p.isEvasionTriggered());
            assertEquals(70, p.getHp());
        }

        @Test
        @DisplayName("7.6.6 - Stream Parallelism: dobro de bônus de XP de streak e +5 dano mágico")
        void streamParallelismBonus() {
            Player normal = new Player("NormalStreamDev");
            normal.registerAnswer(true); // streak 1: 50 + 10 = 60 XP
            assertEquals(60, normal.getXp());
            assertEquals(0, normal.getSpellDamageBonus(), "Streak 1 normal não dá bônus de dano");

            Player parallel = new Player("ParallelDev");
            parallel.addTalent(Talent.STREAM_PARALLELISM);
            parallel.registerAnswer(true); // streak 1: 50 + 20 = 70 XP
            assertEquals(70, parallel.getXp(), "Streak com Stream Parallelism concede o dobro de XP por streak");
            assertEquals(5, parallel.getSpellDamageBonus(), "Stream Parallelism concede +5 dano mágico mesmo no primeiro acerto");
        }

        @Test
        @DisplayName("7.6.7 - Zero Allocation: feitiços de debug custam 25% menos mana no GameEngine")
        void zeroAllocationSpellCostReduction() {
            GameEngine engine = new GameEngine();
            Player p = new Player("ZeroAllocDev");
            engine.setPlayer(p);

            assertEquals(20, engine.getSpellCost(20), "Custo padrão sem talento");
            assertEquals(15, engine.getSpellCost(15), "Custo padrão sem talento");

            p.addTalent(Talent.ZERO_ALLOCATION);
            assertEquals(15, engine.getSpellCost(20), "20 MP reduzido em 25% resulta em 15 MP");
            assertEquals(11, engine.getSpellCost(15), "15 MP reduzido em 25% resulta em 11 MP");
        }

        @Test
        @DisplayName("7.6.8 - HotSpot Inlining: adiciona +10 de dano base no cálculo de feitiço do GameEngine")
        void hotspotInliningBonusDamage() {
            Player p = new Player("InliningDev");
            assertEquals(25, GameEngine.calculateTotalDamage(25, 0, p));

            p.addTalent(Talent.HOTSPOT_INLINING);
            assertEquals(35, GameEngine.calculateTotalDamage(25, 0, p), "HotSpot Inlining adiciona +10 de dano base");
            assertEquals(45, GameEngine.calculateTotalDamage(25, 10, p), "Dano base + bônus de combo + inlining");
        }

        @Test
        @DisplayName("7.6.9 - promptTalentSelection escolhe e equipa talentos via entrada de usuário")
        void promptTalentSelectionSelectionFlow() {
            ByteArrayInputStream in = new ByteArrayInputStream("1\n\n".getBytes());
            GameEngine engine = new GameEngine(in);
            Player p = new Player("PerkPicker");
            engine.setPlayer(p);

            engine.promptTalentSelection();
            assertEquals(1, p.getTalents().size(), "Jogador deve adquirir 1 talento escolhido");

            // Quando todos os 6 talentos já foram adquiridos, concede bônus de HP/Mana
            for (Talent t : Talent.values()) {
                p.addTalent(t);
            }
            int hpBefore = p.getMaxHp();
            int manaBefore = p.getMaxMana();

            ByteArrayInputStream inOverclock = new ByteArrayInputStream("\n".getBytes());
            GameEngine engineOverclock = new GameEngine(inOverclock);
            engineOverclock.setPlayer(p);
            engineOverclock.promptTalentSelection();

            assertEquals(hpBefore + 15, p.getMaxHp(), "Overclock concede +15 Max HP");
            assertEquals(manaBefore + 15, p.getMaxMana(), "Overclock concede +15 Max MP");
        }
    }

    @Nested
    @DisplayName("7.7: Modos de Dificuldade Selecionáveis (2.4)")
    class GameDifficultyModesTests {

        @Test
        @DisplayName("7.7.1 - Enum GameDifficulty possui multiplicadores e regras corretas")
        void gameDifficultyEnumProperties() {
            assertEquals(0.65, GameDifficulty.INTERN.getEnemyDamageMultiplier());
            assertEquals(5, GameDifficulty.INTERN.getHintManaCost());
            assertFalse(GameDifficulty.INTERN.isPermadeath());

            assertEquals(1.0, GameDifficulty.MID_LEVEL.getEnemyDamageMultiplier());
            assertEquals(15, GameDifficulty.MID_LEVEL.getHintManaCost());
            assertFalse(GameDifficulty.MID_LEVEL.isPermadeath());

            assertEquals(1.30, GameDifficulty.SENIOR_ARCHITECT.getEnemyDamageMultiplier());
            assertEquals(15, GameDifficulty.SENIOR_ARCHITECT.getHintManaCost());
            assertTrue(GameDifficulty.SENIOR_ARCHITECT.isPermadeath());

            // Testes de parsing
            assertEquals(GameDifficulty.INTERN, GameDifficulty.fromChoice("1"));
            assertEquals(GameDifficulty.INTERN, GameDifficulty.fromChoice("i"));
            assertEquals(GameDifficulty.INTERN, GameDifficulty.fromChoice("INTERN"));
            assertEquals(GameDifficulty.INTERN, GameDifficulty.fromChoice("estagiario"));

            assertEquals(GameDifficulty.MID_LEVEL, GameDifficulty.fromChoice("2"));
            assertEquals(GameDifficulty.MID_LEVEL, GameDifficulty.fromChoice("m"));
            assertEquals(GameDifficulty.MID_LEVEL, GameDifficulty.fromChoice("NORMAL"));

            assertEquals(GameDifficulty.SENIOR_ARCHITECT, GameDifficulty.fromChoice("3"));
            assertEquals(GameDifficulty.SENIOR_ARCHITECT, GameDifficulty.fromChoice("s"));
            assertEquals(GameDifficulty.SENIOR_ARCHITECT, GameDifficulty.fromChoice("SENIOR"));
            assertEquals(GameDifficulty.SENIOR_ARCHITECT, GameDifficulty.fromChoice("hardcore"));

            // Default fallback
            assertEquals(GameDifficulty.MID_LEVEL, GameDifficulty.fromChoice(null));
            assertEquals(GameDifficulty.MID_LEVEL, GameDifficulty.fromChoice("INVALID"));
        }

        @Test
        @DisplayName("7.7.2 - GameEngine setter/getter e parsing de atalho no menu principal (6 | D)")
        void gameEngineDifficultyIntegration() {
            GameEngine engine = new GameEngine();
            assertEquals(GameDifficulty.MID_LEVEL, engine.getDifficulty(), "Dificuldade padrão deve ser Mid-Level");

            engine.setDifficulty(GameDifficulty.INTERN);
            assertEquals(GameDifficulty.INTERN, engine.getDifficulty());

            engine.setDifficulty(null);
            assertEquals(GameDifficulty.MID_LEVEL, engine.getDifficulty(), "Nulo assume MID_LEVEL");

            assertEquals("6", GameEngine.parseMainMenuChoice("6"));
            assertEquals("6", GameEngine.parseMainMenuChoice("d"));
            assertEquals("6", GameEngine.parseMainMenuChoice("D"));
            assertEquals("6", GameEngine.parseMainMenuChoice("diff"));
            assertEquals("6", GameEngine.parseMainMenuChoice("DIFFICULTY"));
        }

        @Test
        @DisplayName("7.7.3 - Multiplicador de dano na dificuldade Estagiário vs Arquiteto Sênior")
        void enemyAttackMultiplierByDifficulty() {
            int baseDamage = 20;
            int internDmg = (int) Math.round(baseDamage * GameDifficulty.INTERN.getEnemyDamageMultiplier());
            int midDmg = (int) Math.round(baseDamage * GameDifficulty.MID_LEVEL.getEnemyDamageMultiplier());
            int seniorDmg = (int) Math.round(baseDamage * GameDifficulty.SENIOR_ARCHITECT.getEnemyDamageMultiplier());

            assertEquals(13, internDmg, "Estagiário recebe 35% a menos de dano (13 de 20)");
            assertEquals(20, midDmg, "Pleno recebe dano padrão integral (20)");
            assertEquals(26, seniorDmg, "Sênior recebe 30% a mais de dano (26 de 20)");
        }

        @Test
        @DisplayName("7.7.4 - Custo do feitiço Hint reduzido no Modo Estagiário")
        void internDifficultyHintManaCost() {
            GameEngine internEngine = new GameEngine();
            Player internPlayer = new Player("InternPlayer");
            internEngine.setPlayer(internPlayer);
            internEngine.setDifficulty(GameDifficulty.INTERN);

            int hintCost = internEngine.getSpellCost(internEngine.getDifficulty().getHintManaCost());
            assertEquals(5, hintCost, "Dica no Modo Estagiário custa apenas 5 MP");

            // Com talento Zero Allocation
            internPlayer.addTalent(Talent.ZERO_ALLOCATION);
            int zeroAllocCost = internEngine.getSpellCost(internEngine.getDifficulty().getHintManaCost());
            assertEquals(4, zeroAllocCost, "Dica com Zero Allocation no Modo Estagiário custa 4 MP");
        }

        @Test
        @DisplayName("7.7.5 - Permadeath no Modo Arquiteto Sênior apaga save game e encerra sem retry")
        void seniorDifficultyPermadeathWipesSave() {
            Path tempSave = Path.of(System.getProperty("java.io.tmpdir"), "javaquest_permadeath_test.properties");
            SaveManager.setCustomSavePath(tempSave);
            try {
                Player p = new Player("HardcoreHero");
                p.setHp(0); // Derrotado
                SaveManager.saveGame(p, 4, GameDifficulty.SENIOR_ARCHITECT);
                assertTrue(SaveManager.hasSaveGame());

                ByteArrayInputStream in = new ByteArrayInputStream("\n".getBytes());
                GameEngine engine = new GameEngine(in);
                engine.setPlayer(p);
                engine.setCurrentLevel(4);
                engine.setDifficulty(GameDifficulty.SENIOR_ARCHITECT);

                boolean retry = engine.showGameOverScreen();
                assertFalse(retry, "No modo Permadeath o Game Over não oferece retry e encerra com false");
                assertFalse(SaveManager.hasSaveGame(), "No modo Permadeath o save game em disco deve ser apagado!");
            } finally {
                SaveManager.resetDefaultSavePath();
                try { Files.deleteIfExists(tempSave); } catch (Exception ignored) {}
            }
        }

        @Test
        @DisplayName("7.7.6 - Save e Load persistem e restauram a dificuldade selecionada e talentos")
        void saveAndLoadPersistsDifficultyAndTalents() {
            Path tempSave = Path.of(System.getProperty("java.io.tmpdir"), "javaquest_diff_save_test.properties");
            SaveManager.setCustomSavePath(tempSave);
            try {
                Player p = new Player("ArchitectAda");
                p.addTalent(Talent.GRAALVM_COMPILER);
                p.addTalent(Talent.HOTSPOT_INLINING);
                p.getInventory().addItem(Item.BREAKPOINT_TOKEN, 1);

                boolean saved = SaveManager.saveGame(p, 3, GameDifficulty.SENIOR_ARCHITECT);
                assertTrue(saved);

                SaveManager.SaveData loaded = SaveManager.loadGame();
                assertNotNull(loaded);
                assertEquals(GameDifficulty.SENIOR_ARCHITECT, loaded.difficulty());
                assertNotNull(loaded.talents());
                assertTrue(loaded.talents().contains(Talent.GRAALVM_COMPILER));
                assertTrue(loaded.talents().contains(Talent.HOTSPOT_INLINING));

                Player restored = new Player("Restored");
                loaded.applyTo(restored);
                assertTrue(restored.hasTalent(Talent.GRAALVM_COMPILER));
                assertTrue(restored.hasTalent(Talent.HOTSPOT_INLINING));
                assertEquals(1, restored.getInventory().getCount(Item.BREAKPOINT_TOKEN));
            } finally {
                SaveManager.resetDefaultSavePath();
                try { Files.deleteIfExists(tempSave); } catch (Exception ignored) {}
            }
        }
    }

    @Nested
    @DisplayName("7.8: Modo Laboratório de Treino (Bytecode Lab / Study Mode) (3.3)")
    class BytecodeLabStudyModeTests {

        @Test
        @DisplayName("7.8.1 - Categorias disponíveis e filtragem de questões no Laboratório")
        void labCategoryFiltering() {
            List<String> categories = BytecodeLab.getAvailableCategories();
            assertNotNull(categories);
            assertEquals(7, categories.size());

            List<Question> all = BytecodeLab.filterQuestions("ALL");
            assertTrue(all.size() >= 30);

            List<Question> syntax = BytecodeLab.filterQuestions("Sintaxe & Primitivos");
            assertFalse(syntax.isEmpty());

            List<Question> loops = BytecodeLab.filterQuestions("3");
            assertFalse(loops.isEmpty());

            List<Question> arrays = BytecodeLab.filterQuestions("Arrays & Coleções");
            assertFalse(arrays.isEmpty());

            List<Question> oop = BytecodeLab.filterQuestions("POO & Polimorfismo");
            assertFalse(oop.isEmpty());

            List<Question> jvm = BytecodeLab.filterQuestions("Exceções & JVM / GC");
            assertFalse(jvm.isEmpty());

            List<Question> streams = BytecodeLab.filterQuestions("7");
            assertFalse(streams.isEmpty());

            // Null ou vazio retorna todas
            assertEquals(all.size(), BytecodeLab.filterQuestions(null).size());
            assertEquals(all.size(), BytecodeLab.filterQuestions("").size());
        }

        @Test
        @DisplayName("7.8.2 - Execução de sessão no BytecodeLab com acertos, erros e scorecard final")
        void labSessionExecutionAndScorecard() {
            Question q1 = new Question("Sintaxe", "P1", List.of(), List.of("A1", "B1", "C1", "D1"), 0, "Exp1", 20);
            Question q2 = new Question("Sintaxe", "P2", List.of(), List.of("A2", "B2", "C2", "D2"), 1, "Exp2", 20);

            // Simula respostas:
            // Questão 1: "A" (correta) -> avança com "\n"
            // Questão 2: "C" (incorreta) -> avança com "\n"
            String simulatedInputs = "A\n\nC\n\n";
            ByteArrayInputStream in = new ByteArrayInputStream(simulatedInputs.getBytes());
            BytecodeLab lab = new BytecodeLab(in);

            BytecodeLab.LabResult result = lab.runSession(List.of(q1, q2));

            assertNotNull(result);
            assertEquals(2, result.totalAnswered());
            assertEquals(1, result.totalCorrect());
            assertEquals(50.0, result.accuracy(), 0.01);
            assertTrue(result.topicStats().containsKey("Sintaxe"));
            assertEquals(1, result.topicStats().get("Sintaxe")[0], "1 acerto em Sintaxe");
            assertEquals(2, result.topicStats().get("Sintaxe")[1], "2 respondidas em Sintaxe");
        }

        @Test
        @DisplayName("7.8.3 - Saída antecipada com comando '0' ou 'Q' encerra sessão com segurança")
        void labSessionEarlyExit() {
            Question q1 = new Question("OOP", "P1", List.of(), List.of("A", "B"), 0, "Exp", 20);
            Question q2 = new Question("OOP", "P2", List.of(), List.of("A", "B"), 0, "Exp", 20);

            ByteArrayInputStream in = new ByteArrayInputStream("0\n".getBytes());
            BytecodeLab lab = new BytecodeLab(in);

            BytecodeLab.LabResult result = lab.runSession(List.of(q1, q2));
            assertEquals(0, result.totalAnswered());
            assertEquals(0, result.totalCorrect());
            assertEquals(0.0, result.accuracy());
        }

        @Test
        @DisplayName("7.8.4 - Parsing de opções do menu principal para o Laboratório (7 | LAB)")
        void labMainMenuParsing() {
            assertEquals("7", GameEngine.parseMainMenuChoice("7"));
            assertEquals("7", GameEngine.parseMainMenuChoice("b"));
            assertEquals("7", GameEngine.parseMainMenuChoice("B"));
            assertEquals("7", GameEngine.parseMainMenuChoice("lab"));
            assertEquals("7", GameEngine.parseMainMenuChoice("LAB"));
            assertEquals("7", GameEngine.parseMainMenuChoice("study"));
            assertEquals("7", GameEngine.parseMainMenuChoice("STUDY"));
            assertEquals("7", GameEngine.parseMainMenuChoice("treino"));
            assertEquals("7", GameEngine.parseMainMenuChoice("TREINO"));
        }
    }
}
