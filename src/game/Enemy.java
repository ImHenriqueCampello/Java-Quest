package game;

import java.util.List;

public class Enemy {
    public enum BossMechanic {
        NONE,
        COSMIC_VOID,       // Level 1: NullPointer Imp deals bonus critical damage after 2 consecutive wrong answers
        CPU_THROTTLE,      // Level 2: Infinite Loop Serpent drains 5 MP per turn
        ARRAY_SLICE,       // Level 3: IndexOutOfBounds Golem shuffles question options
        COUPLING_LOCK,     // Level 4: Spaghetti Chimera locks heal spell for 2 turns
        HEAP_OVERFLOW,     // Level 5: Memory Leak Hydra regenerates 5 HP each turn
        DEEP_RECURSION     // Level 6: StackOverflow Dragon increases attack damage by +3 each turn
    }

    private final int level;
    private final String name;
    private final String title;
    private final String description;
    private final int maxHp;
    private int currentHp;
    private final int attackDamage;
    private final String attackName;
    private final String attackMessage;
    private final String color;

    private final List<String> idle1;
    private final List<String> idle2;
    private final List<String> attackFrame;
    private final List<String> hurtFrame;

    private final BossMechanic mechanic;
    private int consecutiveMisses = 0;
    private int mechanicCounter = 0;
    private int turnCount = 0;
    private int couplingLockTurns = 0;

    public Enemy(int level, String name, String title, String description,
                 int maxHp, int attackDamage, String attackName, String attackMessage,
                 String color, List<String> idle1, List<String> idle2,
                 List<String> attackFrame, List<String> hurtFrame) {
        this(level, name, title, description, maxHp, attackDamage, attackName, attackMessage,
             color, idle1, idle2, attackFrame, hurtFrame, defaultMechanicForLevel(level));
    }

    public Enemy(int level, String name, String title, String description,
                 int maxHp, int attackDamage, String attackName, String attackMessage,
                 String color, List<String> idle1, List<String> idle2,
                 List<String> attackFrame, List<String> hurtFrame,
                 BossMechanic mechanic) {
        this.level = level;
        this.name = name;
        this.title = title;
        this.description = description;
        this.maxHp = maxHp;
        this.currentHp = maxHp;
        this.attackDamage = attackDamage;
        this.attackName = attackName;
        this.attackMessage = attackMessage;
        this.color = color;
        this.idle1 = idle1;
        this.idle2 = idle2;
        this.attackFrame = attackFrame;
        this.hurtFrame = hurtFrame;
        this.mechanic = (mechanic == null) ? BossMechanic.NONE : mechanic;
    }

    public static BossMechanic defaultMechanicForLevel(int level) {
        return switch (level) {
            case 1 -> BossMechanic.COSMIC_VOID;
            case 2 -> BossMechanic.CPU_THROTTLE;
            case 3 -> BossMechanic.ARRAY_SLICE;
            case 4 -> BossMechanic.COUPLING_LOCK;
            case 5 -> BossMechanic.HEAP_OVERFLOW;
            case 6 -> BossMechanic.DEEP_RECURSION;
            default -> BossMechanic.NONE;
        };
    }

