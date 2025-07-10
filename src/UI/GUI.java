package UI;

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

    private Game game;

    private List<Move> availableMoves = new ArrayList<>();

    public GUI() {
        game = new Game(this);
        setTitle("Weiß ist am Zug");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setIconImage(new ImageIcon(getClass().getResource("/UI/Icon.png")).getImage());

        SoundPlayer.play("src\\UI\\Imperial march.wav", true, (float) -10.0);

        JPanel panel = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawBoard(g);
                drawMoveHints(g);
                drawPieces(g);
            }
        };

        panel.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                game.squareClicked(e.getY() / TILE_SIZE, e.getX() / TILE_SIZE);
            }
        });

        add(panel);
        Dimension d = new Dimension((TILE_SIZE * Board.BOARD_SIZE), (TILE_SIZE * Board.BOARD_SIZE));
        panel.setSize(d);
        panel.setMinimumSize(d);
        panel.setPreferredSize(d);
        panel.setMaximumSize(d);
        pack();
        setLocationRelativeTo(null);
    }

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
        g2.setColor(new Color(128, 128, 128, 160)); // halbtransparenter Grau

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
