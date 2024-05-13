public interface Entity {
    public void update(long dt, Map curMap);
    public void destroy();
    public int getTeam();
    boolean isColliding(CollisionBox other);
    public double damage(double damage);
    public void setDx(double dx);
    public void setDy(double dy);
}
