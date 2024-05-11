import java.awt.Graphics2D;
import java.io.File;
import java.util.ArrayList;

public class Bullet implements GameObject, Entity {
    private double x, y, dx, dy, angle;
    private int width, height, zIndex;
    private Sprite sprite;
    private GameFrame frame;
    private GameCanvas canvas;
    private ArrayList<CollisionBox> collBoxes;

    public Bullet(String type, double x, double y, double dx, double dy, double angle, GameCanvas canvas, GameFrame frame) {
        System.out.println("new bullet " + dx + " " + dy + " of type " + type);
        this.x = x;
        this.y = y;
        this.dx = dx;
        this.dy = dy;
        ArrayList<File> sprite_frames = new ArrayList<File>();
        switch (type) {
            case "player":
                sprite_frames.add(new File("sprites/bullet.png"));
                width = 13;
                height = 13;
                break;
            default:
                sprite_frames.add(new File("sprites/bullet.png"));
                width = 0;
                height = 0;
        }
        sprite = new Sprite(sprite_frames);
        sprite.setAngle(angle);
        zIndex = 1000001;
        this.canvas = canvas;
        canvas.addGameObject(this);
        this.frame = frame;
        frame.addEntity(this);
        collBoxes = new ArrayList<CollisionBox>();
        collBoxes.add(new CollisionBox(x+3, y+3, 7, 7));
    }
    public void update(long dt) {}
    public void update(long dt, Map curMap) {
        x += dx*dt/1000;
        y += dy*dt/1000;
        collBoxes.get(0).setPos(x,y+3);
        for (CollisionBox box : curMap.getCollisionBoxes()) {
            if (isColliding(box)) {
                System.out.println("test");
                destroy();
                break;
            }
        }
    }

    public void destroy() {
        canvas.removeGameObject(this);
        frame.removeEntity(this);
    }

    public void draw(Graphics2D g2d) {
        sprite.draw(g2d, height, x, y);
        if (GameStarter.debugMode) {
            for (CollisionBox box : collBoxes) {
                box.draw(g2d);
            }
        }
    }

    public double getX() {return x;}
    public double getY() {return y;}
    public int getWidth() {return width;}
    public int getHeight() {return height;}
    public int getZIndex() {return zIndex;}
    public void setAngle(double angle) {this.angle = angle;}
    public double getAngle() {return angle;}
    public ArrayList<CollisionBox> getCollisionBoxes() {return null;}
    public boolean isColliding(CollisionBox other) {
        return collBoxes.get(0).isColliding(other);
    }

    @Override
    public int compareTo(GameObject other) {
        return Integer.compare(this.getZIndex(), other.getZIndex());
    }
}
