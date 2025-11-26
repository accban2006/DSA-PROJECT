public class Cell {
    public final int row;
    public final int col;

    private boolean mine;
    private boolean revealed;
    private boolean flagged;
    private int adjacent;

    public Cell(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public boolean hasMine() { return mine; }
    public void setMine(boolean mine) { this.mine = mine; }

    public boolean isRevealed() { return revealed; }
    public void setRevealed(boolean revealed) { this.revealed = revealed; }

    public boolean isFlagged() { return flagged; }
    public void setFlagged(boolean flagged) { this.flagged = flagged; }

    public int getAdjacent() { return adjacent; }
    public void setAdjacent(int adjacent) { this.adjacent = adjacent; }
}