public class PointCalculator {

    // Calculate points for reveal action
    public static int calculateRevealPoints(Board board, int r, int c, java.util.List<CellDelta> deltas) {
        int points = 0;
        for (CellDelta d : deltas) {
            Cell cell = board.cell(d.row, d.col);
            if (cell != null && d.nextRevealed && !cell.hasMine()) {
                points += 10; // safe reveal
            }
        }
        // Penalty if clicked a mine
        Cell start = board.cell(r, c);
        if (start != null && start.hasMine() && !deltas.isEmpty()) {
            points -= 20;
        }
        // Bonus if win
        if (!deltas.isEmpty() && deltas.get(deltas.size() - 1).nextStatus == GameStatus.WON) {
            points += 100;
        }
        return points;
    }

    // Calculate points for flag action
    public static int calculateFlagPoints(Board board, int r, int c, java.util.List<CellDelta> deltas) {
        int points = 0;
        if (!deltas.isEmpty()) {
            Cell cell = board.cell(r, c);
            if (cell != null) {
                if (cell.isFlagged() && cell.hasMine()) {
                    points += 5; // correct flag
                } else if (cell.isFlagged() && !cell.hasMine()) {
                    points -= 5; // incorrect flag
                }
            }
        }
        return points;
    }
}