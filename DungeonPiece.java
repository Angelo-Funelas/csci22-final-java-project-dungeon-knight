import java.util.ArrayList;

public abstract class DungeonPiece {
    ArrayList<CollisionBox> collBoxes = new ArrayList<CollisionBox>();
 
    public ArrayList<CollisionBox> getCollisionBoxes() {
        return collBoxes;
    }
}
