package AI;

import Pieces.*;
import Scripts.*;
import java.util.List;
import java.util.Random;

public class EasyAI extends AbstractAI {

    @Override
    public Move getMove(Board board, PieceColor atTurn) {
        List<Move> allMoves = getallMoves(board, atTurn);
        if (allMoves.isEmpty()) {
            System.err.print("EasyAI: No legal moves available for " + atTurn + "\n");
            return null; // No legal moves available
        }
        return allMoves.get(new Random().nextInt(allMoves.size()));
    }

}