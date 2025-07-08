package UI;

import javax.sound.sampled.*;
import java.io.File;

public class SoundPlayer {
    public static void play(String filePath) {
        try {
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(new File(filePath));
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            clip.start();
        } catch (Exception e) {
            System.err.println("Sound konnte nicht abgespielt werden: " + e.getMessage());
        }
    }
}