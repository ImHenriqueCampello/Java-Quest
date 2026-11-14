package game;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("2.3 - QuestionTest: Modelo de Questão e Randomização")
public class QuestionTest {

    private Question createSampleQuestion() {
        return new Question(
            "Java Primitives",
            "What is the default value of a boolean field in Java?",
            List.of("class Test {", "    boolean flag;", "}"),
            List.of("true", "false", "0", "null"),
            1, // "false" is index 1
            "Instance boolean fields default to false.",
            25
        );
    }

    @Nested
    @DisplayName("2.3.1 - 2.3.3: Construtor e Getters")
    class ConstructorAndGettersTests {

        @Test
        @DisplayName("2.3.1 & 2.3.2 - Construtor atribui todos os campos e getters retornam corretamente")
        void constructorAndGetters() {
            Question q = createSampleQuestion();
            assertEquals("Java Primitives", q.getTopic());
            assertEquals("What is the default value of a boolean field in Java?", q.getPrompt());
            assertNotNull(q.getCodeSnippet());
            assertEquals(3, q.getCodeSnippet().size());
            assertEquals(4, q.getOptions().size());
            assertEquals(1, q.getCorrectOptionIndex());
            assertEquals("Instance boolean fields default to false.", q.getExplanation());
            assertEquals(25, q.getDamage());
        }

        @Test
        @DisplayName("2.3.3 - Questão suporta codeSnippet nulo ou lista vazia")
        void nullOrEmptySnippet() {
            Question qNull = new Question(
                "OOP", "What is polymorphism?", null,
                List.of("A", "B", "C", "D"), 0, "Expl", 20
            );
            assertNull(qNull.getCodeSnippet());

            Question qEmpty = new Question(
                "OOP", "What is polymorphism?", List.of(),
                List.of("A", "B", "C", "D"), 0, "Expl", 20
            );
            assertNotNull(qEmpty.getCodeSnippet());
            assertTrue(qEmpty.getCodeSnippet().isEmpty());
        }
    }

    @Nested
    @DisplayName("2.3.4 - 2.3.9: Randomização de Alternativas (withRandomizedOptions)")
    class RandomizationTests {

        @Test
        @DisplayName("2.3.4 - withRandomizedOptions cria uma nova instância sem mutar a original")
        void randomizationReturnsNewInstance() {
            Question original = createSampleQuestion();
            Question randomized = original.withRandomizedOptions();

            assertNotSame(original, randomized);
            assertEquals(1, original.getCorrectOptionIndex());
            assertEquals(List.of("true", "false", "0", "null"), original.getOptions());
        }

        @Test
        @DisplayName("2.3.5 - Randomização preserva a resposta correta semanticamente")
        void randomizationPreservesCorrectAnswer() {
            Question original = createSampleQuestion();
            String originalCorrectText = original.getOptions().get(original.getCorrectOptionIndex());

            for (int i = 0; i < 50; i++) {
                Question randomized = original.withRandomizedOptions();
                String newCorrectText = randomized.getOptions().get(randomized.getCorrectOptionIndex());
                assertEquals(originalCorrectText, newCorrectText,
                    "A resposta correta selecionada pelo índice deve ser a mesma semanticamente");
            }
        }

        @Test
        @DisplayName("2.3.6 - Randomização mantém exatamente 4 opções")
        void randomizationMaintainsFourOptions() {
            Question original = createSampleQuestion();
            Question randomized = original.withRandomizedOptions();
            assertEquals(4, randomized.getOptions().size());
        }

        @Test
        @DisplayName("2.3.7 - Randomização mantém todas as 4 alternativas originais sem duplicatas")
        void randomizationPreservesAllOptions() {
            Question original = createSampleQuestion();
            Question randomized = original.withRandomizedOptions();

            Set<String> originalSet = new HashSet<>(original.getOptions());
            Set<String> randomizedSet = new HashSet<>(randomized.getOptions());

            assertEquals(originalSet, randomizedSet);
            assertEquals(4, randomizedSet.size());
        }

