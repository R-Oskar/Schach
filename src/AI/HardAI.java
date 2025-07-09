package AI;

import Scripts.*;

import Pieces.*;

public class HardAI extends AbstractAI {

    @Override
    public Move getMove(Game game, Board board, PieceColor atTurn) {
        String bestMove = "";
        try {
            StockfishEngine engine = new StockfishEngine();
            if (engine.startEngine(
                    "src\\AI\\stockfish-windows-x86-64-avx2\\stockfish\\stockfish-windows-x86-64-avx2.exe")) {
                bestMove = engine.getBestMove(boardToFEN(game, board, atTurn), 15);
                System.out.println("Best move: " + bestMove);
                engine.stopEngine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return parseUCIMove(bestMove);
    }

    public static Move parseUCIMove(String uci) {
        if (uci.length() < 4)
            throw new IllegalArgumentException("Invalid move: " + uci);

        int fromCol = uci.charAt(0) - 'a';
        int fromRow = 8 - (uci.charAt(1) - '0'); // '8' → 0, '1' → 7

        int toCol = uci.charAt(2) - 'a';
        int toRow = 8 - (uci.charAt(3) - '0');

        return new Move(fromRow, fromCol, toRow, toCol);
    }

    public static String boardToFEN(Game game, Board board, PieceColor atTurn) {
        StringBuilder fen = new StringBuilder();

        for (int row = 0; row < 8; row++) {
            int emptyCount = 0;
            for (int col = 0; col < 8; col++) {
                char piece = ' ';
                if (board.getBoard()[row][col] != null) {
                    piece = board.getBoard()[row][col].getFen();
                }
                if (piece == ' ') {
                    emptyCount++;
                } else {
                    if (emptyCount > 0) {
                        fen.append(emptyCount);
                        emptyCount = 0;
                    }
                    fen.append(piece);
                }
            }
            if (emptyCount > 0) {
                fen.append(emptyCount);
            }
            if (row < 7) {
                fen.append('/');
            }
        }

        // Append remaining FEN fields
        fen.append(" ")
                .append((atTurn == PieceColor.WHITE) ? "w" : "b") // 'w' or 'b'
                .append(" ")
                .append(board.getCastlingRights()) // e.g., "KQkq" or "-"
                .append(" ")
                .append("-") // e.g., "e3" or "-"
                .append(" ")
                .append("25") // halfmove clock (e.g., for 50-move rule)
                .append(" ")
                .append("25"); // fullmove number

        return fen.toString();
    }

}
