import java.awt.Graphics2D;
import java.io.File;
import java.util.ArrayList;

public class GoblinGuard implements GameObject, Entity {
    private Sprite sprite;
    private int scale, width, height, zIndex;
    private double x,y,dx,dy, lastx,lasty, health;
    private long lastPing;
    private boolean moveUp, moveDown, moveLeft, moveRight, ally;
    private ArrayList<CollisionBox> collBoxes;
    private GameCanvas canvas;
    private GameFrame frame;
    private ArrayList<Entity> entities;
    private Weapon weapon;
    private final int team = 1;
    private final double friction = 0.8;

    public GoblinGuard(GameCanvas canvas, GameFrame frame, double x, double y, AnimationThread animationThread) {
        ArrayList<File> sprite_frames = new ArrayList<File>();
        sprite_frames.add(new File("sprites/goblinGuard_frame_0.png"));
        sprite_frames.add(new File("sprites/goblinGuard_frame_1.png"));
        sprite = new Sprite(sprite_frames, 2);
        this.canvas = canvas;
        this.frame = frame;
        this.x = x;
        this.y = y;
        this.dx = 0;
        this.dy = 0;
        scale = 25;
        zIndex = 999999;
        collBoxes = new ArrayList<CollisionBox>();
        CollisionBox collBox = new CollisionBox(x+4, y+2, scale-8, scale-2);
        collBoxes.add(collBox);
        frame.addEntity(this);
        canvas.addGameObject(this);
        animationThread.addSprite(sprite);
        health = 100;
    }

    public double getX() {return x;}
    public double getY() {return y;}
    public double getlX() {return lastx;}
    public double getlY() {return lasty;}
    public double getDx() {return dx;}
    public double getDy() {return dy;}
    public void setX(double x) {this.x = x; lastx = x;}
    public void setY(double y) {this.y = y; lasty = y;}
    public void setDx(double dx) {this.dx = dx;}
    public void setDy(double dy) {this.dy = dy;}
    public Weapon getWeapon() {return weapon;}
    public Sprite getSprite() {return sprite;}
    public void setUp(boolean b) {moveUp = b;}
    public void setDown(boolean b) {moveDown = b;}
    public void setLeft(boolean b) {moveLeft = b;}
    public void setRight(boolean b) {moveRight = b;}
    public int getWidth() {return width;}
    public int getHeight() {return height;}
    public ArrayList<CollisionBox> getCollisionBoxes() {return collBoxes;}
    public boolean isColliding(CollisionBox other) {return collBoxes.get(0).isColliding(other);}
    public boolean isColliding(CollisionBox other, double offsetX, double offsetY) {return collBoxes.get(0).isColliding(other, offsetX, offsetY);}
    public int getZIndex() {return zIndex;}
    public int getTeam() {return team;}

    public double damage(double dmg) {
        health -= dmg;
        double healthTemp = health;
        if (health<=0) {
            destroy();
        }
        return healthTemp;
    }

    public void update(long dt, Map curMap) {
        dx *= friction;
        dy *= friction;
        
        x += dx*dt/1000;
        y += dy*dt/1000;
        collBoxes.get(0).setPos(x+4,y+2);

        if (curMap!=null) {
            for (CollisionBox box : curMap.getCollisionBoxes()) {
                if (isColliding(box)) {
                    x -= dx*dt/1000;
                    y -= dy*dt/1000;
                    if (box.getWidth()<box.getHeight()) {
                        dx *= -1;
                    } else {
                        dy *= -1;
                    }
                    break;
                }
            }
        }

        collBoxes.get(0).setPos(x+4,y+2);
    }

    public void destroy() {
        canvas.removeGameObject(this);
        frame.removeEntity(this);
    }

    public void draw(Graphics2D g2d) {
        sprite.draw(g2d, scale, x,y);
        // weapon.draw(g2d);
        if (GameStarter.debugMode) {
            for (CollisionBox box : collBoxes) {
                box.draw(g2d);
            }
        }
    }

    @Override
    public int compareTo(GameObject other) {
        return Integer.compare(this.getZIndex(), other.getZIndex());
    }

}