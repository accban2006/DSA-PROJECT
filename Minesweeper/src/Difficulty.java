public enum Difficulty {
    EASY(9, 9, 10),
    NORMAL(15, 15, 40),
    HARD(27, 27, 120);

    private final int rows;
    private final int cols;
    private final int mines;

    Difficulty(int rows, int cols, int mines) {
        this.rows = rows;
        this.cols = cols;
        this.mines = mines;
    }

    public int rows() { return rows; }
    public int cols() { return cols; }
    public int mines() { return mines; }

    @Override
    public String toString() {
        return name() + " (" + rows + "x" + cols + ", " + mines + " mines)";
    }
}