import java.awt.event.*;
import java.awt.image.BufferedImage;

import javax.swing.*;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;

public class GameFrame implements KeyListener {
    private JFrame frame;
    static int canvasWidth = 800;
    static int canvasHeight = 600;
    private GameCanvas canvas;
    private AnimationThread AnimationThread;
    private long lastFrameTime;
    private Map curMap;
    private Player player;
    private ArrayList<Player> allies;
    private HashMap<Integer, Player> clients;

    private Socket socket;
    private int clientID;
    private ReadFromServer rfsRunnable;
    private WriteToServer wtsRunnable;
    private Thread readThread, writeThread;

    public GameFrame() {
        frame = new JFrame();
    }

    private void connectToServer() {
        try {
            socket = new Socket("localhost", 55555);
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            rfsRunnable = new ReadFromServer(in);
            wtsRunnable = new WriteToServer(out);

            readThread = new Thread(rfsRunnable);
            writeThread = new Thread(wtsRunnable);
            readThread.start();
            writeThread.start();

            allies = new ArrayList<Player>();
            clients = new HashMap<Integer, Player>();
        } catch (IOException ex) {
            System.out.println("IOException from connectToServer()");
        }
    }

    private class ReadFromServer implements Runnable {
        private DataInputStream dataIn;

        public ReadFromServer(DataInputStream in) {
            dataIn = in;
        }

        public void run() {
            while (true) {
                try {
                    String command = dataIn.readUTF();
                    if (command.startsWith("com_")) {
                        switch (command) {
                            case "com_setID":
                                clientID = dataIn.readInt();
                                System.out.println("Connected to server as Client #" + clientID);
                                break;
                            case "com_newClient":
                                int newClientId = dataIn.readInt();
                                if (newClientId != clientID) {
                                    Player ally = new Player(26);
                                    canvas.addGameObject(ally);
                                    AnimationThread.addSprite(ally.getSprite());
                                    clients.put(newClientId, ally);
                                }
                                break;
                            case "com_setAllyPos":
                                int targetID = dataIn.readInt();
                                double data_x = dataIn.readDouble();
                                double data_y = dataIn.readDouble();
                                System.out.println(data_x + " " + data_y);
                                if (targetID!=clientID) {
                                    Player targetAlly = clients.get(targetID);
                                    if (targetAlly != null) {
                                        targetAlly.setX(data_x);
                                        targetAlly.setY(data_y);
                                    }
                                }
                                break;
                        }
                    }
                } catch (IOException ex) {
                    System.out.println("IOException from ReadFromServer Thread");
                }
            }
        }
    }

    private class WriteToServer implements Runnable {
        private DataOutputStream dataOut;

        public WriteToServer(DataOutputStream out) {
            dataOut = out;
        }

        public void setPos() {
            try {
                dataOut.writeUTF("setPos");
                dataOut.writeDouble(player.getX());
                dataOut.writeDouble(player.getY());
            } catch (IOException ex) {
                System.out.println("IOException at setPos");
            } 
        }

        public void run() {
            while (true) {
                setPos();
                try {
                    Thread.sleep(25); // some delay for writing data
                }
                catch (InterruptedException ex) {
                    System.out.println("InterruptedException from WTS run()");
                }
            }
        }
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
        Timer gameTicker = new Timer(1000/GameStarter.framerate, new ActionListener() {
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
        connectToServer();
    }

    public void prepareLevel() {
        curMap = new Map(3, 3, canvas);
        int[] startingPos = curMap.getStartingPos();
        player.setX(startingPos[0]);
        player.setY(startingPos[1]);
    }

    public void tick() {
        long currentTime = System.currentTimeMillis();
        long deltaTime = currentTime - lastFrameTime;

        player.update(deltaTime, curMap);

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