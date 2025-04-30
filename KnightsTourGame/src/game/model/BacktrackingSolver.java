package game.model;

public class BacktrackingSolver {
    private final Board board;
    private long startTime, endTime;
    private static final long TIMEOUT_NANO = 3_000_000_000L; // 3 seconds timeout

    public BacktrackingSolver(Board board) {
        this.board = board;
    }

    public boolean solve(int startX, int startY) {
        board.resetBoard();
        board.board[startX][startY] = 0;
        startTime = System.nanoTime();
        boolean success = solveUtil(startX, startY, 1);
        endTime = System.nanoTime();
        return success;
    }

    private boolean solveUtil(int x, int y, int moveCount) {
        if (System.nanoTime() - startTime > TIMEOUT_NANO) {
            return false; // Timeout
        }
        if (moveCount == Board.SIZE * Board.SIZE) return true;

        for (int i = 0; i < 8; i++) {
            int nextX = x + KnightMove.moveX[i];
            int nextY = y + KnightMove.moveY[i];
            if (board.isSafe(nextX, nextY)) {
                board.board[nextX][nextY] = moveCount;
                if (solveUtil(nextX, nextY, moveCount + 1))
                    return true;
                board.board[nextX][nextY] = -1;
            }
        }
        return false;
    }

    public long getExecutionTimeMillis() {
        return (endTime - startTime) / 1_000_000;
    }
}
