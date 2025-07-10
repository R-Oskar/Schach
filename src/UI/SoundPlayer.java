package UI;

import javax.sound.sampled.*;
import java.io.File;

public class SoundPlayer {
    public static Clip play(String filePath) {
        return play(filePath, false, 0);
    }

    public static Clip play(String filePath, boolean looping, float volumeDb) {
        try {
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(new File(filePath));
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            clip.start();

            if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                gainControl.setValue(volumeDb);
            }

            if (looping) {
                clip.loop(Clip.LOOP_CONTINUOUSLY);
            }
            return clip;
        } catch (Exception e) {
            System.err.println("Sound konnte nicht abgespielt werden: " + e.getMessage());
        }
        return null;
    }

    public static void toggleMute(Clip clip) {
        if (clip.isActive()) {
            clip.stop();
        } else {
            clip.start();
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }
}