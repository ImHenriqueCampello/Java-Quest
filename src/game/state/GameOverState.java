package game.state;

import game.Ansi;

/**
 * Estado representando a tela de Derrota e Opção de Retry.
 */
public class GameOverState implements GameState {

    private boolean entered = false;

    @Override
    public void enter(GameContext ctx) {
        entered = true;
    }

    @Override
    public void update(GameContext ctx) {
        // Aguarda input de retry ou desistência
    }

    @Override
    public void render(GameContext ctx) {
        ctx.getIo().println(Ansi.colorize("=== GAME OVER STATE ===", Ansi.BRIGHT_RED));
    }

    @Override
    public void exit(GameContext ctx) {
        entered = false;
    }

    public boolean isEntered() {
        return entered;
    }
}
