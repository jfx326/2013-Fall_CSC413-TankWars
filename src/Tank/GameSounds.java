package Tank;


import javax.sound.sampled.*;
import java.net.URL;

public class GameSounds {
    private URL url;
    private AudioInputStream ais;
    private Clip c;
    
    GameSounds() {}
    
    public void playSound(String name) {
        url = Tank.class.getResource(name);

        try {
            ais = AudioSystem.getAudioInputStream(url);
            c = AudioSystem.getClip();
            c.open(ais);

            if (name.equals("Resources/Sound/Music.wav")) {
                c.loop(javax.sound.midi.Sequencer.LOOP_CONTINUOUSLY);
            }

            c.start();
        } catch (Exception e) {
        }
    }
}