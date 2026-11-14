package game;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("2.4 - QuestionBankTest: Integridade Global do Banco de Questões")
public class QuestionBankTest {

    @Nested
    @DisplayName("2.4.1 - 2.4.7: Cobertura por Nível e Fallback")
    class LevelCoverageTests {

        @ParameterizedTest(name = "Nível {0} possui banco de questões cadastrado e >= 5 questões")
        @ValueSource(ints = {1, 2, 3, 4, 5, 6})
        @DisplayName("2.4.1 - 2.4.6 - Cada nível possui ao menos 5 perguntas")
        void levelsHaveMinimumQuestions(int level) {
            List<Question> questions = QuestionBank.getQuestionsForLevel(level);
            assertNotNull(questions, "Lista de questões do nível " + level + " não deve ser nula");
            assertTrue(questions.size() >= 5,
                "Nível " + level + " deve ter pelo menos 5 perguntas, mas tem " + questions.size());
        }

        @ParameterizedTest(name = "Nível inválido {0} retorna fallback do nível 1")
        @ValueSource(ints = {0, -1, -50, 7, 8, 99})
        @DisplayName("2.4.7 - Níveis fora da faixa 1-6 retornam fallback seguro para o nível 1")
        void invalidLevelFallbacksToLevelOne(int invalidLevel) {
            List<Question> fallbackQuestions = QuestionBank.getQuestionsForLevel(invalidLevel);
            List<Question> levelOneQuestions = QuestionBank.getQuestionsForLevel(1);
            assertEquals(levelOneQuestions.size(), fallbackQuestions.size(),
                "Fallback deve retornar o mesmo número de perguntas do nível 1");
            Set<String> levelOnePrompts = new HashSet<>();
            for (Question q : levelOneQuestions) {
                levelOnePrompts.add(q.getPrompt());
            }
            Set<String> fallbackPrompts = new HashSet<>();
            for (Question q : fallbackQuestions) {
                fallbackPrompts.add(q.getPrompt());
            }
            assertEquals(levelOnePrompts, fallbackPrompts,
                "Fallback deve conter o mesmo conjunto de questões do nível 1");
        }
    }

    @Nested
    @DisplayName("2.4.8: Varredura Exaustiva de Integridade de Todas as Perguntas")
    class ExhaustiveIntegrityScanTests {

        @ParameterizedTest(name = "Varredura exaustiva de integridade nas perguntas do nível {0}")
        @ValueSource(ints = {1, 2, 3, 4, 5, 6})
        @DisplayName("2.4.8 - Validação estrutural de cada pergunta, alternativas e explicações")
        void validateEveryQuestionInLevel(int level) {
            List<Question> questions = QuestionBank.getQuestionsForLevel(level);

            for (int i = 0; i < questions.size(); i++) {
                Question q = questions.get(i);
                String qRef = "Nível " + level + " Questão [" + i + "]: ";

                // Tópico
                assertNotNull(q.getTopic(), qRef + "Tópico não pode ser nulo");
                assertFalse(q.getTopic().trim().isEmpty(), qRef + "Tópico não pode ser vazio");

                // Prompt
                assertNotNull(q.getPrompt(), qRef + "Prompt não pode ser nulo");
                assertFalse(q.getPrompt().trim().isEmpty(), qRef + "Prompt não pode ser vazio");

                // Explicação
                assertNotNull(q.getExplanation(), qRef + "Explicação não pode ser nula");
                assertFalse(q.getExplanation().trim().isEmpty(), qRef + "Explicação não pode ser vazia");

                // Dano
                assertTrue(q.getDamage() > 0, qRef + "Dano deve ser estritamente positivo (> 0)");

                // Alternativas
                List<String> options = q.getOptions();
                assertNotNull(options, qRef + "Lista de alternativas não pode ser nula");
                assertEquals(4, options.size(), qRef + "Toda pergunta DEVE conter rigorosamente 4 alternativas");

                // Índice correto
                assertTrue(q.getCorrectOptionIndex() >= 0 && q.getCorrectOptionIndex() < 4,
                    qRef + "correctOptionIndex deve estar entre 0 e 3");

                // Sem opções vazias ou nulas
                Set<String> distinctOptions = new HashSet<>();
                for (int optIdx = 0; optIdx < options.size(); optIdx++) {
                    String opt = options.get(optIdx);
                    assertNotNull(opt, qRef + "Alternativa " + optIdx + " não pode ser nula");
                    assertFalse(opt.trim().isEmpty(), qRef + "Alternativa " + optIdx + " não pode ser vazia");
                    distinctOptions.add(opt);
                }

                // Opções únicas
                assertEquals(4, distinctOptions.size(),
                    qRef + "Todas as 4 alternativas devem ser mutuamente exclusivas e distintas");

                // Code Snippet
                if (q.getCodeSnippet() != null) {
                    for (int sIdx = 0; sIdx < q.getCodeSnippet().size(); sIdx++) {
                        assertNotNull(q.getCodeSnippet().get(sIdx),
                            qRef + "Linha de código " + sIdx + " não pode ser nula");
                    }
                }
            }
        }
    }

