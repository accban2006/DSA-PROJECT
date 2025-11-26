import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MinesweeperUI extends JFrame {
    private Game game;
    private JButton[][] buttons;
    private JLabel playerLabel;
    private JLabel scoreLabel;
    private JLabel statusLabel;

    public MinesweeperUI(Difficulty difficulty, String playerName) {
        super("Minesweeper");
        this.game = new Game(difficulty, playerName);
        initUI();
        render();
    }

    /** Initialize UI layout */
    private void initUI() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        setJMenuBar(createMenuBar());
        add(createToolbar(), BorderLayout.NORTH);
        add(createGrid(), BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
    }

    /** Menu bar with difficulty options */
    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu gameMenu = new JMenu("Game");
        JMenu difficultyMenu = new JMenu("Difficulty");

        ButtonGroup group = new ButtonGroup();
        for (Difficulty diff : Difficulty.values()) {
            JRadioButtonMenuItem item = new JRadioButtonMenuItem(diff.toString());
            group.add(item);
            if (diff == game.getDifficulty()) item.setSelected(true);
            item.addActionListener(e -> resetGame(diff));
            difficultyMenu.add(item);
        }

        JMenuItem newGameItem = new JMenuItem("New Game");
        newGameItem.addActionListener(e -> resetGame(game.getDifficulty()));

        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));

        gameMenu.add(newGameItem);
        gameMenu.add(difficultyMenu);
        gameMenu.addSeparator();
        gameMenu.add(exitItem);

        menuBar.add(gameMenu);
        return menuBar;
    }

    /** Toolbar with undo/redo/reset and labels */
    private JToolBar createToolbar() {
        JToolBar tb = new JToolBar();
        tb.setFloatable(false);

        JButton undo = new JButton("Undo");
        undo.addActionListener(e -> {
            game.getHistory().undo(game.getBoard(), game);
            render();
        });

        JButton redo = new JButton("Redo");
        redo.addActionListener(e -> {
            game.getHistory().redo(game.getBoard(), game);
            render();
        });

        JButton reset = new JButton("Reset");
        reset.addActionListener(e -> resetGame(game.getDifficulty()));

        playerLabel = new JLabel("Player: " + game.getPlayerName());
        scoreLabel = new JLabel("Score: 0");
        statusLabel = new JLabel("Status: RUNNING");

        tb.add(undo);
        tb.add(redo);
        tb.addSeparator();
        tb.add(playerLabel);
        tb.add(Box.createHorizontalStrut(16));
        tb.add(scoreLabel);
        tb.add(Box.createHorizontalStrut(16));
        tb.add(statusLabel);
        tb.add(Box.createHorizontalGlue());
        tb.add(reset);

        return tb;
    }

    /** Grid of buttons for the board */
    private JPanel createGrid() {
        Board board = game.getBoard();
        int rows = board.rows();
        int cols = board.cols();
        JPanel panel = new JPanel(new GridLayout(rows, cols));
        buttons = new JButton[rows][cols];

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                JButton b = createButton(r, c);
                buttons[r][c] = b;
                panel.add(b);
            }
        }
        return panel;
    }

    /** Create a single cell button */
    private JButton createButton(int r, int c) {
        JButton b = new JButton();
        b.setPreferredSize(new Dimension(32, 32));
        b.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (!b.isEnabled()) return;
                if (SwingUtilities.isLeftMouseButton(e)) {
                    game.getHistory().execute(game.getBoard(), game, new RevealCommand(r, c));
                } else if (SwingUtilities.isRightMouseButton(e)) {
                    game.getHistory().execute(game.getBoard(), game, new FlagCommand(r, c));
                }
                render();
            }
        });
        return b;
    }

    /** Update UI after each move */
    private void render() {
        Board board = game.getBoard();
        for (int r = 0; r < board.rows(); r++) {
            for (int c = 0; c < board.cols(); c++) {
                updateButton(buttons[r][c], board.cell(r, c));
            }
        }
        scoreLabel.setText("Score: " + game.getScore());
        statusLabel.setText("Status: " + board.status());
        playerLabel.setText("Player: " + game.getPlayerName());
        setTitle("Minesweeper - " + game.getDifficulty() + " - " + game.getPlayerName());
        pack();
    }

    /** Classic Minesweeper visuals */
    private void updateButton(JButton b, Cell cell) {
        boolean isRunning = game.getBoard().status() == GameStatus.RUNNING;

        if (cell.isRevealed()) {
            b.setEnabled(false);
            if (cell.hasMine()) {
                b.setText("💣"); // mine
                b.setBackground(new Color(180, 60, 60));
                b.setForeground(Color.WHITE);
            } else {
                int adj = cell.getAdjacent();
                b.setText(adj == 0 ? "" : Integer.toString(adj));
                b.setBackground(new Color(220, 220, 220));
                b.setForeground(colorForAdj(adj));
            }
        } else {
            b.setEnabled(isRunning);
            if (cell.isFlagged()) {
                b.setText("🚩"); // flag
                b.setForeground(Color.RED);
            } else {
                b.setText("");
                b.setForeground(Color.BLACK);
            }
            b.setBackground(null);
        }
    }

    /** Reset game with new difficulty */
    private void resetGame(Difficulty difficulty) {
        this.game = new Game(difficulty, game.getPlayerName());
        getContentPane().removeAll();
        setJMenuBar(createMenuBar());
        add(createToolbar(), BorderLayout.NORTH);
        add(createGrid(), BorderLayout.CENTER);
        revalidate();
        render();
    }

    /** Color coding for adjacency numbers */
    private Color colorForAdj(int adj) {
        switch (adj) {
            case 1: return new Color(0, 0, 255);       // blue
            case 2: return new Color(0, 128, 0);       // green
            case 3: return new Color(255, 0, 0);       // red
            case 4: return new Color(0, 0, 128);       // dark blue
            case 5: return new Color(128, 0, 0);       // dark red
            case 6: return new Color(0, 128, 128);     // teal
            case 7: return new Color(0, 0, 0);         // black
            case 8: return new Color(128, 128, 128);   // gray
            default: return Color.BLACK;
        }
    }
}