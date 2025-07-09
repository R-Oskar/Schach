package AI;

import java.util.ArrayList;
import java.util.List;

import Pieces.Piece;
import Pieces.PieceColor;
import Scripts.Board;
import Scripts.Game;
import Scripts.Move;

abstract public class AbstractAI {

    public abstract Move getMove(Game game, Board board, PieceColor atTurn);
    
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
}
