import java.awt.*;
import java.awt.event.*;
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
    private double zoom;
    private static double mouseX, mouseY, translateX, translateY;
    
    public static double getMouseX() {return mouseX;};
    public static double getMouseY() {return mouseY;};
    public static double getTranslateX() {return translateX;};
    public static double getTranslateY() {return translateY;};
    
    public GameCanvas(int w, int h) {
        width = w;
        height = h;
        setPreferredSize(new Dimension(w,h));
        GameObjects = new ArrayList<GameObject>();
        DrawThread drawThread = new DrawThread(this);
        drawThread.start();
        backgroundColor = new Color(5, 31, 41,255);
        setFont(new Font("Arial", Font.PLAIN, 6));
        zoom = GameStarter.zoom;
        translateX = 0;
        translateY = 0;
        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                Point componentCoords = SwingUtilities.convertPoint(GameCanvas.this, e.getPoint(), GameCanvas.this);
                mouseX = componentCoords.x;
                mouseY = componentCoords.y;
            }
        });
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
            translateX = (-target.getX()*zoom)+(width/2)-(target.getWidth()/2);
            translateY = (-target.getY()*zoom)+(height/2)-(target.getHeight()/2);
            g2d.translate(translateX, translateY);
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

