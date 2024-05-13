import javax.swing.*;
import java.awt.event.*;

public class DrawThread extends Thread {
    GameCanvas canvas;
    public DrawThread(GameCanvas c) {
        this.canvas = c;
    }
    public void run() {
        Timer timer = new Timer(1000/GameStarter.framerate, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                canvas.repaint();
            }
        });
        timer.start();
    }
}
