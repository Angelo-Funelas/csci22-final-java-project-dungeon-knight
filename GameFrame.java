import java.awt.event.*;
import java.awt.image.BufferedImage;

import javax.swing.*;

public class GameFrame implements KeyListener {
    private JFrame frame;
    static int canvasWidth = 800;
    static int canvasHeight = 600;
    private Player player;
    private GameCanvas canvas;
    private AnimationThread AnimationThread;
    private long lastFrameTime;
    DungeonPiece dungeon1;

    public GameFrame() {
        frame = new JFrame();
    }

    public void setUpGUI() {
        canvas = new GameCanvas(canvasWidth,canvasHeight);
        frame.setSize(canvasWidth,canvasHeight);
        frame.setTitle("Final Project - Dungeon Knight");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.add(canvas);
        frame.pack();
        frame.addKeyListener(this);
        frame.setFocusable(true);
        frame.addWindowFocusListener(new WindowAdapter() {
            public void windowLostFocus(WindowEvent e) {
                player.setUp(false);
                player.setDown(false);
                player.setLeft(false);
                player.setRight(false);
            }
        });
    }

    public void startGameLoop() {
        lastFrameTime = System.currentTimeMillis();
        Timer gameTicker = new Timer(30, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                tick();
            }
        });
        gameTicker.start();
    }

    public void startAnimationThread() {
        AnimationThread = new AnimationThread();
        AnimationThread.start();
    }

    public void initPlayer() {
        player = new Player(26);
        canvas.addGameObject(player);
        AnimationThread.addSprite(player.getSprite());
        canvas.focus(player);
    }

    public void prepareLevel() {
        DungeonGenerator dg = new DungeonGenerator();
        dungeon1 = dg.GenerateBattleRoom(23,23,0,0,0,0);
        canvas.addGameObject(dungeon1);
    }

    public void tick() {
        long currentTime = System.currentTimeMillis();
        long deltaTime = currentTime - lastFrameTime;

        player.update(deltaTime);
        if (player.isColliding(dungeon1)) {
            System.out.println("colliding");
        }

        lastFrameTime = currentTime;
    }

    @Override
    public void keyTyped(KeyEvent e) {}
    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
        if (code == KeyEvent.VK_W) {player.setUp(false);}
        if (code == KeyEvent.VK_S) {player.setDown(false);}
        if (code == KeyEvent.VK_A) {player.setLeft(false);}
        if (code == KeyEvent.VK_D) {player.setRight(false);}
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        if (code == KeyEvent.VK_W) {player.setUp(true);}
        if (code == KeyEvent.VK_S) {player.setDown(true);}
        if (code == KeyEvent.VK_A) {player.setLeft(true);}
        if (code == KeyEvent.VK_D) {player.setRight(true);}
    }
}