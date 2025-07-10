package UI;

import javax.sound.sampled.Clip;
import javax.swing.*;

import Pieces.Piece;
import Scripts.Board;
import Scripts.Game;
import Scripts.Move;

import java.util.ArrayList;
import java.util.List;

import java.awt.*;
import java.awt.event.*;

public class GUI extends JFrame {
    private static final int TILE_SIZE = 90;
    private static final int BOARD_SIZE = Board.BOARD_SIZE;

    private Game game;
    private List<Move> availableMoves = new ArrayList<>();
    private Clip backroundMusic;

    public GUI() {
        game = new Game(this);

        setTitle("Weiß ist am Zug");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setIconImage(new ImageIcon(getClass().getResource("/UI/Icon.png")).getImage());

        backroundMusic = SoundPlayer.play("src\\UI\\Imperial march.wav", true, -10.0f);

        // Linkes Panel (Beispiel: Info)
        JPanel leftPanel = new JPanel();
        leftPanel.setPreferredSize(new Dimension(150, TILE_SIZE * BOARD_SIZE));
        leftPanel.setBackground(Color.DARK_GRAY);

        // Mittleres Panel: Schachbrett
        JPanel boardPanel = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawBoard(g);
                drawMoveHints(g);
                drawPieces(g);
            }
        };
        Dimension boardSize = new Dimension(TILE_SIZE * BOARD_SIZE, TILE_SIZE * BOARD_SIZE);
        boardPanel.setPreferredSize(boardSize);
        boardPanel.setMaximumSize(boardSize);
        boardPanel.setMinimumSize(boardSize);
        boardPanel.setBackground(Color.DARK_GRAY); // Match dark squares

        boardPanel.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                game.squareClicked(e.getY() / TILE_SIZE, e.getX() / TILE_SIZE);
            }
        });

        // Rechtes Panel (z.B. Buttons)
        JPanel rightPanel = new JPanel();
        rightPanel.setPreferredSize(new Dimension(150, TILE_SIZE * BOARD_SIZE));
        rightPanel.setBackground(Color.LIGHT_GRAY);

        JButton muteButton = new JButton();
        ImageIcon originalIcon2 = new ImageIcon(getClass().getResource("/UI/speaker.png"));
        Image scaledImage2 = originalIcon2.getImage().getScaledInstance(22, 22, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon2 = new ImageIcon(scaledImage2);
        muteButton.setIcon(scaledIcon2);
        muteButton.setPreferredSize(new Dimension(80, 30));
        muteButton.setFocusPainted(false);

        // Boolean-Flag zum Zustand merken
        final boolean[] isMuted = { false };

        muteButton.addActionListener(e -> {
            SoundPlayer.toggleMute(backroundMusic);
            if (isMuted[0]) {
                ImageIcon originalIcon = new ImageIcon(getClass().getResource("/UI/speaker.png"));
                Image scaledImage = originalIcon.getImage().getScaledInstance(22, 22, Image.SCALE_SMOOTH);
                ImageIcon scaledIcon = new ImageIcon(scaledImage);
                muteButton.setIcon(scaledIcon);

                isMuted[0] = false;
            } else {
                ImageIcon originalIcon = new ImageIcon(getClass().getResource("/UI/mute.png"));
                Image scaledImage = originalIcon.getImage().getScaledInstance(22, 22, Image.SCALE_SMOOTH);
                ImageIcon scaledIcon = new ImageIcon(scaledImage);
                muteButton.setIcon(scaledIcon);
                
                isMuted[0] = true;
            }
        });

        rightPanel.add(muteButton);

        JButton resetButton = new JButton("new game");
        resetButton.setPreferredSize(new Dimension(100, 30));
        resetButton.setFocusPainted(false);

        resetButton.addActionListener(e -> {
            game.reset();
            repaint();
        });
        leftPanel.add(resetButton);

        // Gesamtes Layout: BoxLayout horizontal
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));
        mainPanel.setBackground(Color.DARK_GRAY); // Match left panel

        mainPanel.add(leftPanel);
        mainPanel.add(boardPanel);
        mainPanel.add(rightPanel);

        getContentPane().setBackground(Color.DARK_GRAY); // Match left panel
        getContentPane().add(mainPanel);

        pack();
        setLocationRelativeTo(null);
        setResizable(false);
    }

    // Deine bisherigen draw Methoden hier...

    private void drawBoard(Graphics g) {
        for (int row = 0; row < Board.BOARD_SIZE; row++) {
            for (int col = 0; col < Board.BOARD_SIZE; col++) {
                boolean isLight = (row + col) % 2 == 0;
                g.setColor(isLight ? Color.LIGHT_GRAY : Color.DARK_GRAY);
                g.fillRect(col * TILE_SIZE, row * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            }
        }
    }

    private void drawPieces(Graphics g) {
        g.setFont(new Font("SansSerif", Font.PLAIN, 48));
        for (int row = 0; row < Board.BOARD_SIZE; row++) {
            for (int col = 0; col < Board.BOARD_SIZE; col++) {
                Piece piece = game.getBoard().getPiece(row, col);
                if (piece != null) {
                    g.setColor(piece.getColor().toAwtColor());
                    g.drawString(piece.getSymbol(), col * TILE_SIZE + 20, row * TILE_SIZE + 55);
                }
            }
        }
    }

    public void showMoveHints(List<Move> moves) {
        this.availableMoves = moves;
        repaint();
    }

    public void clearAvailableMoves() {
        this.availableMoves.clear();
        repaint();
    }

    private void drawMoveHints(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(new Color(128, 128, 128, 160)); // halbtransparent grau

        int diameter = TILE_SIZE / 4;

        for (Move move : availableMoves) {
            int row = move.toRow;
            int col = move.toCol;

            int x = col * TILE_SIZE + (TILE_SIZE - diameter) / 2;
            int y = row * TILE_SIZE + (TILE_SIZE - diameter) / 2;

            g2.fillOval(x, y, diameter, diameter);
        }
    }
}
