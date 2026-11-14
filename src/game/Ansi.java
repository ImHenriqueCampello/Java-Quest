package game;

public class Ansi {
    public static final String RESET = "\u001B[0m";
    public static final String BOLD = "\u001B[1m";
    public static final String DIM = "\u001B[2m";
    public static final String ITALIC = "\u001B[3m";
    public static final String UNDERLINE = "\u001B[4m";

    // Standard foreground
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String MAGENTA = "\u001B[35m";
    public static final String CYAN = "\u001B[36m";
    public static final String WHITE = "\u001B[37m";

    // Bright foreground
    public static final String BRIGHT_BLACK = "\u001B[90m";
    public static final String BRIGHT_RED = "\u001B[91m";
    public static final String BRIGHT_GREEN = "\u001B[92m";
    public static final String BRIGHT_YELLOW = "\u001B[93m";
    public static final String BRIGHT_BLUE = "\u001B[94m";
    public static final String BRIGHT_MAGENTA = "\u001B[95m";
    public static final String BRIGHT_CYAN = "\u001B[96m";
    public static final String BRIGHT_WHITE = "\u001B[97m";

    // Cursor & Screen controls
    public static final String CURSOR_HOME = "\033[H";
    public static final String HIDE_CURSOR = "\033[?25l";
    public static final String SHOW_CURSOR = "\033[?25h";
    public static final String CLEAR_TO_END_OF_LINE = "\033[K";
    public static final String CLEAR_TO_END_OF_SCREEN = "\033[J";
    public static final String ENTER_ALT_SCREEN = "\033[?1049h";
    public static final String EXIT_ALT_SCREEN = "\033[?1049l";

    // Synchronized output batching (supported in modern terminals: iTerm2, Kitty, Alacritty, GNOME, WezTerm, Windows Terminal)
    public static final String SYNC_START = "\033[?2026h";
    public static final String SYNC_END = "\033[?2026l";

    // Frame rate constants
    public static final int TARGET_FPS = 30;
    public static final int FRAME_MS = 1000 / TARGET_FPS; // ~33ms

    public static volatile boolean FAST_MODE = false;
    private static Boolean compactModeOverride = null;

    private static final java.io.PrintStream ORIGINAL_OUT = System.out;
    private static volatile boolean colorEnabled = !isNoColorEnv();
    private static volatile boolean highContrast = false;
    private static volatile Theme currentTheme = Theme.CYBERPUNK;

    private static final java.io.PrintStream NO_COLOR_PRINT_STREAM = new java.io.PrintStream(new java.io.OutputStream() {
        private final java.io.ByteArrayOutputStream lineBuf = new java.io.ByteArrayOutputStream();

        @Override
        public synchronized void write(int b) {
            lineBuf.write(b);
            if (b == '\n') {
                flushBuffer();
            }
        }

        @Override
        public synchronized void write(byte[] b, int off, int len) {
            for (int i = off; i < off + len; i++) {
                lineBuf.write(b[i]);
                if (b[i] == '\n') {
                    flushBuffer();
                }
            }
        }

        private void flushBuffer() {
            if (lineBuf.size() > 0) {
                String str = lineBuf.toString(java.nio.charset.StandardCharsets.UTF_8);
                ORIGINAL_OUT.print(stripAnsi(str));
                lineBuf.reset();
            }
        }

        @Override
        public synchronized void flush() {
            flushBuffer();
            ORIGINAL_OUT.flush();
        }
    }, true, java.nio.charset.StandardCharsets.UTF_8);

    static {
        if (!colorEnabled) {
            System.setOut(NO_COLOR_PRINT_STREAM);
        }
    }

    private static boolean isNoColorEnv() {
        String env = System.getenv("NO_COLOR");
        return env != null && !env.isEmpty();
    }

    public static boolean isColorEnabled() {
        return colorEnabled;
    }

