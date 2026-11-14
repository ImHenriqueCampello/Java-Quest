package game.state;

import game.Ansi;
import game.Player;

/**
 * Estado representando o Menu Principal do jogo.
 */
public class MainMenuState implements GameState {

    private boolean entered = false;

    @Override
    public void enter(GameContext ctx) {
        entered = true;
    }

    @Override
    public void update(GameContext ctx) {
        // Lógica de leitura de comandos e transição de estado
        if (ctx.getPlayer() == null) {
            ctx.setPlayer(new Player("Byte Paladin"));
        }
    }

    @Override
    public void render(GameContext ctx) {
        ctx.getIo().println(Ansi.colorize("=== JAVA QUEST: MAIN MENU STATE ===", Ansi.BRIGHT_CYAN));
    }

    @Override
    public void exit(GameContext ctx) {
        entered = false;
    }

    public boolean isEntered() {
        return entered;
    }
}
