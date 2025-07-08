package Pieces;
import java.awt.Color;

public enum PieceColor {
    WHITE(Color.WHITE),
    BLACK(Color.BLACK);

    private final Color awtColor;

    PieceColor(Color awtColor) {
        this.awtColor = awtColor;
    }

    public Color toAwtColor() {
        return awtColor;
    }

    /**
     * Gibt die gegenteilige Farbe zurück.
     */
    public PieceColor switchColor() {
        return this == WHITE ? BLACK : WHITE;
    }
}