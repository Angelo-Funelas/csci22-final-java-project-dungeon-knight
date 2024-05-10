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

    private class mapModelPiece {
        private boolean[] doors;
        private int tileWidth, tileHeight;

        public mapModelPiece(boolean[] doors) {
            this.doors = doors;
        }
    }

    public Map(int gridRods, int gridHeight, GameCanvas canvas, int seed) {
        this.canvas = canvas;
        mapPieces = new ArrayList<DungeonPiece>();
        Random random = new Random(seed);

        boolean[] doors = new boolean[4];
        int doorOpening = random.nextInt(4);
        Arrays.fill(doors, false);
        doors[doorOpening] = true;

        DungeonRoom starterRoom = DungeonGenerator.GenerateBattleRoom(-(16*16/2), -(16*16/2), 17, 17, true,true,true,true, random);
        startingPos = starterRoom.getRoomCenter();

        addPiece(starterRoom);
        this.canvas.addGameObject(starterRoom);
        // 16 between 27, 27, 17, 13

        ArrayList<mapModelPiece> mapModel = new ArrayList<mapModelPiece>();

        int rows = 4; // Number of rows
        int cols = 4; // Number of columns

        // Populate the grid with elements
        // for (int i = 0; i < rows; i++) {
        //     ArrayList<mapModelPiece> row = new ArrayList<>();
        //     for (int j = 0; j < cols; j++) {
        //         // Add a new Tile object to each position in the grid
        //         row.add(new Tile(/* Constructor arguments */));
        //     }
        //     grid.add(row);
        // }

        starterRoom.attach("left", 27,27, random, canvas, this);
        starterRoom.attach("right", 17,17, random, canvas, this);
        starterRoom.attach("up", 27,27, random, canvas, this);
        starterRoom.attach("down", 27,27, random, canvas, this);
    }

    public int[] getStartingPos() {
        return startingPos;
    }

    public void addPiece(DungeonPiece piece) {
        mapPieces.add(piece);
    }
}
