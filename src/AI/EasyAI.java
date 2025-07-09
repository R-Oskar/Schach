package AI;

import Pieces.*;
import Scripts.*;
import java.util.List;
import java.util.Random;

public class EasyAI extends AbstractAI {
    public static Move randomMove(Board board, PieceColor atTurn) {
        int row, col;
        Piece piece = null;
        Random rand = new Random();

        while (true) {
            row = rand.nextInt(8);
            col = rand.nextInt(8);
            piece = board.getPiece(row, col);

            if (piece != null && piece.getColor() == atTurn) {
                List<Move> moves = board.getLegalMoves(row, col);
                if (!moves.isEmpty()) {
                    return moves.get(rand.nextInt(moves.size()));
                }
            }
        }
    }
}