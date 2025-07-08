package Pieces;

import java.util.List;

import Scripts.Board;
import Scripts.Move;

public class Bishop extends Piece {
    public Bishop(PieceColor color) {
        super(color.equals(PieceColor.WHITE) ? "♗" : "♝", color);
    }

    @Override
    public List<Move> getBasicMoves(Board board, int row, int col) {
        int[] dRow = { -1, -1, 1, 1 };
        int[] dCol = { -1, 1, -1, 1 };
        return getSlidingMoves(board, row, col, dRow, dCol);
    }
}
