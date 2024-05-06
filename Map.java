import java.util.ArrayList;

public class Map {
    static ArrayList<DungeonPiece> mapPieces = new ArrayList<DungeonPiece>();

    public static ArrayList<CollisionBox> getCollisionBoxes() {
        ArrayList<CollisionBox> res = new ArrayList<CollisionBox>();
        for (DungeonPiece piece : mapPieces) {
            res.addAll(piece.getCollisionBoxes());
        }
        return res;
    }

    public static void addPiece(DungeonPiece piece) {
        mapPieces.add(piece);
    }
}