    public static void setColorEnabled(boolean enabled) {
        colorEnabled = enabled;
        if (!enabled) {
            if (System.out != NO_COLOR_PRINT_STREAM) {
                System.setOut(NO_COLOR_PRINT_STREAM);
            }
        } else {
            if (System.out == NO_COLOR_PRINT_STREAM) {
                System.setOut(ORIGINAL_OUT);
            }
        }
    }

    public static boolean isHighContrast() {
        return highContrast;
    }

    public static void setHighContrast(boolean enabled) {
        highContrast = enabled;
    }

    public static Theme getTheme() {
        return currentTheme;
    }

    public static void setTheme(Theme theme) {
        if (theme != null) {
            currentTheme = theme;
        }
    }

    public static String themePrimary() {
        if (!colorEnabled) return "";
        return highContrast ? BRIGHT_WHITE + BOLD : currentTheme.getPrimary();
    }

    public static String themeSecondary() {
        if (!colorEnabled) return "";
        return highContrast ? BRIGHT_YELLOW + BOLD : currentTheme.getSecondary();
    }

    public static String themeAccent() {
        if (!colorEnabled) return "";
        return highContrast ? BRIGHT_CYAN + BOLD : currentTheme.getAccent();
    }

    public static String themeText() {
        if (!colorEnabled) return "";
        return highContrast ? BRIGHT_WHITE : currentTheme.getText();
    }

    public static String themeMuted() {
        if (!colorEnabled) return "";
        return highContrast ? WHITE : currentTheme.getMuted();
    }

    public static String themeSuccess() {
        if (!colorEnabled) return "";
        return highContrast ? BRIGHT_GREEN + BOLD : currentTheme.getSuccess();
    }

    public static String themeDanger() {
        if (!colorEnabled) return "";
        return highContrast ? BRIGHT_RED + BOLD : currentTheme.getDanger();
    }

    /**
     * Kinetic screen shake effect for critical hits and heavy impacts.
     */
    public static void screenShake(int intensity, int durationMs) {
        if (FAST_MODE || !colorEnabled) return;
        int steps = Math.max(1, intensity);
        int perStep = Math.max(5, durationMs / (steps * 2));
        for (int i = 0; i < steps; i++) {
            System.out.print("\033[1C"); // Move cursor 1 col right
            System.out.flush();
            sleep(perStep);
            System.out.print("\033[1D"); // Move cursor 1 col left
            System.out.flush();
            sleep(perStep);
        }
    }

    /**
     * Detects terminal vertical height in rows/lines.
     */
    public static int getTerminalHeight() {
        String linesEnv = System.getenv("LINES");
        if (linesEnv != null) {
            try {
                int h = Integer.parseInt(linesEnv.trim());
                if (h > 0) return h;
            } catch (NumberFormatException ignored) {}
        }
        try {
            Process p = new ProcessBuilder("tput", "lines").start();
            if (p.waitFor(150, java.util.concurrent.TimeUnit.MILLISECONDS)) {
                String out = new String(p.getInputStream().readAllBytes()).trim();
                int h = Integer.parseInt(out);
                if (h > 0) return h;
            }
        } catch (Exception ignored) {}
        return 24; // Safe default for standard VT100/ANSI terminals
    }

    /**
     * Returns true if compact 24-line terminal mode should be used.
     */
    public static boolean isCompactMode() {
        if (compactModeOverride != null) {
            return compactModeOverride;
        }
        return getTerminalHeight() < 28;
    }

    public static void setCompactMode(boolean compact) {
        compactModeOverride = compact;
    }

    public static void clearCompactModeOverride() {
        compactModeOverride = null;
    }

    public static void initTerminal() {
        if (!colorEnabled) return;
        System.out.print(ENTER_ALT_SCREEN + CURSOR_HOME + CLEAR_TO_END_OF_SCREEN);
        System.out.flush();
    }

    public static void restoreTerminal() {
        if (!colorEnabled) return;
        System.out.print(EXIT_ALT_SCREEN + SHOW_CURSOR);
        System.out.flush();
    }

