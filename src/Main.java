import game.Ansi;
import game.GameEngine;

public class Main {
    public static void main(String[] args) {
        GameEngine.configureCliArgs(args);

        Ansi.initTerminal();
        Runtime.getRuntime().addShutdownHook(new Thread(Ansi::restoreTerminal));
        try {
            GameEngine engine = new GameEngine();
            engine.start();
        } finally {
            Ansi.restoreTerminal();
        }
    }
}
