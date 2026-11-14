package game;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

public class Player {
    private String name;
    private int maxHp;
    private int hp;
    private int maxMana;
    private int mana;
    private int level;
    private int xp;
    private int comboStreak;
    private int highestStreak;
    private int questionsAnswered;
    private int correctAnswers;
    private boolean shieldActive;
    private boolean cleanArchShieldActive;
    private boolean jitBoosterActive;
    private boolean breakpointTriggered;
    private boolean evasionTriggered;
    private Double forcedEvasionRoll = null;
    private final Inventory inventory;
    private final Set<Talent> talents = EnumSet.noneOf(Talent.class);

    public Player(String name) {
        this.name = (name == null || name.trim().isEmpty()) ? "Byte Paladin" : name.trim();
        this.maxHp = 100;
        this.hp = 100;
        this.maxMana = 60;
        this.mana = 60;
        this.level = 1;
        this.xp = 0;
        this.comboStreak = 0;
        this.highestStreak = 0;
        this.questionsAnswered = 0;
        this.correctAnswers = 0;
        this.shieldActive = false;
        this.cleanArchShieldActive = false;
        this.jitBoosterActive = false;
        this.breakpointTriggered = false;
        this.evasionTriggered = false;
        this.inventory = new Inventory();
    }

    public int takeDamage(int incomingDamage) {
        if (incomingDamage <= 0) {
            return 0;
        }
        if (cleanArchShieldActive) {
            cleanArchShieldActive = false;
            AchievementManager.unlock(Achievement.CLEAN_ARCHITECTURE);
            return 0;
        }
        if (hasTalent(Talent.PATTERN_MATCHING_REFLEX) && shouldEvade()) {
            this.evasionTriggered = true;
            return 0;
        }
        int finalDamage = incomingDamage;
        if (shieldActive) {
            if (hasTalent(Talent.TYPE_SAFETY_SHIELD)) {
                finalDamage = Math.max(1, incomingDamage / 4); // 75% reduction!
            } else {
                finalDamage = Math.max(1, incomingDamage / 2); // 50% standard
            }
            shieldActive = false;
        }
        if (this.hp - finalDamage <= 0 && inventory != null && inventory.hasItem(Item.BREAKPOINT_TOKEN)) {
            inventory.consumeItem(Item.BREAKPOINT_TOKEN);
            this.hp = Math.max(1, this.maxHp / 4);
            this.breakpointTriggered = true;
            AchievementManager.unlock(Achievement.BREAKPOINT_SAVIOR);
            return finalDamage;
        }
        this.hp = Math.max(0, this.hp - finalDamage);
        return finalDamage;
    }

    private boolean shouldEvade() {
        return (forcedEvasionRoll != null) ? forcedEvasionRoll < 0.20 : Math.random() < 0.20;
    }

    public void heal(int amount) {
        if (amount <= 0) {
            return;
        }
        this.hp = Math.min(this.maxHp, this.hp + amount);
    }

    public boolean spendMana(int cost) {
        if (cost <= 0) {
            return true;
        }
        if (this.mana >= cost) {
            this.mana -= cost;
            return true;
        }
        return false;
    }

    public void restoreMana(int amount) {
        if (amount <= 0) {
            return;
        }
        this.mana = Math.min(this.maxMana, this.mana + amount);
    }

    public void registerAnswer(boolean correct) {
        questionsAnswered++;
        if (correct) {
            correctAnswers++;
            comboStreak++;
            if (comboStreak > highestStreak) {
                highestStreak = comboStreak;
            }
            int streakMultiplier = hasTalent(Talent.STREAM_PARALLELISM) ? 20 : 10;
            xp += 50 + (comboStreak * streakMultiplier);
            int manaRecovery = hasTalent(Talent.GRAALVM_COMPILER) ? 20 : 15;
            restoreMana(manaRecovery);
            if (comboStreak >= 10) {
                AchievementManager.unlock(Achievement.CLEAN_CODE);
            }
            if (xp >= 500) {
                AchievementManager.unlock(Achievement.HOTSPOT_OPTIMIZER);
            }
        } else {
            comboStreak = 0;
        }
    }

    public void levelUp(int newLevel) {
        this.level = newLevel;
        this.maxHp += 20;
        this.hp = this.maxHp;
        this.maxMana += 15;
        this.mana = this.maxMana;
    }

    public int getSpellDamageBonus() {
        int bonus = 0;
        if (comboStreak >= 3) bonus = 20;
        else if (comboStreak >= 2) bonus = 10;
        if (hasTalent(Talent.STREAM_PARALLELISM) && comboStreak >= 1) {
            bonus += 5;
        }
        return bonus;
    }

    public boolean isDefeated() {
        return hp <= 0;
    }

    public Set<Talent> getTalents() { return Collections.unmodifiableSet(talents); }
    public void addTalent(Talent talent) {
        if (talent != null) {
            talents.add(talent);
            if (talents.size() >= 3) {
                AchievementManager.unlock(Achievement.POLYGLOT_PROGRAMMER);
            }
        }
    }
    public boolean hasTalent(Talent talent) { return talents.contains(talent); }
    public void clearTalents() { talents.clear(); }
    public boolean isEvasionTriggered() { return evasionTriggered; }
    public void clearEvasionTriggered() { this.evasionTriggered = false; }
    public void setForcedEvasionRoll(Double roll) { this.forcedEvasionRoll = roll; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getMaxHp() { return maxHp; }
    public int getHp() { return hp; }
    public int getMaxMana() { return maxMana; }
    public int getMana() { return mana; }
    public int getLevel() { return level; }
    public int getXp() { return xp; }
    public int getComboStreak() { return comboStreak; }
    public int getHighestStreak() { return highestStreak; }
    public int getQuestionsAnswered() { return questionsAnswered; }
    public int getCorrectAnswers() { return correctAnswers; }
    public boolean isShieldActive() { return shieldActive; }
    public void setShieldActive(boolean shieldActive) { this.shieldActive = shieldActive; }
    public boolean isCleanArchShieldActive() { return cleanArchShieldActive; }
    public void setCleanArchShieldActive(boolean cleanArchShieldActive) { this.cleanArchShieldActive = cleanArchShieldActive; }
    public boolean isJitBoosterActive() { return jitBoosterActive; }
    public void setJitBoosterActive(boolean jitBoosterActive) { this.jitBoosterActive = jitBoosterActive; }
    public boolean isBreakpointTriggered() { return breakpointTriggered; }
    public void clearBreakpointTriggered() { this.breakpointTriggered = false; }
    public Inventory getInventory() { return inventory; }

    public void setHp(int hp) { this.hp = Math.min(this.maxHp, Math.max(0, hp)); }
    public void setMaxHp(int maxHp) { this.maxHp = maxHp; }
    public void setMana(int mana) { this.mana = Math.min(this.maxMana, Math.max(0, mana)); }
    public void setMaxMana(int maxMana) { this.maxMana = maxMana; }
    public void setLevel(int level) { this.level = level; }
    public void setXp(int xp) { this.xp = xp; }
    public void setComboStreak(int comboStreak) { this.comboStreak = comboStreak; }
    public void setHighestStreak(int highestStreak) { this.highestStreak = highestStreak; }
    public void setQuestionsAnswered(int questionsAnswered) { this.questionsAnswered = questionsAnswered; }
    public void setCorrectAnswers(int correctAnswers) { this.correctAnswers = correctAnswers; }
}
