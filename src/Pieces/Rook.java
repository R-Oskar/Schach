package Pieces;

import java.util.List;

import Scripts.Board;
import Scripts.Move;

public class Rook extends Piece {
    public boolean hasMoved;

    public Rook(PieceColor color) {
        super(color.equals(PieceColor.WHITE) ? "♖" : "♜", color);
        hasMoved = false;
    }

    public boolean hasMoved() {
        return hasMoved;
    }

    public void setHasMoved(boolean moved) {
        this.hasMoved = moved;
    }

    public boolean canMove(int row, int col, int targetRow, int targetCol) {
        return col == targetCol || row == targetRow;
    }

    @Override
    public List<Move> getBasicMoves(Board board, int row, int col) {
        int[] dRow = { -1, 1, 0, 0 };
        int[] dCol = { 0, 0, -1, 1 };
        return getSlidingMoves(board, row, col, dRow, dCol);
    }

    @Override
    public Rook clone() {
        Rook cloned = (Rook) super.clone();
        cloned.hasMoved = this.hasMoved;
        return cloned;
    }

    public char getFen() {
        return (color == PieceColor.WHITE) ? 'R' : 'r';
    }
}