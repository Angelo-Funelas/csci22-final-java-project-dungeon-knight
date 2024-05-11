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
    private int width, height, scaleY;
    private int faceDir = 1;
    private boolean isWalking;
    private double maxJumpHeight, jheight;
    private double angle;

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
        width = cur_sprite.getWidth();
        height = cur_sprite.getHeight();
        maxJumpHeight = 2;
        angle = 0;
        scaleY = 1;
    }
    public double getJheight() {
        return jheight;
    }
    public void update() {
        sprite_i = (sprite_i+=1)%frames.size();
        cur_sprite = frames.get(sprite_i);
        if (isWalking) {
            jheight = (maxJumpHeight/2)*(Math.sin(2*Math.PI*((sprite_i)-1.75)/7))+(maxJumpHeight/2);
        } else {
            jheight = 0;
        }
    }
    public void setAngle(double angle) {
        this.angle = angle;
    }


    public void setWalking(boolean b) {isWalking = b;}
    public void flipVertical(int scaleY) {
        this.scaleY = scaleY;
    }

    public void draw(Graphics2D g2d, int scale, double x, double y) {
        AffineTransform reset = g2d.getTransform();
        if (faceDir == -1) {
            x += width;
        }
        g2d.translate(x, y-jheight);
        if (angle!=0) {
            if (scaleY == -1) {
                g2d.translate(0, height);
                g2d.rotate(angle, width/2, -height/2);
            } else {
                g2d.rotate(angle, width/2, height/2);
            }
        }
        g2d.drawImage(cur_sprite, 0, 0, scale*faceDir, scale*scaleY, null);
        g2d.setTransform(reset);
    }
    public void faceLeft() {faceDir = -1;}
    public void faceRight() {faceDir = 1;}
    public int getWidth() {return width;}
    public int getHeight() {return height;}
    public int getFaceDir() {return faceDir;}
    public boolean isWalkingBool() {return isWalking;}
}