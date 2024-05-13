import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Map {
    private ArrayList<DungeonPiece> mapPieces;
    private GameCanvas canvas;
    private final String[] dirs = {"up","down","left","right"};
    private boolean endRoomGenerated;

    public Map(int gridRods, int gridHeight, GameCanvas canvas, int seed) {
        this.canvas = canvas;
        mapPieces = new ArrayList<DungeonPiece>();
        Random random = new Random(seed);
        endRoomGenerated = false;

        boolean[] doors = new boolean[4];
        int doorOpening = random.nextInt(4);
        Arrays.fill(doors, false);
        doors[doorOpening] = true;

        int maxDepth = 3;
        int gridSize = 1+maxDepth*2;
        ArrayList<ArrayList<mapModelPiece>> mapGrid = new ArrayList<ArrayList<mapModelPiece>>();

        // Populate the grid with null values
        for (int i = 0; i < gridSize; i++) {
            ArrayList<mapModelPiece> row = new ArrayList<>(gridSize);
            for (int j = 0; j < gridSize; j++) {
                row.add(null);
            }
            mapGrid.add(row);
        }

        mapModelPiece rootRoom = new mapModelPiece();
        mapGrid.get(maxDepth).set(maxDepth, rootRoom);

        rootRoom.generatePaths(random, 0, maxDepth, mapGrid, maxDepth, maxDepth);
        rootRoom.generatePieces(null, random, 0, maxDepth, this);
    }

    public void destroy() {
        for (DungeonPiece piece : mapPieces) {
            piece.destroy(canvas);
        }
        mapPieces.clear();
    }

    public void addPiece(DungeonPiece piece) {
        mapPieces.add(piece);
    }

    public ArrayList<CollisionBox> getCollisionBoxes() {
        ArrayList<CollisionBox> res = new ArrayList<CollisionBox>();
        for (DungeonPiece piece : mapPieces) {
            res.addAll(piece.getCollisionBoxes());
        }
        return res;
    }

    private class mapModelPiece {
        private ArrayList<mapModelPiece> paths;

        public mapModelPiece() {
            paths = new ArrayList<mapModelPiece>(Arrays.asList(null, null, null, null));
        }

        public ArrayList<mapModelPiece> getPaths() {
            return paths;
        }

        public mapModelPiece addPath(int dir) {
            mapModelPiece newRoom = new mapModelPiece();
            paths.set(dir, newRoom);
            return newRoom;
        }
        public int getRandomDir(Random r, ArrayList<ArrayList<mapModelPiece>> mapGrid, int x, int y) {
            ArrayList<Integer> availableDirs = new ArrayList<Integer>();

            for (int i = 0; i < 4; i++) {
                int newX = x+((i==2)?-1:(i==3)?1:0);
                int newY = y+((i==0)?-1:(i==1)?1:0);
                if ((mapGrid.size()>newX&&newX>=0)&&(mapGrid.size()>newY&&newY>=0)&&(mapGrid.get(newX).get(newY)==null)) { // if within grid
                    availableDirs.add(i);
                }
            }
            if (availableDirs.size()>0) {
                return availableDirs.get(r.nextInt(availableDirs.size()));
            } else {
                return -1;
            }
        }
        
        public void generatePaths(Random r, int depth, int maxDepth, ArrayList<ArrayList<mapModelPiece>> mapGrid, int x, int y) {
            if (depth < maxDepth) {
                int roomCount = r.nextInt(3) + 1;

                if (depth == 0) {
                    roomCount = 1;
                }
                System.err.println("Generating: "+roomCount+" rooms");
                for (int i = 0; i<roomCount; i++) {
                    int randomDir = getRandomDir(r, mapGrid, x, y);
                    if (randomDir == -1) {break;}
                    mapModelPiece newRoomModel = addPath(randomDir);
                    int newX = x+((randomDir==2)?-1:(randomDir==3)?1:0);
                    int newY = y+((randomDir==0)?-1:(randomDir==1)?1:0);
                    System.out.println("adding path " + dirs[randomDir]);
                    mapGrid.get(newX).set(newY, newRoomModel);
                    newRoomModel.generatePaths(r, depth+1, maxDepth, mapGrid, newX ,newY);
                }
            }
        }

        public void generatePieces(DungeonRoom parentRoom, Random r, int depth, int maxDepth, Map map) {
            int[] roomSizes = {17, 27};
            boolean[] rmDoors = {false, false, false, false};
            ArrayList<Integer> availablePaths = new ArrayList<Integer>();
            DungeonRoom rootRoom;

            if (depth<maxDepth) {
                for (int i=0;i<paths.size();i++) {

                    mapModelPiece pathRoom = paths.get(i);
                    if (pathRoom!=null) {
                        rmDoors[i] = true;
                        availablePaths.add(i);
                    }
                }
            }

            if (parentRoom==null) {
                rootRoom = DungeonGenerator.GenerateBattleRoom(-(16*16/2), -(16*16/2), 17, 17, rmDoors[0],rmDoors[1],rmDoors[2],rmDoors[3], r);
                addPiece(rootRoom);
                canvas.addGameObject(rootRoom);
            } else {
                rootRoom = parentRoom;
            }
            int endRoom = -1;
            if (depth == maxDepth-1 && !endRoomGenerated) {
                endRoom = availablePaths.get(r.nextInt(availablePaths.size()));
                endRoomGenerated = true;
                System.out.println("endroom: "+endRoom);
            }
            for (Integer i : availablePaths) {
                if (rmDoors[i]) {
                    int roomSize;
                    if (i==endRoom) {
                        roomSize = 13;
                    } else {
                        roomSize = roomSizes[r.nextInt(roomSizes.length)];
                    }

                    boolean[] newRmDoors = {false, false, false, false};
                    ArrayList<mapModelPiece> newRmPaths = paths.get(i).getPaths();
                    for (int j=0; j<4;j++) {
                        if (newRmPaths.get(j)!=null) {
                            newRmDoors[j] = true;
                        }
                    }

                    DungeonRoom newRoom = rootRoom.attach(dirs[i], roomSize, roomSize, newRmDoors, r, canvas, map);
                    System.out.println("attaching room " + dirs[i]);
                    paths.get(i).generatePieces(newRoom, r, depth+1, maxDepth, map);
                }
            }
        }
    }
}
