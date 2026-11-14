package game;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;

/**
 * Implementação em memória de TerminalIO para testes automatizados com scripts de assert.
 */
public class VirtualTerminalIO implements TerminalIO {

    private final Deque<String> inputQueue = new ArrayDeque<>();
    private final StringBuilder outputBuffer = new StringBuilder();
    private final List<String> printedLines = new ArrayList<>();
    private int terminalWidth = 80;
    private int terminalHeight = 24;
    private boolean interactive = false;

    public VirtualTerminalIO(String... scriptedInputs) {
        if (scriptedInputs != null) {
            inputQueue.addAll(Arrays.asList(scriptedInputs));
        }
    }

    public synchronized void enqueueInput(String... inputs) {
        if (inputs != null) {
            inputQueue.addAll(Arrays.asList(inputs));
        }
    }

    @Override
    public synchronized void print(String text) {
        if (text != null) {
            outputBuffer.append(text);
        }
    }

    @Override
    public synchronized void println(String text) {
        if (text != null) {
            outputBuffer.append(text).append("\n");
            printedLines.add(text);
        } else {
            outputBuffer.append("\n");
            printedLines.add("");
        }
    }

    @Override
    public synchronized void printAnsi(String ansiCode) {
        if (ansiCode != null) {
            outputBuffer.append(ansiCode);
        }
    }

    @Override
    public synchronized String readLine() {
        if (!inputQueue.isEmpty()) {
            return inputQueue.poll();
        }
        return "";
    }

    @Override
    public synchronized char readChar() {
        String line = readLine();
        return line.isEmpty() ? '\n' : line.charAt(0);
    }

    @Override
    public synchronized void clearScreen() {
        outputBuffer.append("[CLEAR_SCREEN]\n");
    }

    @Override
    public int getTerminalWidth() {
        return terminalWidth;
    }

    public void setTerminalWidth(int width) {
        this.terminalWidth = width;
    }

    @Override
    public int getTerminalHeight() {
        return terminalHeight;
    }

    public void setTerminalHeight(int height) {
        this.terminalHeight = height;
    }

    @Override
    public boolean isInteractive() {
        return interactive;
    }

    public void setInteractive(boolean interactive) {
        this.interactive = interactive;
    }

    public synchronized String getOutputLog() {
        return outputBuffer.toString();
    }

    public synchronized List<String> getPrintedLines() {
        return new ArrayList<>(printedLines);
    }

    public synchronized void clearOutput() {
        outputBuffer.setLength(0);
        printedLines.clear();
    }
}
