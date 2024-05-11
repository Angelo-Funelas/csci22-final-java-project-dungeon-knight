import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

public class RangedWeap extends Weapon implements GameObject {
    private double rateOfFire; // per second
    private Sprite sprite;
    private int width, height, zIndex, xOffset;
    private double x,y,angle;
    private Player parent;
    private int scaleY;
    private boolean ally;

    public RangedWeap(String type, Player parent, boolean ally) {
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
        x = parent.getWidth()*.5;
        y = parent.getWidth()*.5;
        this.ally = ally;
    }

    public int getWidth() {return width;}
    public int getHeight() {return height;}

    public void setAngle(double angle) {this.angle = angle;}
    public double getAngle() {return angle;}

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


    public void update(long dt) {
        if (!ally) {
            double dx = (GameCanvas.getMouseX()-GameCanvas.getTranslateX())/GameStarter.zoom - (parent.getX()+x);
            double dy = (GameCanvas.getMouseY()-GameCanvas.getTranslateY())/GameStarter.zoom - (parent.getY()+y);
            // Calculate the angle using atan2
            angle = Math.atan2(dy, dx);
            sprite.setAngle(angle);
            if (Math.abs(angle)>Math.PI/2) {
                sprite.flipVertical(-1);
                xOffset = -parent.getWidth()/2;
                parent.getSprite().faceLeft();
            } else {
                sprite.flipVertical(1);
                xOffset = 0;
                parent.getSprite().faceRight();
            }
        } else {
            sprite.setAngle(angle);
        }
    }

    public void draw(Graphics2D g2d) {
        sprite.draw(g2d, 13, parent.getX()+x+xOffset,parent.getY()+y - parent.getSprite().getJheight());
    }

    @Override
    public int compareTo(GameObject other) {
        return Integer.compare(this.getZIndex(), other.getZIndex());
    }
}
