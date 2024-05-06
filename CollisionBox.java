import java.awt.Color;
import java.awt.Graphics2D;

public class CollisionBox {
    private double x,y;
    private int width, height;
    static Color debugColor = new Color(194,62,68,160);

    public CollisionBox(double x, double y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void draw(Graphics2D g2d) {
        g2d.setColor(debugColor);
        g2d.fillRect((int)x, (int)y, width, height);
    }

    public void setPos(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {return x;}
    public double getY() {return y;}
    public int getWidth() {return width;}
    public int getHeight() {return height;}
    public boolean isColliding(GameObject other) {
        boolean res = false;
        for (CollisionBox box : other.getCollisionBoxes()) {
            double x1, x2, y1, y2; 
            x1 = x;
            x2 = x+width;
            y1 = y;
            y2 = y+height;
            if (
                x1 < box.getX()+box.getWidth() &&
                x2 > box.getX() &&
                y1 < box.getY()+box.getHeight() &&
                y2 > box.getY()
            ) {
                res = true;
                break;
            };
        }
        return res;
    }
}
