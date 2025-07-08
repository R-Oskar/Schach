package Scripts;

import java.util.List;

import javax.swing.JOptionPane;

import Pieces.*;

import UI.GUI;

public class Game {
    private GUI gui;
    private Board board;

    private Piece selectedPiece;
    private int[] selectedPiecePosition;

    private PieceColor atTurn;

    public Game(GUI gui) {
        board = new Board();
        atTurn = PieceColor.WHITE;
        selectedPiecePosition = new int[2];
        this.gui = gui;
    }

    public void squareClicked(int row, int col) {
        System.out.println("Row: " + row + " | Col: " + col);

        Piece target = board.getPiece(row, col);

        if ((target != null) && ((selectedPiece == null && target.getColor() == atTurn)
                || (selectedPiece != null && selectedPiece.getColor() == target.getColor()))) {

            selectedPiece = target;
            selectedPiecePosition[0] = row;
            selectedPiecePosition[1] = col;
            System.out.println("Piece selected");
            List<Move> legalMoves = board.getLegalMoves(selectedPiecePosition[0], selectedPiecePosition[1]);
            gui.showMoveHints(legalMoves);
            gui.repaint();

        } else if (selectedPiece != null) {
            List<Move> legalMoves = board.getLegalMoves(selectedPiecePosition[0], selectedPiecePosition[1]);

            for (Move move : legalMoves) {
                if (move.toRow == row && move.toCol == col) {
                    if (move.moveType == MoveType.EN_PASSANT) {
                        board.deleteEnPassant();
                    }
                    move(move);
                    return;
                }
            }
        }
    }

    private void move(Move move) {
        if (board.getPiece(move.toRow, move.toCol) != null) {
            move.moveType = MoveType.CAPTURE;
        }
        board.movePiece(new Move(selectedPiecePosition[0], selectedPiecePosition[1], move.toRow, move.toCol));

        if (selectedPiece instanceof Rook) {
            ((Rook) selectedPiece).setHasMoved(true);
        } else if (selectedPiece instanceof King) {
            ((King) selectedPiece).setHasMoved(true);
        }

        gui.clearAvailableMoves();

        promotion(move.toRow, move.toCol);

        if (board.isCheckmate(atTurn)) {
            JOptionPane.showMessageDialog(null, "Schachmatt: " + atTurn.switchColor() +
                    " hat gewonnen");
        }
        if (board.isStalemate(atTurn)) {
            JOptionPane.showMessageDialog(null, "Patt: Unentschieden");
        }

        if (move.moveType == MoveType.EN_PASSANT || move.moveType == MoveType.CAPTURE) {
            UI.SoundPlayer.play("src\\UI\\capture.wav");
        } else {
            UI.SoundPlayer.play("src\\UI\\move-self.wav");
        }

        selectedPiece = null;
        atTurn = atTurn.switchColor();
    }

    private void promotion(int row, int col) {
        Piece movedPiece = getBoard().getPiece(row, col);
        if (movedPiece instanceof Pawn) {
            if ((movedPiece.getColor().equals(PieceColor.WHITE) && row == 0) ||
                    (movedPiece.getColor().equals(PieceColor.BLACK) && row == 7)) {

                String[] options = { "Dame", "Turm", "Läufer", "Springer" };
                String choice = (String) JOptionPane.showInputDialog(
                        null,
                        "Wähle Figur zur Umwandlung:",
                        "Bauernumwandlung",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        options,
                        "Dame");

                PieceColor color = movedPiece.getColor();
                Piece promotedPiece = switch (choice) {
                    case "Turm" -> new Rook(color);
                    case "Läufer" -> new Bishop(color);
                    case "Springer" -> new Knight(color);
                    default -> new Queen(color); // Standard: Dame
                };

                board.setPiece(row, col, promotedPiece);
            }
        }
        gui.repaint();
    }

    public Board getBoard() {
        return board;
    }
}
