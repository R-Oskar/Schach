package Scripts;

import AIs.AI;
import Pieces.*;
import UI.GUI;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

public class Game {
    private GUI gui;
    private Board board;

    private Piece selectedPiece;
    private int[] selectedPiecePosition;

    private PieceColor playerColor;
    private GameMode gameMode;

    public Game(GUI gui) {
        reset();
        this.gui = gui;

    }

    public void reset() {
        gameModeSelection();
        board = new Board();

        selectedPiecePosition = new int[2];
        if (gameMode != GameMode.PLAYER_VS_PLAYER && board.getAtTurn() != playerColor) {
            AIMove();
        }
    }

    public void AIMove() {
        // Titel-Animations-Thread starten
        final boolean[] thinking = { true };

        Thread animationThread = new Thread(() -> {
            String baseTitle = "Die KI denkt nach";
            String[] dots = { ".", "..", "...", ".." };
            int index = 0;

            while (thinking[0]) {
                final String title = baseTitle + dots[index % dots.length];
                javax.swing.SwingUtilities.invokeLater(() -> gui.setTitle(title));
                index++;

                try {
                    Thread.sleep(200); // Geschwindigkeit der Punkte
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        animationThread.start();

        // KI-Berechnung in separatem Thread
        new Thread(() -> {
            try {
                Thread.sleep(200); // optionaler Delay
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Move aiMove = AI.getMove(board, playerColor.switchColor(), gameMode);

            // Animation stoppen
            thinking[0] = false;

            // Zurück in den EDT, um Zug auszuführen und Titel zurückzusetzen
            javax.swing.SwingUtilities.invokeLater(() -> {
                move(aiMove);
                gui.setTitle((board.getAtTurn() == PieceColor.WHITE) ? "Weiß ist am Zug" : "Schwarz ist am Zug");
            });
        }).start();
    }

    public void squareClicked(int row, int col) {
        if (board.getAtTurn() != playerColor && gameMode != GameMode.PLAYER_VS_PLAYER) {
            return;
        }
        Piece target = board.getPiece(row, col);

        if ((target != null) && ((selectedPiece == null && target.getColor() == board.getAtTurn())
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
            selectedPiece = null;
            gui.clearAvailableMoves();
        }
    }

    private void move(Move move) {
        if (board.getPiece(move.toRow, move.toCol) != null) {
            move.moveType = MoveType.CAPTURE;
        }

        board.movePiece(move);

        gui.clearAvailableMoves();

        promotion(move.toRow, move.toCol);

        if (move.moveType == MoveType.EN_PASSANT || move.moveType == MoveType.CAPTURE) {
            UI.SoundPlayer.play("src\\UI\\sounds\\capture.wav");
        } else {
            UI.SoundPlayer.play("src\\UI\\sounds\\move-self.wav");
        }

        board.switchTurn();
        gui.setTitle((board.getAtTurn() == PieceColor.WHITE) ? "Weiß ist am Zug" : "Schwarz ist am Zug");

        if (board.isCheckmate(board.getAtTurn())) {
            JOptionPane optionPane = new JOptionPane(
                    "Schachmatt: " + ((board.getAtTurn().switchColor() == PieceColor.WHITE) ? "Weiß" : "Schwarz")
                            + " hat gewonnen",
                    JOptionPane.INFORMATION_MESSAGE,
                    JOptionPane.DEFAULT_OPTION,
                    new ImageIcon("src\\UI\\pictures\\Icon.png"),
                    new Object[] {}, // keine Buttons → kein Fokus
                    null // kein voreingestellter Wert
            );

            JDialog dialog = optionPane.createDialog("Schachmatt");
            dialog.setFocusableWindowState(false); // verhindert Fokus beim Öffnen
            dialog.setModal(true);
            dialog.setVisible(true);

        }
        if (board.isStalemate(board.getAtTurn())) {
            JOptionPane optionPane = new JOptionPane(
                    "Patt: Unentschieden",
                    JOptionPane.INFORMATION_MESSAGE,
                    JOptionPane.DEFAULT_OPTION,
                    new ImageIcon("src\\UI\\pictures\\Icon.png"));

            JDialog dialog = optionPane.createDialog("Patt");

            // Fokus vom OK-Button wegnehmen:
            dialog.addWindowFocusListener(new java.awt.event.WindowAdapter() {
                public void windowGainedFocus(java.awt.event.WindowEvent e) {
                    dialog.getRootPane().getContentPane().requestFocusInWindow();
                }
            });

            dialog.setVisible(true);
        }
        if (gameMode != GameMode.PLAYER_VS_PLAYER && board.getAtTurn() != playerColor) {
            AIMove();
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
                        new ImageIcon("src\\UI\\pictures\\Icon.png"),
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
        ImageIcon icon = new ImageIcon("src\\UI\\pictures\\Icon.png");
        String[] options = { "Player vs Player", "Random AI", "Easy AI", "Mid AI", "Hard AI" };
        String choice = (String) JOptionPane.showInputDialog(
                null,
                "Wähle deinen Spielmodus: ",
                "Spielmodusauswahl",
                JOptionPane.QUESTION_MESSAGE,
                icon,
                options,
                "Player vs Player");

        gameMode = switch (choice) {
            case "Player vs Player" -> GameMode.PLAYER_VS_PLAYER;
            case "Easy AI" -> GameMode.EASY_AI;
            case "Hard AI" -> GameMode.HARD_AI;
            case "Random AI" -> GameMode.RANDOM_AI;
            case "Mid AI" -> GameMode.MID_AI;
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
                icon,
                options2,
                "Weiß");

        playerColor = switch (choice2) {
            case "Weiß" -> PieceColor.WHITE;
            case "Schwarz" -> PieceColor.BLACK;
            default -> PieceColor.WHITE;
        };
    }
}
