import java.awt.Graphics2D;
import java.io.File;
import java.util.ArrayList;

public class Portal implements GameObject {
    private Sprite sprite;
    private int scale, width, height, zIndex;
    private double x,y;
    private ArrayList<CollisionBox> collBoxes;
    private GameCanvas canvas;

    public Portal(int scale, int x, int y, GameCanvas canvas, AnimationThread animationThread) {
        ArrayList<File> sprite_frames = new ArrayList<File>();
        sprite_frames.add(new File("sprites/portal_frame_0.png"));
        sprite_frames.add(new File("sprites/portal_frame_1.png"));
        sprite_frames.add(new File("sprites/portal_frame_2.png"));
        sprite_frames.add(new File("sprites/portal_frame_3.png"));
        sprite_frames.add(new File("sprites/portal_frame_4.png"));
        sprite_frames.add(new File("sprites/portal_frame_5.png"));
        sprite = new Sprite(sprite_frames, 1);
        this.scale = scale;
        this.x = x;
        this.y = y;
        zIndex = 1000000;
        width = sprite.getWidth();
        height = sprite.getHeight();
        collBoxes = new ArrayList<CollisionBox>();
        CollisionBox collBox = new CollisionBox(x, y, width-8, height-6);
        collBoxes.add(collBox);
        this.canvas = canvas;
        canvas.addGameObject(this);
        animationThread.addSprite(sprite);
    }

    public double getX() {return x;}
    public double getY() {return y;}
    public Sprite getSprite() {return sprite;}
    public int getWidth() {return width;}
    public int getHeight() {return height;}
    public ArrayList<CollisionBox> getCollisionBoxes() {return collBoxes;}
    public boolean isColliding(CollisionBox other) {return collBoxes.get(0).isColliding(other);}
    public boolean isColliding(CollisionBox other, double offsetX, double offsetY) {return collBoxes.get(0).isColliding(other, offsetX, offsetY);}
    public int getZIndex() {return zIndex;}

    public void draw(Graphics2D g2d) {
        sprite.draw(g2d, scale, x, y);
    }
    
    public void destroy() {
        canvas.removeGameObject(this);
    }

    @Override
    public int compareTo(GameObject other) {
        return Integer.compare(this.getZIndex(), other.getZIndex());
    }
}
