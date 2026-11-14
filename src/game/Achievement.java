package game;

/**
 * Bytecode Badges: Desbloqueáveis temáticos de maestria no Java Quest.
 */
public enum Achievement {
    FIRST_COMMIT("FIRST_COMMIT", "First Commit", "Derrote o primeiro bug da JVM (NullPointer Imp).", "🌱"),
    CLEAN_CODE("CLEAN_CODE", "Clean Code", "Acerte 10 perguntas consecutivas sem cometer falhas.", "✨"),
    ZERO_ALLOCATION("ZERO_ALLOCATION", "Zero Allocation", "Derrote um chefe sem utilizar nenhum feitiço de cura.", "⚡"),
    CONCURRENCY_WIZARD("CONCURRENCY_WIZARD", "Concurrency Wizard", "Acerte 3 perguntas de nível avançado (dificuldade 4+) em sequência.", "🔮"),
    STACK_TRACE_SURVIVOR("STACK_TRACE_SURVIVOR", "Stack Trace Survivor", "Vença uma batalha crítica com apenas 1 a 10 de HP restante.", "🩸"),
    GARBAGE_COLLECTOR("GARBAGE_COLLECTOR", "Garbage Collector", "Derrote o StackOverflow Dragon e expurgue os bugs da JVM.", "🧹"),
    BUG_HUNTER("BUG_HUNTER", "Bug Hunter", "Utilize a Dica do Compilador 5 vezes em sua jornada.", "🐛"),
    ARCHITECT("ARCHITECT", "Senior Architect", "Complete o jogo no Modo Arquiteto Sênior (Hardcore).", "🏛️"),
    COFFEE_ADDICT("COFFEE_ADDICT", "Coffee Addict", "Consuma café (Coffee Brew) 3 vezes para recarregar sua Mana.", "☕"),
    BREAKPOINT_SAVIOR("BREAKPOINT_SAVIOR", "Breakpoint Savior", "Escape de uma exceção letal graças ao Breakpoint Token.", "🛡️"),
    POLYGLOT_PROGRAMMER("POLYGLOT_PROGRAMMER", "Polyglot Programmer", "Desbloqueie pelo menos 3 talentos na árvore de habilidades.", "🎓"),
    BYTECODE_SCHOLAR("BYTECODE_SCHOLAR", "Bytecode Scholar", "Conclua uma sessão no Bytecode Lab com 100% de precisão (mín. 3 questões).", "📚"),
    CLEAN_ARCHITECTURE("CLEAN_ARCHITECTURE", "Clean Architecture", "Absorva 100% do dano de um golpe inimigo com o Clean Arch Shield.", "🏰"),
    OVERCLOCKED_JIT("OVERCLOCKED_JIT", "Overclocked JIT", "Desfira um ataque massivo de 2.5x dano crítico usando o JIT Booster.", "🚀"),
    HOTSPOT_OPTIMIZER("HOTSPOT_OPTIMIZER", "HotSpot Optimizer", "Acumule 500 ou mais de XP em sua carreira de desenvolvedor.", "🔥");

    private final String id;
    private final String title;
    private final String description;
    private final String icon;

    Achievement(String id, String title, String description, String icon) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.icon = icon;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getIcon() { return icon; }
}
