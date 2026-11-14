package game;

public enum Item {
    COFFEE_BREW(
        "COFFEE",
        "Xícara de Java Forte (Coffee Brew)",
        "Restaura 40 MP instantaneamente sem passar o turno.",
        "☕"
    ),
    GC_SCROLL(
        "GC_SCROLL",
        "Pergaminho de Garbage Collection",
        "Elimina 2 alternativas incorretas e purga debuffs da batalha.",
        "📜"
    ),
    JIT_BOOSTER(
        "JIT_BOOSTER",
        "Otimizador JIT Booster",
        "O próximo acerto de código causa 2.5x de dano crítico!",
        "⚡"
    ),
    BREAKPOINT_TOKEN(
        "BREAKPOINT",
        "Breakpoint Token (Segunda Chance)",
        "Salvaguarda passiva: se o HP chegar a zero, evita derrota e restaura 25% de HP.",
        "🛑"
    ),
    CLEAN_ARCH_SHIELD(
        "CLEAN_SHIELD",
        "Clean Architecture Shield",
        "Anula completamente (100% de absorção) o próximo golpe inimigo.",
        "🛡️"
    );

    private final String code;
    private final String displayName;
    private final String description;
    private final String icon;

    Item(String code, String displayName, String description, String icon) {
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

    public static Item fromCode(String code) {
        if (code == null) return null;
        for (Item item : values()) {
            if (item.name().equalsIgnoreCase(code) || item.code.equalsIgnoreCase(code)) {
                return item;
            }
        }
        return null;
    }
}