    @Nested
    @DisplayName("2.4.9 - 2.4.38: Testes de Integridade Temática por Fase")
    class ThematicIntegrityTests {

        @Test
        @DisplayName("2.4.9 - 2.4.14 - Nível 1 aborda Primitivos, Strings, Imutabilidade e Modificadores")
        void levelOneThemes() {
            List<Question> questions = QuestionBank.getQuestionsForLevel(1);
            Set<String> topics = new HashSet<>();
            for (Question q : questions) topics.add(q.getTopic());

            assertTrue(topics.contains("Primitive Types"));
            assertTrue(topics.contains("Default Values"));
            assertTrue(topics.contains("String Immutability"));
            assertTrue(topics.contains("Final Keyword"));
            assertTrue(topics.contains("Character vs String"));
            assertTrue(topics.contains("Integer Precision"));
        }

        @Test
        @DisplayName("2.4.15 - 2.4.19 - Nível 2 aborda Laços de Repetição, Operadores e Branching")
        void levelTwoThemes() {
            List<Question> questions = QuestionBank.getQuestionsForLevel(2);
            Set<String> topics = new HashSet<>();
            for (Question q : questions) topics.add(q.getTopic());

            assertTrue(topics.contains("For Loop and Continue"));
            assertTrue(topics.contains("Short-Circuit Operators"));
            assertTrue(topics.contains("Switch Fall-Through"));
            assertTrue(topics.contains("Do-While Guarantees"));
            assertTrue(topics.contains("While Loop Termination"));
            assertTrue(topics.contains("Post-Increment in Expressions"));
        }

        @Test
        @DisplayName("2.4.20 - 2.4.24 - Nível 3 aborda Arrays, Memória, Strings e Listas")
        void levelThreeThemes() {
            List<Question> questions = QuestionBank.getQuestionsForLevel(3);
            Set<String> topics = new HashSet<>();
            for (Question q : questions) topics.add(q.getTopic());

            assertTrue(topics.contains("Array Indexing"));
            assertTrue(topics.contains("String Equality"));
            assertTrue(topics.contains("Array Length Property"));
            assertTrue(topics.contains("ArrayList Operations"));
            assertTrue(topics.contains("Default Array Values"));
            assertTrue(topics.contains("String Substring Bounds"));
        }

        @Test
        @DisplayName("2.4.25 - 2.4.29 - Nível 4 aborda OOP, Polimorfismo, Encapsulamento e Herança")
        void levelFourThemes() {
            List<Question> questions = QuestionBank.getQuestionsForLevel(4);
            Set<String> topics = new HashSet<>();
            for (Question q : questions) topics.add(q.getTopic());

            assertTrue(topics.contains("Dynamic Method Dispatch"));
            assertTrue(topics.contains("Overloading vs Overriding"));
            assertTrue(topics.contains("Encapsulation Modifiers"));
            assertTrue(topics.contains("Super Constructor Call"));
            assertTrue(topics.contains("Interface Capabilities"));
            assertTrue(topics.contains("Abstract Class Constructors"));
        }

        @Test
        @DisplayName("2.4.30 - 2.4.34 - Nível 5 aborda Exceções, Garbage Collection e Gerenciamento de Recursos")
        void levelFiveThemes() {
            List<Question> questions = QuestionBank.getQuestionsForLevel(5);
            Set<String> topics = new HashSet<>();
            for (Question q : questions) topics.add(q.getTopic());

            assertTrue(topics.contains("Try-Catch-Finally Order"));
            assertTrue(topics.contains("Checked vs Unchecked Exceptions"));
            assertTrue(topics.contains("Garbage Collection Eligibility"));
            assertTrue(topics.contains("Static Variables"));
            assertTrue(topics.contains("Try-With-Resources"));
            assertTrue(topics.contains("Throw vs Throws"));
        }

        @Test
        @DisplayName("2.4.35 - 2.4.38 - Nível 6 aborda Recursão, Streams, Generics e Concorrência")
        void levelSixThemes() {
            List<Question> questions = QuestionBank.getQuestionsForLevel(6);
            Set<String> topics = new HashSet<>();
            for (Question q : questions) topics.add(q.getTopic());

            assertTrue(topics.contains("Recursion Base Case"));
            assertTrue(topics.contains("Java Streams API"));
            assertTrue(topics.contains("Generics Type Erasure"));
            assertTrue(topics.contains("Optional Monad"));
            assertTrue(topics.contains("Functional Interface"));
            assertTrue(topics.contains("Thread-Safe Map"));
        }
    }
}
