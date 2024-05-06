import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;;

public class GameCanvas extends JComponent {                                      
    private int width;
    private int height;
    private ArrayList<GameObject> GameObjects;
    private Color backgroundColor;
    
    public GameCanvas(int w, int h) {
        width = w;
        height = h;
        setPreferredSize(new Dimension(w,h));
        GameObjects = new ArrayList<GameObject>();
        DrawThread drawThread = new DrawThread(this);
        drawThread.start();
        backgroundColor = new Color(5, 31, 41,255);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHints(rh);
        AffineTransform reset = g2d.getTransform();
        g2d.scale(3, 3);
        Rectangle2D.Double bg = new Rectangle2D.Double(-width/2,-height/2,width*2,height*2);
        g2d.setPaint(backgroundColor);
        g2d.fill(bg);

        for (GameObject object : GameObjects) {
            object.draw(g2d);
        }
        g2d.setTransform(reset);
    }

    public void addGameObject(GameObject obj) {
        GameObjects.add(obj);
    }
}

