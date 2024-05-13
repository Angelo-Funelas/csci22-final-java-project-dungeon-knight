import java.awt.Color;
import java.awt.Graphics2D;

public class CollisionBox {
    private double x,y;
    private int width, height;
    private static Color debugColor = new Color(194,62,68,160);

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
    public void setX(double x) {this.x = x;}
    public void setY(double y) {this.y = y;}
    public int getWidth() {return width;}
    public int getHeight() {return height;}
    
    public boolean isColliding(CollisionBox other) {
        double x1, x2, y1, y2; 
        x1 = x;
        x2 = x+width;
        y1 = y;
        y2 = y+height;
        return (
            x1 < other.getX()+other.getWidth() &&
            x2 > other.getX() &&
            y1 < other.getY()+other.getHeight() &&
            y2 > other.getY()
        );
    }
    public boolean isColliding(CollisionBox other, double offsetX, double offsetY) {
        double x1, x2, y1, y2; 
        x1 = x+offsetX;
        x2 = x+offsetX+width;
        y1 = y+offsetY;
        y2 = y+offsetY+height;
        return (
            x1 < other.getX()+other.getWidth() &&
            x2 > other.getX() &&
            y1 < other.getY()+other.getHeight() &&
            y2 > other.getY()
        );
    }
}
