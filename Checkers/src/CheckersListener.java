import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class CheckersListener extends MouseAdapter {
    Checkers checkers;

    public CheckersListener(Checkers checkers) {
        this.checkers = checkers;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (checkers.game.getWinner() != null) {
            // Reset the game
            checkers.start();
        } else {
            // Convert mouse coordinates to board coordinates, then send to logic handler
            int gridX = (e.getX() - 8) / Checkers.TILE_SIZE;
            int gridY = (e.getY() - 31) / Checkers.TILE_SIZE;
            if (gridX >= 0 && gridX < Checkers.BOARD_SIZE && gridY >= 0 && gridY < Checkers.BOARD_SIZE) {
                checkers.game.locationClicked(gridX, gridY);
                checkers.repaint();
            }
        }
    }
}
