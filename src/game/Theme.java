package game;

/**
 * Visual palettes and themes for terminal presentation.
 */
public enum Theme {
    CYBERPUNK("Cyberpunk Bytecode", "Ciano, Magenta e Amarelo Neon",
        Ansi.BRIGHT_CYAN, Ansi.BRIGHT_MAGENTA, Ansi.BRIGHT_YELLOW, Ansi.WHITE, Ansi.BRIGHT_BLACK, Ansi.BRIGHT_GREEN, Ansi.BRIGHT_RED),

    MATRIX("The Matrix Phosphor", "Verde CRT retro monocromático",
        Ansi.GREEN, Ansi.BRIGHT_GREEN, Ansi.BRIGHT_WHITE, Ansi.BRIGHT_GREEN, Ansi.DIM + Ansi.GREEN, Ansi.BRIGHT_GREEN + Ansi.BOLD, Ansi.BRIGHT_WHITE),

    DRACULA("Dracula / Dark IDE", "Púrpura, Laranja e Cinza suave de IDE moderna",
        Ansi.MAGENTA, Ansi.BRIGHT_MAGENTA, Ansi.YELLOW, Ansi.WHITE, Ansi.BRIGHT_BLACK, Ansi.GREEN, Ansi.RED),

    MONOKAI("Classic Monokai", "Amarelo, Verde e Vermelho clássicos",
        Ansi.YELLOW, Ansi.GREEN, Ansi.BRIGHT_RED, Ansi.BRIGHT_WHITE, Ansi.BRIGHT_BLACK, Ansi.BRIGHT_GREEN, Ansi.BRIGHT_RED);

    private final String displayName;
    private final String description;
    private final String primary;
    private final String secondary;
    private final String accent;
    private final String text;
    private final String muted;
    private final String success;
    private final String danger;

    Theme(String displayName, String description,
          String primary, String secondary, String accent,
          String text, String muted, String success, String danger) {
        this.displayName = displayName;
        this.description = description;
        this.primary = primary;
        this.secondary = secondary;
        this.accent = accent;
        this.text = text;
        this.muted = muted;
        this.success = success;
        this.danger = danger;
    }

    public String getDisplayName() { return displayName; }
    public String getDescription() { return description; }
    public String getPrimary() { return primary; }
    public String getSecondary() { return secondary; }
    public String getAccent() { return accent; }
    public String getText() { return text; }
    public String getMuted() { return muted; }
    public String getSuccess() { return success; }
    public String getDanger() { return danger; }

    public static Theme fromCode(String code) {
        if (code == null || code.trim().isEmpty()) return CYBERPUNK;
        String clean = code.trim().toUpperCase();
        return switch (clean) {
            case "1", "CYBER", "CYBERPUNK" -> CYBERPUNK;
            case "2", "MATRIX", "NEO", "CRT" -> MATRIX;
            case "3", "DRACULA", "DARK", "IDE" -> DRACULA;
            case "4", "MONOKAI", "CLASSIC" -> MONOKAI;
            default -> {
                for (Theme t : values()) {
                    if (t.name().equalsIgnoreCase(clean) || t.displayName.equalsIgnoreCase(code.trim())) {
                        yield t;
                    }
                }
                yield CYBERPUNK;
            }
        };
    }
}
