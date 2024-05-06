import java.awt.Graphics2D;

public interface GameObject {
    void draw(Graphics2D g2d);
    double getX();
    double getY();
    int getWidth();
    int getHeight();
}