        @Test
        @DisplayName("2.3.8 - Randomização preserva metadados (topic, prompt, snippet, explanation, damage)")
        void randomizationPreservesMetadata() {
            Question original = createSampleQuestion();
            Question randomized = original.withRandomizedOptions();

            assertEquals(original.getTopic(), randomized.getTopic());
            assertEquals(original.getPrompt(), randomized.getPrompt());
            assertEquals(original.getCodeSnippet(), randomized.getCodeSnippet());
            assertEquals(original.getExplanation(), randomized.getExplanation());
            assertEquals(original.getDamage(), randomized.getDamage());
        }

        @Test
        @DisplayName("2.3.9 - Distribuição estatística: a resposta correta visita todas as posições (0, 1, 2, 3)")
        void randomizationDistributesAcrossAllIndices() {
            Question original = createSampleQuestion();
            Set<Integer> visitedIndices = new HashSet<>();

            for (int i = 0; i < 150; i++) {
                Question randomized = original.withRandomizedOptions();
                visitedIndices.add(randomized.getCorrectOptionIndex());
            }

            assertTrue(visitedIndices.contains(0), "Índice 0 deve ter sido sorteado");
            assertTrue(visitedIndices.contains(1), "Índice 1 deve ter sido sorteado");
            assertTrue(visitedIndices.contains(2), "Índice 2 deve ter sido sorteado");
            assertTrue(visitedIndices.contains(3), "Índice 3 deve ter sido sorteado");
        }
    }

    @Nested
    @DisplayName("2.3.10 - 2.3.24: Casos Limite e Formatação Especial")
    class EdgeCasesTests {

        @Test
        @DisplayName("2.3.10 - Textos com generics e delimitadores (<>, ?, &)")
        void genericsTokens() {
            Question q = new Question(
                "Generics", "What does List<? extends Number> mean?",
                List.of("Map<String, List<Integer>> map = new HashMap<>();"),
                List.of("Upper bounded", "Lower bounded", "Raw type", "Unchecked"),
                0, "Explanation with <T>.", 30
            );
            assertEquals("Generics", q.getTopic());
            assertTrue(q.getCodeSnippet().get(0).contains("Map<String, List<Integer>>"));
        }

        @Test
        @DisplayName("2.3.11 - Aspas escapadas e barras em strings (\" \", \\n, \\t)")
        void escapedQuotesAndSlashes() {
            Question q = new Question(
                "Strings", "What does \"\\n\" represent?",
                List.of("String s = \"\\tTab\\nNewline\";"),
                List.of("Newline", "Tab", "Space", "Null"),
                0, "Backslash escapes special chars.", 25
            );
            assertTrue(q.getCodeSnippet().get(0).contains("\"\\tTab\\nNewline\""));
        }

        @Test
        @DisplayName("2.3.12 - Code snippet com múltiplas linhas preserva ordem e integridade")
        void multilineSnippetPreserved() {
            List<String> lines = List.of(
                "public class Example {",
                "    public static void main(String[] args) {",
                "        System.out.println(42);",
                "    }",
                "}"
            );
            Question q = new Question("Syntax", "What does it print?", lines, List.of("42", "0", "null", "Error"), 0, "Prints 42", 25);
            assertEquals(5, q.getCodeSnippet().size());
            assertEquals("        System.out.println(42);", q.getCodeSnippet().get(2));
        }

        @Test
        @DisplayName("2.3.13 - Prompt longo (> 120 caracteres) é suportado integralmente")
        void longPromptSupported() {
            String longPrompt = "Considere um cenário de concorrência onde múltiplas threads tentam acessar e modificar simultaneamente um objeto mutável compartilhado sem sincronização adequada.";
            Question q = new Question("Concurrency", longPrompt, List.of(), List.of("Race Condition", "Deadlock", "Livelock", "Starvation"), 0, "Race condition occurs.", 35);
            assertEquals(longPrompt, q.getPrompt());
            assertTrue(q.getPrompt().length() > 120);
        }

        @Test
        @DisplayName("2.3.14 - Alternativas longas são suportadas")
        void longOptionsSupported() {
            List<String> options = List.of(
                "Uma interface funcional que recebe um argumento do tipo T e produz um resultado do tipo R via método apply",
                "Uma interface que consome um argumento sem retornar resultado",
                "Um predicado booleano de validação",
                "Um fornecedor puro de instâncias"
            );
            Question q = new Question("Functional", "O que é Function<T, R>?", List.of(), options, 0, "Function mapeia T em R.", 30);
            assertEquals(4, q.getOptions().size());
            assertTrue(q.getOptions().get(0).length() > 50);
        }

