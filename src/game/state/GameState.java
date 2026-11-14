package game.state;

/**
 * Interface do State Pattern para desacoplar os ciclos de vida e telas do jogo.
 */
public interface GameState {
    void enter(GameContext ctx);
    void update(GameContext ctx);
    void render(GameContext ctx);
    void exit(GameContext ctx);
}
