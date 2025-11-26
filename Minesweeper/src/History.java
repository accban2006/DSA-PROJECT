import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

public class History {
    private final Deque<ExecutedCommand> undoStack = new ArrayDeque<>();
    private final Deque<ExecutedCommand> redoStack = new ArrayDeque<>();

    public void execute(Board board, Game game, Command cmd) {
        List<CellDelta> deltas = cmd.apply(board, game);
        if (!deltas.isEmpty()) {
            undoStack.push(new ExecutedCommand(cmd, deltas, game.getLastDeltaScore()));
            redoStack.clear();
            game.setLastDeltaScore(0);
        }
    }

    public boolean canUndo() { return !undoStack.isEmpty(); }
    public boolean canRedo() { return !redoStack.isEmpty(); }

    public void undo(Board board, Game game) {
        if (!canUndo()) return;
        ExecutedCommand ec = undoStack.pop();
        game.setLastDeltaScore(ec.scoreDelta);
        ec.command.undo(board, game, ec.deltas);
        redoStack.push(ec);
    }

    public void redo(Board board, Game game) {
        if (!canRedo()) return;
        ExecutedCommand ec = redoStack.pop();
        List<CellDelta> newDeltas = ec.command.apply(board, game);
        int newScoreDelta = game.getLastDeltaScore();
        game.setLastDeltaScore(0);
        undoStack.push(new ExecutedCommand(ec.command, newDeltas, newScoreDelta));
    }

    private static class ExecutedCommand {
        final Command command;
        final List<CellDelta> deltas;
        final int scoreDelta;

        ExecutedCommand(Command command, List<CellDelta> deltas, int scoreDelta) {
            this.command = command;
            this.deltas = deltas;
            this.scoreDelta = scoreDelta;
        }
    }
}