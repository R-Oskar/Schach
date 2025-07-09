package Scripts;

import java.util.List;

import javax.swing.JOptionPane;

import AI.RandomAI;
import Pieces.*;

import UI.GUI;

public class Game {
    private GUI gui;
    private Board board;

    private Piece selectedPiece;
    private int[] selectedPiecePosition;
    private List<Piece> allPieces;

    private PieceColor atTurn;
    private PieceColor playerColor;
    private GameMode gameMode;

    public Game(GUI gui) {
        gameModeSelection();
        board = new Board();
        atTurn = PieceColor.WHITE;
        selectedPiecePosition = new int[2];
        this.gui = gui;
        AIMove();
    }

    public void AIMove() {
        if (gameMode == GameMode.RANDOM_AI && atTurn != playerColor) {
            Move move = RandomAI.randomMove(board, playerColor.switchColor());
            move(move);
        }
    }

    public void squareClicked(int row, int col) {

        Piece target = board.getPiece(row, col);

        if ((target != null) && ((selectedPiece == null && target.getColor() == atTurn)
                || (selectedPiece != null && selectedPiece.getColor() == target.getColor()))) {

            selectedPiece = target;
            selectedPiecePosition[0] = row;
            selectedPiecePosition[1] = col;

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
                    break;
                }
            }
        }
        if (gameMode != GameMode.PLAYER_VS_PLAYER) {
            AIMove();
        }
    }

    private void move(Move move) {
        if (board.getPiece(move.toRow, move.toCol) != null) {
            move.moveType = MoveType.CAPTURE;
        }

        board.movePiece(move);

        if (selectedPiece instanceof Rook) {
            ((Rook) selectedPiece).setHasMoved(true);
        } else if (selectedPiece instanceof King) {
            ((King) selectedPiece).setHasMoved(true);
        }

        gui.clearAvailableMoves();

        promotion(move.toRow, move.toCol);

        if (move.moveType == MoveType.EN_PASSANT || move.moveType == MoveType.CAPTURE) {
            UI.SoundPlayer.play("src\\UI\\capture.wav");
        } else {
            UI.SoundPlayer.play("src\\UI\\move-self.wav");
        }

        selectedPiece = null;
        atTurn = atTurn.switchColor();

        if (board.isCheckmate(atTurn)) {
            JOptionPane.showMessageDialog(null, "Schachmatt: " + atTurn.switchColor() +
                    " hat gewonnen");
        }
        if (board.isStalemate(atTurn)) {
            JOptionPane.showMessageDialog(null, "Patt: Unentschieden");
        }

        gui.repaint();
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

    public void gameModeSelection() {
        String[] options = { "Player vs Player", "vs random AI", "vs Easy AI", "vs Hard AI" };
        String choice = (String) JOptionPane.showInputDialog(
                null,
                "Wähle deinen Spielmodus: ",
                "Spielmodusauswahl",
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                "Player vs Player");

        gameMode = switch (choice) {
            case "Player vs Player" -> GameMode.PLAYER_VS_PLAYER;
            case "vs Easy AI" -> GameMode.EASY_AI;
            case "vs Hard AI" -> GameMode.HARD_AI;
            case "vs random AI" -> GameMode.RANDOM_AI;
            default -> GameMode.PLAYER_VS_PLAYER;
        };

        if (gameMode == GameMode.PLAYER_VS_PLAYER) {
            return;
        }
        String[] options2 = { "Weiß", "Schwarz" };
        String choice2 = (String) JOptionPane.showInputDialog(
                null,
                "Wähle deine Farbe: ",
                "Farbauswahl",
                JOptionPane.QUESTION_MESSAGE,
                null,
                options2,
                "Weiß");

        playerColor = switch (choice2) {
            case "Weiß" -> PieceColor.WHITE;
            case "Schwarz" -> PieceColor.BLACK;
            default -> PieceColor.WHITE;
        };
    }
}
