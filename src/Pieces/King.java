package Pieces;

import java.util.ArrayList;
import java.util.List;

import Scripts.Board;
import Scripts.Move;

public class King extends Piece {

    public King(PieceColor color) {
        super(color.equals(PieceColor.WHITE) ? "♔" : "♚", color);
    }

    public char getFen() {
        return (color == PieceColor.WHITE) ? 'K' : 'k';
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
        if (!ignoreCastling && !hasMoved(board)) {
            if (canCastle(board, row, col, true)) {
                moves.add(new Move(row, col, row, col + 2)); // King moves two right
            }
            if (canCastle(board, row, col, false)) {
                moves.add(new Move(row, col, row, col - 2)); // King moves two left
            }
        }

        return moves;
    }

    private boolean hasMoved(Board board) {
        String castlingRights = board.getCastlingRights();
        if (color == PieceColor.WHITE && (castlingRights.contains("K") || castlingRights.contains("Q"))) {
            return false;
        }
        if (color == PieceColor.BLACK && (castlingRights.contains("k") || castlingRights.contains("q"))) {
            return false;
        }
        return true;
    }

    private boolean canCastle(Board board, int row, int col, boolean kingside) {
        int rookCol = kingside ? 7 : 0;

        String rights = board.getCastlingRights();

        if (color == PieceColor.WHITE) {
            if (kingside && !rights.contains("K"))
                return false;
            if (!kingside && !rights.contains("Q"))
                return false;
        } else {
            if (kingside && !rights.contains("k"))
                return false;
            if (!kingside && !rights.contains("q"))
                return false;
        }

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
        return cloned;
    }
}
