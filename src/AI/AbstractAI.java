package AI;

import java.util.ArrayList;
import java.util.List;

import Pieces.Piece;
import Pieces.PieceColor;
import Scripts.Board;
import Scripts.Move;

public abstract class AbstractAI {

    public static List<Move> allMoves(Board board, PieceColor atTurn) {
        List<Move> allLegalMoves = new ArrayList<Move>();
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = board.getPiece(row, col);
                if (piece == null || piece.getColor() != atTurn)
                    continue;
                else
                    allLegalMoves.addAll(board.getLegalMoves(row, col));
            }
        }
        return allLegalMoves;
    }
}
