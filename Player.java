
import java.io.*;
import java.util.ArrayList;
import java.awt.*;
import java.awt.image.*;
import javax.imageio.*;

public class Player implements GameObject, Entity {
    private Sprite sprite;
    private int scale;
    private double x,y;
    private boolean moveUp, moveDown, moveLeft, moveRight;
    private double speed;

    public Player(int scale) {
        ArrayList<File> sprite_frames = new ArrayList<File>();
        sprite_frames.add(new File("sprites/rogue_frame_0.png"));
        sprite_frames.add(new File("sprites/rogue_frame_1.png"));
        sprite_frames.add(new File("sprites/rogue_frame_2.png"));
        sprite_frames.add(new File("sprites/rogue_frame_3.png"));
        sprite_frames.add(new File("sprites/rogue_frame_4.png"));
        sprite_frames.add(new File("sprites/rogue_frame_5.png"));
        sprite_frames.add(new File("sprites/rogue_frame_6.png"));
        sprite_frames.add(new File("sprites/rogue_frame_7.png"));
        sprite = new Sprite(sprite_frames);
        this.scale = scale;
        x = 80.5;
        y = 80.5;
        speed = 50;
    }

    public double getX() {return x;}
    public double getY() {return y;}

    public void draw(Graphics2D g2d) {
        sprite.draw(g2d, scale, x,y);
    }

    public Sprite getSprite() {
        return sprite;
    }

    public void update(long dt) {
        if (moveUp) {y -= (double) speed * dt / 1000.0;}
        if (moveDown) {y += (double) speed * dt / 1000.0;}
        if (moveLeft) {x -= (double) speed * dt / 1000.0;}
        if (moveRight) {x += (double) speed * dt / 1000.0;}
    }

    public void setUp(boolean b) {moveUp = b;}
    public void setDown(boolean b) {moveDown = b;}
    public void setLeft(boolean b) {moveLeft = b;}
    public void setRight(boolean b) {moveRight = b;}
}
