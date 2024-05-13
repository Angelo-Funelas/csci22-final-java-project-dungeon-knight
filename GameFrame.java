import java.awt.event.*;

import javax.sound.sampled.FloatControl;
import javax.swing.*;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;

public class GameFrame implements KeyListener {
    private JFrame frame;
    static int canvasWidth = 1067;
    static int canvasHeight = 600;
    private GameCanvas canvas;
    private AnimationThread AnimationThread;
    private long lastFrameTime;
    private Map curMap;
    private Player player;
    private ArrayList<Entity> entities, entityQueue, entityRemoveQueue;
    public static boolean addEntitySafe, connectedToServer;
    private HashMap<Integer, Player> clients;

    private Socket socket;
    private int clientID;
    private ReadFromServer rfsRunnable;
    private WriteToServer wtsRunnable;
    private Thread readThread, writeThread;
    private GameFrame thisframe;
    private DataInputStream dataInpStream;
    private DataOutputStream dataOutStream;
    private int lastSeed;
    private GameSound gs;

    public void initializeGame() {
        frame = new JFrame();
        entities = new ArrayList<Entity>();
        entityQueue = new ArrayList<Entity>();
        entityRemoveQueue = new ArrayList<Entity>();
        thisframe = this;
        addEntitySafe = true;
        connectedToServer = false;
        clientID = -1;
        lastSeed = 0;
        gs = new GameSound();
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
        gs.setFile(1);
        gs.play(-8.0f);
        gs.loop();
    }

    public void startAnimationThread() {
        AnimationThread = new AnimationThread();
        AnimationThread.start();
    }

    public void initPlayer() {
        player = new Player(26, 0,0,false, canvas, this, AnimationThread, entities, gs);
        canvas.focus(player);
        new GoblinGuard(canvas, this, 0, 0, AnimationThread);
        connectToServer();
    }
    public void prepareLevel(int seed) {
        curMap = new Map(3, 3, canvas, this, seed, AnimationThread);
        if (seed!=lastSeed) {
            player.setX(0);
            player.setY(0);
            lastSeed = seed;
        }
    }

    private void connectToServer() {
        try {
            socket = new Socket(GameStarter.host, 55555);
            DataInputStream dataInpStream = new DataInputStream(socket.getInputStream());
            DataOutputStream dataOutStream = new DataOutputStream(socket.getOutputStream());

            rfsRunnable = new ReadFromServer(dataInpStream);
            wtsRunnable = new WriteToServer(dataOutStream);

            dataOutStream.writeInt(clientID);
            
            readThread = new Thread(rfsRunnable);
            writeThread = new Thread(wtsRunnable);
            readThread.start();
            writeThread.start();

            clients = new HashMap<Integer, Player>();
            connectedToServer = true;
            canvas.setTM("game");
        } catch (IOException ex) {
            System.out.println("IOException from connectToServer()");
            canvas.setTM("connecting");
            try {
                Thread.sleep(1000); // some delay for writing data
            }
            catch (InterruptedException exc) {}
            connectToServer();
        }
    }

    public void newClient(int id, double x, double y) {
        System.out.println("Client #" + id + " joined!");
        int newClientId = id;
        if (newClientId != clientID) {
            Player ally = new Player(26, (int)x, (int)y, true, canvas, this, AnimationThread, entities, gs);
            clients.put(newClientId, ally);
        }
    }

    public void sendCommand(String command, ArrayList<emitArg> args) {
        if (wtsRunnable.isSendSafe()) {       
            wtsRunnable.sendCommand(command, args);
        }
    }

    
    public void disconnect() {
        try {
            if (dataInpStream != null) dataInpStream.close();
            if (dataOutStream != null) dataOutStream.close();
            
            // Interrupt read and write threads
            rfsRunnable.stopThread();
            wtsRunnable.stopThread();
            
            // Close the socket
            if (socket != null) socket.close();

        } catch (IOException ex) {
            System.out.println("IOException at disconnect()");
        }
    }

    private class ReadFromServer implements Runnable {
        private DataInputStream dataIn;
        private boolean readReady, running;
        private int consequentExceptions, maxExceptions;

        public ReadFromServer(DataInputStream in) {
            dataIn = in;
            readReady = true;
            running = true;
            maxExceptions = 3;
        }

        public void stopThread() {
            running = false;
        }

