package game;

/**
 * Abstração de Entrada e Saída de console e terminal para desacoplar I/O de chamadas estáticas
 * e possibilitar simulação determinística em testes automatizados.
 */
public interface TerminalIO {
    void print(String text);
    void println(String text);
    void printAnsi(String ansiCode);
    String readLine();
    char readChar();
    void clearScreen();
    int getTerminalWidth();
    int getTerminalHeight();
    boolean isInteractive();
}
