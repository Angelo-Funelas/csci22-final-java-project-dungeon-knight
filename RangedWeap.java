import java.awt.Graphics2D;
import java.io.File;
import java.util.ArrayList;

public class RangedWeap extends Weapon implements GameObject {
    private double rateOfFire, bulletSpeed; // per second
    private Sprite sprite;
    private int width, height, zIndex, xOffset;
    private double x,y;
    private Player parent;
    private boolean ally;
    private GameCanvas canvas;
    private GameFrame frame;

    public RangedWeap(String type, Player parent, boolean ally, GameCanvas canvas, GameFrame frame) {
        ArrayList<File> sprite_frames = new ArrayList<File>();
        switch (type) {
            case "badPistol":
                sprite_frames.add(new File("sprites/badPistol.png"));
                break;
            default:
                sprite_frames.add(new File("sprites/badPistol.png"));
                break;
        }
        sprite = new Sprite(sprite_frames);
        zIndex = 1000001;
        this.parent = parent;
        x = parent.getWidth()*.5;
        y = parent.getWidth()*.5;
        this.ally = ally;
        this.angle = 0;
        lastAttackTime = 0;
        bulletSpeed = 400;
        this.canvas = canvas;
        rateOfFire = 2;
        this.frame = frame;
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

    public void update(long dt) {
        x = parent.getX()+parent.getWidth()*.5;
        y = parent.getY()+parent.getHeight()*.5;
        if (!ally) {
            double dx = (GameCanvas.mouseX-GameCanvas.translateX)/GameStarter.zoom - (x);
            double dy = (GameCanvas.mouseY-GameCanvas.translateY)/GameStarter.zoom - (y);
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
            if (GameCanvas.isLeftButtonPressed) {
                long currentTime = System.currentTimeMillis();
                long timeElapsed = currentTime - lastAttackTime;
                if (timeElapsed>=1000/rateOfFire) {
                    attack();
                    lastAttackTime = currentTime;
                }
            }
        } else {
            sprite.setAngle(angle);
            if (Math.abs(angle)>Math.PI/2) {
                sprite.flipVertical(-1);
                xOffset = -parent.getWidth()/2;
            } else {
                sprite.flipVertical(1);
                xOffset = 0;
            }
        }
    }

    public void attack() {
        double dx = Math.cos(angle)*bulletSpeed;
        double dy = Math.sin(angle)*bulletSpeed;
        if (GameFrame.connectedToServer) {
            ArrayList<emitArg> args = new ArrayList<emitArg>();
            args.add(new emitArg("utf", "player"));
            args.add(new emitArg("double", x+xOffset));
            args.add(new emitArg("double", y));
            args.add(new emitArg("double", dx));
            args.add(new emitArg("double", dy));
            args.add(new emitArg("double", angle));
            frame.sendCommand("newBullet", args);
        }

        new Bullet("player", x+xOffset, y, dx, dy, angle, canvas, frame);
    }

    public void draw(Graphics2D g2d) {
        sprite.draw(g2d, 13, x+xOffset,y-parent.getSprite().getJheight());
    }

    @Override
    public int compareTo(GameObject other) {
        return Integer.compare(this.getZIndex(), other.getZIndex());
    }
}
