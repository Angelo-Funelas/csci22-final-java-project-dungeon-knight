import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

public class DungeonRoom implements GameObject, DungeonPiece {
    BufferedImage image;
    int tileWidth, tileHeight, width, height;
    int x,y,zIndex;
    ArrayList<CollisionBox> collBoxes = new ArrayList<CollisionBox>();

    public double getX() {return x;}
    public double getY() {return y;}

    public DungeonRoom(BufferedImage img, int zIndex,int tileWidth, int tileHeight, int x, int y, ArrayList<CollisionBox> collBoxes) {
        image = img;
        this.zIndex = zIndex;
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        width = img.getWidth();
        height = img.getHeight();
        this.x = x;
        this.y = y-8;
        for (CollisionBox box : collBoxes) {
            box.setX(box.getX() + this.x);
            box.setY(box.getY() + this.y);
        }
        this.collBoxes = collBoxes;
    }
    public void draw(Graphics2D g2d) {
        g2d.drawImage(image, x,y, width, height, null);
        if (GameStarter.debugMode) {
            for (CollisionBox box : collBoxes) {
                box.draw(g2d);
            }
        }
    }

    public int[] getRoomCenter() {
        int[] res = {x+(width/2), y+(height/2)};
        return res;
    } 

    public ArrayList<CollisionBox> getCollisionBoxes() {
        return collBoxes;
    }

    public int getWidth() {return width;}
    public int getHeight() {return height;}

    public void setX(int x) {this.x = x;}
    public void setY(int y) {this.y = y;}

    public int getHallLength() {
        return (27-this.tileHeight)/2;
    }

    public boolean isColliding(CollisionBox other) {return false;}
    public int getZIndex() {
        return zIndex;
    }
    @Override
    public int compareTo(GameObject other) {
        return Integer.compare(this.getZIndex(), other.getZIndex());
    }
    public void attach(String direction, int tileWidth, int tileHeight, Random random, GameCanvas canvas, Map map) {
        DungeonHallway hallway;
        DungeonRoom room;
        int newHallLength = (27-tileHeight)/2;
        int hallLength = 16+this.getHallLength()+newHallLength;
        int hallwayX, hallwayY, roomX, roomY;
        switch (direction) {
            case "left": 
                hallwayX = x - hallLength*16;
                hallwayY = (y+8) + height/2 -4*16;
                
                roomX = x - 16*(hallLength+tileWidth);
                roomY = hallwayY - ((tileHeight-7)*16/2);

                hallway = DungeonGenerator.GenerateHallway(hallwayX, hallwayY, hallLength, 7, random, false);
                room = DungeonGenerator.GenerateBattleRoom(roomX, roomY, tileWidth, tileHeight, false,false,false,true, random); 
                break;
            case "right":
                hallwayX = x + width;
                hallwayY = (y+8) + height/2 -4*16;
                
                roomX = hallwayX + hallLength*16;
                roomY = hallwayY - ((tileHeight-7)*16/2);

                hallway = DungeonGenerator.GenerateHallway(hallwayX, hallwayY, hallLength, 7, random, false);
                room = DungeonGenerator.GenerateBattleRoom(roomX, roomY, tileWidth, tileHeight, false,false,true,false, random); 
                break;
            case "up":
                hallwayX = x + width/2 - 4*16 + 8;
                hallwayY = (y+8) - hallLength*16;
                
                roomX = hallwayX - ((tileWidth-7)*16/2);
                roomY = (y+8) - 16*(hallLength+tileHeight);

                hallway = DungeonGenerator.GenerateHallway(hallwayX, hallwayY, 7, hallLength, random, true);
                room = DungeonGenerator.GenerateBattleRoom(roomX, roomY, tileWidth, tileHeight, false,true,false,false, random); 
                break;
            case "down":
                hallwayX = x + width/2 - 4*16 + 8;
                hallwayY = (y+8) + this.tileHeight*16;
                
                roomX = hallwayX - ((tileWidth-7)*16/2);
                roomY = (y+8) + 16*(this.tileHeight+hallLength);

                hallway = DungeonGenerator.GenerateHallway(hallwayX, hallwayY, 7, hallLength, random, true);
                room = DungeonGenerator.GenerateBattleRoom(roomX, roomY, tileWidth, tileHeight, true,false,false,false, random); 
                break;
            default:
                hallway = DungeonGenerator.GenerateHallway(0, 0, 0, 0, random, false);
                room = DungeonGenerator.GenerateBattleRoom(0, 0, 0, 0, false,false,false,false, random); 
            }
        map.addPiece(hallway);
        canvas.addGameObject(hallway);
        map.addPiece(room);
        canvas.addGameObject(room);
    }
}
