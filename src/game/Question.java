package game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Question {
    private final String topic;
    private final String prompt;
    private final List<String> codeSnippet;
    private final List<String> options;
    private final int correctOptionIndex;
    private final String explanation;
    private final int damage;
    private final int difficulty;
    private final List<String> tags;
    private final String jlsReference;

    public Question(String topic, String prompt, List<String> codeSnippet,
                    List<String> options, int correctOptionIndex,
                    String explanation, int damage) {
        this(topic, prompt, codeSnippet, options, correctOptionIndex, explanation, damage,
             inferDifficulty(topic), inferTags(topic), inferJlsReference(topic));
    }

    private static int inferDifficulty(String topic) {
        if (topic == null) return 1;
        String t = topic.toLowerCase();
        if (t.contains("primitive") || t.contains("default values") || t.contains("precision")) return 1;
        if (t.contains("string") || t.contains("final") || t.contains("loop") || t.contains("switch") || t.contains("short-circuit") || t.contains("increment")) return 2;
        if (t.contains("array") || t.contains("collection") || t.contains("list")) return 3;
        if (t.contains("dispatch") || t.contains("overload") || t.contains("overrid") || t.contains("encapsulation") || t.contains("super") || t.contains("interface") || t.contains("abstract") || t.contains("oop")) return 4;
        return 5;
    }

    private static List<String> inferTags(String topic) {
        if (topic == null) return List.of("jvm");
        List<String> list = new ArrayList<>();
        list.add(topic);
        String t = topic.toLowerCase();
        if (t.contains("primitive") || t.contains("default") || t.contains("precision")) {
            list.add("sintaxe");
            list.add("primitivos");
            list.add("jvm");
        } else if (t.contains("string") || t.contains("final") || t.contains("immutab")) {
            list.add("strings");
            list.add("pool");
            list.add("memoria");
            list.add("jvm");
        } else if (t.contains("loop") || t.contains("while") || t.contains("for") || t.contains("switch") || t.contains("short-circuit") || t.contains("increment")) {
            list.add("loops");
            list.add("controle");
            list.add("logica");
        } else if (t.contains("array") || t.contains("list") || t.contains("collection")) {
            list.add("arrays");
            list.add("colecoes");
            list.add("memoria");
        } else if (t.contains("dispatch") || t.contains("overload") || t.contains("overrid") || t.contains("encapsulation") || t.contains("super") || t.contains("interface") || t.contains("abstract") || t.contains("oop")) {
            list.add("poo");
            list.add("herança");
            list.add("polimorfismo");
        } else if (t.contains("try") || t.contains("exception") || t.contains("garbage") || t.contains("static") || t.contains("resource") || t.contains("throw")) {
            list.add("excecoes");
            list.add("gc");
            list.add("jvm");
            list.add("memoria");
        } else {
            list.add("streams");
            list.add("lambdas");
            list.add("concorrencia");
            list.add("generics");
            list.add("jvm");
        }
        return Collections.unmodifiableList(list);
    }

    private static String inferJlsReference(String topic) {
        if (topic == null) return "Java SE Specification / API Reference";
        String t = topic.toLowerCase();
        if (t.contains("primitive") || t.contains("division")) {
            return "JLS §4.2 (Primitive Types and Values), §15.17.2 (Division Operator /)";
        }
        if (t.contains("default values")) {
            return "JLS §4.12.5 (Initial Values of Variables)";
        }
        if (t.contains("string") || t.contains("immutab")) {
            return "JLS §4.3.3 (The String Class), java.lang.String API";
        }
        if (t.contains("final")) {
            return "JLS §4.12.4 (final Variables)";
        }
        if (t.contains("loop") || t.contains("while") || t.contains("for") || t.contains("break")) {
            return "JLS §14.12 (while Statement), §14.15 (break Statement)";
        }
        if (t.contains("equality") || t.contains("equals") || t.contains("==")) {
            return "JLS §15.21 (Equality Operators), §4.3.1 (Objects)";
        }
        if (t.contains("array")) {
            return "JLS §10 (Arrays), §10.6 (Array Initializers)";
        }
        if (t.contains("dispatch") || t.contains("overload") || t.contains("overrid") || t.contains("polymorph")) {
            return "JLS §8.4.8.1 (Overriding), §15.12.4.9 (Dynamic Method Dispatch)";
        }
        if (t.contains("try") || t.contains("exception") || t.contains("throw")) {
            return "JLS §14.20 (The try statement), §11 (Exceptions)";
        }
        if (t.contains("garbage") || t.contains("gc") || t.contains("memory") || t.contains("heap")) {
            return "JLS §12.6 (Finalization of Class Instances), §17.4 (Memory Model)";
        }
        if (t.contains("stream") || t.contains("lambda")) {
            return "JLS §15.27 (Lambda Expressions), java.util.stream API";
        }
        if (t.contains("thread") || t.contains("concurren") || t.contains("volatile")) {
            return "JLS §17 (Threads and Locks), §8.3.1.4 (volatile Fields)";
        }
        if (t.contains("generic") || t.contains("wildcard")) {
            return "JLS §4.5 (Parameterized Types), §4.6 (Type Erasure)";
        }
        return "JLS Java SE Standard Specification (Oracle Documentation)";
    }

    public Question(String topic, String prompt, List<String> codeSnippet,
                    List<String> options, int correctOptionIndex,
                    String explanation, int damage, int difficulty, List<String> tags) {
        this(topic, prompt, codeSnippet, options, correctOptionIndex, explanation, damage, difficulty, tags, inferJlsReference(topic));
    }

    public Question(String topic, String prompt, List<String> codeSnippet,
                    List<String> options, int correctOptionIndex,
                    String explanation, int damage, int difficulty, List<String> tags, String jlsReference) {
        this.topic = topic;
        this.prompt = prompt;
        this.codeSnippet = codeSnippet;
        this.options = options;
        this.correctOptionIndex = correctOptionIndex;
        this.explanation = explanation;
        this.damage = damage;
        this.difficulty = Math.max(1, Math.min(5, difficulty));
        this.tags = (tags == null) ? List.of(topic) : List.copyOf(tags);
        this.jlsReference = (jlsReference == null || jlsReference.isBlank()) ? inferJlsReference(topic) : jlsReference;
    }

    public String getTopic() { return topic; }
    public String getPrompt() { return prompt; }
    public List<String> getCodeSnippet() { return codeSnippet; }
    public List<String> getOptions() { return options; }
    public int getCorrectOptionIndex() { return correctOptionIndex; }
    public String getExplanation() { return explanation; }
    public String getJlsReference() { return jlsReference; }

    public String getFullExplanation() {
        if (jlsReference == null || jlsReference.isEmpty()) {
            return explanation;
        }
        return explanation + " [Referência Oficial: " + jlsReference + "]";
    }

    public int getDamage() { return damage; }
    public int getDifficulty() { return difficulty; }
    public List<String> getTags() { return tags; }

    public String getDifficultyStars() {
        return switch (difficulty) {
            case 1 -> "★☆☆☆☆ [Fácil]";
            case 2 -> "★★☆☆☆ [Iniciante]";
            case 3 -> "★★★☆☆ [Intermediário]";
            case 4 -> "★★★★☆ [Avançado]";
            case 5 -> "★★★★★ [Especialista]";
            default -> "★★★☆☆";
        };
    }

    public boolean hasTag(String tag) {
        if (tag == null || tag.trim().isEmpty()) return false;
        String query = tag.trim().toLowerCase();
        for (String t : tags) {
            if (t.toLowerCase().contains(query)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns a new Question instance with shuffled options and the correctOptionIndex
     * updated accordingly so answers are evenly distributed among A, B, C, and D.
     */
    public Question withRandomizedOptions() {
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < options.size(); i++) {
            indices.add(i);
        }
        Collections.shuffle(indices);

        List<String> newOptions = new ArrayList<>(options.size());
        int newCorrectIndex = 0;
        for (int i = 0; i < indices.size(); i++) {
            int originalIdx = indices.get(i);
            newOptions.add(options.get(originalIdx));
            if (originalIdx == correctOptionIndex) {
                newCorrectIndex = i;
            }
        }
        return new Question(topic, prompt, codeSnippet, newOptions, newCorrectIndex, explanation, damage, difficulty, tags, jlsReference);
    }
}
