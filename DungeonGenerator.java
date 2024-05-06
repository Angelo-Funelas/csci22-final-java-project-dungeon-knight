
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.awt.*;
import javax.imageio.ImageIO;
import java.util.Random;

public class DungeonGenerator {
    ArrayList<BufferedImage> mapList = new ArrayList<BufferedImage>();
    public DungeonGenerator() {
        ArrayList<File> spriteSrcs = new ArrayList<File>();
        spriteSrcs.add(new File("tiles/floor0.png"));
        spriteSrcs.add(new File("tiles/floor1.png"));
        spriteSrcs.add(new File("tiles/floor2.png"));
        spriteSrcs.add(new File("tiles/floorblock.png"));
        spriteSrcs.add(new File("tiles/spike.png"));
        spriteSrcs.add(new File("tiles/spikeretracted.png"));
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
    public BufferedImage GenerateBattleRoom() {
        int areaWidth = 368; // Example area width
        int areaHeight = 384; // Example area height
        BufferedImage combinedImage = new BufferedImage(areaWidth, areaHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = combinedImage.createGraphics();

        // Generate Floor
        for (int y = 1; y < 22; y++) {
            for (int x = 1; x < 22; x++) {
                int randomSpriteIndex;
                Random random = new Random();
                randomSpriteIndex = random.nextInt(2 + 1);
                BufferedImage randomSprite = mapList.get(randomSpriteIndex);
                g2d.drawImage(randomSprite, x * 16, y * 16+7, null);
            }
        }
        // randomSpriteIndex = random.nextInt(7 - 6 + 1) + 6;
        // Generate Walls
        for (int y = 0; y < 23; y++) {
            for (int x = 0; x < 23; x++) {
                int randomSpriteIndex;
                Random random = new Random();
                randomSpriteIndex = random.nextInt(7 - 6 + 1) + 6;
                BufferedImage randomSprite = mapList.get(randomSpriteIndex);
                if (y>0&&y<22) {
                    g2d.drawImage(randomSprite, 0, y*16, null);
                    g2d.drawImage(randomSprite, 22*16, y*16, null);
                    break;
                } else {
                    g2d.drawImage(randomSprite, x * 16, y * 16, null);
                }
            }
        }

        g2d.dispose();
        return combinedImage;
    }
}
