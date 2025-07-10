package AIs;

import java.util.List;
import java.util.Random;

import Pieces.Piece;
import Pieces.PieceColor;
import Scripts.Board;
import Scripts.GameMode;
import Scripts.Move;

abstract public class AI {

    public static Move getMove(Board board, PieceColor atTurn, GameMode aiType) {
        switch (aiType) {
            case RANDOM_AI:
                return getRandomMove(board, atTurn);
            case EASY_AI:
                return getEasyMove(board, atTurn);
            case MID_AI:
                return StockfishEngine.stockFishMove(board, atTurn, 3,4);
            case HARD_AI:
                return StockfishEngine.stockFishMove(board, atTurn, 6,1);
            default:
                return null;
        }
    }

    public static Move getEasyMove(Board board, PieceColor atTurn) {
        Random rand = new Random();
        int ranNum = rand.nextInt(100) + 1;

        if(ranNum <= 70){
            return getRandomMove(board, atTurn);
        }

        return StockfishEngine.stockFishMove(board, atTurn, 5,4);
    }

    public static Move getRandomMove(Board board, PieceColor atTurn) {
        int row, col;
        Piece piece = null;
        Random rand = new Random();

        while (true) {
            row = rand.nextInt(8);
            col = rand.nextInt(8);
            piece = board.getPiece(row, col);

            if (piece == null || piece.getColor() != atTurn) {
                continue;
            }

            List<Move> moves = board.getLegalMoves(row, col);

            if (moves.isEmpty()) {
                continue;
            }
            return moves.get(rand.nextInt(moves.size()));
        }
    }
}
