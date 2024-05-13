
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.awt.*;
import javax.imageio.ImageIO;
import java.util.Random;

public class DungeonGenerator {
    private static ArrayList<BufferedImage> mapList = new ArrayList<BufferedImage>();
    private static int wallThickness = 12;

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
    }

    public static BufferedImage getRandomSprite(Random r, int min, int max) {
        return mapList.get(r.nextInt(max - min + 1)+min);
    }

    public static DungeonRoom GenerateBattleRoom(int gridX, int gridY, int tileWidth, int tileHeight, boolean doorUp, boolean doorDown, boolean doorLeft, boolean doorRight, Random random) {
        int doorTileWidth = 5;
        int doorWidth = doorTileWidth*16;
        int areaWidth = tileWidth*16;
        int areaHeight = (tileHeight*16)+16;
        BufferedImage combinedImage = new BufferedImage(areaWidth, areaHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = combinedImage.createGraphics();
        ArrayList<CollisionBox> collBoxes = new ArrayList<CollisionBox>();
        BufferedImage retractedSpike = mapList.get(4);
        BufferedImage randomSprite;

        // Generate Floor
        for (int y = 0; y < tileHeight; y++) {
            for (int x = 0; x < tileWidth; x++) {
                int randomSpriteIndex;
                randomSpriteIndex = random.nextInt(2 + 1);
                randomSprite = mapList.get(randomSpriteIndex);
                if ((x > 0 && x < tileWidth-1) && (y > 0 && y < tileHeight-1)) {
                    g2d.drawImage(randomSprite, x * 16, y * 16+8, null);
                } else if ((x==0 || x==tileWidth-1) && (y >= Math.floor(tileHeight/2-doorTileWidth/2) && y <= Math.floor(tileHeight/2+doorTileWidth/2))) {
                    if (doorLeft||doorRight) {
                        g2d.drawImage(retractedSpike, x*16, y*16+8, null);
                    }
                } else if ((y==0 || y==tileHeight-1) && (x >= Math.floor(tileWidth/2-doorTileWidth/2) && x <= Math.floor(tileWidth/2+doorTileWidth/2))) {
                    if (doorUp||doorDown) {
                        g2d.drawImage(retractedSpike, x*16, y*16+8, null);
                    }
                }
            }
        }

        // Generate Walls
        for (int y = 0; y < tileHeight; y++) {
            for (int x = 0; x < tileWidth; x++) {
                int randomSpriteIndex;
                randomSpriteIndex = random.nextInt(7 - 6 + 1) + 6;
                randomSprite = mapList.get(randomSpriteIndex);
                if (y>0&&y<tileWidth-1) {
                    // draw side walls
                    if (
                        (y >= Math.floor(tileHeight/2-doorTileWidth/2)
                        && y <= Math.floor(tileHeight/2+doorTileWidth/2))
                    ) {
                        if (!doorLeft) {
                            g2d.drawImage(randomSprite, 0, y*16, null);
                        }
                        if (!doorRight) {
                            g2d.drawImage(randomSprite, (tileWidth-1)*16, y*16, null);
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
                    }
                }
            }
        }

        // generate collision boxes
        if (!doorUp) {
            collBoxes.add(new CollisionBox(0,0,areaWidth,wallThickness));
        } else {
            collBoxes.add(new CollisionBox(0,0,areaWidth/2-doorWidth/2-(16-wallThickness),wallThickness));
            collBoxes.add(new CollisionBox(areaWidth/2+doorWidth/2+(16-wallThickness),0,areaWidth/2-doorWidth/2-(16-wallThickness),wallThickness));
        }
        if (!doorDown) {
            collBoxes.add(new CollisionBox(0,(tileHeight*16)-wallThickness,areaWidth-(16-wallThickness),wallThickness));
        } else {
            collBoxes.add(new CollisionBox(0,(tileHeight*16)-wallThickness,areaWidth/2-doorWidth/2-(16-wallThickness),wallThickness));
            collBoxes.add(new CollisionBox(areaWidth/2+doorWidth/2+(16-wallThickness),(tileHeight*16)-wallThickness,areaWidth/2-doorWidth/2-(16-wallThickness),wallThickness));
        }

        int wallHeight = (areaHeight-16-wallThickness*2)/2-(doorWidth/2); 
        if (!doorLeft) {
            collBoxes.add(new CollisionBox(0,wallThickness,wallThickness,areaHeight-(16+wallThickness+wallThickness)));
        } else {
            collBoxes.add(new CollisionBox(0,wallThickness,wallThickness, wallHeight-4));
            collBoxes.add(new CollisionBox(0,wallThickness+wallHeight+doorWidth+4,wallThickness, wallHeight-4));
        }
        if (!doorRight) {
            collBoxes.add(new CollisionBox(areaWidth-wallThickness,wallThickness,wallThickness,areaHeight-(16+wallThickness*2)));
        } else {
            collBoxes.add(new CollisionBox(areaWidth-wallThickness,wallThickness,wallThickness, wallHeight-4));
            collBoxes.add(new CollisionBox(areaWidth-wallThickness,wallThickness+wallHeight+doorWidth+4,wallThickness, wallHeight-4));
        }
        g2d.dispose();
        DungeonRoom dungeon1Obj = new DungeonRoom(combinedImage, gridY, tileWidth, tileHeight, gridX, gridY, collBoxes);
        return dungeon1Obj;
    }

    public static DungeonHall GenerateHallway(int gridX, int gridY, int tileWidth, int tileHeight, Random random, boolean vertical) {
        int areaWidth = tileWidth*16;
        int areaHeight = (tileHeight*16)+16;
        BufferedImage combinedImage;
        ArrayList<CollisionBox> collBoxes = new ArrayList<CollisionBox>();
        BufferedImage randomSprite;

        combinedImage = new BufferedImage(areaWidth, areaHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = combinedImage.createGraphics();

        // Generate Floor
        int hLoop, wLoop;
        hLoop = tileHeight;
        wLoop = tileWidth;
        if (vertical) {
            wLoop -= 1;
        } else {
            hLoop -= 1;
        }
        for (int y = vertical?0:1; y < hLoop; y++) {
            for (int x = vertical?1:0; x < wLoop; x++) {
                randomSprite = getRandomSprite(random,0,2);
                g2d.drawImage(randomSprite, x * 16, y * 16+8, null);
            }
        }
        // Generate Walls
        if (vertical) {
            for (int y = 0; y<tileHeight; y++) {
                randomSprite = getRandomSprite(random,6,7);
                g2d.drawImage(randomSprite, 0, y * 16, null);
                randomSprite = getRandomSprite(random,6,7);
                g2d.drawImage(randomSprite, (tileWidth-1)*16, y * 16, null);
            }
            collBoxes.add(new CollisionBox(0,0,wallThickness,areaHeight-16));
            collBoxes.add(new CollisionBox(tileWidth*16-wallThickness,0,wallThickness,areaHeight-16));
        } else {
            for (int x = 0; x<tileWidth; x++) {
                randomSprite = getRandomSprite(random,6,7);
                g2d.drawImage(randomSprite, x * 16, 0, null);
                randomSprite = getRandomSprite(random,6,7);
                g2d.drawImage(randomSprite, x * 16, (tileHeight-1)*16, null);
            }
            collBoxes.add(new CollisionBox(0,0,areaWidth,wallThickness));
            collBoxes.add(new CollisionBox(0,(tileHeight*16)-wallThickness,areaWidth,wallThickness));
        }


        g2d.dispose();
        return new DungeonHall(combinedImage, gridY, areaWidth, areaHeight, gridX, gridY, collBoxes);
    }
}
