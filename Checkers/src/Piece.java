import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.File;
import java.util.*;
import javax.imageio.ImageIO;

public abstract class Piece {
    private Color color;
    private Image img;

    public Piece(Color color, String imagePath) {
        this.color = color;
        try {
            BufferedImage bufImg = ImageIO.read(new File(imagePath));
            img = bufImg.getScaledInstance(Checkers.TILE_SIZE - 2 * Checkers.CHECKER_MARGIN, Checkers.TILE_SIZE - 2 * Checkers.CHECKER_MARGIN, Image.SCALE_SMOOTH);
        } catch (IOException e) {
            System.err.println("Failed to read file " + imagePath + "!");
            System.exit(1);
        }
    }

    public Image getImage() {
        return this.img;
    }

    public HashSet<Point> getValidMoves(int fromX, int fromY, CheckersLogic game) {
        // Try moving to all diagonals
        // This code repeats the center block check, but that won't cause any issues
        ArrayList<Point> locsToTry = new ArrayList<>();
        // Add all points in \ diagonal
        int startOffset = Math.min(fromX, fromY);
        for (int toX = fromX - startOffset, toY = fromY - startOffset; toX < Checkers.BOARD_SIZE && toY < Checkers.BOARD_SIZE; toX++, toY++) {
            locsToTry.add(new Point(toX, toY));
        }
        // Add all points in / diagonal
        startOffset = Math.min(fromX, Checkers.BOARD_SIZE - 1 - fromY);
        for (int toX = fromX - startOffset, toY = fromY + startOffset; toX < Checkers.BOARD_SIZE && toY >= 0; toX++, toY--) {
            locsToTry.add(new Point(toX, toY));
        }

        // Try all those points
        HashSet<Point> moves = new HashSet<>();
        boolean jumpMoveFound = false;
        for (Point p : locsToTry) {
            MoveType moveType = getMoveType(fromX, fromY, p.x, p.y, game);
            if (moveType == MoveType.JUMP) {
                if (!jumpMoveFound) {
                    jumpMoveFound = true;
                    // Clear all previous, non-jump moves (since jumps are forced)
                    moves.clear();
                }
                moves.add(p);
            } else if (moveType == MoveType.MOVE && !jumpMoveFound) {
                moves.add(p);
            }
        }

        // Return found moves
        return moves;
    }

    public abstract MoveType getMoveType(int fromX, int fromY, int toX, int toY, CheckersLogic game);
}

class CheckerPiece extends Piece {
    public CheckerPiece(Color color, String imagePath) {
        super(color, imagePath);
    }

    @Override
    public MoveType getMoveType(int fromX, int fromY, int toX, int toY, CheckersLogic game) {
        Piece piece1 = game.getPieceAt(fromX, fromY);
        Piece piece2 = game.getPieceAt(toX, toY);

        // There must be a piece to move, and the destination square must be unoccupied
        if (piece1 == null || piece2 != null) return MoveType.INVALID;
        else {
            int offset = (piece1 == game.red) ? 1 : -1;
            Piece oppPiece1 = (piece1 == game.red) ? game.black : game.red;
            Piece oppPiece2 = (piece1 == game.red) ? game.blackKing : game.redKing;

            // Is this a move or a jump?
            if (Math.abs(toX - fromX) == 1 && toY - fromY == offset) return MoveType.MOVE;
            else if (Math.abs(toX - fromX) == 2 && toY - fromY == 2 * offset) {
                // Is there an opponent piece to jump over?
                Piece midPiece = game.getPieceAt((fromX + toX) / 2, (fromY + toY) / 2);
                if (midPiece == oppPiece1 || midPiece == oppPiece2) return MoveType.JUMP;
            }

            // None of the above apply, so the move is invalid
            return MoveType.INVALID;
        }
    }
}

class KingPiece extends Piece {
    public KingPiece(Color color, String imagePath) {
        super(color, imagePath);
    }

    @Override
    public MoveType getMoveType(int fromX, int fromY, int toX, int toY, CheckersLogic game) {
        Piece piece1 = game.getPieceAt(fromX, fromY);
        Piece piece2 = game.getPieceAt(toX, toY);

        // There must be a piece to move, and the destination square must be unoccupied
        if (piece1 == null || piece2 != null) return MoveType.INVALID;
        else {
            Piece oppPiece1 = (piece1 == game.redKing) ? game.black : game.red;
            Piece oppPiece2 = (piece1 == game.redKing) ? game.blackKing : game.redKing;

            // Is this move diagonal?
            if (Math.abs(toX - fromX) != Math.abs(toY - fromY)) return MoveType.INVALID;

            // Does the move jump past any opponent pieces?
            int cx = toX > fromX ? 1 : -1;
            int cy = toY > fromY ? 1 : -1;
            boolean isJump = false;
            // Initial offset
            int x = fromX + cx;
            int y = fromY + cy;
            for (; x != toX; x += cx, y += cy) {
                Piece midPiece = game.getPieceAt(x, y);
                if (midPiece == oppPiece1 || midPiece == oppPiece2) {
                    if (isJump) return MoveType.INVALID;  // Jumped over 2 pieces
                    else isJump = true;  // Jumped over 1 opponent piece
                } else if (midPiece != null) return MoveType.INVALID;  // Jumped over own piece
            }

            // All checks passed; this move is valid
            return isJump ? MoveType.JUMP : MoveType.MOVE;
        }
    }
}