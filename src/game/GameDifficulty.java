package game;

public enum GameDifficulty {
    INTERN(
        1,
        "Modo Estagiário (Casual / Story)",
        "Inimigos causam 35% a menos de dano. Dica custa apenas 5 MP. Ideal para aprender.",
        0.65,
        5,
        false
    ),
    MID_LEVEL(
        2,
        "Modo Desenvolvedor Pleno (Normal)",
        "Balanceamento clássico e autêntico do Java Quest.",
        1.0,
        15,
        false
    ),
    SENIOR_ARCHITECT(
        3,
        "Modo Arquiteto Sênior (Hardcore / Permadeath)",
        "Inimigos causam 30% a mais de dano. Permadeath: derrota apaga save e reinicia do Nível 1!",
        1.30,
        15,
        true
    );

    private final int optionIndex;
    private final String displayName;
    private final String description;
    private final double enemyDamageMultiplier;
    private final int hintManaCost;
    private final boolean permadeath;

    GameDifficulty(int optionIndex, String displayName, String description,
                   double enemyDamageMultiplier, int hintManaCost, boolean permadeath) {
        this.optionIndex = optionIndex;
        this.displayName = displayName;
        this.description = description;
        this.enemyDamageMultiplier = enemyDamageMultiplier;
        this.hintManaCost = hintManaCost;
        this.permadeath = permadeath;
    }

    public int getOptionIndex() {
        return optionIndex;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public double getEnemyDamageMultiplier() {
        return enemyDamageMultiplier;
    }

    public int getHintManaCost() {
        return hintManaCost;
    }

    public boolean isPermadeath() {
        return permadeath;
    }

    public static GameDifficulty fromChoice(String input) {
        if (input == null) return MID_LEVEL;
        String clean = input.trim().toUpperCase();
        return switch (clean) {
            case "1", "I", "INTERN", "ESTAGIARIO", "CASUAL" -> INTERN;
            case "2", "M", "MID", "PLENO", "NORMAL", "MID_LEVEL" -> MID_LEVEL;
            case "3", "S", "SENIOR", "HARD", "HARDCORE", "SENIOR_ARCHITECT" -> SENIOR_ARCHITECT;
            default -> {
                try {
                    yield GameDifficulty.valueOf(clean);
                } catch (IllegalArgumentException e) {
                    yield MID_LEVEL;
                }
            }
        };
    }
}
