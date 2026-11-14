package game;

import java.io.InputStream;
import java.util.*;

/**
 * Modo Laboratório de Treino (Bytecode Lab / Study Mode)
 * Permite aos desenvolvedores responderem perguntas didáticas livremente,
 * filtrando por assuntos específicos sem risco de Game Over,
 * com feedback detalhado imediato e scorecard estatístico por categoria.
 */
public class BytecodeLab {

    private final Scanner scanner;

    public record LabResult(
        int totalAnswered,
        int totalCorrect,
        double accuracy,
        Map<String, int[]> topicStats
    ) {}

    public BytecodeLab() {
        this(new Scanner(System.in));
    }

    public BytecodeLab(Scanner scanner) {
        this.scanner = scanner;
    }

    public BytecodeLab(InputStream in) {
        this(new Scanner(in));
    }

    public static List<String> getAvailableCategories() {
        return List.of(
            "ALL",
            "Sintaxe & Primitivos",
            "Loops & Controle",
            "Arrays & Coleções",
            "POO & Polimorfismo",
            "Exceções & JVM / GC",
            "Streams & Concorrência"
        );
    }

    public static List<Question> filterQuestions(String category) {
        if (category == null || category.equalsIgnoreCase("ALL") || category.trim().isEmpty() || category.equals("1")) {
            return QuestionBank.getAllQuestions();
        }
        String clean = category.trim().toLowerCase();
        if (clean.equals("2") || clean.contains("sintaxe") || clean.contains("primitiv")) {
            return QuestionBank.getQuestionsByTag("sintaxe");
        } else if (clean.equals("3") || clean.contains("loop") || clean.contains("controle")) {
            return QuestionBank.getQuestionsByTag("loops");
        } else if (clean.equals("4") || clean.contains("array") || clean.contains("coleç") || clean.contains("colec")) {
            return QuestionBank.getQuestionsByTag("colecoes");
        } else if (clean.equals("5") || clean.contains("poo") || clean.contains("polimorf")) {
            return QuestionBank.getQuestionsByTag("poo");
        } else if (clean.equals("6") || clean.contains("exce") || clean.contains("gc") || clean.contains("jvm") || clean.contains("memoria")) {
            return QuestionBank.getQuestionsByTag("excecoes");
        } else if (clean.equals("7") || clean.contains("stream") || clean.contains("concorr") || clean.contains("lambda")) {
            return QuestionBank.getQuestionsByTag("streams");
        }
        List<Question> byTopic = QuestionBank.getQuestionsByTopic(category);
        return byTopic.isEmpty() ? QuestionBank.getAllQuestions() : byTopic;
    }

    public LabResult start() {
        Ansi.clearScreen();
        Ansi.holoBanner("🔬", "BYTECODE LAB: MODO DE TREINO & ESTUDO", "Prática Livre de Java • Sem monstros • Estatísticas em Tempo Real", Ansi.BRIGHT_CYAN);
        System.out.println();

        List<String> categories = getAvailableCategories();
        for (int i = 0; i < categories.size(); i++) {
            String cat = categories.get(i);
            String tag = (i + 1) + " | " + (cat.equals("ALL") ? "TODOS" : cat.toUpperCase());
            Ansi.holoOption(tag, "Treinar banco: " + cat, Ansi.BRIGHT_YELLOW, Ansi.WHITE);
        }
        Ansi.holoOption("0 | Q", "↩️ Retornar ao Menu Principal", Ansi.DIM + Ansi.WHITE, Ansi.DIM + Ansi.WHITE);
        System.out.println();

        String choice = prompt("  Escolha o tópico de treino (1-7 | 0 para voltar): ");
        if (choice == null || choice.trim().equals("0") || choice.trim().equalsIgnoreCase("Q") ||
            choice.trim().equalsIgnoreCase("EXIT") || choice.trim().equalsIgnoreCase("VOLTAR")) {
            return new LabResult(0, 0, 0.0, Collections.emptyMap());
        }

        List<Question> selectedQuestions = new ArrayList<>(filterQuestions(choice));
        if (selectedQuestions.isEmpty()) {
            selectedQuestions = new ArrayList<>(QuestionBank.getAllQuestions());
        }
        Collections.shuffle(selectedQuestions);
        selectedQuestions.replaceAll(Question::withRandomizedOptions);

        return runSession(selectedQuestions);
    }

