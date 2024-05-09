import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Map {
    private ArrayList<DungeonPiece> mapPieces;
    private GameCanvas canvas;
    private int[] startingPos;

    public ArrayList<CollisionBox> getCollisionBoxes() {
        ArrayList<CollisionBox> res = new ArrayList<CollisionBox>();
        for (DungeonPiece piece : mapPieces) {
            res.addAll(piece.getCollisionBoxes());
        }
        return res;
    }

    public Map(int gridRods, int gridHeight, GameCanvas canvas, int seed) {
        this.canvas = canvas;
        mapPieces = new ArrayList<DungeonPiece>();
        Random random = new Random(seed);
        int doorOpening = random.nextInt(4);
        boolean[] doors = new boolean[4];
        Arrays.fill(doors, false);
        doors[doorOpening] = true;

        DungeonPiece starterRoom = DungeonGenerator.GenerateBattleRoom(0-(17*16/2), 0-(17*16/2), 17, 17, doors[0],doors[1],doors[2],doors[3], random);
        startingPos = starterRoom.getRoomCenter();
        addPiece(starterRoom);
        this.canvas.addGameObject(starterRoom);
    }

    public int[] getStartingPos() {
        return startingPos;
    }

    public void addPiece(DungeonPiece piece) {
        mapPieces.add(piece);
    }
}
