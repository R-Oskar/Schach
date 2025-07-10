package AIs;

import Pieces.PieceColor;
import Scripts.Board;
import Scripts.Move;

public class easyAI extends AI {

    public static Move getMove(Board board, PieceColor atTurn) {
        return getEasyMove(board, atTurn);
    }

    public static Move getRandomMove(Board board, PieceColor atTurn) {
        return AI.getRandomMove(board, atTurn);
    }


    public static Move getEasyMove(Board board, PieceColor atTurn) {
        return null;
    }
    
}