        @Test
        @DisplayName("2.3.15 - Code snippet preserva identação de 4 espaços")
        void snippetIndentationPreserved() {
            Question q = new Question("Formatting", "P", List.of("    int x = 10;"), List.of("A", "B", "C", "D"), 0, "E", 20);
            assertTrue(q.getCodeSnippet().get(0).startsWith("    int"));
        }

        @Test
        @DisplayName("2.3.16 - Dano alto (100 de dano)")
        void highDamageValue() {
            Question q = new Question("Boss Spell", "P", List.of(), List.of("A", "B", "C", "D"), 0, "E", 100);
            assertEquals(100, q.getDamage());
        }

        @Test
        @DisplayName("2.3.17 - Dano moderado (35 de dano)")
        void moderateDamageValue() {
            Question q = new Question("Regular", "P", List.of(), List.of("A", "B", "C", "D"), 0, "E", 35);
            assertEquals(35, q.getDamage());
        }

        @Test
        @DisplayName("2.3.18 - Dano baixo (5 de dano)")
        void lowDamageValue() {
            Question q = new Question("Weak", "P", List.of(), List.of("A", "B", "C", "D"), 0, "E", 5);
            assertEquals(5, q.getDamage());
        }

        @Test
        @DisplayName("2.3.19 - Resposta correta no índice 0")
        void correctIndexZero() {
            Question q = new Question("T", "P", List.of(), List.of("CORRETO", "W1", "W2", "W3"), 0, "E", 20);
            assertEquals(0, q.getCorrectOptionIndex());
            assertEquals("CORRETO", q.getOptions().get(q.getCorrectOptionIndex()));
        }

        @Test
        @DisplayName("2.3.20 - Resposta correta no índice 1")
        void correctIndexOne() {
            Question q = new Question("T", "P", List.of(), List.of("W0", "CORRETO", "W2", "W3"), 1, "E", 20);
            assertEquals(1, q.getCorrectOptionIndex());
            assertEquals("CORRETO", q.getOptions().get(q.getCorrectOptionIndex()));
        }

        @Test
        @DisplayName("2.3.21 - Resposta correta no índice 2")
        void correctIndexTwo() {
            Question q = new Question("T", "P", List.of(), List.of("W0", "W1", "CORRETO", "W3"), 2, "E", 20);
            assertEquals(2, q.getCorrectOptionIndex());
            assertEquals("CORRETO", q.getOptions().get(q.getCorrectOptionIndex()));
        }

        @Test
        @DisplayName("2.3.22 - Resposta correta no índice 3")
        void correctIndexThree() {
            Question q = new Question("T", "P", List.of(), List.of("W0", "W1", "W2", "CORRETO"), 3, "E", 20);
            assertEquals(3, q.getCorrectOptionIndex());
            assertEquals("CORRETO", q.getOptions().get(q.getCorrectOptionIndex()));
        }

        @Test
        @DisplayName("2.3.23 - Explicação rica com símbolos técnicos e aspas")
        void richExplanationContent() {
            String expl = "O método 'equals()' compara igualdade semântica enquanto '==' compara referências de memória na JVM.";
            Question q = new Question("OOP", "P", List.of(), List.of("A", "B", "C", "D"), 0, expl, 25);
            assertEquals(expl, q.getExplanation());
        }

        @Test
        @DisplayName("2.3.24 - Snippet contendo comentários de código (// e /* */)")
        void snippetCommentsPreserved() {
            List<String> code = List.of(
                "// Linha de comentário",
                "/* Bloco de comentário */",
                "int a = 10;"
            );
            Question q = new Question("Syntax", "P", code, List.of("A", "B", "C", "D"), 0, "E", 20);
            assertEquals("// Linha de comentário", q.getCodeSnippet().get(0));
            assertEquals("/* Bloco de comentário */", q.getCodeSnippet().get(1));
        }
    }
}
