import java.net.*;
import java.util.ArrayList;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;

public class GameServer {
    private ServerSocket ss;
    private ArrayList<Client> clients;
    private int maxClients = 4;
    private int port = 55555;

    public GameServer() {
        System.out.println("==== Dungeon Knight Server ====");
        try {
            ss = new ServerSocket(port);
            System.out.println("Starter server on port " + port);
        } catch (IOException ex) {
            System.out.println("IOException from constructor");
        }
        clients = new ArrayList<Client>();
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

    public class emitArg {
        private String type;
        private Object value;
        public emitArg(String type, Object value) {this.type = type; this.value = value;}
        public String getType() {return type;}
        public Object getValue() {return value;}
    }

    public void emitAll(String command, ArrayList<emitArg> args) {
        for (Client c : clients) {
            c.emit(command, args);
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
            private double x, y;
            public Player() {
                x = 0;
                y = 0;
            }
            public double getX() {return x;}
            public double getY() {return y;}
            public void setX(double x) {this.x=x;}
            public void setY(double y) {this.y=y;}
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

            } catch (IOException ex) {

            }
        }

        public Client(Socket s, ArrayList<Client> clients) {
            socket = s;
            clientID = totalClientCount;
            totalClientCount++;
            this.clients = clients;
            ArrayList<emitArg> args = new ArrayList<emitArg>();
            args.add(new emitArg("int", clientID));
            emitAll("newClient", args);
            try {
                inputStream = new DataInputStream(s.getInputStream());
                outputStream = new DataOutputStream(s.getOutputStream());

                rfc = new ReadFromClient(this, inputStream);
                wtc = new WriteToClient(this, outputStream);

                wtc.sendClientID();
                player = new Player();

                Thread readThread = new Thread(rfc); 
                Thread writeThread = new Thread(wtc);
                
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

        public ReadFromClient(Client c, DataInputStream in) {
            this.c = c;
            playerID = c.getID();
            dataIn = in;
            consequentExceptions = 0;
            maxExceptions = 8;
            System.out.println("RFC" + playerID + " Runnable created!");
            stopped = false;
        }

        public void close() {
            try {dataIn.close();} catch (IOException ex) {System.out.println("error trying to close dataIn");}
        }
        
        public void stopThread() {
            stopped = true;
        }

        public void run() {
            while (!stopped) {
                try {
                    String command = dataIn.readUTF();
                    // System.out.println("received command " + command);
                    switch (command) {
                        case "setPos":
                            c.getPlayer().setX(dataIn.readDouble());
                            c.getPlayer().setY(dataIn.readDouble());
                            break;
                    }
                } catch (IOException ex) {
                    System.out.println("IOException from ReadFromServer Thread");
                    consequentExceptions++;
                    if (consequentExceptions>maxExceptions) {
                        System.out.println("Exceeded maxexceptions, disconnecting client");
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
        Client c;
        private boolean stopped;

        public WriteToClient(Client c, DataOutputStream out) {
            this.c = c;
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
                    }
                }
            } catch (IOException ex) {
                System.out.println("IOException at sendClientID");
            } 
        }

        public void sendClientID() {
            ArrayList<emitArg> args = new ArrayList<emitArg>();
            args.add(new emitArg("int", playerID));
            sendCommand("setID", args);
        }

        public void run() {
            while (!stopped) {
                for (Client c : clients) {
                    double px = c.getPlayer().getX();
                    double py = c.getPlayer().getY();
                    ArrayList<emitArg> args = new ArrayList<emitArg>();
                    args.add(new emitArg("int", c.getID()));
                    args.add(new emitArg("double", px));
                    args.add(new emitArg("double", py));
                    emitAll("setAllyPos", args);
                }
                try {
                    Thread.sleep(100); // some delay for writing data
                }
                catch (InterruptedException ex) {
                    System.out.println("InterruptedException from WTS run()");
                }
            }
        }
    }
}
