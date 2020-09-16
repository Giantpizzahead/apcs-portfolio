import java.awt.*;
import java.util.HashSet;

public class CheckersLogic {
    private Checkers checkers;
    private Piece[][] board;
    private HashSet<Point> validMoves, movablePieces;
    private Point selectedSquare;
    private boolean isBlackTurn;
    private Piece winner;

    public Piece black;
    public Piece red;
    public Piece blackKing;
    public Piece redKing;

    public CheckersLogic(Checkers checkers) {
        this.checkers = checkers;
        board = new Piece[Checkers.BOARD_SIZE][Checkers.BOARD_SIZE];
        black = new CheckerPiece(Color.BLACK, "res/black_piece.png");
        red = new CheckerPiece(Color.WHITE, "res/red_piece.png");
        blackKing = new KingPiece(Color.BLACK, "res/black_king_piece.png");
        redKing = new KingPiece(Color.WHITE, "res/red_king_piece.png");
        validMoves = new HashSet<>();
        movablePieces = new HashSet<>();
    }

    public void startGame() {
        isBlackTurn = true;
        winner = null;
        startTurn();
    }

    public void startTurn() {
        resetSelected();
        movablePieces.clear();
        // Check if this player must make a jump move
        boolean mustJump = false;
        for (int i = 0; i < Checkers.BOARD_SIZE; i++) {
            for (int j = 0; j < Checkers.BOARD_SIZE; j++) {
                Piece piece = getPieceAt(i, j);
                if ((isBlackTurn && (piece == black || piece == blackKing)) || (!isBlackTurn && (piece == red || piece == redKing))) {
                    HashSet<Point> tempMoves = piece.getValidMoves(i, j, this);
                    if (tempMoves.size() != 0) {
                        Point first = tempMoves.iterator().next();
                        if (getJumpedSquare(i, j, first.x, first.y) != null) {
                            // Jump is forced this turn
                            if (!mustJump) {
                                movablePieces.clear();
                                mustJump = true;
                            }
                            movablePieces.add(new Point(i, j));
                        } else if (!mustJump) {
                            // This piece can move
                            movablePieces.add(new Point(i, j));
                        }
                    }
                }
            }
        }

        if (movablePieces.size() == 0) {
            // This player can't do anything; the other player wins!
            winner = isBlackTurn ? red : black;
            return;
        }

        if (!isBlackTurn && Checkers.RED_AI) {
            Thread thread = new Thread(new CheckersAI(checkers, this));
            thread.start();
        } else if (isBlackTurn && Checkers.BLACK_AI) {
            Thread thread = new Thread(new CheckersAI(checkers, this));
            thread.start();
        }
    }

    public void locationClicked(int x, int y) {
        if (winner != null) return;  // No moving once someone wins
        if ((!isBlackTurn && Checkers.RED_AI) || (isBlackTurn && Checkers.BLACK_AI)) return;  // Ignore clicks during AI's turn
        // System.out.println("location clicked x = " + x + ", y = " + y);
        if (selectedSquare != null) {
            // Try moving the selected piece to this location
            if (isValidMove(x, y)) movePiece(selectedSquare.x, selectedSquare.y, x, y);
            else if (isMovablePiece(x, y)) {
                // Select this piece instead
                selectPiece(x, y);
            } else resetSelected();
        } else {
            // Select this piece if it can move and it's the right type
            if (!movablePieces.contains(new Point(x, y))) return;
            Piece piece = getPieceAt(x, y);
            if (piece == null) return; // Can't select an empty piece
            else if (isBlackTurn && (piece == red || piece == redKing)) return;
            else if (!isBlackTurn && (piece == black || piece == blackKing)) return;

            // Valid piece selection
            selectPiece(x, y);
        }
    }

    public void selectPiece(int x, int y) {
        selectedSquare = new Point(x, y);
        validMoves = getPieceAt(x, y).getValidMoves(x, y, this);
    }

    public void movePiece(int fromX, int fromY, int toX, int toY) {
        Piece piece = getPieceAt(fromX, fromY);
        setPieceAt(toX, toY, piece);
        setPieceAt(fromX, fromY, null);
        // Can only move this piece from now on
        movablePieces.clear();
        movablePieces.add(new Point(toX, toY));

        // If the move was a jump, check for more forced moves
        // Since the move was valid, the check here doesn't need to be too thorough
        Point jumpedSquare = getJumpedSquare(fromX, fromY, toX, toY);
        boolean isJump;
        if (jumpedSquare != null) {
            isJump = true;
            setPieceAt(jumpedSquare.x, jumpedSquare.y, null);
        } else isJump = false;

        // Check for king promotion
        if (piece == black && toY == 0) {
            setPieceAt(toX, toY, blackKing);
        } else if (piece == red && toY == Checkers.BOARD_SIZE - 1) {
            setPieceAt(toX, toY, redKing);
        } else if (isJump) {
            // Check for more forced moves
            validMoves = getPieceAt(toX, toY).getValidMoves(toX, toY, this);
            if (validMoves.size() != 0) {
                // Is the next move a jump move?
                Point point = validMoves.iterator().next();
                if (getJumpedSquare(toX, toY, point.x, point.y) != null) {
                    // There are more forced jump moves; keep this piece selected
                    selectedSquare.x = toX;
                    selectedSquare.y = toY;
                    return;
                }
            }
        }

        // If it got here, go to the next turn
        isBlackTurn = !isBlackTurn;
        startTurn();
    }

    public Point getJumpedSquare(int fromX, int fromY, int toX, int toY) {
        int cx = (toX > fromX) ? 1 : -1;
        int cy = (toY > fromY) ? 1 : -1;
        for (int x = fromX + cx, y = fromY + cy; x != toX; x += cx, y += cy) {
            if (getPieceAt(x, y) != null) {
                // Found piece to jump over
                return new Point(x, y);
            }
        }
        return null;  // No pieces to jump over (this move is not a jump)
    }

    public void resetSelected() {
        selectedSquare = null;
        validMoves.clear();
    }

    public Piece getPieceAt(int x, int y) {
        return this.board[x][y];
    }

    public void setPieceAt(int x, int y, Piece piece) {
        board[x][y] = piece;
    }

    public boolean isValidMove(int x, int y) {
        if (validMoves.size() == 0) return false;
        else return validMoves.contains(new Point(x, y));
    }

    public boolean isMovablePiece(int x, int y) {
        if (movablePieces.size() == 0) return false;
        else return movablePieces.contains(new Point(x, y));
    }

    public boolean hasSquareBeenSelected() {
        return selectedSquare != null;
    }
    
    public boolean isSquareSelected(int x, int y) {
        return selectedSquare != null && selectedSquare.x == x && selectedSquare.y == y;
    }

    public boolean isBlackTurn() {
        return isBlackTurn;
    }

    public HashSet<Point> getMovablePieces() {
        return movablePieces;
    }

    public HashSet<Point> getValidMoves() {
        return validMoves;
    }

    public Piece getWinner() {
        return winner;
    }
}
