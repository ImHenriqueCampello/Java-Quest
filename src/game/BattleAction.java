package game;

/**
 * Hierarquia selada (sealed interface) modelando ações atômicas de combate do jogador.
 */
public sealed interface BattleAction permits
    BattleAction.AnswerQuestion,
    BattleAction.CastDebugHeal,
    BattleAction.CastVariableShield,
    BattleAction.CastCompilerHint,
    BattleAction.UseInventoryItem,
    BattleAction.ForfeitBattle {

    record AnswerQuestion(int optionIndex) implements BattleAction {}
    record CastDebugHeal() implements BattleAction {}
    record CastVariableShield() implements BattleAction {}
    record CastCompilerHint() implements BattleAction {}
    record UseInventoryItem(Item item) implements BattleAction {}
    record ForfeitBattle() implements BattleAction {}

    static BattleAction parse(String choice, int optionIndex, Item item) {
        if (choice == null) return new ForfeitBattle();
        String c = choice.trim().toUpperCase();
        return switch (c) {
            case "1", "CAST", "ANSWER" -> new AnswerQuestion(optionIndex);
            case "A", "HEAL", "REFACTOR" -> new CastDebugHeal();
            case "B", "HINT", "BREAKPOINT" -> new CastCompilerHint();
            case "3", "SHIELD", "TRYCATCH", "TRY_CATCH" -> new CastVariableShield();
            case "5", "ITEM", "USE", "BACKPACK" -> new UseInventoryItem(item);
            case "4", "Q", "FORFEIT", "RETREAT" -> new ForfeitBattle();
            default -> new AnswerQuestion(optionIndex);
        };
    }
}
