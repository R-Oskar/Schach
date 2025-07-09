package AIs;

import java.util.ArrayList;
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
                return easyAiMove(board, atTurn);
            case MID_AI:
                return StockfishEngine.stockFishMove(board, atTurn, 6);
            case HARD_AI:
                return StockfishEngine.stockFishMove(board, atTurn, 15);
            default:
                return null;
        }
    }

    public static Move getRandomMove(Board board, PieceColor atTurn) {
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

    public static Move easyAiMove(Board board, PieceColor atTurn) {
        List<Move> allMoves = getallMoves(board, atTurn);
        if (allMoves.isEmpty()) {
            System.err.print("EasyAI: No legal moves available for " + atTurn + "\n");
            return null; // No legal moves available
        }
        return allMoves.get(new Random().nextInt(allMoves.size()));
    }

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
