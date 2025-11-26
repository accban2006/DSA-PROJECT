public class CellDelta {
    public final int row;
    public final int col;

    public final boolean prevRevealed;
    public final boolean prevFlagged;
    public final boolean nextRevealed;
    public final boolean nextFlagged;

    public final GameStatus prevStatus;
    public final GameStatus nextStatus;

    public CellDelta(int row, int col,
                     boolean prevRevealed, boolean prevFlagged,
                     boolean nextRevealed, boolean nextFlagged,
                     GameStatus prevStatus, GameStatus nextStatus) {
        this.row = row;
        this.col = col;
        this.prevRevealed = prevRevealed;
        this.prevFlagged = prevFlagged;
        this.nextRevealed = nextRevealed;
        this.nextFlagged = nextFlagged;
        this.prevStatus = prevStatus;
        this.nextStatus = nextStatus;
    }
}