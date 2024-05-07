
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.awt.*;
import javax.imageio.ImageIO;
import java.util.Random;

public class DungeonGenerator {
    private static ArrayList<BufferedImage> mapList = new ArrayList<BufferedImage>();

    private DungeonGenerator() {} // make an empty private constructor to prevent it from being instantiated

    static { // initialize sprites
        ArrayList<File> spriteSrcs = new ArrayList<File>();
        spriteSrcs.add(new File("tiles/floor0.png"));
        spriteSrcs.add(new File("tiles/floor1.png"));
        spriteSrcs.add(new File("tiles/floor2.png"));
        spriteSrcs.add(new File("tiles/floorblock.png"));
        spriteSrcs.add(new File("tiles/spikeretracted.png"));
        spriteSrcs.add(new File("tiles/spike.png"));
        spriteSrcs.add(new File("tiles/stonewall.png"));
        spriteSrcs.add(new File("tiles/wall.png"));
        for (File src : spriteSrcs) {
            try {
                mapList.add(ImageIO.read(src));
            } catch (IOException ex) {
                System.out.println("Can't find tiles");
            }
        }
    } // bridges - 21, 2 small - 26
    public static DungeonPiece GenerateBattleRoom(int gridX, int gridY, int tileWidth, int tileHeight, boolean doorUp, boolean doorDown, boolean doorLeft, boolean doorRight) {
        int doorTileWidth = 5;
        int doorWidth = doorTileWidth*16;
        int areaWidth = tileWidth*16;
        int areaHeight = (tileHeight*16)+16;
        BufferedImage combinedImage = new BufferedImage(areaWidth, areaHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = combinedImage.createGraphics();
        ArrayList<CollisionBox> collBoxes = new ArrayList<CollisionBox>();

        // Generate Floor
        for (int y = 1; y < tileHeight-1; y++) {
            for (int x = 1; x < tileWidth-1; x++) {
                int randomSpriteIndex;
                Random random = new Random();
                randomSpriteIndex = random.nextInt(2 + 1);
                BufferedImage randomSprite = mapList.get(randomSpriteIndex);
                g2d.drawImage(randomSprite, x * 16, y * 16+7, null);
            }
        }
        // randomSpriteIndex = random.nextInt(7 - 6 + 1) + 6;
        // Generate Walls
        for (int y = 0; y < tileHeight; y++) {
            for (int x = 0; x < tileWidth; x++) {
                int randomSpriteIndex;
                Random random = new Random();
                randomSpriteIndex = random.nextInt(7 - 6 + 1) + 6;
                BufferedImage randomSprite = mapList.get(randomSpriteIndex);
                BufferedImage retractedSpike = mapList.get(4);
                if (y>0&&y<tileWidth-1) {
                    // draw side walls
                    if (
                        (y >= Math.floor(tileHeight/2-doorTileWidth/2)
                        && y <= Math.floor(tileHeight/2+doorTileWidth/2))
                    ) {
                        if (!doorLeft) {
                            g2d.drawImage(randomSprite, 0, y*16, null);
                        } else {
                            g2d.drawImage(retractedSpike, 0, y*16+7, null);
                        }
                        if (!doorRight) {
                            g2d.drawImage(randomSprite, (tileWidth-1)*16, y*16, null);
                        } else {
                            g2d.drawImage(retractedSpike, (tileWidth-1)*16, y*16+7, null);
                        }
                    } else {
                        g2d.drawImage(randomSprite, 0, y*16, null);
                        g2d.drawImage(randomSprite, (tileWidth-1)*16, y*16, null);
                    }
                    break;
                } else {
                    // draw top and bottom walls
                    if (
                        !(x >= Math.floor(tileWidth/2-doorTileWidth/2)
                        && x <= Math.floor(tileWidth/2+doorTileWidth/2))
                        || (!doorUp && y==0)
                        || (!doorDown && y==tileHeight-1)
                    ) {
                        g2d.drawImage(randomSprite, x * 16, y * 16, null);
                    } else {
                        g2d.drawImage(retractedSpike, x * 16, y * 16+7, null);
                    }
                }
            }
        }

        int wallThickness = 12;
        // generate collision boxes
        if (!doorUp) {
            collBoxes.add(new CollisionBox(0,0,areaWidth,wallThickness));
        } else {
            collBoxes.add(new CollisionBox(0,0,areaWidth/2-doorWidth/2,wallThickness));
            collBoxes.add(new CollisionBox(areaWidth/2+doorWidth/2,0,areaWidth/2-doorWidth/2,wallThickness));
        }
        if (!doorDown) {
            collBoxes.add(new CollisionBox(0,(tileHeight*16)-wallThickness,areaWidth,wallThickness));
        } else {
            collBoxes.add(new CollisionBox(0,(tileHeight*16)-wallThickness,areaWidth/2-doorWidth/2,wallThickness));
            collBoxes.add(new CollisionBox(areaWidth/2+doorWidth/2,(tileHeight*16)-wallThickness,areaWidth/2-doorWidth/2,wallThickness));
        }

        int wallHeight = (areaHeight-16-wallThickness*2)/2-(doorWidth/2); 
        if (!doorLeft) {
            CollisionBox wallTop = new CollisionBox(0,wallThickness,wallThickness,areaHeight-(16+wallThickness+wallThickness));
            collBoxes.add(wallTop);
        } else {
            CollisionBox wallTopT = new CollisionBox(0,wallThickness,wallThickness, wallHeight);
            CollisionBox wallTopB = new CollisionBox(0,wallThickness+wallHeight+doorWidth,wallThickness, wallHeight);
            collBoxes.add(wallTopT);
            collBoxes.add(wallTopB);
        }
        if (!doorRight) {
            CollisionBox wallTop = new CollisionBox(areaWidth-wallThickness,wallThickness,wallThickness,areaHeight-(16+wallThickness*2));
            collBoxes.add(wallTop);
        } else {
            CollisionBox wallTopT = new CollisionBox(areaWidth-wallThickness,wallThickness,wallThickness, wallHeight);
            CollisionBox wallTopB = new CollisionBox(areaWidth-wallThickness,wallThickness+wallHeight+doorWidth,wallThickness, wallHeight);
            collBoxes.add(wallTopT);
            collBoxes.add(wallTopB);
        }

        g2d.dispose();
        DungeonPiece dungeon1Obj = new DungeonPiece(combinedImage, 1, areaWidth, areaHeight, gridX, gridY, collBoxes);
        return dungeon1Obj;
    }
}
