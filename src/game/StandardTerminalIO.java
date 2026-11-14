package game;

import java.util.Scanner;

/**
 * Implementação padrão de TerminalIO utilizando System.out e Scanner(System.in).
 */
public class StandardTerminalIO implements TerminalIO {

    private final Scanner scanner;

    public StandardTerminalIO() {
        this(new Scanner(System.in));
    }

    public StandardTerminalIO(Scanner scanner) {
        this.scanner = (scanner != null) ? scanner : new Scanner(System.in);
    }

    @Override
    public void print(String text) {
        if (text != null) {
            System.out.print(text);
            System.out.flush();
        }
    }

    @Override
    public void println(String text) {
        if (text != null) {
            System.out.println(text);
        } else {
            System.out.println();
        }
        System.out.flush();
    }

    @Override
    public void printAnsi(String ansiCode) {
        if (ansiCode != null && Ansi.isColorEnabled()) {
            System.out.print(ansiCode);
            System.out.flush();
        }
    }

    @Override
    public String readLine() {
        if (scanner.hasNextLine()) {
            return scanner.nextLine();
        }
        return "";
    }

    @Override
    public char readChar() {
        String line = readLine();
        return line.isEmpty() ? '\n' : line.charAt(0);
    }

    @Override
    public void clearScreen() {
        Ansi.clearScreen();
    }

    @Override
    public int getTerminalWidth() {
        return 80;
    }

    @Override
    public int getTerminalHeight() {
        return Ansi.getTerminalHeight();
    }

    @Override
    public boolean isInteractive() {
        return System.console() != null;
    }
}
