import java.awt.*;
import java.util.Iterator;
import java.util.Random;

public class CheckersAI implements Runnable {
    private Checkers checkers;
    private CheckersLogic game;
    private Random random = new Random();

    public CheckersAI(Checkers checkers, CheckersLogic game) {
        this.checkers = checkers;
        this.game = game;
    }

    public void sleep(int low, int high) {
        try {
            Thread.sleep((int) ((random.nextInt(high-low+1) + low) / Checkers.AI_SPEED));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        // Pick a random piece
        sleep(250, 550);
        int choice = random.nextInt(game.getMovablePieces().size());
        Iterator<Point> iter = game.getMovablePieces().iterator();
        for (int i = 0; i < choice; i++, iter.next());
        Point pieceToMove = iter.next();
        game.selectPiece(pieceToMove.x, pieceToMove.y);
        checkers.repaint();

        // Continue moving randomly while moves are still available
        boolean startTurn = game.isBlackTurn();
        while (game.isBlackTurn() == startTurn) {
            sleep(350, 650);
            choice = random.nextInt(game.getValidMoves().size());
            iter = game.getValidMoves().iterator();
            for (int i = 0; i < choice; i++, iter.next());
            Point chosenMove = iter.next();
            game.movePiece(pieceToMove.x, pieceToMove.y, chosenMove.x, chosenMove.y);
            pieceToMove.x = chosenMove.x;
            pieceToMove.y = chosenMove.y;
            checkers.repaint();
        }
    }
}
