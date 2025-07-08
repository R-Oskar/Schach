package Pieces;

import java.util.ArrayList;
import java.util.List;

import Scripts.Board;
import Scripts.Move;
import Scripts.MoveType;

public class Pawn extends Piece {

    public Pawn(PieceColor color) {
        super(color.equals(PieceColor.WHITE) ? "♙" : "♟", color);
    }

    @Override
    public List<Move> getBasicMoves(Board board, int row, int col) {
        List<Move> moves = new ArrayList<>();
        int direction = (color == PieceColor.WHITE) ? -1 : 1;
        int startRow = (color == PieceColor.WHITE) ? 6 : 1;

        // 1 Feld vor
        if (board.getPiece(row + direction, col) == null) {
            moves.add(new Move(row, col, row + direction, col));

            // 2 Felder vor (Startfeld)
            if (row == startRow && board.getPiece(row + 2 * direction, col) == null) {
                moves.add(new Move(row, col, row + 2 * direction, col));
            }
        }

        // Schlagen
        for (int dc = -1; dc <= 1; dc += 2) {
            int newCol = col + dc;
            int newRow = row + direction;

            if (!board.isInsideBoard(newRow, newCol))
                continue;

            Piece target = board.getPiece(newRow, newCol);
            if (target != null && target.getColor() != this.color) {
                moves.add(new Move(row, col, newRow, newCol));
            }
            // En Passant
            Move last = board.getLastMove();
            if (last != null && board.getPiece(row, newCol) instanceof Pawn captured) {
                if (captured.getColor() != this.color &&
                        Math.abs(last.fromRow - last.toRow) == 2 && // Doppelzug
                        last.toRow == row && last.toCol == newCol) {
                    moves.add(new Move(row, col, newRow, newCol, MoveType.EN_PASSANT));
                }
            }
        }

        return moves;
    }
}
