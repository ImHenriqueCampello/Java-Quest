package game.state;

import game.Ansi;
import game.Enemy;

/**
 * Estado representando a Arena de Combate e duelo de perguntas/feitiços.
 */
public class BattleState implements GameState {

    private boolean entered = false;

    @Override
    public void enter(GameContext ctx) {
        entered = true;
        if (ctx.getCurrentEnemy() == null) {
            ctx.setCurrentEnemy(Enemy.createForLevel(ctx.getCurrentLevel()));
        }
    }

    @Override
    public void update(GameContext ctx) {
        if (ctx.getCurrentEnemy() != null && ctx.getCurrentEnemy().isDefeated()) {
            ctx.getStateMachine().changeState(new LevelTransitionState(), ctx);
        } else if (ctx.getPlayer() != null && ctx.getPlayer().isDefeated()) {
            ctx.getStateMachine().changeState(new GameOverState(), ctx);
        }
    }

    @Override
    public void render(GameContext ctx) {
        String enemyName = (ctx.getCurrentEnemy() != null) ? ctx.getCurrentEnemy().getName() : "Unknown Bug";
        ctx.getIo().println(Ansi.colorize("=== ARENA BATTLE STATE: Fighting " + enemyName + " ===", Ansi.BRIGHT_RED));
    }

    @Override
    public void exit(GameContext ctx) {
        entered = false;
    }

    public boolean isEntered() {
        return entered;
    }
}
