import java.util.ArrayList;
import javax.swing.*;
import java.awt.event.*;

public class AnimationThread extends Thread {
    private ArrayList<Sprite> sprites;
    public AnimationThread() {
        sprites = new ArrayList<Sprite>(); 
    }
    public void run() {
        Timer timer = new Timer(60, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                synchronized (sprites) {
                    for (Sprite sprite : sprites) {
                        sprite.animate();
                    }
                }
            }
        });
        timer.start();
    }
    public void addSprite(Sprite sprite) {
        synchronized (sprites) {
            sprites.add(sprite);
        }
    }
}
