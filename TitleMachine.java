import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

public class TitleMachine {
    private int screenWidth, screenHeight;
    private final Color menuColor = new Color(22,31,59,255);
    private final Color textColor = new Color(255,255,255,255);
    private String state;

    public TitleMachine(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        state = "connecting";
    }

    public void setState(String state) {
        this.state = state;
    }

    public void draw(Graphics2D g2d) {
        g2d.setFont(new Font("Impact", Font.PLAIN, 40)); 
        switch (state) {
            case "connecting":
                g2d.setPaint(menuColor);
                g2d.fillRect(0, 0, screenWidth, screenHeight);
                g2d.setPaint(textColor);
                g2d.drawString("Waiting for server...", screenWidth/2-160, screenHeight/2);
                break;
            case "game":
                break;
        }
    }
}
