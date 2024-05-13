public class GameStarter {
    public static boolean debugMode;
    public static double zoom;
    public static int framerate = 60;
    public static String host;
    public static void main(String args[]) {
        try {
            debugMode = (args[0].equals("true"));
            System.out.println("Started in debug mode.");
        } catch (Exception e) {
        }
        try {
            zoom = Double.parseDouble(args[1]);
            System.out.println("Set custom zoom to " + zoom);
        } catch (Exception e) {
            zoom = 3;
        }
        try {
            host = args[2];
            System.out.println("Set custom host to " + host);
        } catch (Exception e) {
            host = "gemplo.com";
        }
        GameFrame game = new GameFrame();
        game.initializeGame();
        game.setUpGUI();
        game.startAnimationThread();
        game.initPlayer();
        game.startGameLoop();
    }
}