        public void run() {
            while (running) {
                if (readReady) {
                    try {
                        readReady = false;
                        String command = dataIn.readUTF();
                        if (command.startsWith("com_")) {
                            switch (command) {
                                case "com_setID":
                                    clientID = dataIn.readInt();
                                    System.out.println("Connected to server as Client #" + clientID);
                                    frame.setTitle(String.format("Final Project - Dungeon Knight (Client #%d)", clientID));
                                    break;
                                case "com_setSeed":
                                    int seed = dataIn.readInt();
                                    System.out.println("Generating map with seed " + seed);
                                    if (curMap!=null) {
                                        System.out.println("destroying old map");
                                        curMap.destroy();
                                    }
                                    prepareLevel(seed);
                                    break;
                                case "com_setAllyPos":
                                    int targetID = dataIn.readInt();
                                    double data_x = dataIn.readDouble();
                                    double data_y = dataIn.readDouble();
                                    double data_dx = dataIn.readDouble();
                                    double data_dy = dataIn.readDouble();
                                    int data_faceDir = dataIn.readInt();
                                    boolean data_isWalking = dataIn.readBoolean();
                                    double data_angle = dataIn.readDouble();
                                    // System.out.println(data_x + " " + data_y);
                                    // System.out.println(data_dx + " " + data_dy);
                                    if (targetID!=clientID) {
                                        Player targetAlly = clients.get(targetID);
                                        if (targetAlly != null) {
                                            targetAlly.ping();
                                            if (targetAlly.getlX()!=data_x||targetAlly.getlY()!=data_y||targetAlly.getWeapon().getAngle()!=data_angle) {
                                                targetAlly.setX(data_x);
                                                targetAlly.setY(data_y);
                                                targetAlly.setRdx(data_dx);
                                                targetAlly.setRdy(data_dy);
                                                if (data_faceDir == -1) {targetAlly.getSprite().faceLeft();} else {targetAlly.getSprite().faceRight();}
                                                targetAlly.getSprite().setWalking(data_isWalking);;
                                                targetAlly.getWeapon().setAngle(data_angle);
                                            }
                                        } else {
                                            newClient(targetID, data_x, data_y);
                                        }
                                    }
                                    break;
                                case "com_newBullet":
                                    String type = dataIn.readUTF();
                                    double x = dataIn.readDouble();
                                    double y = dataIn.readDouble();
                                    double dx = dataIn.readDouble();
                                    double dy = dataIn.readDouble();
                                    double angle = dataIn.readDouble();
                                    double dmg = dataIn.readDouble();
                                    
                                    new Bullet(type, x, y, dx, dy, angle, canvas, thisframe, entities, 0, dmg);
                                    break;
                            }
                        } else {
                            try {
                                while (dataIn.read() != -1) {}
                            } catch (IOException e) {}
                        }
                    } catch (IOException ex) {
                        System.out.println("IOException from ReadFromServer Thread " + ex);
                        consequentExceptions++;
                        try {Thread.sleep(1000);} catch (InterruptedException exc) {}
                        if (consequentExceptions>maxExceptions) {
                            disconnect();
                            connectToServer();
                        }
                    }
                    readReady = true;
                }
            }
        }
    }

    private class WriteToServer implements Runnable {
        private DataOutputStream dataOut;
        private boolean running, sendSafe;

        public WriteToServer(DataOutputStream out) {
            dataOut = out;
            running = true;
        }

        public void stopThread() {
            running = false;
        }

        public boolean isSendSafe() {
            return sendSafe;
        }
        
        public void sendCommand(String command, ArrayList<emitArg> args) {
            try {
                sendSafe = false;
                dataOut.writeUTF("com_"+command);
                for (emitArg arg : args) {
                    switch (arg.getType()) {
                        case "utf":
                            dataOut.writeUTF((String)arg.getValue());
                            break;
                        case "double":
                            dataOut.writeDouble((double)arg.getValue());
                            break;
                        case "int":
                            dataOut.writeInt((int)arg.getValue());
                            break;
                        case "boolean":
                            dataOut.writeBoolean((boolean)arg.getValue());
                            break;
                    }
                }
                dataOut.flush();
                sendSafe = true;
            } catch (IOException ex) {
                System.out.println("IOException at sendCommand");
            } 
        }

        public void setPos() {
            ArrayList<emitArg> args = new ArrayList<emitArg>();
            args.add(new emitArg("double", player.getX()));
            args.add(new emitArg("double", player.getY()));
            args.add(new emitArg("double", player.getRdx()));
            args.add(new emitArg("double", player.getRdy()));
            args.add(new emitArg("int", player.getSprite().getFaceDir()));
            args.add(new emitArg("boolean", player.getSprite().isWalkingBool()));
            args.add(new emitArg("double", player.getWeapon().getAngle()));
            sendCommand("setPos", args);
        }

        public void run() {
            while (running) {
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

    public void addEntity(Entity en) {
        if (addEntitySafe) {
            synchronized(entities) {
                entities.add(en);
            }
        } else {
            entityQueue.add(en);
        }
    }
    public void handleEntityQueue() {
        if (addEntitySafe) {
            for (Entity en : entityQueue) {
                addEntity(en);
            }
            entityQueue.clear();
        }
    }
    public void removeEntity(Entity en) {
        if (addEntitySafe) {
            synchronized(entities) {
                entities.remove(en);
            }
        } else {
            entityRemoveQueue.add(en);
        }
    }
    public void handleEntityRemoveQueue() {
        if (addEntitySafe) {
            for (Entity en : entityRemoveQueue) {
                entities.remove(en);
            }
            entityRemoveQueue.clear();
        }
    }

    public void tick() {
        long currentTime = System.currentTimeMillis();
        long deltaTime = currentTime - lastFrameTime;
        synchronized(entities) {
            addEntitySafe = false;
            if (entities != null) {
                for (Entity en : entities) {
                    en.update(deltaTime, curMap);
                }
            }
            addEntitySafe = true;
        }
        handleEntityQueue();
        handleEntityRemoveQueue();

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