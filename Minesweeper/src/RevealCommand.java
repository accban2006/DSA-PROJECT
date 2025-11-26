import java.util.List;

public class RevealCommand implements Command {
    private final int r;
    private final int c;

    public RevealCommand(int r, int c) {
        this.r = r;
        this.c = c;
    }


    @Override
    public List<CellDelta> apply(Board board, Game game) {
        List<CellDelta> deltas = board.reveal(r, c);
        int deltaScore = PointCalculator.calculateRevealPoints(board, r, c, deltas);
        game.addScore(deltaScore);
        game.setLastDeltaScore(deltaScore);
        return deltas;
    }


    @Override
    public void undo(Board board, Game game, List<CellDelta> deltas) {
        for (int i = deltas.size() - 1; i >= 0; i--) {
            board.revertDelta(deltas.get(i));
        }
        game.addScore(-game.getLastDeltaScore());
        game.setLastDeltaScore(0);
    }
}