    public LabResult runSession(List<Question> questions) {
        if (questions == null || questions.isEmpty()) {
            return new LabResult(0, 0, 0.0, Collections.emptyMap());
        }

        int totalAnswered = 0;
        int totalCorrect = 0;
        Map<String, int[]> topicStats = new LinkedHashMap<>();

        char[] labels = {'A', 'B', 'C', 'D'};

        for (int qIdx = 0; qIdx < questions.size(); qIdx++) {
            Question q = questions.get(qIdx);
            String topic = q.getTopic();
            topicStats.putIfAbsent(topic, new int[]{0, 0});

            Ansi.clearScreen();
            Ansi.holoBanner("🧪", "LAB QUESTION #" + (qIdx + 1) + " / " + questions.size(),
                "Tópico: " + topic + " | Dificuldade: " + q.getDifficultyStars(), Ansi.BRIGHT_YELLOW);
            System.out.println();

            Ansi.holoBadgeLine("PERGUNTA", q.getPrompt(), Ansi.BRIGHT_WHITE + Ansi.BOLD, Ansi.WHITE);

            if (q.getCodeSnippet() != null && !q.getCodeSnippet().isEmpty()) {
                System.out.println();
                Ansi.holoLine(Ansi.colorize("    ┌──[ Código-Fonte JVM ]──────────────────────────────────────┐", Ansi.CYAN));
                for (String codeLine : q.getCodeSnippet()) {
                    Ansi.holoLine(Ansi.colorize("    │ ", Ansi.CYAN) + Ansi.colorize(codeLine, Ansi.BRIGHT_GREEN));
                }
                Ansi.holoLine(Ansi.colorize("    └───" + "─".repeat(57) + "┘", Ansi.CYAN));
            }

            System.out.println();
            List<String> options = q.getOptions();
            for (int i = 0; i < options.size(); i++) {
                String optTag = "[" + labels[i] + "] ";
                Ansi.holoOption(optTag, options.get(i), Ansi.BRIGHT_CYAN, Ansi.WHITE);
            }
            Ansi.holoOption("[0 | Q] ", "Finalizar sessão e exibir relatório de estudo", Ansi.DIM + Ansi.WHITE, Ansi.DIM + Ansi.WHITE);
            System.out.println();

            String answerInput = prompt("  Sua Resposta (A-D | 1-4 | 0 para sair): ");
            if (answerInput == null || answerInput.trim().equals("0") || answerInput.trim().equalsIgnoreCase("Q") ||
                answerInput.trim().equalsIgnoreCase("SAIR") || answerInput.trim().equalsIgnoreCase("EXIT")) {
                break;
            }

            int chosenIndex = GameEngine.parseQuizChoice(answerInput);
            boolean isCorrect = (chosenIndex == q.getCorrectOptionIndex());

            totalAnswered++;
            topicStats.get(topic)[1]++;

            System.out.println();
            if (isCorrect) {
                totalCorrect++;
                topicStats.get(topic)[0]++;
                AudioEngine.playCriticalHit();
                Ansi.holoBanner("✅", "RESPOSTA CORRETA! BYTECODE VÁLIDO!", null, Ansi.BRIGHT_GREEN);
            } else {
                AudioEngine.playHurt();
                Ansi.holoBanner("❌", "RESPOSTA INCORRETA! FALHA DE COMPILAÇÃO!", null, Ansi.BRIGHT_RED);
                String correctText = labels[q.getCorrectOptionIndex()] + ": " + options.get(q.getCorrectOptionIndex());
                Ansi.holoBadgeLine("SOLUÇÃO CORRETA", correctText, Ansi.BRIGHT_GREEN, Ansi.BRIGHT_WHITE + Ansi.BOLD);
            }

            Ansi.holoBadgeLine("EXPLICAÇÃO DIDÁTICA", q.getExplanation(), Ansi.BRIGHT_YELLOW, Ansi.WHITE);
            if (q.getJlsReference() != null && !q.getJlsReference().isEmpty()) {
                Ansi.holoBadgeLine("REFERÊNCIA JLS", q.getJlsReference(), Ansi.BRIGHT_CYAN, Ansi.WHITE);
            }

            double currentAccuracy = (totalAnswered > 0) ? ((double) totalCorrect / totalAnswered) * 100.0 : 0.0;
            System.out.println();
            Ansi.holoBadgeLine("PLACAR DA SESSÃO", String.format("%d/%d corretas (%.1f%% de aproveitamento)",
                totalCorrect, totalAnswered, currentAccuracy), Ansi.BRIGHT_CYAN, Ansi.BRIGHT_WHITE + Ansi.BOLD);

            if (qIdx < questions.size() - 1) {
                String nav = prompt("\n  [Pressione Enter para a próxima questão ou '0' para encerrar]: ");
                if (nav != null && (nav.trim().equals("0") || nav.trim().equalsIgnoreCase("Q"))) {
                    break;
                }
            } else {
                pressEnterToContinue("\n  [Todas as questões deste bloco concluídas! Pressione Enter para ver o relatório]");
            }
        }

        double finalAccuracy = (totalAnswered > 0) ? ((double) totalCorrect / totalAnswered) * 100.0 : 0.0;
        LabResult result = new LabResult(totalAnswered, totalCorrect, finalAccuracy, Collections.unmodifiableMap(topicStats));

        if (totalAnswered >= 3 && totalCorrect == totalAnswered) {
            AchievementManager.unlock(Achievement.BYTECODE_SCHOLAR);
        }

        showSummaryReport(result);
        return result;
    }

