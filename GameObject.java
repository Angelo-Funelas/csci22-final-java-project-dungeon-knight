import java.awt.Graphics2D;
import java.util.ArrayList;

public interface GameObject {
    void draw(Graphics2D g2d);
    double getX();
    double getY();
    int getWidth();
    int getHeight();
    ArrayList<CollisionBox> getCollisionBoxes();
    boolean isColliding(GameObject other);
}