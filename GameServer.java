import java.net.*;
import java.util.ArrayList;
import java.util.Random;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;

public class GameServer {
    private ServerSocket ss;
    private ArrayList<Client> clients;
    private final int maxClients = 4;
    private final int port = 55555;
    private static boolean emitAllReady;
    private int curSeed;
    private boolean holdSeedReset;

    public GameServer() {
        System.out.println("==== Dungeon Knight Server ====");
        try {
            ss = new ServerSocket(port);
            System.out.println("Starter server on port " + port);
        } catch (IOException ex) {
            System.out.println("IOException from constructor: "+ex);
        }
        clients = new ArrayList<Client>();
        emitAllReady = true;
        curSeed = 0;
        holdSeedReset = false;
    }

    public static int generateSeed() {
        Random random = new Random();
        int seed = 100000000 + (random.nextInt(900000000));
        return seed;
    }

    public static void main(String[] args) {
        GameServer gs = new GameServer();
        gs.acceptConnections();
        gs.startGameLoop();
    }

    public void startGameLoop() {
        Timer gameTicker = new Timer(1000/30, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                tick();
            }
        });
        gameTicker.start();
    }

    public void tick() {
        // for (Client c : clients) {
        //     double pX = c.getPlayer().getX();
        //     double pY = c.getPlayer().getY();
        //     System.out.println(pX + " " + pY);
        // }
    }

    public void acceptConnections() {
        Thread gateThread = new Thread(() -> {
            try {
                while (clients.size()<maxClients) {
                    Socket s = ss.accept();
                    Client newClient =  new Client(s, clients);
                    clients.add(newClient);
                }
            } catch (IOException ex) {
                System.out.println("IOException from acceptConnections()");
            }
        });
        gateThread.start();
    }

    public synchronized void emitAll(String command, ArrayList<emitArg> args, int playerID) {
        synchronized (clients) {
            if (emitAllReady) {
                emitAllReady = false;
                for (Client c : clients) {
                    if (c.getID()!=playerID) {
                        c.emit(command, args);
                    }
                }
                emitAllReady = true;
            }
        }
    }

    public class Client {
        private Socket socket;
        private DataInputStream inputStream;
        private DataOutputStream outputStream;
        private static int totalClientCount = 0;
        private int clientID;
        private Thread readThread, writeThread;
        private ReadFromClient rfc;
        private WriteToClient wtc;
        private Player player;
        private ArrayList<Client> clients;

        private class Player {
            private double x, y, dx, dy, angle;
            private int faceDir;
            private boolean isWalking;
            public Player() {
                x = 0;
                y = 0;
                dx = 0;
                dy = 0;
            }
            public double getX() {return x;}
            public double getY() {return y;}
            public void setX(double x) {this.x=x;}
            public void setY(double y) {this.y=y;}
            public double getDx() {return dx;}
            public double getDy() {return dy;}
            public void setDx(double dx) {this.dx=dx;}
            public void setDy(double dy) {this.dy=dy;}
            public void setFaceDir(int dir) {faceDir = dir;}
            public int getFaceDir() {return faceDir;}
            public void setWalking(boolean isWalking) {this.isWalking = isWalking;}
            public boolean getWalking() {return isWalking;}
            public void setAngle(double angle) {this.angle=angle;}
            public double getAngle() {return angle;}
        }

        public int getID() {return clientID;}
        public Player getPlayer() {return player;}

        public void disconnectClient() {
            try {
                if (inputStream != null) inputStream.close();
                if (outputStream != null) outputStream.close();
                
                // Interrupt read and write threads
                rfc.stopThread();
                wtc.stopThread();
                
                // Close the socket
                if (socket != null) socket.close();
                
                // Remove this client from the list of clients
                if (clients != null) clients.remove(this);
                socket.close();

            } catch (IOException ex) {

            }
        }

        public Client(Socket s, ArrayList<Client> clients) {
            socket = s;
            this.clients = clients;
            try {
                inputStream = new DataInputStream(s.getInputStream());
                outputStream = new DataOutputStream(s.getOutputStream());

                int receivedID = inputStream.readInt();
                
                if (receivedID == -1) {
                    clientID = totalClientCount;
                    totalClientCount++;
                } else {
                    clientID = receivedID;
                    totalClientCount = Math.max(receivedID+1,totalClientCount);
                }

                rfc = new ReadFromClient(this, inputStream);
                wtc = new WriteToClient(this, outputStream);

                wtc.sendClientID();

                if (clients.size()==0 && !holdSeedReset) {
                    curSeed = generateSeed();
                }
                wtc.sendSeed();

                player = new Player();

                readThread = new Thread(rfc); 
                writeThread = new Thread(wtc);
                
                readThread.start();
                writeThread.start();

                System.out.println("Client has connected with ID: " + clientID);
            } catch (IOException ex) {
                System.out.println("IOException from Client constructor");
            }
        }
        
        public void emit(String command, ArrayList<emitArg> args) {
            wtc.sendCommand(command, args);
        }
    }


    private class ReadFromClient implements Runnable {
        private int playerID;
        private DataInputStream dataIn;
        Client c;
        private int consequentExceptions, maxExceptions;
        private boolean stopped;
        private String lastValidCom;

        public ReadFromClient(Client c, DataInputStream in) {
            this.c = c;
            playerID = c.getID();
            dataIn = in;
            consequentExceptions = 0;
            maxExceptions = 5;
            System.out.println("RFC" + playerID + " Runnable created!");
            stopped = false;
            lastValidCom = null;
        }
        
        public void stopThread() {
            stopped = true;
        }

        public void run() {
            while (!stopped) {
                try {
                    String command;
                    if (lastValidCom==null) {
                        command = dataIn.readUTF();
                    } else {
                        command = lastValidCom;
                        System.out.println("Resynced stream");
                    }
                    if (command.startsWith("com_")) {
                        switch (command) {
                            case "com_setPos":
                                c.getPlayer().setX(dataIn.readDouble());
                                c.getPlayer().setY(dataIn.readDouble());
                                c.getPlayer().setDx(dataIn.readDouble());
                                c.getPlayer().setDy(dataIn.readDouble());
                                c.getPlayer().setFaceDir(dataIn.readInt());
                                c.getPlayer().setWalking(dataIn.readBoolean());
                                c.getPlayer().setAngle(dataIn.readDouble());
                                break;
                            case "com_newBullet":
                                ArrayList<emitArg> args = new ArrayList<emitArg>();
                                args.add(new emitArg("utf", dataIn.readUTF()));
                                args.add(new emitArg("double", dataIn.readDouble()));
                                args.add(new emitArg("double", dataIn.readDouble()));
                                args.add(new emitArg("double", dataIn.readDouble()));
                                args.add(new emitArg("double", dataIn.readDouble()));
                                args.add(new emitArg("double", dataIn.readDouble()));
                                args.add(new emitArg("double", dataIn.readDouble()));
                                emitAll("newBullet", args, playerID);
                                System.out.println("sent new bullet");
                                break;
                        } 
                    }
                } catch (UTFDataFormatException ex) {
                    System.out.println("IOException from ReadFromServer Thread: " + ex);
                    System.out.println("Client #"+c.getID()+" desynced streams, attempting reconnection... disconnecting");
                    holdSeedReset = true;
                    c.disconnectClient();
                } catch (IOException ex) {
                    System.out.println("IOException from ReadFromServer Thread: " + ex);
                    consequentExceptions++;
                    if (consequentExceptions>maxExceptions) {
                        System.out.println("Exceeded maxexceptions, disconnecting Client #"+c.getID());
                        c.disconnectClient();
                    }
                    try {
                        Thread.sleep(25); // some delay for writing data
                    }
                    catch (InterruptedException iex) {
                        System.out.println("InterruptedException from WTS run()");
                    }
                }
            }
        }
    }

    private class WriteToClient implements Runnable {
        private int playerID;
        private DataOutputStream dataOut;
        private boolean stopped;

        public WriteToClient(Client c, DataOutputStream out) {
            playerID = c.getID();
            dataOut = out;
            stopped = false;
            System.out.println("WTC" + playerID + " Runnable created!");
        }

        public void stopThread() {
            stopped = true;
        }

        public void sendCommand(String command, ArrayList<emitArg> args) {
            try {
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
            } catch (IOException ex) {
                System.out.println("IOException at sendCommand");
            } 
        }

        public void sendClientID() {
            ArrayList<emitArg> args = new ArrayList<emitArg>();
            args.add(new emitArg("int", playerID));
            sendCommand("setID", args);
        }
        public void sendSeed() {
            ArrayList<emitArg> args = new ArrayList<emitArg>();
            args.add(new emitArg("int", curSeed));
            sendCommand("setSeed", args);
        }

        public void run() {
            while (!stopped) {
                for (Client c : clients) {
                    double pX = c.getPlayer().getX();
                    double pY = c.getPlayer().getY();
                    double pDx = c.getPlayer().getDx();
                    double pDy = c.getPlayer().getDy();
                    int pFaceDir = c.getPlayer().getFaceDir();
                    boolean isWalking = c.getPlayer().getWalking();
                    double angle = c.getPlayer().getAngle();

                    ArrayList<emitArg> args = new ArrayList<emitArg>();
                    args.add(new emitArg("int", c.getID()));
                    args.add(new emitArg("double", pX));
                    args.add(new emitArg("double", pY));
                    args.add(new emitArg("double", pDx));
                    args.add(new emitArg("double", pDy));
                    args.add(new emitArg("int", pFaceDir));
                    args.add(new emitArg("boolean", isWalking));
                    args.add(new emitArg("double", angle));
                    emitAll("setAllyPos", args, c.getID());
                }
                try {
                    Thread.sleep(50); // some delay for writing data
                }
                catch (InterruptedException ex) {
                    System.out.println("InterruptedException from WTS run()");
                }
            }
        }
    }
}
