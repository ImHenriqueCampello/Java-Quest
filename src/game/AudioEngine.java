package game;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 8-Bit procedural sound synthesizer using native Java Sound API (javax.sound.sampled).
 * Zero external dependencies; generates retro wave forms procedurally in real-time.
 */
public class AudioEngine {

    private static volatile boolean soundEnabled = true;
    private static final int SAMPLE_RATE = 22050; // 22.05 kHz retro 8-bit rate
    private static final ExecutorService soundPool = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "JavaQuestAudioThread");
        t.setDaemon(true);
        return t;
    });

    public static boolean isSoundEnabled() {
        return soundEnabled;
    }

    public static void setSoundEnabled(boolean enabled) {
        soundEnabled = enabled;
    }

    private static void playToneSequence(Tone... tones) {
        if (!soundEnabled || Ansi.FAST_MODE) {
            return;
        }

        soundPool.submit(() -> {
            try {
                AudioFormat format = new AudioFormat(SAMPLE_RATE, 8, 1, false, false);
                try (SourceDataLine line = AudioSystem.getSourceDataLine(format)) {
                    line.open(format, 4096);
                    line.start();

                    for (Tone t : tones) {
                        int numSamples = (int) ((t.durationMs / 1000.0) * SAMPLE_RATE);
                        byte[] buffer = new byte[numSamples];

                        for (int i = 0; i < numSamples; i++) {
                            double time = (double) i / SAMPLE_RATE;
                            double angle = 2.0 * Math.PI * t.frequency * time;
                            double sample;

                            switch (t.waveform) {
                                case SQUARE -> sample = Math.sin(angle) >= 0 ? 0.7 : -0.7;
                                case SAWTOOTH -> sample = 2.0 * (time * t.frequency - Math.floor(time * t.frequency + 0.5));
                                case NOISE -> sample = (Math.random() * 2.0 - 1.0);
                                case SINE -> sample = Math.sin(angle);
                                default -> sample = Math.sin(angle);
                            }

                            // Linear decay envelope to prevent audio popping
                            double envelope = 1.0 - ((double) i / numSamples);
                            int value = (int) (sample * envelope * 100.0);
                            buffer[i] = (byte) Math.max(-128, Math.min(127, value));
                        }

                        line.write(buffer, 0, buffer.length);
                    }

                    line.drain();
                }
            } catch (Throwable ignored) {
                // Graceful fallback on headless or audio-restricted environments
                Ansi.beep();
            }
        });
    }

    public enum Waveform {
        SINE, SQUARE, SAWTOOTH, NOISE
    }

    public record Tone(double frequency, int durationMs, Waveform waveform) {}

    /**
     * Attack sound: high-speed frequency slide with noise crunch.
     */
    public static void playAttack() {
        playToneSequence(
            new Tone(520, 25, Waveform.SQUARE),
            new Tone(380, 30, Waveform.SAWTOOTH),
            new Tone(220, 35, Waveform.NOISE)
        );
    }

    /**
     * Heal spell: ascending major arpeggio (C5 -> E5 -> G5 -> C6).
     */
    public static void playHeal() {
        playToneSequence(
            new Tone(523.25, 45, Waveform.SINE),
            new Tone(659.25, 45, Waveform.SINE),
            new Tone(783.99, 45, Waveform.SINE),
            new Tone(1046.50, 70, Waveform.SINE)
        );
    }

    /**
     * Try-catch shield: dual resonant square pulse.
     */
    public static void playShield() {
        playToneSequence(
            new Tone(440, 40, Waveform.SQUARE),
            new Tone(880, 80, Waveform.SINE)
        );
    }

    /**
     * Critical hit chime: triumphant multi-tone burst.
     */
    public static void playCriticalHit() {
        playToneSequence(
            new Tone(659.25, 40, Waveform.SQUARE),
            new Tone(880.00, 40, Waveform.SQUARE),
            new Tone(1174.66, 90, Waveform.SQUARE)
        );
    }

    /**
     * Damage hurt sound: low sawtooth down-sweep.
     */
    public static void playHurt() {
        playToneSequence(
            new Tone(220, 30, Waveform.SAWTOOTH),
            new Tone(140, 40, Waveform.SAWTOOTH),
            new Tone(90, 60, Waveform.NOISE)
        );
    }

    /**
     * Victory fanfare: classic 8-bit victory jingle.
     */
    public static void playVictory() {
        playToneSequence(
            new Tone(523.25, 60, Waveform.SQUARE),
            new Tone(523.25, 60, Waveform.SQUARE),
            new Tone(523.25, 60, Waveform.SQUARE),
            new Tone(659.25, 120, Waveform.SQUARE),
            new Tone(783.99, 140, Waveform.SQUARE),
            new Tone(1046.50, 220, Waveform.SQUARE)
        );
    }
}
