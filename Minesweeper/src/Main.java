import javax.swing.SwingUtilities;
import javax.swing.JOptionPane;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            String playerName = JOptionPane.showInputDialog(null,
                    "Enter your name:", "Player Name",
                    JOptionPane.PLAIN_MESSAGE);
            if (playerName == null || playerName.trim().isEmpty()) {
                playerName = "Guest";
            }
            new MinesweeperUI(Difficulty.NORMAL, playerName).setVisible(true);
        });
    }
}