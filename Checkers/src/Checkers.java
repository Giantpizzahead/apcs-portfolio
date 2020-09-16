/**
 * (Project 3/3: Checkers)
 * 
 * It's a checkers game!
 * 
 * In this version of Checkers, you are forced to capture a piece if it
 * is possible, and you must jump over pieces as long as you are able to.
 * 
 * Some game settings are configurable below.
 * 
 * @author Kyle Fu
 */

import java.awt.*;
import javax.swing.*;

public class Checkers extends JFrame {
    public static final int BOARD_SIZE = 8;
    public static final int TILE_SIZE = 100;
    public static final int CHECKER_MARGIN = 2;
    public static final float AI_SPEED = 1f;
    public static final boolean BLACK_AI = true, RED_AI = true;

    CheckersLogic game;
    CheckersListener listener;

    public static void main(String[] args) {
        Checkers checkers = new Checkers();
        checkers.start();
    }

    public Checkers() {
        game = new CheckersLogic(this);
        listener = new CheckersListener(this);
        setTitle("Checkers Board");
        setSize(TILE_SIZE * BOARD_SIZE + 16, TILE_SIZE * BOARD_SIZE + 100);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        addMouseListener(listener);
        MainPanel mainPanel = new MainPanel();
        setContentPane(mainPanel);
        setVisible(true);
    }

    public void start() {
        clearBoard();
        genCheckerBoard();
        game.startGame();
    }

    public void clearBoard() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                game.setPieceAt(i, j, null);
            }
        }
    }

    public void genCheckerBoard() {
        boolean isBlack = false;
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (isBlack) {
                    // Add a new piece if in starting rows
                    if (j < BOARD_SIZE / 2 - 1) {
                        // Red piece
                        game.setPieceAt(i, j, game.red);
                    } else if (j > (BOARD_SIZE + 1) / 2) {
                        // Black piece
                        game.setPieceAt(i, j, game.black);
                    }
                }
                isBlack = !isBlack;
            }
            // Alternate starting column colors if needed
            if (BOARD_SIZE % 2 == 0) isBlack = !isBlack;
        }
    }

    public void genRandom() {
        boolean isBlack = false;
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 1; j < BOARD_SIZE - 1; j++) {
                if (isBlack) {
                    // Add a new piece if random chance allows
                    if (Math.random() < 0.6D) {
                        if (Math.random() < 0.5D) game.setPieceAt(i, j, game.red);
                        else game.setPieceAt(i, j, game.black);
                    }
                }
                isBlack = !isBlack;
            }
            // Alternate starting column colors if needed
            if (BOARD_SIZE % 2 == 0) isBlack = !isBlack;
        }
    }

    class MainPanel extends JPanel {
        public static final int OFFSET_WIDTH = 0;
        public static final int OFFSET_HEIGHT = 0;

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);

            boolean isBlack = false;
            for (int i = 0; i < BOARD_SIZE; i++) {
                for (int j = 0; j < BOARD_SIZE; j++) {
                    // How should this square be colored?
                    if (game.isSquareSelected(i, j)) g.setColor(Color.YELLOW);
                    else if (game.isMovablePiece(i, j)) g.setColor(new Color(170, 170, 80));
                    else if (game.isValidMove(i, j)) g.setColor(new Color(80, 170, 80));
                    else if (isBlack) g.setColor(Color.GRAY);
                    else g.setColor(Color.WHITE);

                    int drawX = i * TILE_SIZE + OFFSET_WIDTH;
                    int drawY = j * TILE_SIZE + OFFSET_HEIGHT;

                    // Draw the rectangle
                    g.fillRect(i * TILE_SIZE + OFFSET_WIDTH, j * TILE_SIZE + OFFSET_HEIGHT, TILE_SIZE, TILE_SIZE);

                    // Draw any pieces
                    Piece piece = game.getPieceAt(i, j);
                    if (piece != null) {
                        g.drawImage(piece.getImage(), drawX + CHECKER_MARGIN, drawY + CHECKER_MARGIN, null);
                    }

                    // Alternate row colors
                    isBlack = !isBlack;
                }
                // Alternate starting column colors if needed
                if (BOARD_SIZE % 2 == 0) isBlack = !isBlack;
            }

            g.setFont(new Font("Arial", Font.BOLD, 30));
            if (game.getWinner() != null) {
                g.setColor(Color.BLUE);
                if (game.getWinner() == game.black) {
                    g.drawString("Black wins the game!", getWidth() / 2 - 150, getHeight() - 25);
                } else if (game.getWinner() == game.red) {
                    g.drawString("Red wins the game!", getWidth() / 2 - 140, getHeight() - 25);
                }
                g.setColor(Color.BLACK);
                g.setFont(new Font("Arial", Font.BOLD, 12));
                g.drawString("Click anywhere to play again.", getWidth() / 2 - 83, getHeight() - 7);
            } else if (game.isBlackTurn()) {
                g.setColor(Color.BLACK);
                g.drawString("Black's turn to move.", getWidth() / 2 - 150, getHeight() - 20);
            } else {
                g.setColor(Color.RED);
                g.drawString("Red's turn to move.", getWidth() / 2 - 140, getHeight() - 20);
            }
        }
    }
}
