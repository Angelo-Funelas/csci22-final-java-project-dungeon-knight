import java.awt.Graphics2D;
import java.io.File;
import java.util.ArrayList;

public class Bullet implements GameObject {
    private double x, y, dx, dy, angle;
    private int width, height, zIndex;
    private Sprite sprite;

    public Bullet(String type, double x, double y, double dx, double dy, double angle, GameCanvas canvas) {
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
        canvas.addGameObject(this);
    }
    public void update(long dt) {
        x += dx*dt/1000;
        y += dy*dt/1000;
    }

    public void draw(Graphics2D g2d) {
        sprite.draw(g2d, height, x, y);
    }

    public double getX() {return x;}
    public double getY() {return y;}
    public int getWidth() {return width;}
    public int getHeight() {return height;}
    public int getZIndex() {return zIndex;}
    public void setAngle(double angle) {this.angle = angle;}
    public double getAngle() {return angle;}
    public ArrayList<CollisionBox> getCollisionBoxes() {return null;}
    public boolean isColliding(CollisionBox other) {return false;}

    @Override
    public int compareTo(GameObject other) {
        return Integer.compare(this.getZIndex(), other.getZIndex());
    }
}
