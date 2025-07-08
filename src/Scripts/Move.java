package Scripts;

public class Move {
    public final int fromRow, fromCol;
    public final int toRow, toCol;
    public MoveType moveType;

    public Move(int fromRow, int fromCol, int toRow, int toCol) {
        this(fromRow, fromCol, toRow, toCol, MoveType.NORMAL);
    }

    public Move(int fromRow, int fromCol, int toRow, int toCol, MoveType moveType) {
        this.fromRow = fromRow;
        this.fromCol = fromCol;
        this.toRow = toRow;
        this.toCol = toCol;
        this.moveType = moveType;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Move other)) return false;
        return this.fromRow == other.fromRow &&
               this.fromCol == other.fromCol &&
               this.toRow == other.toRow &&
               this.toCol == other.toCol &&
               this.moveType == other.moveType;
    }
}
