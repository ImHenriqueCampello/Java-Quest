package game;

public enum Talent {
    GRAALVM_COMPILER(
        "GRAALVM",
        "Compilador GraalVM",
        "Recuperação turbo de Mana (+5 MP por turno de combate).",
        "🚀"
    ),
    TYPE_SAFETY_SHIELD(
        "TYPE_SAFETY",
        "Type Safety Shield",
        "Try-Catch Shield reforçado: reduz 75% do dano em vez de 50%.",
        "🛡️"
    ),
    PATTERN_MATCHING_REFLEX(
        "PATTERN_REFLEX",
        "Pattern Matching Reflex",
        "20% de chance de esquiva completa contra qualquer ataque inimigo.",
        "⚡"
    ),
    STREAM_PARALLELISM(
        "STREAM_PARALLEL",
        "Stream Parallelism",
        "Combo streak concede o dobro de bônus de XP e +5 de dano mágico adicional.",
        "🌊"
    ),
    ZERO_ALLOCATION(
        "ZERO_ALLOC",
        "Zero Allocation",
        "Reduz o custo de Mana de todos os feitiços de depuração em 25%.",
        "💎"
    ),
    HOTSPOT_INLINING(
        "HOTSPOT_INLINE",
        "HotSpot Inlining",
        "Otimização direta em bytecode: +10 de dano base em todas as respostas corretas.",
        "🔥"
    );

    private final String code;
    private final String displayName;
    private final String description;
    private final String icon;

    Talent(String code, String displayName, String description, String icon) {
        this.code = code;
        this.displayName = displayName;
        this.description = description;
        this.icon = icon;
    }

    public String getCode() {
        return code;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public String getIcon() {
        return icon;
    }

    public static Talent fromCode(String code) {
        if (code == null) return null;
        for (Talent t : values()) {
            if (t.name().equalsIgnoreCase(code) || t.code.equalsIgnoreCase(code)) {
                return t;
            }
        }
        return null;
    }
}
