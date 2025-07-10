package UI;

import javax.sound.sampled.*;
import java.io.File;

public class SoundPlayer {
    public static void play(String filePath){
        play(filePath, false);
    }

    public static void play(String filePath, boolean looping) {
        try {
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(new File(filePath));
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            clip.start();
            if(looping){
                clip.loop(Clip.LOOP_CONTINUOUSLY); 
            }
        } catch (Exception e) {
            System.err.println("Sound konnte nicht abgespielt werden: " + e.getMessage());
        }
    }
}