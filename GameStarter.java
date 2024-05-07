public class GameStarter {
    public static boolean debugMode;
    public static int framerate = 75;
    public static void main(String args[]) {
        if (args.length > 0) {
            debugMode = (args[0].equals("true"));
            System.out.println("Started in debug mode.");
        }
        GameFrame game = new GameFrame();
        game.setUpGUI();
        game.startAnimationThread();
        game.initPlayer();
        game.prepareLevel();
        game.startGameLoop();
    }
}
