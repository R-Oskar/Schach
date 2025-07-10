package Pieces;

import java.util.List;

import Scripts.Board;
import Scripts.Move;

public class Queen extends Piece {
    public Queen(PieceColor color) {
        super(color.equals(PieceColor.WHITE) ? "♕" : "♛", color);
    }

    public List<Move> getBasicMoves(Board board, int row, int col) {
        int[] dRow = { -1, -1, -1, 0, 1, 1, 1, 0 };
        int[] dCol = { -1, 0, 1, 1, 1, 0, -1, -1 };
        return getSlidingMoves(board, row, col, dRow, dCol);
    }

     public char getFen(){
        return (color == PieceColor.WHITE) ? 'Q' : 'q';
    }
}
