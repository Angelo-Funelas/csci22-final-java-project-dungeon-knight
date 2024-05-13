import java.awt.Graphics2D;

public abstract class Weapon {
    double angle;
    long lastAttackTime;

    public double getAngle() {return angle;}
    public void setAngle(double angle) {}
    public void draw(Graphics2D g2d) {}
    public void update(long dt, Map curMap) {}
    public void attack() {}
}
