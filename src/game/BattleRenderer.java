package game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BattleRenderer {

    // 74 inner characters + 4 border characters = 78 columns total (fits safely in 80-col terminals)
    private static final int ARENA_INNER_WIDTH = 74;

    // High framerate animation constants (30 FPS, ~33ms per frame)
    public static final int TARGET_FPS = 30;
    public static final long FRAME_TIME_MS = 1000 / TARGET_FPS; // ~33ms per frame

    /**
     * Sleep helper with delta-timing to maintain steady 30 FPS.
     */
    private static void sleepFrame(long frameStartTime) {
        long elapsed = System.currentTimeMillis() - frameStartTime;
        long sleepTime = FRAME_TIME_MS - elapsed;
        if (sleepTime > 0) {
            Ansi.sleep((int) sleepTime);
        }
    }

    /**
     * 26-column row buffer ensuring exact column counts and safe coordinate clipping.
     */
    public static class CellRow {
        private final char[] ch = new char[26];
        private final String[] color = new String[26];

        public CellRow() {
            Arrays.fill(ch, ' ');
        }

        public void put(int col, String text, String c) {
            if (text == null) return;
            for (int i = 0; i < text.length(); i++) {
                int p = col + i;
                if (p >= 0 && p < 26) {
                    ch[p] = text.charAt(i);
                    color[p] = c;
                }
            }
        }

        public String render() {
            StringBuilder sb = new StringBuilder(64);
            String last = null;
            for (int i = 0; i < 26; i++) {
                String cur = color[i];
                if (cur != null && !cur.equals(last)) {
                    sb.append(cur);
                    last = cur;
                } else if (cur == null && last != null) {
                    sb.append(Ansi.RESET);
                    last = null;
                }
                sb.append(ch[i]);
            }
            if (last != null) sb.append(Ansi.RESET);
            return sb.toString();
        }
    }

    /**
     * 8-row x 26-column dynamic graphics canvas for mid-arena animations.
     */
    public static class MidCanvas {
        private final CellRow[] rows = new CellRow[CharacterSprite.SPRITE_HEIGHT];

        public MidCanvas() {
            for (int i = 0; i < rows.length; i++) {
                rows[i] = new CellRow();
            }
        }

        public void put(int row, int col, String text, String color) {
            if (row >= 0 && row < rows.length) {
                rows[row].put(col, text, color);
            }
        }

        public List<String> render() {
            List<String> list = new ArrayList<>(rows.length);
            for (CellRow r : rows) {
                list.add(r.render());
            }
            return list;
        }
    }

    public static String getHpBar(int current, int max, int length, String defaultColor) {
        if (max <= 0) max = 1;
        current = Math.max(0, Math.min(current, max));
        int filled = (int) Math.round(((double) current / max) * length);
        int empty = length - filled;

        // Dynamic health color feedback
        double ratio = (double) current / max;
        String barColor;
        if (ratio > 0.5) {
            barColor = defaultColor;
        } else if (ratio > 0.25) {
            barColor = Ansi.BRIGHT_YELLOW;
        } else {
            barColor = Ansi.BRIGHT_RED + Ansi.BOLD;
        }

        return barColor + "█".repeat(filled) + Ansi.BRIGHT_BLACK + "░".repeat(empty) + Ansi.RESET;
    }

    public static String getManaBar(int current, int max, int length) {
        if (max <= 0) max = 1;
        current = Math.max(0, Math.min(current, max));
        int filled = (int) Math.round(((double) current / max) * length);
        int empty = length - filled;
        return Ansi.BRIGHT_CYAN + "█".repeat(filled) + Ansi.BRIGHT_BLACK + "░".repeat(empty) + Ansi.RESET;
    }

    /**
     * Overload supporting dynamic HP tweening for smooth health bar animations at 30 FPS.
     */
    public static void renderFrame(Player player, Enemy enemy,
                                   int playerHp, int enemyHp,
                                   List<String> playerSprite,
                                   List<String> enemySprite,
                                   List<String> midGapLines,
                                   String statusMessage,
                                   boolean clearBelow,
                                   boolean inPlace) {
        StringBuilder sb = new StringBuilder(2048);

        if (inPlace) {
            sb.append("\0337\033[s"); // Save cursor position (DEC + SCO)
        }

        // Terminal Synchronized Update begin (eliminates screen tearing on modern terminals)
        sb.append(Ansi.SYNC_START);

        // Reposition cursor to row 1, col 1 without clearing the screen or filling scrollback
        sb.append(Ansi.CURSOR_HOME);

        // Top border
        sb.append(Ansi.BRIGHT_CYAN).append("╔").append("═".repeat(ARENA_INNER_WIDTH + 2)).append("╗").append(Ansi.RESET)
          .append(Ansi.CLEAR_TO_END_OF_LINE).append("\n");
        
        // Header line 1: Level arena name & Enemy name
        String locName = "LEVEL " + enemy.getLevel() + ": " + getArenaName(enemy.getLevel());
        String enemyTag = "TARGET: " + enemy.getName();
        appendTwoColumnRow(sb,
            Ansi.colorize(locName, Ansi.BRIGHT_YELLOW + Ansi.BOLD),
            Ansi.colorize(enemyTag, enemy.getColor() + Ansi.BOLD)
        );

        // Header line 2: Player title & Enemy subtitle
        appendTwoColumnRow(sb,
            Ansi.colorize(player.getName() + " (Lv " + player.getLevel() + ")", Ansi.BOLD + Ansi.BRIGHT_WHITE),
            Ansi.colorize(enemy.getTitle(), Ansi.DIM + Ansi.WHITE)
        );

        // Header line 3: HP bars using tweened HP values!
        int safePlayerHp = Math.max(0, Math.min(playerHp, player.getMaxHp()));
        int safeEnemyHp = Math.max(0, Math.min(enemyHp, enemy.getMaxHp()));
        String playerHpText = String.format("HP: [%s] %3d/%-3d",
            getHpBar(safePlayerHp, player.getMaxHp(), 10, Ansi.BRIGHT_GREEN),
            safePlayerHp, player.getMaxHp());
        String enemyHpText = String.format("HP: [%s] %3d/%-3d",
            getHpBar(safeEnemyHp, enemy.getMaxHp(), 10, Ansi.BRIGHT_RED),
            safeEnemyHp, enemy.getMaxHp());
        appendTwoColumnRow(sb, playerHpText, enemyHpText);

        // Header line 4: Mana & Buffs
        String streakStr = player.getComboStreak() > 1 
            ? Ansi.colorize(" [🔥 STREAK x" + player.getComboStreak() + "]", Ansi.BRIGHT_YELLOW + Ansi.BOLD) 
            : "";
        String shieldStr = player.isShieldActive() 
            ? Ansi.colorize(" [🛡️ SHIELD]", Ansi.BRIGHT_CYAN + Ansi.BOLD) 
            : "";
        if (player.isCleanArchShieldActive()) {
            shieldStr += Ansi.colorize(" [🛡️ CLEAN ARCH 100%]", Ansi.BRIGHT_GREEN + Ansi.BOLD);
        }
        if (player.isJitBoosterActive()) {
            shieldStr += Ansi.colorize(" [⚡ JIT 2.5x]", Ansi.BRIGHT_YELLOW + Ansi.BOLD);
        }
        String buffs = (streakStr + shieldStr).trim();
        if ((double) safePlayerHp / player.getMaxHp() <= 0.30 && safePlayerHp > 0) {
            String critWarning = Ansi.colorize(" [⚠️ CRITICAL HP!]", Ansi.BRIGHT_RED + Ansi.BOLD);
            buffs = (buffs.isEmpty()) ? critWarning.trim() : buffs + critWarning;
        }

        String manaText = String.format("MP: [%s] %2d/%-2d  %s",
            getManaBar(player.getMana(), player.getMaxMana(), 8),
            player.getMana(), player.getMaxMana(),
            buffs);
        appendFullRow(sb, manaText);

        sb.append(Ansi.BRIGHT_CYAN).append("╠").append("═".repeat(ARENA_INNER_WIDTH + 2)).append("╣").append(Ansi.RESET)
          .append(Ansi.CLEAR_TO_END_OF_LINE).append("\n");

        // 8-line battlefield: Player(24) + Gap(26) + Enemy(24) = 74 characters
        for (int i = 0; i < CharacterSprite.SPRITE_HEIGHT; i++) {
            String pLine = (i < playerSprite.size()) ? playerSprite.get(i) : " ".repeat(CharacterSprite.SPRITE_WIDTH);
            String mLine = (midGapLines != null && i < midGapLines.size()) ? midGapLines.get(i) : " ".repeat(26);
            String eLine = (i < enemySprite.size()) ? enemySprite.get(i) : " ".repeat(CharacterSprite.SPRITE_WIDTH);

            // Player sprite line formatting
            String pPadded;
            if (pLine.contains("\u001B[")) {
                pPadded = Ansi.padVisibleRight(pLine, 24) + Ansi.RESET;
            } else {
                pPadded = Ansi.colorize(Ansi.padVisibleRight(pLine, 24), Ansi.BRIGHT_GREEN);
            }

            // Mid arena gap (effects & projectiles)
            String mPadded = Ansi.padVisibleRight(mLine, 26);

            // Enemy sprite line formatting
            String ePadded;
            if (eLine.contains("\u001B[")) {
                ePadded = Ansi.padVisibleRight(eLine, 24) + Ansi.RESET;
            } else {
                ePadded = Ansi.colorize(Ansi.padVisibleRight(eLine, 24), enemy.getColor());
            }

            appendRowRaw(sb, pPadded + mPadded + ePadded);
        }

        sb.append(Ansi.BRIGHT_CYAN).append("╠").append("═".repeat(ARENA_INNER_WIDTH + 2)).append("╣").append(Ansi.RESET)
          .append(Ansi.CLEAR_TO_END_OF_LINE).append("\n");

        // Status log box
        String log = (statusMessage != null && !statusMessage.isEmpty()) 
            ? statusMessage 
            : "Prepare your next bytecode instruction, Programmer!";
        appendFullRow(sb, "▶ " + log);

        sb.append(Ansi.BRIGHT_CYAN).append("╚").append("═".repeat(ARENA_INNER_WIDTH + 2)).append("╝").append(Ansi.RESET)
          .append(Ansi.CLEAR_TO_END_OF_LINE).append("\n");

        if (clearBelow) {
            // Erase any leftover lines below the arena (e.g. from previous menus or user inputs)
            sb.append(Ansi.CLEAR_TO_END_OF_SCREEN);
        }

        // Terminal Synchronized Update end
        sb.append(Ansi.SYNC_END);

        if (inPlace) {
            sb.append("\033[u\0338"); // Restore cursor position (SCO + DEC)
        }

        // Render atomically in one pass to eliminate flicker and terminal scroll
        System.out.print(sb.toString());
        System.out.flush();
    }

    public static void renderFrame(Player player, Enemy enemy,
                                   int playerHp, int enemyHp,
                                   List<String> playerSprite,
                                   List<String> enemySprite,
                                   List<String> midGapLines,
                                   String statusMessage) {
        renderFrame(player, enemy, playerHp, enemyHp, playerSprite, enemySprite, midGapLines, statusMessage, true, false);
    }

    public static void renderFrame(Player player, Enemy enemy,
                                   List<String> playerSprite,
                                   List<String> enemySprite,
                                   List<String> midGapLines,
                                   String statusMessage) {
        renderFrame(player, enemy, player.getHp(), enemy.getCurrentHp(), playerSprite, enemySprite, midGapLines, statusMessage, true, false);
    }

    public static void renderFrame(Player player, Enemy enemy,
                                   List<String> playerSprite,
                                   List<String> enemySprite,
                                   List<String> midGapLines,
                                   String statusMessage,
                                   boolean clearBelow,
                                   boolean inPlace) {
        renderFrame(player, enemy, player.getHp(), enemy.getCurrentHp(), playerSprite, enemySprite, midGapLines, statusMessage, clearBelow, inPlace);
    }

    private static void appendTwoColumnRow(StringBuilder sb, String left, String right) {
        int half = ARENA_INNER_WIDTH / 2; // 37
        String leftPadded = Ansi.padVisibleRight(left, half);
        String rightPadded = Ansi.padVisibleLeft(right, ARENA_INNER_WIDTH - half); // 37
        appendRowRaw(sb, leftPadded + rightPadded);
    }

    private static void appendFullRow(StringBuilder sb, String text) {
        String padded = Ansi.padVisibleRight(text, ARENA_INNER_WIDTH);
        appendRowRaw(sb, padded);
    }

    private static void appendRowRaw(StringBuilder sb, String content) {
        sb.append(Ansi.BRIGHT_CYAN).append("║ ").append(Ansi.RESET)
          .append(content)
          .append(Ansi.BRIGHT_CYAN).append(" ║").append(Ansi.RESET)
          .append(Ansi.CLEAR_TO_END_OF_LINE).append("\n");
    }

    public static String getArenaName(int level) {
        return switch (level) {
            case 1 -> "Syntax Swamp";
            case 2 -> "Recursion Ravine";
            case 3 -> "Array Archipelago";
            case 4 -> "OOP Citadel";
            case 5 -> "The Heap Tomb";
            case 6 -> "THE MONOLITH CORE";
            default -> "Virtual Machine";
        };
    }

    public static List<String> shiftSpriteRight(List<String> sprite, int spaces) {
        if (sprite == null || spaces <= 0) return sprite;
        List<String> shifted = new ArrayList<>(sprite.size());
        String pad = " ".repeat(spaces);
        for (String line : sprite) {
            String s = pad + line;
            if (s.length() > CharacterSprite.SPRITE_WIDTH) {
                s = s.substring(0, CharacterSprite.SPRITE_WIDTH);
            }
            shifted.add(s);
        }
        return shifted;
    }

    public static List<String> shiftSpriteLeft(List<String> sprite, int spaces) {
        if (sprite == null || spaces <= 0) return sprite;
        List<String> shifted = new ArrayList<>(sprite.size());
        for (String line : sprite) {
            String s = (line.length() >= spaces) ? line.substring(spaces) + " ".repeat(spaces) : line;
            shifted.add(s);
        }
        return shifted;
    }

    /**
     * Hit stun flash effect: renders sprite highlighted in bright white with a red background (\033[97;41m).
     */
    public static List<String> flashSprite(List<String> sprite) {
        if (sprite == null) return List.of();
        List<String> flashed = new ArrayList<>(sprite.size());
        for (String line : sprite) {
            String plain = Ansi.stripAnsi(line);
            flashed.add("\033[97;41m" + plain + Ansi.RESET);
        }
        return flashed;
    }

    private static List<String> createEmptyMid() {
        List<String> mid = new ArrayList<>(CharacterSprite.SPRITE_HEIGHT);
        for (int i = 0; i < CharacterSprite.SPRITE_HEIGHT; i++) {
            mid.add(" ".repeat(26));
        }
        return mid;
    }

    // =========================================================================
    // --- CONTINUOUS COMBAT IDLE ANIMATION CONTROLLER ---
    // =========================================================================

    /**
     * Active combat idle session that keeps both combatants subtly animated in a movement loop
     * while the user is reading questions or choosing commands.
     */
    public static class IdleSession implements AutoCloseable {
        private final Thread thread;
        private volatile boolean running = true;
        private volatile String statusMessage;

        public IdleSession(Player player, Enemy enemy, String initialStatus) {
            this.statusMessage = initialStatus;
            this.thread = new Thread(() -> {
                int frame = 0;
                while (running) {
                    try {
                        Thread.sleep(220); // ~4.5 FPS combat bobbing loop
                    } catch (InterruptedException e) {
                        break;
                    }
                    if (!running) break;
                    frame++;
                    renderCombatIdleFrame(player, enemy, frame, this.statusMessage);
                }
            }, "BattleIdleAnimationThread");
            this.thread.setDaemon(true);
            this.thread.start();
        }

        public void setStatusMessage(String message) {
            this.statusMessage = message;
        }

        public Thread getThread() {
            return thread;
        }

        @Override
        public void close() {
            running = false;
            thread.interrupt();
            try {
                thread.join(250);
            } catch (InterruptedException ignored) {}
        }
    }

    public static IdleSession startIdleAnimation(Player player, Enemy enemy, String statusMessage) {
        return new IdleSession(player, enemy, statusMessage);
    }

    public static void renderCombatIdleFrame(Player player, Enemy enemy, int frame, String statusMessage) {
        // Subtle combat stance movement cycle (8 steps)
        int step = frame % 8;

        List<String> pBase;
        int pShift = 0;
        switch (step) {
            case 0 -> {
                pBase = CharacterSprite.PLAYER_IDLE_1;
                pShift = 0;
            }
            case 1 -> {
                pBase = CharacterSprite.PLAYER_IDLE_2; // blinking, staff sparkles
                pShift = 1; // leaning/stepping 1 col forward towards enemy
            }
            case 2 -> {
                pBase = CharacterSprite.PLAYER_CHARGE; // raising staff, mana glyphs
                pShift = 0;
            }
            case 3 -> {
                pBase = CharacterSprite.PLAYER_IDLE_2;
                pShift = 1;
            }
            case 4 -> {
                pBase = CharacterSprite.PLAYER_IDLE_1;
                pShift = 0;
            }
            case 5 -> {
                pBase = CharacterSprite.PLAYER_IDLE_1;
                pShift = 1; // stepping forward in ready stance
            }
            case 6 -> {
                pBase = CharacterSprite.PLAYER_CHARGE;
                pShift = 0;
            }
            default -> {
                pBase = CharacterSprite.PLAYER_IDLE_2;
                pShift = 0;
            }
        }
        List<String> pSprite = (pShift > 0) ? shiftSpriteRight(pBase, pShift) : pBase;

        List<String> eBase;
        int eShift = 0;
        switch (step) {
            case 0 -> {
                eBase = enemy.getIdle1();
                eShift = 0;
            }
            case 1 -> {
                eBase = enemy.getIdle2(); // breathing/glowing
                eShift = 1; // stepping 1 col forward towards hero
            }
            case 2 -> {
                eBase = enemy.getIdle1();
                eShift = 1;
            }
            case 3 -> {
                eBase = enemy.getIdle2();
                eShift = 0;
            }
            case 4 -> {
                eBase = enemy.getIdle1();
                eShift = 0;
            }
            case 5 -> {
                eBase = enemy.getIdle2();
                eShift = 1;
            }
            case 6 -> {
                eBase = enemy.getIdle1();
                eShift = 0;
            }
            default -> {
                eBase = enemy.getIdle2();
                eShift = 1;
            }
        }
        List<String> eSprite = (eShift > 0) ? shiftSpriteLeft(eBase, eShift) : eBase;

        // Dynamic combat clash particles in the center gap
        MidCanvas canvas = new MidCanvas();
        int cycle = frame % 6;
        switch (cycle) {
            case 0 -> {
                canvas.put(2, 6, "·", Ansi.DIM + Ansi.BRIGHT_CYAN);
                canvas.put(3, 9, "✦", Ansi.BRIGHT_YELLOW);
                canvas.put(4, 15, "·", enemy.getColor());
                canvas.put(5, 18, "✦", Ansi.DIM + Ansi.BRIGHT_YELLOW);
            }
            case 1 -> {
                canvas.put(2, 8, "✦", Ansi.BRIGHT_CYAN);
                canvas.put(3, 11, "⚡", Ansi.BRIGHT_YELLOW);
                canvas.put(4, 13, "⚡", Ansi.BRIGHT_RED);
                canvas.put(5, 16, "·", enemy.getColor());
            }
            case 2 -> {
                canvas.put(2, 10, "· ✦ ·", Ansi.DIM + Ansi.BRIGHT_CYAN);
                canvas.put(3, 8, "⚡ ⚔️ ⚡", Ansi.BRIGHT_YELLOW + Ansi.BOLD);
                canvas.put(4, 10, "· ✦ ·", Ansi.DIM + enemy.getColor());
            }
            case 3 -> {
                canvas.put(1, 11, "·", Ansi.BRIGHT_YELLOW);
                canvas.put(3, 7, "[⚔️ DUEL]", Ansi.BRIGHT_WHITE + Ansi.BOLD);
                canvas.put(4, 9, "✦   ✦", Ansi.DIM + Ansi.BRIGHT_CYAN);
            }
            case 4 -> {
                canvas.put(2, 12, "✦", Ansi.BRIGHT_YELLOW);
                canvas.put(3, 9, "· ⚔️ ·", Ansi.BRIGHT_YELLOW);
                canvas.put(5, 11, "·", Ansi.DIM + Ansi.BRIGHT_CYAN);
            }
            default -> {
                canvas.put(2, 7, "·", Ansi.BRIGHT_CYAN);
                canvas.put(3, 10, "✦ ⚡ ✦", Ansi.BRIGHT_YELLOW);
                canvas.put(4, 17, "·", enemy.getColor());
            }
        }

        renderFrame(player, enemy, player.getHp(), enemy.getCurrentHp(),
            pSprite, eSprite, canvas.render(), statusMessage, false, true);
    }

    // =========================================================================
    // --- 30 FPS SMOOTH ANIMATIONS ---
    // =========================================================================

    public static void playIdleAnimation(Player player, Enemy enemy, int loops) {
        Ansi.hideCursor();
        try {
            int totalFrames = Math.max(15, loops * 15); // Smooth 15 frames = ~500ms at 30 FPS
            for (int f = 0; f < totalFrames; f++) {
                long frameStart = System.currentTimeMillis();

                List<String> pSprite = ((f / 5) % 2 == 0) ? CharacterSprite.PLAYER_IDLE_1 : CharacterSprite.PLAYER_IDLE_2;
                List<String> eSprite = ((f / 5) % 2 == 0) ? enemy.getIdle1() : enemy.getIdle2();

                MidCanvas canvas = new MidCanvas();
                int p1Col = (f * 2) % 24;
                int p2Col = (24 - (f * 2) % 24);
                int p3Col = (8 + (int)(6 * Math.sin(f * 0.3))) % 24;
                if (p3Col < 0) p3Col += 24;

                canvas.put(2, p1Col, "·", Ansi.DIM + Ansi.BRIGHT_CYAN);
                canvas.put(5, p2Col, "✦", Ansi.DIM + Ansi.BRIGHT_YELLOW);
                canvas.put(3, p3Col, "·", Ansi.DIM + Ansi.BRIGHT_CYAN);

                renderFrame(player, enemy, pSprite, eSprite, canvas.render(), "Standing by for compiler instructions...");
                sleepFrame(frameStart);
            }
        } finally {
            Ansi.showCursor();
        }
    }

    public static void animatePlayerAttack(Player player, Enemy enemy, int damage, boolean isCritical) {
        Ansi.hideCursor();
        int oldEnemyHp = enemy.getCurrentHp();
        int targetEnemyHp = Math.max(0, oldEnemyHp - damage);

        try {
            // PHASE 1: Mana Gathering & Bytecode Compilation (8 frames at 30 FPS, ~270ms)
            for (int f = 0; f < 8; f++) {
                long start = System.currentTimeMillis();
                MidCanvas canvas = new MidCanvas();
                int sparkCol = Math.max(0, 16 - (f * 2));
                canvas.put(2, sparkCol, "✦", Ansi.BRIGHT_CYAN);
                canvas.put(3, sparkCol + 2, "⚡", Ansi.BRIGHT_YELLOW);
                canvas.put(4, sparkCol + 1, "·", Ansi.BRIGHT_CYAN);

                String rune = (f % 2 == 0) ? "⚡" : "{;}";
                canvas.put(3, 0, rune, Ansi.BRIGHT_YELLOW + Ansi.BOLD);

                renderFrame(player, enemy, CharacterSprite.PLAYER_CHARGE, enemy.getIdle1(), canvas.render(),
                    Ansi.colorize("Compiling bytecode spell... Parsing AST...", Ansi.BRIGHT_CYAN));
                sleepFrame(start);
            }

            // PHASE 2: Projectile Flight across Mid Arena (12 frames at 30 FPS, ~400ms)
            for (int f = 0; f < 12; f++) {
                long start = System.currentTimeMillis();
                int headCol = (int) (f * (24.0 / 11.0)); // moves smoothly 0 to 24

                MidCanvas canvas = new MidCanvas();
                canvas.put(3, headCol - 10, "==[javac]==", Ansi.BRIGHT_CYAN + Ansi.BOLD);
                canvas.put(3, headCol - 1, "►►►", Ansi.BRIGHT_YELLOW + Ansi.BOLD);
                canvas.put(2, headCol - 3, "⚡", Ansi.BRIGHT_YELLOW);
                canvas.put(4, headCol - 4, "✦", Ansi.BRIGHT_CYAN);

                renderFrame(player, enemy, CharacterSprite.PLAYER_ATTACK, enemy.getIdle1(), canvas.render(),
                    Ansi.colorize("JIT Compiler executing native bytecode stream...", Ansi.BRIGHT_YELLOW));
                sleepFrame(start);
            }

            // Trigger actual damage in enemy model
            enemy.takeDamage(damage);
            Ansi.beep();
            AudioEngine.playAttack();
            if (isCritical) {
                AudioEngine.playCriticalHit();
                Ansi.screenShake(3, 80);
            }

            // PHASE 3: Detonation Blast, Shockwave & Smooth HP Drain (12 frames at 30 FPS, ~400ms)
            for (int f = 0; f < 12; f++) {
                long start = System.currentTimeMillis();

                double progress = Math.min(1.0, f / 10.0);
                int tweenedHp = (int) Math.round(oldEnemyHp - (oldEnemyHp - targetEnemyHp) * progress);
                List<String> eSprite;
                if (f < 2) {
                    eSprite = flashSprite(enemy.getHurtFrame());
                } else if (f < 5) {
                    eSprite = shiftSpriteRight(enemy.getHurtFrame(), 1);
                } else {
                    eSprite = enemy.getHurtFrame();
                }

                MidCanvas canvas = new MidCanvas();
                if (f < 4) {
                    canvas.put(2, 13, "\\   |   /", Ansi.BRIGHT_RED);
                    canvas.put(3, 11, "-- *CRASH!* --", Ansi.BRIGHT_RED + Ansi.BOLD);
                    canvas.put(4, 13, "/   |   \\", Ansi.BRIGHT_RED);
                } else if (f < 8) {
                    canvas.put(2, 12, "( ( SHOCK ) )", Ansi.BRIGHT_YELLOW);
                    canvas.put(3, 11, "<<< *KABOOM* >>>", Ansi.BRIGHT_RED + Ansi.BOLD);
                    canvas.put(4, 12, "( ( WAVE  ) )", Ansi.BRIGHT_YELLOW);
                } else {
                    canvas.put(3, 13, "*  ·  ✦", Ansi.DIM + Ansi.BRIGHT_YELLOW);
                    canvas.put(4, 12, "·  *  ·", Ansi.DIM + Ansi.BRIGHT_CYAN);
                }

                int textRow = Math.max(1, 5 - (f / 3));
                canvas.put(textRow, 14, String.format("-%2d HP!", damage), Ansi.BRIGHT_YELLOW + Ansi.BOLD);

                String msg = Ansi.colorize((isCritical ? "🔥 CRITICAL HIT! " : "⚔️ DIRECT HIT! ") +
                    enemy.getName() + " took " + damage + " damage!", Ansi.BRIGHT_RED + Ansi.BOLD);

                renderFrame(player, enemy, player.getHp(), tweenedHp,
                    (f < 4) ? CharacterSprite.PLAYER_ATTACK : CharacterSprite.PLAYER_IDLE_1,
                    eSprite, canvas.render(), msg);
                sleepFrame(start);
            }

            // PHASE 4: Settle (4 frames at 30 FPS, ~130ms)
            for (int f = 0; f < 4; f++) {
                long start = System.currentTimeMillis();
                renderFrame(player, enemy, CharacterSprite.PLAYER_IDLE_1, enemy.getIdle1(), null,
                    enemy.getName() + " is reeling from the damage!");
                sleepFrame(start);
            }
            Ansi.sleep(250);
        } finally {
            Ansi.showCursor();
        }
    }

    public static void animateEnemyAttack(Player player, Enemy enemy) {
        animateEnemyAttack(player, enemy, 1.0);
    }

    public static void animateEnemyAttack(Player player, Enemy enemy, double difficultyMultiplier) {
        Ansi.hideCursor();
        int level = enemy.getLevel();

        String bugTag = switch (level) {
            case 1 -> "[null]";
            case 2 -> "while(t)";
            case 3 -> "[SMASH]";
            case 4 -> "{goto}";
            case 5 -> "[0xOOM]";
            case 6 -> "🔥STACK🔥";
            default -> "[BUG]";
        };

        boolean cleanShield = player.isCleanArchShieldActive();
        boolean shield = player.isShieldActive();
        boolean hasTypeSafety = player.hasTalent(Talent.TYPE_SAFETY_SHIELD);
        int rawDmg = (int) Math.round(enemy.getEffectiveAttackDamage() * difficultyMultiplier);
        int actualDamage;
        if (cleanShield) {
            actualDamage = 0;
        } else if (shield) {
            actualDamage = hasTypeSafety ? Math.max(1, rawDmg / 4) : Math.max(1, rawDmg / 2);
        } else {
            actualDamage = rawDmg;
        }
        int oldPlayerHp = player.getHp();
        int targetPlayerHp = Math.max(0, oldPlayerHp - actualDamage);

        try {
            // PHASE 1: Enemy Windup (8 frames at 30 FPS, ~270ms)
            for (int f = 0; f < 8; f++) {
                long start = System.currentTimeMillis();
                MidCanvas canvas = new MidCanvas();
                int sparkCol = Math.min(23, 8 + (f * 2));
                canvas.put(2, sparkCol, "✦", enemy.getColor());
                canvas.put(3, sparkCol - 2, "<WARN>", Ansi.BRIGHT_RED + Ansi.BOLD);
                canvas.put(4, sparkCol, "·", enemy.getColor());

                renderFrame(player, enemy, CharacterSprite.PLAYER_IDLE_1, enemy.getAttackFrame(), canvas.render(),
                    Ansi.colorize(enemy.getName() + " prepares " + enemy.getAttackName() + "!", enemy.getColor() + Ansi.BOLD));
                sleepFrame(start);
            }

            // PHASE 2: Projectile Flight right to left (12 frames at 30 FPS, ~400ms)
            for (int f = 0; f < 12; f++) {
                long start = System.currentTimeMillis();
                int headCol = 24 - (int) (f * (24.0 / 11.0));

                MidCanvas canvas = new MidCanvas();
                canvas.put(3, headCol, "◄◄◄", Ansi.BRIGHT_RED + Ansi.BOLD);
                canvas.put(3, headCol + 3, "<=" + bugTag + "==", enemy.getColor() + Ansi.BOLD);
                canvas.put(2, headCol + 4, "·", enemy.getColor());
                canvas.put(4, headCol + 5, "✦", enemy.getColor());

                renderFrame(player, enemy,
                    (cleanShield || shield) ? CharacterSprite.PLAYER_CHARGE : CharacterSprite.PLAYER_IDLE_1,
                    enemy.getAttackFrame(), canvas.render(),
                    Ansi.colorize(enemy.getAttackMessage(), Ansi.BRIGHT_RED));
                sleepFrame(start);
            }

            // Player takes damage in model
            player.takeDamage(rawDmg);
            Ansi.beep();
            boolean breakpointTriggered = player.isBreakpointTriggered();
            boolean evasionTriggered = player.isEvasionTriggered();
            if (breakpointTriggered) {
                targetPlayerHp = player.getHp();
                player.clearBreakpointTriggered();
            } else if (evasionTriggered) {
                targetPlayerHp = oldPlayerHp;
                actualDamage = 0;
                player.clearEvasionTriggered();
            }

            if (cleanShield || evasionTriggered) {
                AudioEngine.playShield();
            } else {
                AudioEngine.playHurt();
                if (!shield) {
                    Ansi.screenShake(2, 60);
                }
            }

            // PHASE 3: Impact / Shield Deflection & Smooth HP Drain (12 frames at 30 FPS, ~400ms)
            for (int f = 0; f < 12; f++) {
                long start = System.currentTimeMillis();

                double progress = Math.min(1.0, f / 10.0);
                int tweenedHp = (int) Math.round(oldPlayerHp - (oldPlayerHp - targetPlayerHp) * progress);

                MidCanvas canvas = new MidCanvas();
                List<String> pSprite;

                if (evasionTriggered) {
                    pSprite = CharacterSprite.PLAYER_IDLE_1;
                    if (f < 5) {
                        canvas.put(2, 0, "⚡ EVADED! ⚡", Ansi.BRIGHT_YELLOW + Ansi.BOLD);
                        canvas.put(3, 0, "PATTERN MATCH REFLEX", Ansi.BRIGHT_CYAN + Ansi.BOLD);
                    } else {
                        canvas.put(3, 2, "✦ MISS ✦", Ansi.BRIGHT_GREEN);
                    }
                    String msg = Ansi.colorize("⚡ PATTERN MATCHING REFLEX: Dodged attack completely with 0 damage!", Ansi.BRIGHT_YELLOW + Ansi.BOLD);
                    renderFrame(player, enemy, tweenedHp, enemy.getCurrentHp(), pSprite, enemy.getIdle1(), canvas.render(), msg);
                } else if (cleanShield) {
                    pSprite = CharacterSprite.PLAYER_CHARGE;
                    if (f < 5) {
                        canvas.put(2, 0, "🛡️ CLEAN ARCH 🛡️", Ansi.BRIGHT_GREEN + Ansi.BOLD);
                        canvas.put(3, 0, " 100% ABSORPTION! ", Ansi.BRIGHT_CYAN + Ansi.BOLD);
                    } else {
                        int ripCol = (f - 5) * 4;
                        canvas.put(2, ripCol, "◈ ─ 🛡️ ─ ◈", Ansi.BRIGHT_GREEN);
                        canvas.put(3, ripCol, "*NULLIFIED* ──>>", Ansi.BRIGHT_CYAN);
                        canvas.put(4, ripCol, "◈ ─ 🛡️ ─ ◈", Ansi.BRIGHT_GREEN);
                    }
                    String msg = Ansi.colorize("🛡️ CLEAN ARCHITECTURE SHIELD NULLIFIED ALL DAMAGE (100% ABSORBED)!", Ansi.BRIGHT_GREEN + Ansi.BOLD);
                    renderFrame(player, enemy, tweenedHp, enemy.getCurrentHp(), pSprite, enemy.getIdle1(), canvas.render(), msg);
                } else if (breakpointTriggered) {
                    pSprite = CharacterSprite.PLAYER_CHARGE;
                    if (f < 5) {
                        canvas.put(2, 0, "🛑 BREAKPOINT! 🛑", Ansi.BRIGHT_YELLOW + Ansi.BOLD);
                        canvas.put(3, 0, "TRY-CATCH REVIVAL", Ansi.BRIGHT_GREEN + Ansi.BOLD);
                    } else {
                        canvas.put(2, 2, "✦ EXCEPTION CAUGHT ✦", Ansi.BRIGHT_GREEN);
                        canvas.put(3, 2, "Restored 25% HP!", Ansi.BRIGHT_YELLOW);
                    }
                    String msg = Ansi.colorize("🛑 BREAKPOINT TOKEN SAVED YOU! Fatal crash intercepted! Revived with 25% HP!", Ansi.BRIGHT_YELLOW + Ansi.BOLD);
                    renderFrame(player, enemy, tweenedHp, enemy.getCurrentHp(), pSprite, enemy.getIdle1(), canvas.render(), msg);
                } else if (shield) {
                    pSprite = CharacterSprite.PLAYER_CHARGE;
                    if (f < 5) {
                        String shieldTag = hasTypeSafety ? "TYPE SAFETY" : "TRY { CATCH }";
                        canvas.put(2, 0, "║ " + shieldTag + " ║", Ansi.BRIGHT_CYAN + Ansi.BOLD);
                        canvas.put(3, 0, "║ DEFLECT! ║  \\  |  /", Ansi.BRIGHT_CYAN);
                    } else {
                        int ripCol = (f - 5) * 4;
                        canvas.put(2, ripCol, "✦ ─ ◈ ─ ✦", Ansi.BRIGHT_CYAN);
                        canvas.put(3, ripCol, "*DEFLECT* ──>>", Ansi.BRIGHT_YELLOW);
                        canvas.put(4, ripCol, "✦ ─ ◈ ─ ✦", Ansi.BRIGHT_CYAN);
                    }
                    int textRow = Math.max(1, 5 - (f / 3));
                    String typeTag = hasTypeSafety ? " (-75%)" : " (-50%)";
                    canvas.put(textRow, 0, String.format("-%2d HP%s", actualDamage, typeTag), Ansi.BRIGHT_CYAN + Ansi.BOLD);

                    String shieldName = hasTypeSafety ? "🛡️ TYPE SAFETY SHIELD (-75% DMG)" : "🛡️ TRY-CATCH SHIELD (-50% DMG)";
                    String msg = Ansi.colorize(shieldName + "! Took " + actualDamage + " damage!", Ansi.BRIGHT_CYAN + Ansi.BOLD);
                    renderFrame(player, enemy, tweenedHp, enemy.getCurrentHp(), pSprite, enemy.getIdle1(), canvas.render(), msg);
                } else {
                    pSprite = (f < 2) ? flashSprite(CharacterSprite.PLAYER_HURT) : ((f < 5) ? shiftSpriteLeft(CharacterSprite.PLAYER_HURT, 1) : CharacterSprite.PLAYER_HURT);
                    if (f < 5) {
                        canvas.put(2, 0, "\\   |   /", Ansi.RED);
                        canvas.put(3, 0, "- *BOOM!* -", Ansi.BRIGHT_RED + Ansi.BOLD);
                        canvas.put(4, 0, "/   |   \\", Ansi.RED);
                    } else {
                        canvas.put(3, 2, ".  *  ·", Ansi.DIM + Ansi.BRIGHT_RED);
                        canvas.put(4, 1, "·  ✦  .", Ansi.DIM + Ansi.BRIGHT_YELLOW);
                    }

                    int textRow = Math.max(1, 5 - (f / 3));
                    canvas.put(textRow, 0, String.format("-%2d HP (CRASH!)", actualDamage), Ansi.BRIGHT_RED + Ansi.BOLD);

                    String msg = Ansi.colorize("💥 RUNTIME EXCEPTION! Took " + actualDamage + " damage!", Ansi.BRIGHT_RED + Ansi.BOLD);
                    renderFrame(player, enemy, tweenedHp, enemy.getCurrentHp(), pSprite, enemy.getIdle1(), canvas.render(), msg);
                }

                sleepFrame(start);
            }

            // PHASE 4: Settle (4 frames at 30 FPS, ~130ms)
            for (int f = 0; f < 4; f++) {
                long start = System.currentTimeMillis();
                renderFrame(player, enemy, CharacterSprite.PLAYER_IDLE_1, enemy.getIdle1(), null,
                    "Recovering from runtime crash...");
                sleepFrame(start);
            }
            Ansi.sleep(250);
        } finally {
            Ansi.showCursor();
        }
    }

    public static void renderHealAnimation(Player player, Enemy enemy, int healAmount) {
        Ansi.hideCursor();
        int oldHp = player.getHp();
        int targetHp = Math.min(player.getMaxHp(), oldHp + healAmount);
        player.heal(healAmount);
        AudioEngine.playHeal();

        try {
            // 16 frames at 30 FPS (~530ms)
            for (int f = 0; f < 16; f++) {
                long start = System.currentTimeMillis();

                // Smooth HP bar filling
                double progress = Math.min(1.0, f / 12.0);
                int tweenedHp = (int) Math.round(oldHp + (targetHp - oldHp) * progress);

                MidCanvas canvas = new MidCanvas();

                // Floating green restoration particles rising
                for (int i = 0; i < 3; i++) {
                    int particleRow = (6 - ((f * 2 + i * 4) % 7));
                    int particleCol = (i * 8 + (f % 5));
                    String spark = (i % 2 == 0) ? "💚" : "✦";
                    canvas.put(particleRow, particleCol, spark, Ansi.BRIGHT_GREEN);
                }

                // Banner in center
                if (f < 4) {
                    canvas.put(2, 2, "  ✦   ·       ", Ansi.BRIGHT_GREEN);
                    canvas.put(3, 2, "    ·   ✦ {refactor} ", Ansi.BRIGHT_CYAN);
                    canvas.put(4, 2, "  ·   *       ", Ansi.BRIGHT_GREEN);
                } else if (f < 11) {
                    canvas.put(1, 2, "╔══════════════╗", Ansi.BRIGHT_GREEN);
                    canvas.put(2, 2, "║  REFACTORED! ║", Ansi.BRIGHT_GREEN + Ansi.BOLD);
                    canvas.put(3, 2, String.format("║   +%2d HP 💚  ║", healAmount), Ansi.BRIGHT_GREEN + Ansi.BOLD);
                    canvas.put(4, 2, "╚══════════════╝", Ansi.BRIGHT_GREEN);
                    canvas.put(5, 2, "✦ ✦ [CLEAN] ✦ ✦", Ansi.BRIGHT_YELLOW);
                } else {
                    canvas.put(2, 3, "\\   |   /", Ansi.BRIGHT_GREEN);
                    canvas.put(3, 1, String.format("-- ✦ +%2d HP ✦ --", healAmount), Ansi.BRIGHT_GREEN + Ansi.BOLD);
                    canvas.put(4, 3, "/   |   \\", Ansi.BRIGHT_GREEN);
                }

                renderFrame(player, enemy, tweenedHp, enemy.getCurrentHp(),
                    (f < 11) ? CharacterSprite.PLAYER_CHARGE : CharacterSprite.PLAYER_IDLE_2,
                    enemy.getIdle1(), canvas.render(),
                    Ansi.colorize("Refactored code and recovered " + healAmount + " HP!", Ansi.BRIGHT_GREEN + Ansi.BOLD));
                sleepFrame(start);
            }

            renderFrame(player, enemy, CharacterSprite.PLAYER_IDLE_1, enemy.getIdle1(), null,
                "Ready for next bytecode instruction!");
            Ansi.sleep(200);
        } finally {
            Ansi.showCursor();
        }
    }

    public static void renderShieldAnimation(Player player, Enemy enemy) {
        Ansi.hideCursor();
        player.setShieldActive(true);
        AudioEngine.playShield();

        try {
            // 16 frames at 30 FPS (~530ms)
            for (int f = 0; f < 16; f++) {
                long start = System.currentTimeMillis();

                MidCanvas canvas = new MidCanvas();
                if (f < 4) {
                    canvas.put(2, 2, "   try {", Ansi.BRIGHT_CYAN);
                    canvas.put(3, 2, "     protect();", Ansi.BRIGHT_WHITE);
                    canvas.put(4, 2, "   }", Ansi.BRIGHT_CYAN);
                } else if (f < 11) {
                    canvas.put(1, 1, "╔════════════════╗", Ansi.BRIGHT_CYAN);
                    canvas.put(2, 1, "║ TRY-CATCH CAGE ║ 🛡️", Ansi.BRIGHT_CYAN + Ansi.BOLD);
                    canvas.put(3, 1, "║ DAMAGE HALVED! ║", Ansi.BRIGHT_WHITE + Ansi.BOLD);
                    canvas.put(4, 1, "╚════════════════╝", Ansi.BRIGHT_CYAN);
                    canvas.put(5, 1, "◈ ─ ◈ ─ ◈ ─ ◈ ─ ◈", Ansi.BRIGHT_CYAN);
                } else {
                    canvas.put(2, 2, "✦ ─ ◈ ─ ◈ ─ ✦", Ansi.BRIGHT_CYAN);
                    canvas.put(3, 1, "[EXCEPTION GUARD]", Ansi.BRIGHT_WHITE + Ansi.BOLD);
                    canvas.put(4, 2, "✦ ─ ◈ ─ ◈ ─ ✦", Ansi.BRIGHT_CYAN);
                }

                renderFrame(player, enemy,
                    (f < 11) ? CharacterSprite.PLAYER_CHARGE : CharacterSprite.PLAYER_IDLE_2,
                    enemy.getIdle1(), canvas.render(),
                    Ansi.colorize("🛡️ Try-Catch Shield deployed! Next incoming bug damage halved!", Ansi.BRIGHT_CYAN + Ansi.BOLD));
                sleepFrame(start);
            }

            renderFrame(player, enemy, CharacterSprite.PLAYER_IDLE_1, enemy.getIdle1(), null,
                "Shield primed. Ready for next action!");
            Ansi.sleep(200);
        } finally {
            Ansi.showCursor();
        }
    }

    public static void animateEnemyDefeated(Player player, Enemy enemy) {
        Ansi.hideCursor();
        try {
            // PHASE 1: Fatal Glitch & Rupture (8 frames at 30 FPS, ~270ms)
            for (int f = 0; f < 8; f++) {
                long start = System.currentTimeMillis();
                MidCanvas canvas = new MidCanvas();
                canvas.put(2, 7, "* * POP * *", Ansi.BRIGHT_YELLOW);
                canvas.put(3, 5, "<<FATAL_ERROR>>", Ansi.BRIGHT_RED + Ansi.BOLD);
                canvas.put(4, 7, "* * POP * *", Ansi.BRIGHT_YELLOW);

                // Flickering between hurt frame and glitch frame
                List<String> eSprite = (f % 2 == 0) ? enemy.getHurtFrame() : CharacterSprite.DEATH_FRAME_1;

                renderFrame(player, enemy, player.getHp(), 0,
                    CharacterSprite.PLAYER_IDLE_1, eSprite, canvas.render(),
                    Ansi.colorize("FATAL CRASH! " + enemy.getName() + " encountered unrecoverable runtime exception!", Ansi.BRIGHT_YELLOW + Ansi.BOLD));
                sleepFrame(start);
            }

            // PHASE 2: Matrix Rain / Binary Dissolution (8 frames at 30 FPS, ~270ms)
            for (int f = 0; f < 8; f++) {
                long start = System.currentTimeMillis();
                List<String> eSprite = (f < 4) ? CharacterSprite.DEATH_FRAME_2 : CharacterSprite.DEATH_FRAME_3;

                MidCanvas canvas = new MidCanvas();
                int pCol = 10 + (f * 3);
                canvas.put(1, pCol % 24, "0 1", Ansi.DIM + Ansi.BRIGHT_CYAN);
                canvas.put(3, (pCol + 4) % 24, "0x00", Ansi.DIM + Ansi.BRIGHT_GREEN);
                canvas.put(5, (pCol + 8) % 24, "1 0", Ansi.DIM + Ansi.BRIGHT_CYAN);

                renderFrame(player, enemy, player.getHp(), 0,
                    CharacterSprite.PLAYER_IDLE_2, eSprite, canvas.render(),
                    Ansi.colorize("Garbage Collector sweeping deallocated bytes from Eden space...", Ansi.BRIGHT_CYAN));
                sleepFrame(start);
            }

            // PHASE 3: Victory Fanfare & Firework Fountain (14 frames at 30 FPS, ~460ms)
            Ansi.beep();
            AudioEngine.playVictory();
            for (int f = 0; f < 14; f++) {
                long start = System.currentTimeMillis();
                MidCanvas canvas = new MidCanvas();

                int starRow = Math.max(1, 5 - (f / 3));
                canvas.put(starRow - 1, 3, "★   ✦   ★   ✦   ★", Ansi.BRIGHT_YELLOW + Ansi.BOLD);
                canvas.put(starRow, 1, "\\ | /  VICTORY!  \\ | /", Ansi.BRIGHT_GREEN + Ansi.BOLD);
                canvas.put(starRow + 1, 2, "--*--  BUG ERADICATED", Ansi.BRIGHT_YELLOW + Ansi.BOLD);
                canvas.put(starRow + 2, 1, "/ | \\             / | \\", Ansi.BRIGHT_GREEN + Ansi.BOLD);
                canvas.put(starRow + 3, 3, "✦   ★   ✦   ★   ✦", Ansi.BRIGHT_YELLOW + Ansi.BOLD);

                renderFrame(player, enemy, player.getHp(), 0,
                    CharacterSprite.PLAYER_VICTORY, CharacterSprite.DEATH_FRAME_4, canvas.render(),
                    Ansi.colorize("🏆 VICTORY! You defeated " + enemy.getName() + "!", Ansi.BRIGHT_GREEN + Ansi.BOLD));
                sleepFrame(start);
            }
            Ansi.sleep(300);
        } finally {
            Ansi.showCursor();
        }
    }

    public static void animatePlayerDefeated(Player player, Enemy enemy) {
        Ansi.hideCursor();
        try {
            // 12 frames at 30 FPS (~400ms)
            for (int f = 0; f < 12; f++) {
                long start = System.currentTimeMillis();
                MidCanvas canvas = new MidCanvas();
                if (f % 4 < 2) {
                    canvas.put(3, 2, "=== STACK OVERFLOW ===", Ansi.BRIGHT_RED + Ansi.BOLD);
                }
                renderFrame(player, enemy, 0, enemy.getCurrentHp(),
                    CharacterSprite.PLAYER_HURT, enemy.getIdle1(), canvas.render(),
                    Ansi.colorize("FATAL ERROR: Stack trace overflowed! You have fallen in combat...", Ansi.BRIGHT_RED + Ansi.BOLD));
                sleepFrame(start);
            }
            Ansi.sleep(300);
        } finally {
            Ansi.showCursor();
        }
    }

    /**
     * Animated entrance for a stage/level:
     * Glowing cybernetic gate manifests and expands on screen at steady 30 FPS.
     */
    public static void animateStageIntroEnter(int stageNumber, Enemy enemy) {
        Ansi.hideCursor();
        try {
            Ansi.clearScreen();
            String arenaName = getArenaName(stageNumber);
            String title = ">>> STAGE " + stageNumber + " / 6: " + arenaName.toUpperCase() + " <<<";
            String bossTag = "TARGET BOSS: " + enemy.getName() + " (" + enemy.getTitle() + ")";

            // Expand border frame vertically (1 to 5 rows) at 30 FPS
            for (int f = 1; f <= 5; f++) {
                long start = System.currentTimeMillis();
                StringBuilder sb = new StringBuilder(1024);
                sb.append(Ansi.CURSOR_HOME);
                sb.append(Ansi.BRIGHT_CYAN).append("╔").append("═".repeat(76)).append("╗\n").append(Ansi.RESET);

                for (int r = 0; r < f; r++) {
                    String lineContent;
                    if (r == f / 2) {
                        lineContent = Ansi.center(Ansi.colorize(title, Ansi.BRIGHT_YELLOW + Ansi.BOLD), 74);
                    } else if (r == f / 2 + 1 && f >= 4) {
                        lineContent = Ansi.center(Ansi.colorize(bossTag, enemy.getColor() + Ansi.BOLD), 74);
                    } else {
                        lineContent = " ".repeat(74);
                    }
                    sb.append(Ansi.BRIGHT_CYAN).append("║ ").append(Ansi.RESET)
                      .append(lineContent)
                      .append(Ansi.BRIGHT_CYAN).append(" ║\n").append(Ansi.RESET);
                }
                sb.append(Ansi.BRIGHT_CYAN).append("╚").append("═".repeat(76)).append("╝\n").append(Ansi.RESET);
                sb.append(Ansi.CLEAR_TO_END_OF_SCREEN);
                System.out.print(sb.toString());
                System.out.flush();
                sleepFrame(start);
            }
        } finally {
            Ansi.showCursor();
        }
    }

    /**
     * Animated exit transition for the stage screen into the combat arena:
     * High-speed matrix deallocation and shutter dissolve at steady 30 FPS.
     */
    public static void animateStageIntroExit(int stageNumber, Enemy enemy) {
        Ansi.hideCursor();
        try {
            Ansi.clearScreen();
            // 8 frames of exit transition at 30 FPS (~270ms)
            for (int f = 0; f < 8; f++) {
                long start = System.currentTimeMillis();
                StringBuilder sb = new StringBuilder(1024);
                sb.append(Ansi.CURSOR_HOME);

                int wipe = f * 5;
                int beamLen = Math.max(0, 38 - wipe);
                String padL = "═".repeat(beamLen) + " ".repeat(wipe);
                String padR = " ".repeat(wipe) + "═".repeat(beamLen);

                String headText = (f < 4) ? "✦ [DEPLOYING BATTLEGROUND] ✦" : "✦ ── [COMMENCING DUEL] ── ✦";
                sb.append(Ansi.BRIGHT_CYAN).append("╔").append(padL).append(padR).append("╗\n").append(Ansi.RESET);

                for (int r = 0; r < 5; r++) {
                    String bits = ((f + r) % 2 == 0) ? "0 1 0 1 " : "1 0 1 0 ";
                    String edgeL = (beamLen > 0) ? bits.repeat(Math.max(1, beamLen / 8)) : "";
                    String rowContent = Ansi.padVisibleRight(edgeL, 37) + Ansi.padVisibleLeft(edgeL, 37);
                    if (r == 2 && f >= 2 && f <= 5) {
                        rowContent = Ansi.center(Ansi.colorize(headText, Ansi.BRIGHT_YELLOW + Ansi.BOLD), 74);
                    } else {
                        rowContent = Ansi.colorize(rowContent, Ansi.DIM + Ansi.BRIGHT_GREEN);
                    }
                    sb.append(Ansi.BRIGHT_CYAN).append("║ ").append(Ansi.RESET)
                      .append(rowContent)
                      .append(Ansi.BRIGHT_CYAN).append(" ║\n").append(Ansi.RESET);
                }

                sb.append(Ansi.BRIGHT_CYAN).append("╚").append(padL).append(padR).append("╝\n").append(Ansi.RESET);
                sb.append(Ansi.CLEAR_TO_END_OF_SCREEN);
                System.out.print(sb.toString());
                System.out.flush();
                sleepFrame(start);
            }
            Ansi.clearScreen();
        } finally {
            Ansi.showCursor();
        }
    }

    /**
     * Standoff duel animation between Player and Enemy for every quiz question.
     * Both creatures enter combat stances, crackling energy collides in the center gap,
     * and a battle clash banner bursts on screen before the quiz begins at steady 30 FPS.
     */
    public static void animateQuizFaceOff(Player player, Enemy enemy) {
        Ansi.hideCursor();
        try {
            // 12 punchy frames at 30 FPS (~400ms)
            for (int f = 0; f < 12; f++) {
                long start = System.currentTimeMillis();
                MidCanvas canvas = new MidCanvas();

                if (f < 4) {
                    // Windup / Gathering energy sparks from both sides
                    int pCol = f * 3;
                    int eCol = 24 - (f * 3);
                    canvas.put(2, pCol, "✦", Ansi.BRIGHT_CYAN);
                    canvas.put(3, pCol + 1, "⚡", Ansi.BRIGHT_YELLOW);
                    canvas.put(4, pCol, "·", Ansi.BRIGHT_CYAN);

                    canvas.put(2, eCol, "·", enemy.getColor());
                    canvas.put(3, eCol - 1, "§", Ansi.BRIGHT_RED);
                    canvas.put(4, eCol, "✦", enemy.getColor());

                    renderFrame(player, enemy, CharacterSprite.PLAYER_CHARGE, enemy.getAttackFrame(), canvas.render(),
                        Ansi.colorize("⚡ AST CONFRONTATION: Hero and " + enemy.getName() + " square off! ⚡", Ansi.BRIGHT_YELLOW + Ansi.BOLD));
                } else if (f < 8) {
                    // Duel clash in the center of the arena!
                    canvas.put(1, 7, ".  ✦  *  ·  .", Ansi.BRIGHT_YELLOW);
                    canvas.put(2, 4, "\\   |   ⚔️   |   /", Ansi.BRIGHT_CYAN);
                    canvas.put(3, 2, "⚡⚡ [ ⚔️ CODE DUEL ⚔️ ] ⚡⚡", Ansi.BRIGHT_YELLOW + Ansi.BOLD);
                    canvas.put(4, 4, "/   |   ⚔️   |   \\", Ansi.BRIGHT_RED);
                    canvas.put(5, 7, "·  *  ✦  ·  *", Ansi.BRIGHT_CYAN);

                    renderFrame(player, enemy, CharacterSprite.PLAYER_CHARGE, enemy.getAttackFrame(), canvas.render(),
                        Ansi.colorize("⚔️ CODE DUEL ENGAGED! Solve the quiz to cast your spell! ⚔️", Ansi.BRIGHT_GREEN + Ansi.BOLD));
                } else {
                    // Settling into focused combat stance
                    canvas.put(2, 8, "·  ✦  ·", Ansi.DIM + Ansi.BRIGHT_CYAN);
                    canvas.put(3, 3, "[ READY TO COMPILE ]", Ansi.BRIGHT_YELLOW + Ansi.BOLD);
                    canvas.put(4, 8, "✦  ·  ✦", Ansi.DIM + Ansi.BRIGHT_YELLOW);

                    renderFrame(player, enemy, CharacterSprite.PLAYER_CHARGE, enemy.getIdle1(), canvas.render(),
                        "Analyze the bytecode question below...");
                }

                sleepFrame(start);
            }
            Ansi.sleep(150);
        } finally {
            Ansi.showCursor();
        }
    }
}
