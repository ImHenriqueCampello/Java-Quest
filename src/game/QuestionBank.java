package game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuestionBank {

    public static List<Question> getQuestionsForLevel(int level) {
        if (level < 1 || level > 6) {
            return getQuestionsForLevel(1);
        }

        List<Question> questions = new ArrayList<>();

        switch (level) {
            case 1 -> {
                // LEVEL 1: Java Basics, Variables & Primitives
                questions.add(new Question(
                    "Primitive Types",
                    "What will be the output of this integer division in Java?",
                    List.of(
                        "int a = 7;",
                        "int b = 2;",
                        "System.out.println(a / b);"
                    ),
                    List.of("3.5", "4", "3", "Compilation Error"),
                    2,
                    "In Java, dividing two integers performs integer division, discarding any remainder (7 / 2 = 3).",
                    35
                ));

                questions.add(new Question(
                    "Default Values",
                    "What is the default value of an uninitialized instance boolean variable in Java?",
                    List.of(
                        "class Hero {",
                        "    boolean isAlive; // default?",
                        "}"
                    ),
                    List.of("false", "true", "null", "0"),
                    0,
                    "In Java, boolean instance variables default to 'false'. (Local variables have no default and must be initialized before use).",
                    35
                ));

                questions.add(new Question(
                    "String Immutability",
                    "What does this code print to the console?",
                    List.of(
                        "String word = \"Java\";",
                        "word.concat(\" Rules\");",
                        "System.out.println(word);"
                    ),
                    List.of("\"Java Rules\"", "\"null\"", "Compilation Error", "\"Java\""),
                    3,
                    "Strings in Java are immutable! 'concat()' returns a new String, but 'word' was not reassigned, so it remains \"Java\".",
                    35
                ));

                questions.add(new Question(
                    "Final Keyword",
                    "What is the effect of the 'final' keyword on a variable?",
                    List.of(
                        "final double PI = 3.14159;",
                        "// PI = 3.14; // ?"
                    ),
                    List.of(
                        "The variable can only be assigned once and cannot be changed.",
                        "The variable becomes private to the class.",
                        "The variable is deleted by garbage collection immediately.",
                        "The variable is converted to a String."
                    ),
                    0,
                    "The 'final' keyword prevents reassignment. Once initialized, its value cannot be modified.",
                    35
                ));

                questions.add(new Question(
                    "Character vs String",
                    "Which line correctly assigns a single character literal in Java?",
                    List.of(
                        "// Which syntax is valid?"
                    ),
                    List.of(
                        "char letter = \"J\";",
                        "char letter = 'J';",
                        "char letter = `J`;",
                        "char letter = (char) \"J\";"
                    ),
                    1,
                    "In Java, 'char' literals use single quotes (e.g., 'J'), whereas Strings use double quotes (e.g., \"J\").",
                    35
                ));

                questions.add(new Question(
                    "Integer Precision",
                    "Which Java primitive integer type has 64 bits of storage?",
                    List.of(
                        "byte: 8 bits",
                        "short: 16 bits",
                        "int: 32 bits",
                        "???: 64 bits"
                    ),
                    List.of("int", "float", "long", "double"),
                    2,
                    "'long' is a 64-bit signed two's complement integer in Java.",
                    35
                ));
            }

            case 2 -> {
                // LEVEL 2: Conditionals, Logic & Loops
                questions.add(new Question(
                    "For Loop and Continue",
                    "What will this code print?",
                    List.of(
                        "int sum = 0;",
                        "for (int i = 0; i < 4; i++) {",
                        "    if (i == 2) continue;",
                        "    sum += i;",
                        "}",
                        "System.out.println(sum);"
                    ),
                    List.of("4", "6", "3", "5"),
                    0,
                    "'continue' skips the rest of the loop body for i = 2. So sum = 0 + 1 + 3 = 4.",
                    40
                ));

                questions.add(new Question(
                    "Short-Circuit Operators",
                    "What will be the value of 'x' after this executes?",
                    List.of(
                        "int x = 5;",
                        "if (x > 10 && ++x > 5) {",
                        "    System.out.println(\"Yes\");",
                        "}",
                        "System.out.println(x);"
                    ),
                    List.of("6", "10", "5", "Compilation Error"),
                    2,
                    "The '&&' operator short-circuits! Because 'x > 10' is false, the right-hand operand '++x > 5' is never evaluated.",
                    40
                ));

                questions.add(new Question(
                    "Switch Fall-Through",
                    "What is printed by this switch statement?",
                    List.of(
                        "int val = 2;",
                        "switch (val) {",
                        "    case 1: System.out.print(\"A\");",
                        "    case 2: System.out.print(\"B\");",
                        "    case 3: System.out.print(\"C\"); break;",
                        "    default: System.out.print(\"D\");",
                        "}"
                    ),
                    List.of("B", "BCD", "A B C", "BC"),
                    3,
                    "Without a 'break' statement after 'case 2:', execution falls through into 'case 3:', printing 'BC' before hitting break.",
                    40
                ));

                questions.add(new Question(
                    "Do-While Guarantees",
                    "How many times is the body of a 'do-while' loop guaranteed to execute?",
                    List.of(
                        "do {",
                        "    // body",
                        "} while (condition);"
                    ),
                    List.of(
                        "At least 1 time",
                        "0 times (if condition is false initially)",
                        "At least 2 times",
                        "Until the computer restarts"
                    ),
                    0,
                    "A 'do-while' loop evaluates its condition after executing the body, guaranteeing at least one run.",
                    40
                ));

                questions.add(new Question(
                    "While Loop Termination",
                    "What is printed when this loop finishes?",
                    List.of(
                        "int i = 0;",
                        "while (i < 5) {",
                        "    i += 2;",
                        "}",
                        "System.out.println(i);"
                    ),
                    List.of("4", "6", "5", "Infinite Loop"),
                    1,
                    "i starts at 0 -> 2 -> 4 -> 6. At 6, 'i < 5' is false, the loop exits and prints 6.",
                    40
                ));

                questions.add(new Question(
                    "Post-Increment in Expressions",
                    "What is the output of this calculation?",
                    List.of(
                        "int a = 3;",
                        "int b = a++ * 2;",
                        "System.out.println(b);"
                    ),
                    List.of("8", "6", "7", "Compilation Error"),
                    1,
                    "Post-increment 'a++' uses the current value of 'a' (3) in the expression (3 * 2 = 6), and then increments 'a' to 4 afterwards.",
                    40
                ));
            }

            case 3 -> {
                // LEVEL 3: Arrays, Strings & Collections
                questions.add(new Question(
                    "Array Indexing",
                    "What happens when you run this code?",
                    List.of(
                        "int[] numbers = {10, 20, 30};",
                        "System.out.println(numbers[3]);"
                    ),
                    List.of(
                        "Prints 0",
                        "Prints null",
                        "Prints 30",
                        "ArrayIndexOutOfBoundsException is thrown at runtime"
                    ),
                    3,
                    "Java arrays are 0-indexed. An array of size 3 has valid indices 0, 1, and 2. Index 3 throws ArrayIndexOutOfBoundsException.",
                    40
                ));

                questions.add(new Question(
                    "String Equality",
                    "What will this code print?",
                    List.of(
                        "String a = new String(\"Byte\");",
                        "String b = new String(\"Byte\");",
                        "System.out.println((a == b) + \" \" + a.equals(b));"
                    ),
                    List.of("true true", "false false", "false true", "true false"),
                    2,
                    "'==' checks if both references point to the exact same memory address (false for two 'new' objects), while '.equals()' compares actual contents (true).",
                    40
                ));

                questions.add(new Question(
                    "Array Length Property",
                    "Which syntax is correct to get the size of an array named 'data'?",
                    List.of(
                        "int[] data = new int[5];"
                    ),
                    List.of("data.length()", "data.length", "data.size()", "data.count"),
                    1,
                    "Arrays use the public field '.length'. Strings use '.length()', and Collections (like ArrayList) use '.size()'.",
                    40
                ));

                questions.add(new Question(
                    "ArrayList Operations",
                    "What is printed after these ArrayList operations?",
                    List.of(
                        "List<String> list = new ArrayList<>();",
                        "list.add(\"Alpha\");",
                        "list.add(\"Beta\");",
                        "list.remove(0);",
                        "System.out.println(list.get(0));"
                    ),
                    List.of("Alpha", "null", "Beta", "IndexOutOfBoundsException"),
                    2,
                    "Removing index 0 removes \"Alpha\", shifting \"Beta\" into index 0.",
                    40
                ));

                questions.add(new Question(
                    "Default Array Values",
                    "What is printed when accessing a newly allocated int array element?",
                    List.of(
                        "int[] arr = new int[10];",
                        "System.out.println(arr[5]);"
                    ),
                    List.of("null", "0", "Garbage memory value", "Compilation Error"),
                    1,
                    "In Java, newly created arrays of primitives are automatically zero-initialized ('0' for int, 'false' for boolean).",
                    40
                ));

                questions.add(new Question(
                    "String Substring Bounds",
                    "What does substring(0, 5) return for the String \"JavaWarrior\"?",
                    List.of(
                        "String hero = \"JavaWarrior\";",
                        "System.out.println(hero.substring(0, 5));"
                    ),
                    List.of("\"Java\"", "\"JavaWa\"", "\"Warrior\"", "\"JavaW\""),
                    3,
                    "In Java, substring(beginIndex, endIndex) is inclusive of beginIndex (0) and exclusive of endIndex (5), yielding indices 0,1,2,3,4: \"JavaW\".",
                    40
                ));
            }

            case 4 -> {
                // LEVEL 4: OOP, Classes, Inheritance & Polymorphism
                questions.add(new Question(
                    "Dynamic Method Dispatch",
                    "What is printed when this polymorphism code is executed?",
                    List.of(
                        "class Weapon { void attack() { System.out.print(\"Slash \"); } }",
                        "class MagicStaff extends Weapon { void attack() { System.out.print(\"Zap \"); } }",
                        "Weapon w = new MagicStaff();",
                        "w.attack();"
                    ),
                    List.of("Zap ", "Slash ", "Slash Zap ", "Compilation Error"),
                    0,
                    "Java uses dynamic method dispatch. Because the actual object at runtime is a MagicStaff, its overridden attack() is called.",
                    45
                ));

                questions.add(new Question(
                    "Overloading vs Overriding",
                    "Which statement correctly describes Method Overloading?",
                    List.of(
                        "void castSpell(int power) { ... }",
                        "void castSpell(int power, String element) { ... }"
                    ),
                    List.of(
                        "Replacing a parent class method with the same signature.",
                        "Hiding a private field with a getter method.",
                        "Defining methods with the same name but different parameter lists in the same class.",
                        "Using the 'super' keyword to call a constructor."
                    ),
                    2,
                    "Overloading happens within the same class when methods share the same name but differ in their parameters (types, number, or order).",
                    45
                ));

                questions.add(new Question(
                    "Encapsulation Modifiers",
                    "Which access modifier restricts access strictly to the class where the member is declared?",
                    List.of(
                        "class Vault {",
                        "    ??? String secretKey;",
                        "}"
                    ),
                    List.of("public", "protected", "default (package-private)", "private"),
                    3,
                    "'private' is the most restrictive access modifier in Java, hiding the member from all other classes.",
                    45
                ));

                questions.add(new Question(
                    "Super Constructor Call",
                    "What happens if a subclass constructor does not explicitly call super()?",
                    List.of(
                        "class Hero extends Player {",
                        "    Hero() {",
                        "        System.out.println(\"Hero born\");",
                        "    }",
                        "}"
                    ),
                    List.of(
                        "The compiler automatically inserts a call to super() with no arguments as the first statement.",
                        "The program will not compile.",
                        "The parent class is never instantiated.",
                        "A NullPointerException is thrown at runtime."
                    ),
                    0,
                    "If a constructor doesn't explicitly call super(...) or this(...), the compiler inserts a no-argument 'super()' call as its first line.",
                    45
                ));

                questions.add(new Question(
                    "Interface Capabilities",
                    "Can an interface in modern Java (Java 8+) have a method implementation?",
                    List.of(
                        "interface Spellcaster {",
                        "    default void chant() { System.out.println(\"Lumos\"); }",
                        "}"
                    ),
                    List.of(
                        "No, interfaces can only have abstract methods.",
                        "Only if the interface extends an abstract class.",
                        "Yes, using 'default' or 'static' keywords.",
                        "Only in private inner classes."
                    ),
                    2,
                    "Starting with Java 8, interfaces can provide concrete implementations via 'default' and 'static' methods.",
                    45
                ));

                questions.add(new Question(
                    "Abstract Class Constructors",
                    "Can an abstract class define constructors in Java?",
                    List.of(
                        "abstract class Monster {",
                        "    Monster(String name) { ... }",
                        "}"
                    ),
                    List.of(
                        "No, because abstract classes cannot be directly instantiated.",
                        "Yes, and they are invoked by subclass constructors via super().",
                        "Only if all methods in the class are static.",
                        "Only if the constructor is private."
                    ),
                    1,
                    "Abstract classes CAN have constructors! They cannot be instantiated directly with 'new Monster()', but subclasses call them via super().",
                    45
                ));
            }

            case 5 -> {
                // LEVEL 5: Exceptions, Memory & Garbage Collection
                questions.add(new Question(
                    "Try-Catch-Finally Order",
                    "What is printed when this block executes?",
                    List.of(
                        "try {",
                        "    int x = 10 / 0;",
                        "} catch (ArithmeticException e) {",
                        "    System.out.print(\"Caught \");",
                        "} finally {",
                        "    System.out.print(\"Finally \");",
                        "}"
                    ),
                    List.of("Caught ", "Caught Finally ", "Finally ", "Compilation Error"),
                    1,
                    "The catch block handles the division by zero, printing 'Caught ', and the 'finally' block is guaranteed to run, printing 'Finally '.",
                    45
                ));

                questions.add(new Question(
                    "Checked vs Unchecked Exceptions",
                    "Which of the following is an Unchecked (Runtime) Exception in Java?",
                    List.of(
                        "// Which exception does NOT need to be explicitly declared with 'throws'?"
                    ),
                    List.of("IOException", "SQLException", "ClassNotFoundException", "NullPointerException"),
                    3,
                    "NullPointerException extends RuntimeException and is an unchecked exception, so the compiler does not force you to declare or catch it.",
                    45
                ));

                questions.add(new Question(
                    "Garbage Collection Eligibility",
                    "When does an object on the Java Heap become eligible for Garbage Collection?",
                    List.of(
                        "Object obj = new Object();",
                        "obj = null;"
                    ),
                    List.of(
                        "When there are no reachable references pointing to it from any live thread.",
                        "When System.gc() is called explicitly.",
                        "Exactly 10 seconds after it is created.",
                        "Only when the JVM shuts down."
                    ),
                    0,
                    "An object is eligible for garbage collection as soon as it is no longer reachable through any reference chains from live GC roots.",
                    45
                ));

                questions.add(new Question(
                    "Static Variables",
                    "What will this code print?",
                    List.of(
                        "class Bug {",
                        "    static int count = 0;",
                        "    Bug() { count++; }",
                        "}",
                        "new Bug();",
                        "new Bug();",
                        "System.out.println(Bug.count);"
                    ),
                    List.of("0", "2", "1", "Compilation Error"),
                    1,
                    "Static variables belong to the class itself, not individual instances. Each constructor call increments the shared 'count', resulting in 2.",
                    45
                ));

                questions.add(new Question(
                    "Try-With-Resources",
                    "What interface must a resource implement to be managed automatically in a try-with-resources statement?",
                    List.of(
                        "try (Scanner sc = new Scanner(System.in)) {",
                        "    // auto-closed here!",
                        "}"
                    ),
                    List.of("Serializable", "Cloneable", "AutoCloseable", "Runnable"),
                    2,
                    "Any class that implements java.lang.AutoCloseable (or java.io.Closeable) can be used with try-with-resources.",
                    45
                ));

                questions.add(new Question(
                    "Throw vs Throws",
                    "Which keyword is used in a method declaration to declare exceptions that may occur?",
                    List.of(
                        "public void readData() ??? IOException {",
                        "    throw new IOException();",
                        "}"
                    ),
                    List.of("throw", "catching", "raising", "throws"),
                    3,
                    "'throws' is placed in the method signature to declare exceptions, whereas 'throw' is used inside the method body to actually throw an exception object.",
                    45
                ));
            }

            case 6 -> {
                // LEVEL 6: Master Architect - Recursion, Streams & Generics (Final Boss)
                questions.add(new Question(
                    "Recursion Base Case",
                    "What is the return value of mystery(4)?",
                    List.of(
                        "int mystery(int n) {",
                        "    if (n <= 1) return 1;",
                        "    return n + mystery(n - 1);",
                        "}"
                    ),
                    List.of("10", "7", "24", "StackOverflowError"),
                    0,
                    "mystery(4) = 4 + mystery(3) = 4 + 3 + mystery(2) = 4 + 3 + 2 + mystery(1) = 4 + 3 + 2 + 1 = 10.",
                    50
                ));

                questions.add(new Question(
                    "Java Streams API",
                    "What will be the output of this Java Stream pipeline?",
                    List.of(
                        "List<Integer> numbers = List.of(1, 2, 3, 4, 5);",
                        "int result = numbers.stream()",
                        "    .filter(n -> n % 2 == 0)",
                        "    .mapToInt(n -> n * 3)",
                        "    .sum();",
                        "System.out.println(result);"
                    ),
                    List.of("12", "18", "30", "15"),
                    1,
                    "Even numbers are 2 and 4. Multiplying each by 3 gives 6 and 12. Summing them yields 6 + 12 = 18.",
                    50
                ));

                questions.add(new Question(
                    "Generics Type Erasure",
                    "What happens to generic type parameters like List<String> during compilation?",
                    List.of(
                        "List<String> words = new ArrayList<>();"
                    ),
                    List.of(
                        "They are preserved as separate bytecode classes like ArrayList_String.class.",
                        "They cause a compilation error unless reflection is enabled.",
                        "Type Erasure removes generic type info at compile time, replacing it with raw types / Object and casts.",
                        "They are converted to C++ templates."
                    ),
                    2,
                    "Java implements generics using Type Erasure to maintain backward compatibility with older bytecode versions.",
                    50
                ));

                questions.add(new Question(
                    "Optional Monad",
                    "What does this code print?",
                    List.of(
                        "String name = null;",
                        "String result = Optional.ofNullable(name)",
                        "                        .orElse(\"Default Warrior\");",
                        "System.out.println(result);"
                    ),
                    List.of("null", "NullPointerException", "Empty Optional", "\"Default Warrior\""),
                    3,
                    "'Optional.ofNullable(null)' wraps a null value safely, and '.orElse(\"Default Warrior\")' returns the fallback value.",
                    50
                ));

                questions.add(new Question(
                    "Functional Interface",
                    "What defines a Functional Interface in Java?",
                    List.of(
                        "@FunctionalInterface",
                        "interface Spell {",
                        "    void cast();",
                        "}"
                    ),
                    List.of(
                        "An interface with exactly one abstract method.",
                        "An interface with zero methods.",
                        "An interface with only static methods.",
                        "Any interface that can be instantiated with 'new'."
                    ),
                    0,
                    "A functional interface has exactly one abstract method and can be implemented using lambda expressions or method references.",
                    50
                ));

                questions.add(new Question(
                    "Thread-Safe Map",
                    "Which Map implementation provides concurrent thread-safety without locking the entire map?",
                    List.of(
                        "Map<String, Integer> cache = new ???<>();"
                    ),
                    List.of("HashMap", "TreeMap", "Hashtable", "ConcurrentHashMap"),
                    3,
                    "ConcurrentHashMap provides high-performance concurrent reads and segment/bucket-level synchronized writes.",
                    50
                ));
            }
        }

        int defaultDiff = switch (level) {
            case 1 -> 1;
            case 2 -> 2;
            case 3 -> 3;
            case 4 -> 4;
            case 5 -> 4;
            case 6 -> 5;
            default -> 1;
        };

        // Randomize the order of options for each question so answers are dynamically
        // distributed across A, B, C, and D on every game session and replay!
        List<Question> randomizedQuestions = new ArrayList<>(questions.size());
        for (Question q : questions) {
            int diff = (q.getDifficulty() == 1 && defaultDiff > 1) ? defaultDiff : q.getDifficulty();
            Question enriched = new Question(
                q.getTopic(), q.getPrompt(), q.getCodeSnippet(), q.getOptions(),
                q.getCorrectOptionIndex(), q.getExplanation(), q.getDamage(),
                diff, q.getTags()
            );
            randomizedQuestions.add(enriched.withRandomizedOptions());
        }

        // Shuffle questions so every game run is fresh!
        Collections.shuffle(randomizedQuestions);
        return randomizedQuestions;
    }

    /**
     * Alias for getQuestionsForLevel, returning a newly randomized question batch with shuffled options.
     */
    public static List<Question> getRandomizedQuestionsForLevel(int level) {
        return getQuestionsForLevel(level);
    }

    public static List<Question> getAllQuestions() {
        List<Question> all = new ArrayList<>();
        for (int lvl = 1; lvl <= 6; lvl++) {
            all.addAll(getQuestionsForLevel(lvl));
        }
        return all;
    }

    public static List<Question> getQuestionsByTopic(String topic) {
        if (topic == null || topic.trim().isEmpty()) return List.of();
        String qTopic = topic.trim().toLowerCase();
        return getAllQuestions().stream()
            .filter(q -> q.getTopic().toLowerCase().contains(qTopic))
            .toList();
    }

    public static List<Question> getQuestionsByDifficulty(int difficulty) {
        return getAllQuestions().stream()
            .filter(q -> q.getDifficulty() == difficulty)
            .toList();
    }

    public static List<Question> getQuestionsByTag(String tag) {
        if (tag == null || tag.trim().isEmpty()) return List.of();
        return getAllQuestions().stream()
            .filter(q -> q.hasTag(tag))
            .toList();
    }
}
