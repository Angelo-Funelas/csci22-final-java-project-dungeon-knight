
import java.io.*;
import java.util.ArrayList;
import java.awt.*;
import java.awt.image.*;
import javax.imageio.*;
import javax.sound.sampled.spi.MixerProvider;

public class Player implements GameObject, Entity {
    private Sprite sprite;
    private int scale, width, height;
    private double x,y,dx,dy;
    private boolean moveUp, moveDown, moveLeft, moveRight;
    private double acceleration, friction, maxSpeed, speedModifier;
    private ArrayList<CollisionBox> collBoxes;

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
        sprite = new Sprite(sprite_frames, 2);
        this.scale = scale;
        x = 80.5;
        y = 80.5;
        acceleration = 25;
        maxSpeed = 90;
        friction = 0.8;
        width = sprite.getWidth();
        height = sprite.getHeight();
        collBoxes = new ArrayList<CollisionBox>();
        CollisionBox collBox = new CollisionBox(x, y, width-8, height-6);
        collBoxes.add(collBox);
    }

    public double getX() {return x;}
    public double getY() {return y;}

    public void draw(Graphics2D g2d) {
        sprite.draw(g2d, scale, x,y);
        if (GameStarter.debugMode) {
            for (CollisionBox box : collBoxes) {
                box.draw(g2d);
            }
        }
    }

    public Sprite getSprite() {
        return sprite;
    }
    // sqrt((speed*speed)/2) = a
    public void update(long dt) {
        if (moveUp) {dy += (double) (-acceleration) * dt / 1000.0;}
        if (moveDown) {dy += (double) (acceleration) * dt / 1000.0;}
        if (moveLeft) {dx += (double) (-acceleration) * dt / 1000.0; sprite.faceLeft();}
        if (moveRight) {dx += (double) (acceleration) * dt / 1000.0; sprite.faceRight();}

        double cur_maxSpeed = maxSpeed;
        if ((moveUp || moveDown) && (moveLeft || moveRight) && !(moveUp && moveDown) && !(moveLeft && moveRight)) {
            cur_maxSpeed = Math.sqrt(cur_maxSpeed*cur_maxSpeed/2);
        }
        cur_maxSpeed *= dt / 1000.0;
        if (moveUp||moveDown||moveLeft||moveRight) {sprite.setWalking(true);} else {sprite.setWalking(false);}

        dx = Math.max(-cur_maxSpeed, Math.min(cur_maxSpeed, dx));
        dy = Math.max(-cur_maxSpeed, Math.min(cur_maxSpeed, dy));
        dx *= friction;
        dy *= friction;

        boolean collidedX = false;
        boolean collidedY = false;
        for (CollisionBox box : Map.getCollisionBoxes()) {
            if (isColliding(box, dx, 0)) {
                collidedX = true;
                dx = 0;
            }
            if (isColliding(box, 0, dy)) {
                collidedY = true;
                dy = 0;
            }
        }
        if (!collidedX) {
            x += dx;
        }
        if (!collidedY) {
            y += dy;
        }

        collBoxes.get(0).setPos(x+4,y+5);
    }

    public void setUp(boolean b) {moveUp = b;}
    public void setDown(boolean b) {moveDown = b;}
    public void setLeft(boolean b) {moveLeft = b;}
    public void setRight(boolean b) {moveRight = b;}

    public int getWidth() {return width;}
    public int getHeight() {return height;}

    public ArrayList<CollisionBox> getCollisionBoxes() {
        return collBoxes;
    }

    public boolean isColliding(CollisionBox other) {
        return collBoxes.get(0).isColliding(other);
    }

    public boolean isColliding(CollisionBox other, double offsetX, double offsetY) {
        return collBoxes.get(0).isColliding(other, offsetX, offsetY);
    }

}
