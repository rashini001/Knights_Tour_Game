package game.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class WarnsdorffSolver {
    private final Board board;
    private long startTime, endTime;

    public WarnsdorffSolver(Board board) {
        this.board = board;
    }

    public boolean solve(int startX, int startY) {
        board.resetBoard();
        int x = startX, y = startY;
        board.board[x][y] = 0;
        startTime = System.nanoTime();

        for (int move = 1; move < Board.SIZE * Board.SIZE; move++) {
            List<int[]> candidates = new ArrayList<>();
            for (int i = 0; i < 8; i++) {
                int nextX = x + KnightMove.moveX[i];
                int nextY = y + KnightMove.moveY[i];
                if (board.isSafe(nextX, nextY)) {
                    int onwardMoves = countOnwardMoves(nextX, nextY);
                    candidates.add(new int[]{nextX, nextY, onwardMoves});
                }
            }

            if (candidates.isEmpty()) {
                endTime = System.nanoTime();
                return false;
            }

            candidates.sort(Comparator.comparingInt(a -> a[2]));
            x = candidates.get(0)[0];
            y = candidates.get(0)[1];
            board.board[x][y] = move;
        }

        endTime = System.nanoTime();
        return true;
    }

    private int countOnwardMoves(int x, int y) {
        int count = 0;
        for (int i = 0; i < 8; i++) {
            int nextX = x + KnightMove.moveX[i];
            int nextY = y + KnightMove.moveY[i];
            if (board.isSafe(nextX, nextY)) {
                count++;
            }
        }
        return count;
    }

    public long getExecutionTimeMillis() {
        return (endTime - startTime) / 1_000_000;
    }
}
