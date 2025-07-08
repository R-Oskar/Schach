package Pieces;

import java.util.ArrayList;
import java.util.List;

import Scripts.Board;
import Scripts.Move;

public class Knight extends Piece {
    public Knight(PieceColor color) {
        super(color.equals(PieceColor.WHITE) ? "♘" : "♞", color);
    }

    @Override
    public List<Move> getBasicMoves(Board board, int row, int col) {
        List<Move> moves = new ArrayList<>();
        int[] dRow = { -2, -1, 1, 2, 2, 1, -1, -2 };
        int[] dCol = { 1, 2, 2, 1, -1, -2, -2, -1 };

        for (int i = 0; i < 8; i++) {
            int r = row + dRow[i];
            int c = col + dCol[i];

            if (board.isInsideBoard(r, c)) {
                Piece target = board.getPiece(r, c);
                if (target == null || target.getColor() != this.color) {
                    moves.add(new Move(row, col, r, c));
                }
            }
        }

        return moves;
    }
}
