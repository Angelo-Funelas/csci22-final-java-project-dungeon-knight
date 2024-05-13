import java.io.File;
import java.net.URL;

import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;


public class GameSound {
    private Clip clip;
    private URL soundFiles[] = new URL[2];
    
    public GameSound() {
        soundFiles[0] = getClass().getResource("audio/shoot.wav");
        soundFiles[1] = getClass().getResource("audio/music.wav");
    }
    public void setFile(int i) {
        try {
            AudioInputStream ais = AudioSystem.getAudioInputStream(soundFiles[i]);
            clip = AudioSystem.getClip();
            clip.open(ais);
        } catch (Exception e) {
            System.out.println("Error setting file: " + e);
        }
    }
    public void play(float volume) {
        FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        gainControl.setValue(volume);
        clip.start();
    }
    public void loop() {
        clip.loop(Clip.LOOP_CONTINUOUSLY);
    }
    public void stop() {
        clip.stop();
    }
}
