package game;

/**
 * Configuration Record centralizando constantes de balanceamento de combate.
 * Elimina valores mágicos espalhados pelo código do jogo.
 */
public record CombatConfig(
    int baseHealAmount,
    int healManaCost,
    int shieldManaCost,
    double shieldDamageReduction,
    int hintManaCost,
    int baseTurnManaRecovery,
    int comboStreakBonusDamageTier1,
    int comboStreakBonusDamageTier2,
    long idleAnimationDelayMs,
    long actionAnimationDelayMs
) {
    public static CombatConfig defaultBalance() {
        return new CombatConfig(35, 20, 15, 0.50, 15, 15, 10, 20, 220L, 120L);
    }
}
