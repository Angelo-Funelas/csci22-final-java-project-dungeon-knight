import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class DungeonPiece implements GameObject {
    BufferedImage image;
    int scaleX, scaleY, width, height;
    double x,y;

    public double getX() {return x;}
    public double getY() {return y;}

    public DungeonPiece(BufferedImage img, int scaleX, int scaleY) {
        image = img;
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        width = img.getWidth();
        height = img.getHeight();
    }
    public void draw(Graphics2D g2d) {
        g2d.drawImage(image, 0,0, scaleX, scaleY, null);
    }
    public int getWidth() {return width;}
    public int getHeight() {return height;}
    public boolean isColliding() {return true;}
}
