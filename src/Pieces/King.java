package Pieces;

import java.util.ArrayList;
import java.util.List;

import Scripts.Board;
import Scripts.Move;

public class King extends Piece {
    public boolean hasMoved;

    public King(PieceColor color) {
        super(color.equals(PieceColor.WHITE) ? "♔" : "♚", color);
        hasMoved = false;
    }

    public boolean hasMoved() {
        return hasMoved;
    }

    public char getFen(){
        return (color == PieceColor.WHITE) ? 'K' : 'k';
    }

    public void setHasMoved(boolean moved) {
        this.hasMoved = moved;
    }

    @Override
    public List<Move> getBasicMoves(Board board, int row, int col) {
        return getBasicMoves(board, row, col, false);
    }

    public List<Move> getBasicMoves(Board board, int row, int col, boolean ignoreCastling) {
        List<Move> moves = new ArrayList<>();
        int[] dRow = { -1, -1, -1, 0, 1, 1, 1, 0 };
        int[] dCol = { -1, 0, 1, 1, 1, 0, -1, -1 };

        for (int i = 0; i < 8; i++) {
            int newRow = row + dRow[i];
            int newCol = col + dCol[i];

            if (board.isInsideBoard(newRow, newCol)) {
                Piece target = board.getPiece(newRow, newCol);
                if (target == null || target.getColor() != this.color) {
                    moves.add(new Move(row, col, newRow, newCol));
                }
            }
        }

        // Castling (nur wenn nicht ignoriert)
        if (!ignoreCastling && !hasMoved) {
            if (canCastle(board, row, col, true)) {
                moves.add(new Move(row, col, row, col + 2)); // King moves two right
            }
            if (canCastle(board, row, col, false)) {
                moves.add(new Move(row, col, row, col - 2)); // King moves two left
            }
        }

        return moves;
    }

    private boolean canCastle(Board board, int row, int col, boolean kingside) {
        int rookCol = kingside ? 7 : 0;
        Piece rook = board.getPiece(row, rookCol);
        if (!(rook instanceof Rook) || ((Rook) rook).hasMoved())
            return false;

        int dir = kingside ? 1 : -1;
        int endCol = kingside ? 6 : 2;
        // Check squares between king and rook are empty
        for (int c = col + dir; kingside ? c < rookCol : c > rookCol; c += dir) {
            if (board.getPiece(row, c) != null)
                return false;
        }

        // Check king not in check and does not move through/into check
        for (int c = col; kingside ? c <= endCol : c >= endCol; c += dir) {
            Board copy = board.copy();
            copy.setPiece(row, col, null);
            copy.setPiece(row, c, this);
            if (copy.isInCheck(this.color))
                return false;
            copy.setPiece(row, c, null);
            copy.setPiece(row, col, this);
        }
        return true;
    }

    @Override
    public King clone() {
        King cloned = (King) super.clone();
        cloned.hasMoved = this.hasMoved;
        return cloned;
    }
}