    public static Enemy createForLevel(int level) {
        return switch (level) {
            case 1 -> new Enemy(
                1,
                "NullPointer Imp",
                "Scourge of Uninitialized References",
                "A mischievous bug born when variables point into the void.",
                60, 15,
                "Null Pointer Dereference",
                "The Imp throws an unassigned object reference at you!",
                Ansi.BRIGHT_MAGENTA,
                CharacterSprite.IMP_IDLE_1,
                CharacterSprite.IMP_IDLE_2,
                CharacterSprite.IMP_ATTACK,
                CharacterSprite.IMP_HURT
            );
            case 2 -> new Enemy(
                2,
                "Infinite Loop Serpent",
                "Devourer of CPU Cycles",
                "A hypnotic snake locked in while(true) with no break condition.",
                80, 20,
                "Eternal While Loop",
                "The Serpent constricts your execution thread in an endless loop!",
                Ansi.BRIGHT_GREEN,
                CharacterSprite.SERPENT_IDLE_1,
                CharacterSprite.SERPENT_IDLE_2,
                CharacterSprite.SERPENT_ATTACK,
                CharacterSprite.SERPENT_HURT
            );
            case 3 -> new Enemy(
                3,
                "IndexOutOfBounds Golem",
                "Crusher of Array Boundaries",
                "Forged from raw memory brackets, it strikes when i >= length.",
                100, 25,
                "Off-By-One Slam",
                "The Golem queries element [length] and shatters your array!",
                Ansi.BRIGHT_YELLOW,
                CharacterSprite.GOLEM_IDLE_1,
                CharacterSprite.GOLEM_IDLE_2,
                CharacterSprite.GOLEM_ATTACK,
                CharacterSprite.GOLEM_HURT
            );
            case 4 -> new Enemy(
                4,
                "Spaghetti Code Chimera",
                "Abomination of Anti-Patterns",
                "A tangled abomination with 5000-line methods and circular inheritance.",
                120, 30,
                "Spaghetti Entanglement",
                "The Chimera lashes out with God-Objects and tight coupling!",
                Ansi.BRIGHT_CYAN,
                CharacterSprite.CHIMERA_IDLE_1,
                CharacterSprite.CHIMERA_IDLE_2,
                CharacterSprite.CHIMERA_ATTACK,
                CharacterSprite.CHIMERA_HURT
            );
            case 5 -> new Enemy(
                5,
                "Memory Leak Hydra",
                "Siphon of the JVM Heap",
                "Each unclosed stream and static cache sprouts two new heads.",
                140, 35,
                "OutOfMemory Error",
                "The Hydra floods your Heap with uncollectable references!",
                Ansi.BRIGHT_RED,
                CharacterSprite.HYDRA_IDLE_1,
                CharacterSprite.HYDRA_IDLE_2,
                CharacterSprite.HYDRA_ATTACK,
                CharacterSprite.HYDRA_HURT
            );
            case 6 -> new Enemy(
                6,
                "THE STACKOVERFLOW DRAGON",
                "Apex Predator of the Call Stack",
                "An ancient titan that consumes stack frames without a base case.",
                180, 40,
                "Deep Recursion Inferno",
                "The Dragon unleashes an unbounded recursive method call!",
                Ansi.RED,
                CharacterSprite.DRAGON_IDLE_1,
                CharacterSprite.DRAGON_IDLE_2,
                CharacterSprite.DRAGON_ATTACK,
                CharacterSprite.DRAGON_HURT
            );
            default -> createForLevel(1);
        };
    }

    public void takeDamage(int amount) {
        if (amount <= 0) {
            return;
        }
        this.currentHp = Math.max(0, this.currentHp - amount);
    }

    public void heal(int amount) {
        if (amount <= 0) {
            return;
        }
        this.currentHp = Math.min(this.maxHp, this.currentHp + amount);
    }

    public boolean isDefeated() {
        return currentHp <= 0;
    }

    public void incrementTurnCount() {
        this.turnCount++;
    }

    public void recordPlayerMiss() {
        this.consecutiveMisses++;
    }

    public void recordPlayerHit() {
        this.consecutiveMisses = 0;
    }

    public void incrementMechanicCounter() {
        this.mechanicCounter++;
    }

    public void setMechanicCounter(int val) {
        this.mechanicCounter = val;
    }

    public int getEffectiveAttackDamage() {
        if (mechanic == BossMechanic.DEEP_RECURSION) {
            return attackDamage + (mechanicCounter * 3);
        }
        if (mechanic == BossMechanic.COSMIC_VOID && consecutiveMisses >= 2) {
            return attackDamage + 10;
        }
        return attackDamage;
    }

    public int getCouplingLockTurns() { return couplingLockTurns; }
    public void setCouplingLockTurns(int turns) { this.couplingLockTurns = Math.max(0, turns); }
    public void decrementCouplingLockTurns() { if (couplingLockTurns > 0) couplingLockTurns--; }

    public BossMechanic getMechanic() { return mechanic; }
    public int getConsecutiveMisses() { return consecutiveMisses; }
    public int getMechanicCounter() { return mechanicCounter; }
    public int getTurnCount() { return turnCount; }

    public int getLevel() { return level; }
    public String getName() { return name; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public int getMaxHp() { return maxHp; }
    public int getCurrentHp() { return currentHp; }
    public int getAttackDamage() { return attackDamage; }
    public String getAttackName() { return attackName; }
    public String getAttackMessage() { return attackMessage; }
    public String getColor() { return color; }

    public List<String> getIdle1() { return idle1; }
    public List<String> getIdle2() { return idle2; }
    public List<String> getAttackFrame() { return attackFrame; }
    public List<String> getHurtFrame() { return hurtFrame; }
}
