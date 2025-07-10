package AIs;

import java.io.*;

import Pieces.PieceColor;
import Scripts.Board;
import Scripts.Move;

public class StockfishEngine {

    private Process stockfishProcess;
    private BufferedReader reader;
    private BufferedWriter writer;

    public boolean startEngine(String stockfishPath) {
        try {
            stockfishProcess = new ProcessBuilder(stockfishPath).redirectErrorStream(true).start();
            reader = new BufferedReader(new InputStreamReader(stockfishProcess.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(stockfishProcess.getOutputStream()));
            sendCommand("uci"); // initialize UCI mode
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
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

    public static String boardToFEN(Board board, PieceColor atTurn) {
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
                .append((atTurn == PieceColor.WHITE) ? "w" : "b")
                .append(" ")
                .append(board.getCastlingRights())
                .append(" ")
                .append("-") 
                .append(" ")
                .append("25") // halfmove clock (e.g., for 50-move rule)
                .append(" ")
                .append("25"); // fullmove number

        return fen.toString();
    }

    public static Move stockFishMove(Board board, PieceColor atTurn, int depth) {
        String bestMove = "";
        try {
            StockfishEngine engine = new StockfishEngine();
            if (engine.startEngine(
                    "src\\AIs\\stockfish-windows-x86-64-avx2\\stockfish\\stockfish-windows-x86-64-avx2.exe")) {
                bestMove = engine.getBestMove(boardToFEN(board, atTurn), depth);
                engine.stopEngine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return parseUCIMove(bestMove);
    }

    public void sendCommand(String command) throws IOException {
        writer.write(command + "\n");
        writer.flush();
    }

    public String getBestMove(String fen, int depth) throws IOException {
        sendCommand("position fen " + fen);
        sendCommand("go depth " + depth);
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.startsWith("bestmove")) {
                return line.split(" ")[1];
            }
        }
        return null;
    }

    public void stopEngine() throws IOException {
        sendCommand("quit");
        writer.close();
        reader.close();
        stockfishProcess.destroy();
    }
}
