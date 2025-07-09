package Scripts;

import Pieces.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Board {
    public final static int BOARD_SIZE = 8;
    private Piece[][] board;
    private Move lastMove;

    private String castlingRights;
    private PieceColor atTurn;

    public String getCastlingRights() {
        return castlingRights;
    }

    public Board() {
        board = setupInitialPosition();
        atTurn = PieceColor.WHITE;
        castlingRights = "KQkq";
    }

    public void switchTurn(){
        atTurn = atTurn.switchColor();
    }

    public PieceColor getAtTurn() {
        return atTurn;
    }

    public Piece[][] getBoard() {
        return board;
    }

    public static Piece[][] setupInitialPosition() {
        Piece[][] board = new Piece[BOARD_SIZE][BOARD_SIZE];

        for (int col = 0; col < BOARD_SIZE; col++) {
            board[1][col] = new Pawn(PieceColor.BLACK);
            board[6][col] = new Pawn(PieceColor.WHITE);
        }

        // schwarze Figuren
        board[0][0] = new Rook(PieceColor.BLACK);
        board[0][1] = new Knight(PieceColor.BLACK);
        board[0][2] = new Bishop(PieceColor.BLACK);
        board[0][3] = new Queen(PieceColor.BLACK);
        board[0][4] = new King(PieceColor.BLACK);
        board[0][5] = new Bishop(PieceColor.BLACK);
        board[0][6] = new Knight(PieceColor.BLACK);
        board[0][7] = new Rook(PieceColor.BLACK);

        // weiße Figuren
        board[7][0] = new Rook(PieceColor.WHITE);
        board[7][1] = new Knight(PieceColor.WHITE);
        board[7][2] = new Bishop(PieceColor.WHITE);
        board[7][3] = new Queen(PieceColor.WHITE);
        board[7][4] = new King(PieceColor.WHITE);
        board[7][5] = new Bishop(PieceColor.WHITE);
        board[7][6] = new Knight(PieceColor.WHITE);
        board[7][7] = new Rook(PieceColor.WHITE);

        return board;
    }

    public boolean isInsideBoard(int row, int col) {
        return row >= 0 && row < 8 && col >= 0 && col < 8;
    }

    public Piece getPiece(int row, int col) {
        return board[row][col];
    }

    public void setPiece(int row, int col, Piece piece) {
        board[row][col] = piece;
    }

    public List<Piece> getPieces() {
        List<Piece> pieces = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece piece = board[i][j];
                if (piece != null) {
                    pieces.add(piece);
                }
            }
        }
        return pieces;
    }

    public List<Move> getLegalMoves(int row, int col) {
        Piece piece = getPiece(row, col);
        if (piece == null)
            return Collections.emptyList();

        List<Move> basicMoves = piece.getBasicMoves(this, row, col);
        List<Move> legalMoves = new ArrayList<>();

        for (Move move : basicMoves) {
            Board copy = this.copy();
            copy.setPiece(move.toRow, move.toCol, piece);
            copy.setPiece(move.fromRow, move.fromCol, null);

            if (!copy.isInCheck(piece.getColor())) {
                legalMoves.add(move);
            }
        }
        return legalMoves;
    }

    public void movePiece(Move move) {
        Piece piece = getPiece(move.fromRow, move.fromCol);

        // Assume castlingRights is a field or accessible variable
        // Example: castlingRights = "KQkq";

        if (piece instanceof King && Math.abs(move.toCol - move.fromCol) == 2) {
            int row = move.fromRow;

            if (move.toCol == 6) { // Short castling
                setPiece(row, 6, piece);
                setPiece(row, 4, null);
                Piece rook = getPiece(row, 7);
                setPiece(row, 5, rook);
                setPiece(row, 7, null);
                if (rook instanceof Rook)
                    ((Rook) rook).setHasMoved(true);

                // Remove castling right (K or k)
                if (piece.getColor() == PieceColor.WHITE) {
                    castlingRights = castlingRights.replace("K", "");
                } else {
                    castlingRights = castlingRights.replace("k", "");
                }

            } else if (move.toCol == 2) { // Long castling
                setPiece(row, 2, piece);
                setPiece(row, 4, null);
                Piece rook = getPiece(row, 0);
                setPiece(row, 3, rook);
                setPiece(row, 0, null);
                if (rook instanceof Rook)
                    ((Rook) rook).setHasMoved(true);

                // Remove castling right (Q or q)
                if (piece.getColor() == PieceColor.WHITE) {
                    castlingRights = castlingRights.replace("Q", "");
                } else {
                    castlingRights = castlingRights.replace("q", "");
                }
            }

            if (piece instanceof King)
                ((King) piece).setHasMoved(true);

            lastMove = move;

            // Optional: If no rights left, replace with "-"
            if (castlingRights.isEmpty())
                castlingRights = "-";

            return;
        }

        // En Passant schlagen
        if (move.moveType == MoveType.EN_PASSANT && piece instanceof Pawn) {
            int capturedRow = move.fromRow;
            int capturedCol = move.toCol;
            setPiece(capturedRow, capturedCol, null); // entferne gegnerischen Bauern
        }

        setPiece(move.toRow, move.toCol, piece);
        setPiece(move.fromRow, move.fromCol, null);
        lastMove = move;
    }

    public Board copy() {
        Board newBoard = new Board();

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = this.getPiece(row, col);
                newBoard.setPiece(row, col, piece);
            }
        }

        return newBoard;
    }

    public boolean isCheckmate(PieceColor color) {
        if (!isInCheck(color)) {
            return false;
        }
        // Gibt es irgendeinen legalen Zug, der das Schach verhindert?
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = getPiece(row, col);
                if (piece != null && piece.getColor() == color) {
                    List<Move> legalMoves = getLegalMoves(row, col);
                    if (!legalMoves.isEmpty()) {
                        return false;
                    }
                }
            }
        }

        return true; // Kein legaler Zug gefunden → matt
    }

    public boolean isStalemate(PieceColor color) {
        if (isInCheck(color))
            return false;

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = getPiece(row, col);
                if (piece != null && piece.getColor() == color) {
                    List<Move> legalMoves = getLegalMoves(row, col);
                    if (!legalMoves.isEmpty()) {
                        return false;
                    }
                }
            }
        }

        return true; // Kein legaler Zug → patt
    }

    public void deleteEnPassant() {
        setPiece(lastMove.toRow, lastMove.toCol, null);
    }

    public Move getLastMove() {
        return lastMove;
    }

    public boolean isInCheck(PieceColor color) {
        int[] kingPos = findKingPosition(color);

        if (kingPos == null)
            return false; // Kein König vorhanden → kein Schach
        // Gegnerische Farbe
        PieceColor opponent = (color == PieceColor.WHITE) ? PieceColor.BLACK : PieceColor.WHITE;

        // Alle gegnerischen Züge durchsuchen
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = getPiece(row, col);
                if (piece == null || piece.getColor() != opponent) {
                    continue;
                }
                List<Move> moves;
                if (piece instanceof King) {
                    King king = (King) piece;
                    moves = king.getBasicMoves(this, row, col, true);
                } else {
                    moves = piece.getBasicMoves(this, row, col);
                }

                for (Move move : moves) {
                    if (move.toRow == kingPos[0] && move.toCol == kingPos[1]) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private int[] findKingPosition(PieceColor color) {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = getPiece(row, col);
                if (piece instanceof King && piece.getColor() == color) {
                    int[] pos = { row, col };
                    return pos;
                }
            }
        }
        return null; // König nicht gefunden
    }
}
