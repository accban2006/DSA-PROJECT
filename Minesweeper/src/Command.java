import java.util.List;

public interface Command {
    List<CellDelta> apply(Board board, Game game);
    void undo(Board board, Game game, List<CellDelta> deltas);
}