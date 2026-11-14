package game.state;

/**
 * Máquina de estados finitos orquestrando transições de ciclo de vida do jogo.
 */
public class StateMachine {

    private GameState currentState;

    public synchronized void changeState(GameState newState, GameContext ctx) {
        if (currentState != null) {
            currentState.exit(ctx);
        }
        currentState = newState;
        if (currentState != null) {
            currentState.enter(ctx);
        }
    }

    public synchronized void update(GameContext ctx) {
        if (currentState != null) {
            currentState.update(ctx);
        }
    }

    public synchronized void render(GameContext ctx) {
        if (currentState != null) {
            currentState.render(ctx);
        }
    }

    public synchronized GameState getCurrentState() {
        return currentState;
    }
}
