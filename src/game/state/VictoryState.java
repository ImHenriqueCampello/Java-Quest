package game.state;

import game.Ansi;

/**
 * Estado representando a Vitória Final e Conquista do Monólito JVM.
 */
public class VictoryState implements GameState {

    private boolean entered = false;

    @Override
    public void enter(GameContext ctx) {
        entered = true;
    }

    @Override
    public void update(GameContext ctx) {
        // Encerramento da sessão
    }

    @Override
    public void render(GameContext ctx) {
        ctx.getIo().println(Ansi.colorize("=== VICTORY STATE: JVM Purged! ===", Ansi.BRIGHT_YELLOW));
    }

    @Override
    public void exit(GameContext ctx) {
        entered = false;
    }

    public boolean isEntered() {
        return entered;
    }
}
