import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

public class RangedWeap implements GameObject {
    private double rateOfFire; // per second
    private Sprite sprite;
    private int width, height, zIndex;
    private double x,y;
    private Player parent;

    public RangedWeap(String type, Player parent) {
        ArrayList<File> sprite_frames = new ArrayList<File>();
        switch (type) {
            case "badPistol":
                sprite_frames.add(new File("sprites/badPistol.png"));
            default:
                sprite_frames.add(new File("sprites/badPistol.png"));
        }
        sprite = new Sprite(sprite_frames, 2);
        zIndex = 1000001;
        this.parent = parent;
        x = parent.getWidth()*.6;
        y = parent.getWidth()*.6;
    }

    public int getWidth() {return width;}
    public int getHeight() {return height;}

    public ArrayList<CollisionBox> getCollisionBoxes() {
        return null;
    }

    public boolean isColliding(CollisionBox other) {
        return false;
    }

    public int getZIndex() {
        return zIndex;
    }

    public double getX() {return x;}
    public double getY() {return y;}

    public void draw(Graphics2D g2d) {
        sprite.draw(g2d, 13, parent.getX()+x,parent.getY()+y - parent.getSprite().getJheight());
    }

    @Override
    public int compareTo(GameObject other) {
        return Integer.compare(this.getZIndex(), other.getZIndex());
    }
}
