package Pieces;

import java.util.ArrayList;
import java.util.List;

import Scripts.Board;
import Scripts.Move;

abstract public class Piece implements Cloneable {
    protected PieceColor color;
    private String symbol;

    public Piece(String symbol, PieceColor color) {
        this.color = color;
        this.symbol = symbol;

    }

    public PieceColor getColor() {
        return color;
    }

    public String getSymbol() {
        return symbol;
    }

    @Override
    public Piece clone() {

        try {
            return (Piece) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(); // Sollte nie passieren, wenn Cloneable implementiert
        }
    }

    public abstract List<Move> getBasicMoves(Board board, int row, int col);

    protected List<Move> getSlidingMoves(Board board, int row, int col, int[] dRow, int[] dCol) {
        List<Move> moves = new ArrayList<>();

        for (int dir = 0; dir < dRow.length; dir++) {
            int r = row + dRow[dir];
            int c = col + dCol[dir];

            while (board.isInsideBoard(r, c)) {
                Piece target = board.getPiece(r, c);
                if (target == null) {
                    moves.add(new Move(row, col, r, c));
                } else {
                    if (target.getColor() != this.color) {
                        moves.add(new Move(row, col, r, c));
                    }
                    break; // Figur blockiert
                }
                r += dRow[dir];
                c += dCol[dir];
            }
        }

        return moves;
    }

    public String toString() {
        return "Piece{" +
                "symbol='" + symbol + '\'' +
                ", color=" + color +
                '}';
    }
}
