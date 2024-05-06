import javax.swing.*;
import java.awt.event.*;

public class DrawThread extends Thread {
    GameCanvas c;
    public DrawThread(GameCanvas c) {
        this.c = c;
    }
    public void run() {
        Timer timer = new Timer(40, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                c.repaint();
            }
        });
        timer.start();
    }
}
