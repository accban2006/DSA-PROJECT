import java.util.*;

public class Board {
    private final int rows;
    private final int cols;
    private final int mines;

    private final Cell[][] grid;
    private GameStatus status = GameStatus.RUNNING;
    private int unrevealedSafe;
    private boolean firstMove = true;

    public Board(int rows, int cols, int mines) {
        if (rows <= 0 || cols <= 0) throw new IllegalArgumentException("Invalid size");
        if (mines < 0 || mines >= rows * cols) throw new IllegalArgumentException("Invalid mines");
        this.rows = rows;
        this.cols = cols;
        this.mines = mines;
        this.grid = new Cell[rows][cols];
        initEmpty();
        placeMinesRandom();
        computeAdjacency();
        countSafe();
    }

    public static Board fromDifficulty(Difficulty difficulty) {
        return new Board(difficulty.rows(), difficulty.cols(), difficulty.mines());
    }

    private void initEmpty() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                grid[r][c] = new Cell(r, c);
            }
        }
    }

    private void placeMinesRandom() {
        List<int[]> cells = new ArrayList<>();
        for (int r = 0; r < rows; r++)
            for (int c = 0; c < cols; c++)
                cells.add(new int[]{r, c});
        Collections.shuffle(cells, new Random());
        for (int i = 0; i < mines; i++) {
            int[] p = cells.get(i);
            grid[p[0]][p[1]].setMine(true);
        }
    }

    private void computeAdjacency() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                int count = 0;
                for (Cell nb : neighbors(grid[r][c])) {
                    if (nb.hasMine()) count++;
                }
                grid[r][c].setAdjacent(count);
            }
        }
    }

    private void countSafe() {
        int safe = 0;
        for (int r = 0; r < rows; r++)
            for (int c = 0; c < cols; c++)
                if (!grid[r][c].hasMine()) safe++;
        this.unrevealedSafe = safe;
    }

    public int rows() { return rows; }
    public int cols() { return cols; }
    public GameStatus status() { return status; }
    public Cell cell(int r, int c) { return inBounds(r, c) ? grid[r][c] : null; }

    private boolean inBounds(int r, int c) {
        return r >= 0 && r < rows && c >= 0 && c < cols;
    }

    private List<Cell> neighbors(Cell cell) {
        List<Cell> nbs = new ArrayList<>(8);
        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                if (dr == 0 && dc == 0) continue;
                int rr = cell.row + dr, cc = cell.col + dc;
                if (inBounds(rr, cc)) nbs.add(grid[rr][cc]);
            }
        }
        return nbs;
    }

    private void ensureFirstClickSafe(Cell start) {
        if (!firstMove || !start.hasMine()) return;
        start.setMine(false);
        // Place mine elsewhere
        for (int r = 0; r < rows; r++) {
            boolean placed = false;
            for (int c = 0; c < cols; c++) {
                Cell candidate = grid[r][c];
                if (candidate != start && !candidate.hasMine()) {
                    candidate.setMine(true);
                    placed = true;
                    break;
                }
            }
            if (placed) break;
        }
        computeAdjacency();
        countSafe();
    }

    public List<CellDelta> reveal(int r, int c) {
        if (status != GameStatus.RUNNING) return Collections.emptyList();
        Cell start = cell(r, c);
        if (start == null || start.isRevealed() || start.isFlagged()) return Collections.emptyList();

        GameStatus prevStatus = status;
        if (firstMove) {
            ensureFirstClickSafe(start);
            firstMove = false;
        }

        List<CellDelta> deltas = new ArrayList<>();

        if (start.hasMine()) {
            deltas.add(new CellDelta(r, c,
                    false, start.isFlagged(),
                    true, start.isFlagged(),
                    prevStatus, GameStatus.LOST));
            start.setRevealed(true);
            status = GameStatus.LOST;
            return deltas;
        }

        // BFS flood fill for zeros
        Queue<Cell> q = new ArrayDeque<>();
        q.add(start);
        while (!q.isEmpty()) {
            Cell cur = q.poll();
            if (cur.isRevealed() || cur.isFlagged()) continue;

            deltas.add(new CellDelta(cur.row, cur.col,
                    false, cur.isFlagged(),
                    true, cur.isFlagged(),
                    prevStatus, prevStatus));

            cur.setRevealed(true);
            if (!cur.hasMine()) {
                unrevealedSafe--;
            }

            if (cur.getAdjacent() == 0) {
                for (Cell nb : neighbors(cur)) {
                    if (!nb.isRevealed() && !nb.isFlagged()) q.add(nb);
                }
            }
        }

        if (unrevealedSafe == 0 && status == GameStatus.RUNNING) {
            status = GameStatus.WON;
            int lastIdx = deltas.size() - 1;
            CellDelta last = deltas.get(lastIdx);
            deltas.set(lastIdx, new CellDelta(
                    last.row, last.col,
                    last.prevRevealed, last.prevFlagged,
                    last.nextRevealed, last.nextFlagged,
                    prevStatus, GameStatus.WON
            ));
        }
        return deltas;
    }

    public List<CellDelta> toggleFlag(int r, int c) {
        if (status != GameStatus.RUNNING) return Collections.emptyList();
        Cell cell = cell(r, c);
        if (cell == null || cell.isRevealed()) return Collections.emptyList();

        GameStatus prevStatus = status;
        boolean prevFlag = cell.isFlagged();
        boolean nextFlag = !prevFlag;
        cell.setFlagged(nextFlag);

        return Collections.singletonList(new CellDelta(
                r, c,
                cell.isRevealed(), prevFlag,
                cell.isRevealed(), nextFlag,
                prevStatus, prevStatus
        ));
    }

    public void applyDelta(CellDelta d) {
        Cell cell = this.cell(d.row, d.col);
        if (cell == null) return;
        boolean wasRevealed = cell.isRevealed();
        boolean wasFlagged = cell.isFlagged();

        cell.setRevealed(d.nextRevealed);
        cell.setFlagged(d.nextFlagged);

        if (!wasRevealed && d.nextRevealed && !cell.hasMine()) {
            unrevealedSafe = Math.max(0, unrevealedSafe - 1);
        } else if (wasRevealed && !d.nextRevealed && !cell.hasMine()) {
            unrevealedSafe++;
        }

        status = d.nextStatus;
    }

    public void revertDelta(CellDelta d) {
        Cell cell = this.cell(d.row, d.col);
        if (cell == null) return;
        boolean wasRevealed = cell.isRevealed();

        cell.setRevealed(d.prevRevealed);
        cell.setFlagged(d.prevFlagged);

        if (wasRevealed && !d.prevRevealed && !cell.hasMine()) {
            unrevealedSafe++;
        } else if (!wasRevealed && d.prevRevealed && !cell.hasMine()) {
            unrevealedSafe = Math.max(0, unrevealedSafe - 1);
        }

        status = d.prevStatus;
    }
}