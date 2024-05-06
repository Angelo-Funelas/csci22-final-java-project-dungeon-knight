import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class DungeonPiece implements GameObject {
    BufferedImage image;
    int scaleX, scaleY, width, height;
    double x,y;
    ArrayList<CollisionBox> collBoxes = new ArrayList<CollisionBox>();

    public double getX() {return x;}
    public double getY() {return y;}

    public DungeonPiece(BufferedImage img, int scaleX, int scaleY, ArrayList<CollisionBox> collBoxes) {
        image = img;
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        width = img.getWidth();
        height = img.getHeight();
        this.collBoxes = collBoxes;
    }
    public void draw(Graphics2D g2d) {
        g2d.drawImage(image, 0,0, scaleX, scaleY, null);
        if (GameStarter.debugMode) {
            for (CollisionBox box : collBoxes) {
                box.draw(g2d);
            }
        }
    }

    public ArrayList<CollisionBox> getCollisionBoxes() {
        return collBoxes;
    }

    public int getWidth() {return width;}
    public int getHeight() {return height;}
    public boolean isColliding(CollisionBox other) {return false;}
}
