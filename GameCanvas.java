import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;

public class GameCanvas extends JComponent {                                      
    private int width;
    private int height;
    private ArrayList<GameObject> GameObjects;
    private Color backgroundColor;
    private GameObject target;
    private double zoom = 3;
    
    public GameCanvas(int w, int h) {
        width = w;
        height = h;
        setPreferredSize(new Dimension(w,h));
        GameObjects = new ArrayList<GameObject>();
        DrawThread drawThread = new DrawThread(this);
        drawThread.start();
        backgroundColor = new Color(5, 31, 41,255);
    }

    public void focus(GameObject obj) {
        target = obj;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHints(rh);

        Rectangle2D.Double bg = new Rectangle2D.Double(-width/2,-height/2,width*2,height*2);
        g2d.setPaint(backgroundColor);
        g2d.fill(bg);

        AffineTransform reset = g2d.getTransform();
        if (target != null) {
            g2d.translate((-target.getX()*zoom)+(width/2)-(target.getWidth()/2), (-target.getY()*zoom)+(height/2)-(target.getHeight()/2));
            g2d.scale(zoom, zoom);
        }

        synchronized (GameObjects) {
            for (GameObject object : GameObjects) {
                object.draw(g2d);
            }
        }
        g2d.setTransform(reset);
    }

    public void addGameObject(GameObject obj) {
        synchronized (GameObjects) {
            GameObjects.add(obj);
            Collections.sort(GameObjects);
        }
    }
}

