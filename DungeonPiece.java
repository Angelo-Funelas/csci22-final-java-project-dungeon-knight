import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class DungeonPiece implements GameObject {
    BufferedImage image;
    int scaleX, scaleY, width, height;
    int x,y,zIndex;
    ArrayList<CollisionBox> collBoxes = new ArrayList<CollisionBox>();

    public double getX() {return x;}
    public double getY() {return y;}

    public DungeonPiece(BufferedImage img, int zIndex, int scaleX, int scaleY, int gridX, int gridY, ArrayList<CollisionBox> collBoxes) {
        image = img;
        this.zIndex = zIndex;
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        width = img.getWidth();
        height = img.getHeight();
        this.x = gridX*80*16;
        this.y = gridY*80*16;
        for (CollisionBox box : collBoxes) {
            box.setX(box.getX() + this.x);
            box.setY(box.getY() + this.y);
        }
        this.collBoxes = collBoxes;
    }
    public void draw(Graphics2D g2d) {
        g2d.drawImage(image, x,y, scaleX, scaleY, null);
        if (GameStarter.debugMode) {
            for (CollisionBox box : collBoxes) {
                box.draw(g2d);
            }
        }
    }

    public int[] getRoomCenter() {
        int[] res = {x+(scaleX/2), y+(scaleY/2)};
        return res;
    } 

    public ArrayList<CollisionBox> getCollisionBoxes() {
        return collBoxes;
    }

    public int getWidth() {return width;}
    public int getHeight() {return height;}
    public boolean isColliding(CollisionBox other) {return false;}
    public int getZIndex() {
        return zIndex;
    }
    @Override
    public int compareTo(GameObject other) {
        return Integer.compare(this.getZIndex(), other.getZIndex());
    }
}
