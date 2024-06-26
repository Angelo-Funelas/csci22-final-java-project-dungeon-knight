import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class DungeonHall extends DungeonPiece implements GameObject  {
    BufferedImage image;
    int scaleX, scaleY, width, height;
    int x,y,zIndex;

    public double getX() {return x;}
    public double getY() {return y;}

    public DungeonHall(BufferedImage img, int zIndex, int scaleX, int scaleY, int x, int y, ArrayList<CollisionBox> collBoxes) {
        image = img;
        this.zIndex = zIndex;
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        width = img.getWidth();
        height = img.getHeight();
        this.x = x;
        this.y = y-8;
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

    public void destroy(GameCanvas canvas) {
        System.out.println("removing dungeon hall");
        canvas.removeGameObject(this);
        System.out.println("removed dungeon hall");
    }

    public int[] getRoomCenter() {
        int[] res = {x+(scaleX/2), y+(scaleY/2)};
        return res;
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
