import java.awt.Graphics2D;
import java.io.File;
import java.util.ArrayList;

public class Bullet implements GameObject, Entity {
    private double x, y, dx, dy, angle, dmg;
    private int width, height, zIndex, team;
    private Sprite sprite;
    private GameFrame frame;
    private GameCanvas canvas;
    private ArrayList<CollisionBox> collBoxes;
    private ArrayList<Entity> entities;

    public Bullet(String type, double x, double y, double dx, double dy, double angle, GameCanvas canvas, GameFrame frame, ArrayList<Entity> entities, int team, double dmg) {
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
        sprite = new Sprite(sprite_frames, -1);
        sprite.setAngle(angle);
        zIndex = 1000011;
        this.canvas = canvas;
        canvas.addGameObject(this);
        this.frame = frame;
        frame.addEntity(this);
        collBoxes = new ArrayList<CollisionBox>();
        collBoxes.add(new CollisionBox(x+3, y+3, 7, 7));
        this.entities = entities;
        this.team = team;
        this.dmg = dmg;
    }
    public void update(long dt, Map curMap) {
        x += dx*dt/1000;
        y += dy*dt/1000;
        collBoxes.get(0).setPos(x,y+3);
        for (CollisionBox box : curMap.getCollisionBoxes()) {
            if (isColliding(box)) {
                destroy();
                break;
            }
        }
        for (Entity entity : entities) {
            if ((entity.getTeam()!=team) && entity.isColliding(collBoxes.get(0))) {
                destroy();
                entity.damage(dmg);
                entity.setDx(dx*0.4);
                entity.setDy(dy*0.4);
                break;
            }
        }
    }

    public void destroy() {
        canvas.removeGameObject(this);
        frame.removeEntity(this);
    }

    public double damage(double dmg) {return -1;}

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
    public void setDx(double dx) {this.dx = dx;}
    public void setDy(double dy) {this.dy = dy;}
    public int getWidth() {return width;}
    public int getHeight() {return height;}
    public int getZIndex() {return zIndex;}
    public void setAngle(double angle) {this.angle = angle;}
    public double getAngle() {return angle;}
    public ArrayList<CollisionBox> getCollisionBoxes() {return null;}
    public boolean isColliding(CollisionBox other) {
        return collBoxes.get(0).isColliding(other);
    }
    public int getTeam() {return team;}

    @Override
    public int compareTo(GameObject other) {
        return Integer.compare(this.getZIndex(), other.getZIndex());
    }
}
