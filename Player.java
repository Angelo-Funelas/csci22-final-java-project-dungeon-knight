
import java.io.*;
import java.util.ArrayList;
import java.awt.*;

public class Player implements GameObject, Entity {
    private Sprite sprite;
    private int scale, width, height, zIndex,curWeapon_i;
    private double x,y,dx,dy;
    private boolean moveUp, moveDown, moveLeft, moveRight, ally;
    private double acceleration, friction, maxSpeed;
    private ArrayList<CollisionBox> collBoxes;
    private Color debugColor = new Color(255, 0, 0, 255);
    private ArrayList<Weapon> weapons;
    private GameCanvas canvas;
    private GameFrame frame;

    public Player(int scale, int x, int y, boolean ally, GameCanvas canvas, GameFrame frame) {
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
        this.ally = ally;
        this.canvas = canvas;
        this.x = x;
        this.y = y;
        dx = 0;
        dy = 0;
        acceleration = 30;
        maxSpeed = 200;
        this.friction = 0.8;
        zIndex = 1000000;
        width = sprite.getWidth();
        height = sprite.getHeight();
        collBoxes = new ArrayList<CollisionBox>();
        CollisionBox collBox = new CollisionBox(x, y, width-8, height-6);
        collBoxes.add(collBox);
        weapons = new ArrayList<Weapon>();
        weapons.add(new RangedWeap("badPistol", this, ally, canvas, frame));
        curWeapon_i = 0;
        canvas.addGameObject(this);
        this.frame = frame;
    }

    public double getX() {return x;}
    public double getY() {return y;}
    public double getDx() {return dx;}
    public double getDy() {return dy;}
    public void setX(double x) {this.x = x;}
    public void setY(double y) {this.y = y;}
    public void setDx(double dx) {this.dx = dx;}
    public void setDy(double dy) {this.dy = dy;}
    public Weapon getWeapon() {return weapons.get(curWeapon_i);}

    public void draw(Graphics2D g2d) {
        sprite.draw(g2d, scale, x,y);
        for (Weapon weapon : weapons) {
            weapon.draw(g2d);
        }
        if (GameStarter.debugMode) {
            g2d.setPaint(debugColor);
            g2d.drawString("X: " + (int)x, (int)x, (int)y-8);
            g2d.drawString("Y: " + (int)y, (int)x, (int)y-2);
            for (CollisionBox box : collBoxes) {
                box.draw(g2d);
            }
        }
    }

    public Sprite getSprite() {
        return sprite;
    }
    // sqrt((speed*speed)/2) = a
    public void update(long dt) {}
    public void update(long dt, Map curMap) {
        if (!ally) {
            if (moveUp) {dy += (double) (-acceleration) * dt / 1000.0;}
            if (moveDown) {dy += (double) (acceleration) * dt / 1000.0;}
            if (moveLeft) {dx += (double) (-acceleration) * dt / 1000.0;}
            if (moveRight) {dx += (double) (acceleration) * dt / 1000.0;}
    
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
            for (CollisionBox box : curMap.getCollisionBoxes()) {
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
        } else {
            dx *= friction;
            dy *= friction;
            x += dx;
            y += dy;
        }
        for (Weapon weapon : weapons) {
            weapon.update(dt);
        }

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

    public int getZIndex() {
        return zIndex;
    }
    @Override
    public int compareTo(GameObject other) {
        return Integer.compare(this.getZIndex(), other.getZIndex());
    }

}
