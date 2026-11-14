package game.state;

import game.Ansi;

/**
 * Estado representando a Transição e Vitória de Nível / Level Up.
 */
public class LevelTransitionState implements GameState {

    private boolean entered = false;

    @Override
    public void enter(GameContext ctx) {
        entered = true;
    }

    @Override
    public void update(GameContext ctx) {
        if (ctx.getCurrentLevel() >= 6) {
            ctx.getStateMachine().changeState(new VictoryState(), ctx);
        } else {
            ctx.setCurrentLevel(ctx.getCurrentLevel() + 1);
            if (ctx.getPlayer() != null) {
                ctx.getPlayer().levelUp(ctx.getCurrentLevel());
            }
            ctx.setCurrentEnemy(null);
            ctx.getStateMachine().changeState(new BattleState(), ctx);
        }
    }

    @Override
    public void render(GameContext ctx) {
        ctx.getIo().println(Ansi.colorize("=== LEVEL TRANSITION STATE: Level Cleared! ===", Ansi.BRIGHT_GREEN));
    }

    @Override
    public void exit(GameContext ctx) {
        entered = false;
    }

    public boolean isEntered() {
        return entered;
    }
}
