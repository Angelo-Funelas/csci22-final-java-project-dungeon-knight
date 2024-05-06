import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class DungeonPiece implements GameObject {
    BufferedImage image;
    int scaleX, scaleY;
    public DungeonPiece(BufferedImage img, int scaleX, int scaleY) {
        image = img;
        this.scaleX = scaleX;
        this.scaleY = scaleY;
    }
    public void draw(Graphics2D g2d) {
        g2d.drawImage(image, 0,0, scaleX, scaleY, null);
    }
}
