package AI;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

import Pieces.*;
import Pieces.PieceColor;
import Scripts.Board;
import Scripts.Move;

abstract public class AbstractAI {
    public static final HashMap<Piece, Integer> pieceValues = new HashMap<Piece, Integer>() {{
        put(Piece.PAWN, 1);
        put(Piece.KNIGHT, 3);
        put(Piece.BISHOP, 3);
        put(Piece.ROOK, 5);
        put(Piece.QUEEN, 9);
        put(Piece.KING, 0); // King is invaluable
    }};

    public abstract Move getMove(Board board, PieceColor atTurn);
    
    public static List<Move> getallMoves(Board board, PieceColor atTurn) {
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
    /**
     * Evaluates the position of the board for the given player. Whit is positive, black is negative.
     */
    public static int positionEvaluation(Board board, PieceColor atTurn) {
        int evaluation = 0;
        board.getPieces()

        return evaluation;

    }
}
