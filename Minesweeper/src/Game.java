public class Game {
    private Board board;
    private final History history;
    private int score;
    private int lastDeltaScore;
    private Difficulty difficulty;
    private String playerName;

    public Game(Difficulty difficulty, String playerName) {
        this.difficulty = difficulty;
        this.board = Board.fromDifficulty(difficulty);
        this.history = new History();
        this.score = 0;
        this.lastDeltaScore = 0;
        this.playerName = playerName;
    }

    public Board getBoard() { return board; }
    public History getHistory() { return history; }
    public int getScore() { return score; }
    public void addScore(int points) { score += points; }
    public int getLastDeltaScore() { return lastDeltaScore; }
    public void setLastDeltaScore(int s) { lastDeltaScore = s; }
    public Difficulty getDifficulty() { return difficulty; }
    public String getPlayerName() { return playerName; }

    public void reset(Difficulty newDifficulty) {
        this.difficulty = newDifficulty;
        this.board = Board.fromDifficulty(newDifficulty);
        this.score = 0;
        this.lastDeltaScore = 0;
    }
}