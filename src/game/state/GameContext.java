package game.state;

import game.CombatConfig;
import game.Enemy;
import game.GameDifficulty;
import game.Player;
import game.TerminalIO;

/**
 * Contexto compartilhado para a máquina de estados do jogo.
 */
public class GameContext {

    private Player player;
    private Enemy currentEnemy;
    private int currentLevel = 1;
    private GameDifficulty difficulty = GameDifficulty.MID_LEVEL;
    private final TerminalIO io;
    private final CombatConfig config;
    private final StateMachine stateMachine;
    private boolean running = true;

    public GameContext(TerminalIO io, CombatConfig config, StateMachine stateMachine) {
        this.io = io;
        this.config = (config != null) ? config : CombatConfig.defaultBalance();
        this.stateMachine = stateMachine;
    }

    public Player getPlayer() { return player; }
    public void setPlayer(Player player) { this.player = player; }

    public Enemy getCurrentEnemy() { return currentEnemy; }
    public void setCurrentEnemy(Enemy currentEnemy) { this.currentEnemy = currentEnemy; }

    public int getCurrentLevel() { return currentLevel; }
    public void setCurrentLevel(int currentLevel) { this.currentLevel = currentLevel; }

    public GameDifficulty getDifficulty() { return difficulty; }
    public void setDifficulty(GameDifficulty difficulty) { this.difficulty = difficulty; }

    public TerminalIO getIo() { return io; }
    public CombatConfig getConfig() { return config; }
    public StateMachine getStateMachine() { return stateMachine; }

    public boolean isRunning() { return running; }
    public void setRunning(boolean running) { this.running = running; }
}
