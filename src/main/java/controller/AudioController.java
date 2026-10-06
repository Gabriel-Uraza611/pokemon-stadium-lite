package controller;

import javax.sound.sampled.*;
import java.io.InputStream;

public class AudioController {

    private Clip currentClip;

    /**
     * Reproduce un archivo de audio en bucle desde resources.
     * Detiene cualquier audio previo.
     *
     * @param resourcePath ruta relativa en resources (ej: "audio/selection.wav")
     */
    public void playLoop(String resourcePath) {
        stop();
        try {
            InputStream audioSrc = getClass().getClassLoader().getResourceAsStream(resourcePath);
            if (audioSrc == null) {
                System.err.println("[AudioController] Archivo no encontrado: " + resourcePath);
                return;
            }

            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioSrc);
            currentClip = AudioSystem.getClip();
            currentClip.open(audioStream);
            currentClip.loop(Clip.LOOP_CONTINUOUSLY);
            currentClip.start();

        } catch (Exception e) {
            System.err.println("[AudioController] Error reproduciendo " + resourcePath);
            e.printStackTrace();
        }
    }

    /**
     * Detiene y libera el audio actual.
     */
    public void stop() {
        if (currentClip != null) {
            if (currentClip.isRunning()) {
                currentClip.stop();
            }
            currentClip.close();
            currentClip = null;
        }
    }

    /**
     * Cambia a otra pista (stop + playLoop).
     */
    public void switchTo(String resourcePath) {
        playLoop(resourcePath);
    }

    public boolean isPlaying() {
        return currentClip != null && currentClip.isRunning();
    }
}