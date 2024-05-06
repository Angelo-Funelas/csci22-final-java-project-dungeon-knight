import java.io.*;
import java.util.ArrayList;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;
import javax.imageio.*;

public class Sprite {
    private ArrayList<BufferedImage> frames;
    private BufferedImage cur_sprite;
    private int sprite_i;
    public Sprite(ArrayList<File> frames) {
        ArrayList<BufferedImage> new_frames = new ArrayList<BufferedImage>();
        for (File file : frames) {
            try {
                new_frames.add(ImageIO.read(file));
            } catch (IOException ex) {
                System.out.println("Can't find sprites");
            }
        }
        this.frames = new_frames;
        sprite_i = 0;
        cur_sprite = this.frames.get(sprite_i);
    }
    public void update() {
        sprite_i = (sprite_i+=1)%frames.size();
        cur_sprite = frames.get(sprite_i);
    }
    public void draw(Graphics2D g2d, int scale, double x, double y) {
        AffineTransform reset = g2d.getTransform();
        g2d.translate(x, y);
        g2d.drawImage(cur_sprite, 0, 0, scale, scale, null);
        g2d.setTransform(reset);
    }
}