    private void showSummaryReport(LabResult result) {
        Ansi.clearScreen();
        Ansi.beep();
        Ansi.holoBanner("📊", "RELATÓRIO DO LABORATÓRIO DE ESTUDO", "Desempenho Geral e Estatísticas por Tópico", Ansi.BRIGHT_CYAN);
        System.out.println();

        Ansi.holoBadgeLine("QUESTÕES RESPONDIDAS", String.valueOf(result.totalAnswered()), Ansi.BRIGHT_CYAN, Ansi.WHITE);
        Ansi.holoBadgeLine("ACERTOS", String.valueOf(result.totalCorrect()), Ansi.BRIGHT_GREEN, Ansi.WHITE);
        Ansi.holoBadgeLine("APROVEITAMENTO GERAL", String.format("%.1f%%", result.accuracy()), Ansi.BRIGHT_YELLOW, Ansi.BRIGHT_WHITE + Ansi.BOLD);
        System.out.println();

        if (!result.topicStats().isEmpty()) {
            Ansi.holoLine("  " + Ansi.colorize("──▶ DESEMPENHO DETALHADO POR TÓPICO:", Ansi.BRIGHT_YELLOW + Ansi.BOLD));
            for (Map.Entry<String, int[]> entry : result.topicStats().entrySet()) {
                String topic = entry.getKey();
                int correct = entry.getValue()[0];
                int total = entry.getValue()[1];
                double pct = (total > 0) ? ((double) correct / total) * 100.0 : 0.0;
                String score = String.format("%d/%d (%.0f%%)", correct, total, pct);
                String color = (pct >= 80.0) ? Ansi.BRIGHT_GREEN : (pct >= 50.0 ? Ansi.BRIGHT_YELLOW : Ansi.BRIGHT_RED);
                Ansi.holoBadgeLine(topic, score, color, Ansi.WHITE);
            }
            System.out.println();
        }

        pressEnterToContinue("  [Pressione Enter para retornar ao Menu Principal...]");
    }

    private String prompt(String message) {
        Ansi.showCursor();
        Ansi.type(Ansi.colorize(message, Ansi.BRIGHT_YELLOW + Ansi.BOLD));
        if (scanner.hasNextLine()) {
            return scanner.nextLine();
        }
        return "";
    }

    private void pressEnterToContinue() {
        pressEnterToContinue("  [Pressione Enter para continuar...]");
    }

    private void pressEnterToContinue(String message) {
        if (Ansi.FAST_MODE) return;
        Ansi.showCursor();
        Ansi.type(Ansi.colorize(message, Ansi.DIM + Ansi.WHITE));
        if (scanner.hasNextLine()) {
            scanner.nextLine();
        }
    }
}
