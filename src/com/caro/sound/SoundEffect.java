package com.caro.sound;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;

/**
 * Tạo và phát hiệu ứng âm thanh trực tiếp (Synthesizer PCM)
 * mà không cần phụ thuộc vào file .wav bên ngoài.
 */
public class SoundEffect {
    private static final float SAMPLE_RATE = 44100f;

    /**
     * Phát âm thanh khi người chơi đặt quân cờ.
     */
    public static void playMoveSound() {
        playToneAsync(880, 50, 0.4); // Nốt La cao (880Hz), 50ms
    }

    /**
     * Phát âm thanh khi Undo hoặc Redo.
     */
    public static void playUndoSound() {
        playToneAsync(440, 70, 0.3); // Nốt La chuẩn (440Hz), 70ms
    }

    /**
     * Phát âm thanh cảnh báo khi sắp hết thời gian (dưới 5 giây).
     */
    public static void playAlertSound() {
        playToneAsync(600, 100, 0.5);
    }

    /**
     * Phát đoạn nhạc chiến thắng (hợp âm vui mừng).
     */
    public static void playWinSound() {
        new Thread(() -> {
            try {
                int[] notes = {523, 659, 784, 1046}; // C5, E5, G5, C6
                for (int note : notes) {
                    playToneSync(note, 120, 0.5);
                    Thread.sleep(30);
                }
            } catch (Exception ignored) {
            }
        }).start();
    }

    private static void playToneAsync(final int hz, final int msecs, final double vol) {
        new Thread(() -> playToneSync(hz, msecs, vol)).start();
    }

    private static void playToneSync(int hz, int msecs, double vol) {
        try {
            byte[] buf = new byte[msecs * (int) SAMPLE_RATE / 1000];
            for (int i = 0; i < buf.length; i++) {
                double angle = i / (SAMPLE_RATE / hz) * 2.0 * Math.PI;
                // Áp dụng envelope làm mượt âm thanh tránh tiếng lách cách (click/pop)
                double factor = 1.0;
                int fadeLen = buf.length / 8;
                if (i < fadeLen) {
                    factor = (double) i / fadeLen;
                } else if (i > buf.length - fadeLen) {
                    factor = (double) (buf.length - i) / fadeLen;
                }
                buf[i] = (byte) (Math.sin(angle) * 127.0 * vol * factor);
            }

            AudioFormat af = new AudioFormat(SAMPLE_RATE, 8, 1, true, false);
            SourceDataLine sdl = AudioSystem.getSourceDataLine(af);
            sdl.open(af);
            sdl.start();
            sdl.write(buf, 0, buf.length);
            sdl.drain();
            sdl.close();
        } catch (Exception ignored) {
            // Không làm gián đoạn chương trình nếu máy tính không có thiết bị âm thanh
        }
    }
}
