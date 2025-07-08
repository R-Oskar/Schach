package Main;

import javax.swing.SwingUtilities;

import UI.GUI;

public class Play {
    public static void main(String[] args) {
        startGame();
    }

    public static void startGame() {
        SwingUtilities.invokeLater(() -> {
            new GUI().setVisible(true);
        });
    }
}