    public static void clearScreen() {
        if (!colorEnabled) {
            System.out.println("\n" + "=".repeat(78) + "\n");
            return;
        }
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    public static void hideCursor() {
        if (!colorEnabled) return;
        System.out.print(HIDE_CURSOR);
        System.out.flush();
    }

    public static void showCursor() {
        if (!colorEnabled) return;
        System.out.print(SHOW_CURSOR);
        System.out.flush();
    }

    public static void sleep(int ms) {
        if (FAST_MODE) return;
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static void beep() {
        System.out.print("\u0007");
        System.out.flush();
    }

    public static String colorize(String text, String colorCode) {
        if (!colorEnabled || text == null) return text == null ? "" : text;
        if (highContrast) {
            if (colorCode != null && (colorCode.contains("\u001B[2m") || colorCode.contains("\u001B[90m"))) {
                colorCode = WHITE;
            }
        }
        return colorCode + text + RESET;
    }

    public static String stripAnsi(String s) {
        if (s == null) return "";
        return s.replaceAll("\u001B\\[[;?0-9]*[a-zA-Z]|\u001B[78]|\u0007", "");
    }

    public static int getVisibleLength(String s) {
        if (s == null) return 0;
        return stripAnsi(s).length();
    }

    public static String padVisibleRight(String s, int n) {
        if (s == null) s = "";
        int vis = getVisibleLength(s);
        if (vis >= n) return s;
        return s + " ".repeat(n - vis);
    }

    public static String padVisibleLeft(String s, int n) {
        if (s == null) s = "";
        int vis = getVisibleLength(s);
        if (vis >= n) return s;
        return " ".repeat(n - vis) + s;
    }

    public static String center(String s, int n) {
        if (s == null) s = "";
        int vis = getVisibleLength(s);
        if (vis >= n) return s;
        int left = (n - vis) / 2;
        int right = n - vis - left;
        return " ".repeat(left) + s + " ".repeat(right);
    }

    // Unified tick rate across all animations and text (25ms = 40 FPS)
    public static final int UNIFIED_ANIM_SPEED_MS = 25;

    /**
     * Projects a menu or combat option as a whole Holographic HUD Card with a cyber beacon.
     * Materializes the entire option row in a synchronized 25ms pulse (NOT typing!).
     */
    public static void holoOption(String key, String text, String keyColor, String textColor) {
        if (!colorEnabled) {
            System.out.println("  [" + key + "] -> " + text);
            System.out.flush();
            return;
        }
        String beacon = colorize("  ◈ [" + key + "] ──▶ ", keyColor + BOLD);
        String label = colorize(text, textColor);
        System.out.println(beacon + label);
        System.out.flush();
        sleep(UNIFIED_ANIM_SPEED_MS);
    }

    public static void holoOption(String key, String text) {
        holoOption(key, text, BRIGHT_YELLOW, BRIGHT_WHITE + BOLD);
    }

    /**
     * Projects a multiple choice quiz option with holographic status indicator in 25ms.
     */
    public static void holoChoice(char label, String text, boolean isEliminated) {
        if (!colorEnabled) {
            if (isEliminated) {
                System.out.println("  [" + label + "] X " + text + " [DISCARDED]");
            } else {
                System.out.println("  [" + label + "] -> " + text);
            }
            System.out.flush();
            return;
        }
        if (isEliminated) {
            String badge = colorize("  ◈ [" + label + "] ✖ ", DIM + BRIGHT_BLACK);
            String labelText = colorize(text + " [DISCARDED BY DEBUGGER BREAKPOINT]", DIM + RED);
            System.out.println(badge + labelText);
        } else {
            String beacon = colorize("  ◈ [" + label + "] ──▶ ", BRIGHT_YELLOW + BOLD);
            String labelText = colorize(text, BRIGHT_WHITE);
            System.out.println(beacon + labelText);
        }
        System.out.flush();
        sleep(UNIFIED_ANIM_SPEED_MS);
    }

    /**
     * Holographic line projection: materializes an entire line in a synchronized 25ms scanline tick.
     * Zero typing simulation; complete, atomic line presentation.
     */
    public static void holoLine(String line) {
        if (line == null) line = "";
        System.out.println(line);
        System.out.flush();
        sleep(UNIFIED_ANIM_SPEED_MS);
    }

    /**
     * Telemetry badge line: projects a cybernetic tagged status line.
     */
    public static void holoBadgeLine(String badge, String text, String badgeColor, String textColor) {
        if (!colorEnabled) {
            System.out.println("  [" + badge + "] " + text);
            System.out.flush();
            return;
        }
        String b = colorize("  ⚡ [" + badge + "] ──▶ ", badgeColor + BOLD);
        String t = colorize(text, textColor);
        System.out.println(b + t);
        System.out.flush();
        sleep(UNIFIED_ANIM_SPEED_MS);
    }

    /**
     * Holographic cyber status banner.
     */
    public static void holoBanner(String icon, String title, String subtitle, String color) {
        int width = 74;
        if (!colorEnabled) {
            String header = (icon != null && !icon.isEmpty() ? icon + " " : "") + title;
            System.out.println("=".repeat(width));
            System.out.println("  " + header);
            if (subtitle != null && !subtitle.isEmpty()) {
                System.out.println("  " + subtitle);
            }
            System.out.println("=".repeat(width));
            System.out.flush();
            return;
        }
        holoLine(color + BOLD + "  ╔" + "═".repeat(width) + "╗" + RESET);
        String header = (icon != null && !icon.isEmpty() ? icon + " " : "") + title;
        holoLine(color + "  ║ " + RESET + center(colorize(header, color + BOLD), width - 2) + color + " ║" + RESET);
        if (subtitle != null && !subtitle.isEmpty()) {
            holoLine(color + "  ║ " + RESET + center(colorize(subtitle, WHITE), width - 2) + color + " ║" + RESET);
        }
        holoLine(color + BOLD + "  ╚" + "═".repeat(width) + "╝" + RESET);
    }

    /**
     * Holographic panel/card with top badge title, rows, and bottom frame.
     */
    public static void holoCard(String title, java.util.List<String> lines, String borderColor, String titleColor) {
        int width = 74;
        if (!colorEnabled) {
            System.out.println("--- [ " + title + " ] ---");
            for (String line : lines) {
                System.out.println("  " + line);
            }
            System.out.println("-".repeat(width));
            System.out.flush();
            return;
        }
        String topTag = "── ✦ [ " + title + " ] ";
        int tagLen = getVisibleLength(topTag);
        int padLen = Math.max(0, width - tagLen);
        holoLine(borderColor + "  ┌" + topTag + "─".repeat(padLen) + "┐" + RESET);
        for (String line : lines) {
            String padded = padVisibleRight("  " + line, width);
            holoLine(borderColor + "  │" + RESET + padded + borderColor + "│" + RESET);
        }
        holoLine(borderColor + "  └" + "─".repeat(width) + "┘" + RESET);
    }

    /**
     * Holographic multi-line reveal: paints text line-by-line at the unified 25ms speed.
     * Replaces old typewriter/streamWords methods so text NEVER mimics keyboard typing.
     */
    public static void holoReveal(String text) {
        if (text == null) return;
        if (text.contains("\n")) {
            String[] lines = text.split("\n", -1);
            for (int i = 0; i < lines.length; i++) {
                if (i == lines.length - 1 && lines[i].isEmpty()) {
                    continue;
                }
                holoLine(lines[i]);
            }
        } else {
            holoLine(text);
        }
    }

    public static void streamWords(String text, int wordDelayMs) {
        holoReveal(text);
    }

    public static void streamWords(String text) {
        holoReveal(text);
    }

    public static void typeLine(String text, int charDelayMs) {
        holoReveal(text);
    }

    public static void typeLine(String text) {
        holoReveal(text);
    }

    public static void fastTypeLine(String text) {
        holoReveal(text);
    }

    public static void type(String text, int charDelayMs) {
        if (text == null) return;
        System.out.print(text);
        System.out.flush();
    }

    public static void type(String text) {
        if (text == null) return;
        System.out.print(text);
        System.out.flush();
    }
}
