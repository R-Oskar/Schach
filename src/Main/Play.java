package Main;

import UI.GUI;
import javax.swing.SwingUtilities;

public class Play {
    public static void main(String[] args) {
        startGame();
    }
    // start the game here
    public static void startGame() {
        SwingUtilities.invokeLater(() -> {
            new GUI().setVisible(true);
        });
    }
}
