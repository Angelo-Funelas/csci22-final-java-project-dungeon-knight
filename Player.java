
import java.io.*;
import java.util.ArrayList;
import java.awt.*;

public class Player implements GameObject, Entity {
    private Sprite sprite;
    private int scale, width, height, zIndex,curWeapon_i, score;
    private double x,y,dx,dy,rdx,rdy,lastx,lasty,health;
    private long lastPing;
    private boolean moveUp, moveDown, moveLeft, moveRight, ally;
    private double acceleration, friction, maxSpeed;
    private ArrayList<CollisionBox> collBoxes;
    private Color debugColor = new Color(255, 0, 0, 255);
    private ArrayList<Weapon> weapons;
    private GameCanvas canvas;
    private GameFrame frame;
    private ArrayList<Entity> entities;
    private final int team = 0;
    private long dt;
    private GameSound gs;

    public Player(int scale, int x, int y, boolean ally, GameCanvas canvas, GameFrame frame, AnimationThread animationThread, ArrayList<Entity> entities, GameSound gs) {
        ArrayList<File> sprite_frames = new ArrayList<File>();
        sprite_frames.add(new File("sprites/rogue_frame_0.png"));
        sprite_frames.add(new File("sprites/rogue_frame_1.png"));
        sprite_frames.add(new File("sprites/rogue_frame_2.png"));
        sprite_frames.add(new File("sprites/rogue_frame_3.png"));
        sprite_frames.add(new File("sprites/rogue_frame_4.png"));
        sprite_frames.add(new File("sprites/rogue_frame_5.png"));
        sprite_frames.add(new File("sprites/rogue_frame_6.png"));
        sprite_frames.add(new File("sprites/rogue_frame_7.png"));
        sprite = new Sprite(sprite_frames, -1);
        this.scale = scale;
        this.ally = ally;
        this.canvas = canvas;
        this.x = x;
        this.y = y;
        dx = 0;
        dy = 0;
        acceleration = 30;
        maxSpeed = 200;
        friction = 0.8;
        zIndex = 1000000;
        if (!ally) {zIndex+=10;}
        width = sprite.getWidth();
        height = sprite.getHeight();
        collBoxes = new ArrayList<CollisionBox>();
        CollisionBox collBox = new CollisionBox(x, y, width-8, height-6);
        collBoxes.add(collBox);
        weapons = new ArrayList<Weapon>();
        this.gs = gs;
        weapons.add(new RangedWeap("badPistol", this, ally, canvas, frame, entities, gs));
        curWeapon_i = 0;
        canvas.addGameObject(this);
        this.frame = frame;
        frame.addEntity(this);
        lastPing = System.currentTimeMillis();
        animationThread.addSprite(sprite);
    }

    public void ping() {lastPing = System.currentTimeMillis();}

    // getters and setters
    public double getX() {return x;}
    public double getY() {return y;}
    public double getlX() {return lastx;}
    public double getlY() {return lasty;}
    public double getDx() {return dx;}
    public double getDy() {return dy;}
    public double getRdx() {return rdx;}
    public double getRdy() {return rdy;}
    public void setX(double x) {this.x = x; lastx = x;}
    public void setY(double y) {this.y = y; lasty = y;}
    public void setDx(double dx) {this.dx = dx;}
    public void setDy(double dy) {this.dy = dy;}
    public void setRdx(double rdx) {this.rdx = rdx;}
    public void setRdy(double rdy) {this.rdy = rdy;}
    public Weapon getWeapon() {return weapons.get(curWeapon_i);}
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

    public void draw(Graphics2D g2d) {
        sprite.draw(g2d, scale, x,y);
        for (Weapon weapon : weapons) {
            weapon.draw(g2d);
        }
        if (GameStarter.debugMode) {
            g2d.setPaint(debugColor);
            g2d.drawString("dt: " + dt, (int)x, (int)y-14);
            g2d.drawString("X: " + (int)x, (int)x, (int)y-8);
            g2d.drawString("Y: " + (int)y, (int)x, (int)y-2);
            for (CollisionBox box : collBoxes) {
                box.draw(g2d);
            }
        }
    }

    public void update(long dt, Map curMap) {
        this.dt = dt;
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
            rdx = dx/dt;
            rdy = dy/dt;
            boolean collidedX = false;
            boolean collidedY = false;
            if (curMap!=null) {
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
            }
            if (!collidedX) {
                x += dx;
            }
            if (!collidedY) {
                y += dy;
            }
            if (curMap!=null && !curMap.isEnded() && isColliding(curMap.getPortal().getCollisionBoxes().get(0))) {
                curMap.end();
                ArrayList<emitArg> args = new ArrayList<emitArg>();
                frame.sendCommand("reachedEnd", args);
                curMap.getPortal().destroy();
                curMap.clearPortal();
                System.out.println("test");
            }
        } else {
            rdx *= friction;
            rdy *= friction;
            x += rdx*dt;
            y += rdy*dt;
            long currentTime = System.currentTimeMillis();
            if (currentTime-lastPing>5*1000) {
                destroy();
            }
        }
        collBoxes.get(0).setPos(x+4,y+5);
        for (Weapon weapon : weapons) {
            weapon.update(dt, curMap);
        }
    }

    public void destroy() {
        canvas.removeGameObject(this);
        frame.removeEntity(this);
    }

    @Override
    public int compareTo(GameObject other) {
        return Integer.compare(this.getZIndex(), other.getZIndex());
    }

}
