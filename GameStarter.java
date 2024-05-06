public class GameStarter {
    public static void main(String args[]) {
        GameFrame game = new GameFrame();

        game.setUpGUI();
        game.startAnimationThread();
        game.prepareLevel();
        game.initPlayer();
        game.startGameLoop();
    }
}
