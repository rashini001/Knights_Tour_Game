package game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class WarnsdorffSolver {
    private final Board board;
    private long startTime, endTime;

    public WarnsdorffSolver(Board board) {
        this.board = board;
    }

    private int getDegree(int x, int y) {
        int count = 0;
        for (int i = 0; i < 8; i++) {
            int nx = x + KnightMove.moveX[i];
            int ny = y + KnightMove.moveY[i];
            if (board.isSafe(nx, ny)) count++;
        }
        return count;
    }

    public boolean solve(int startX, int startY) {
        board.resetBoard();
        board.board[startX][startY] = 0;
        startTime = System.nanoTime();
        int x = startX, y = startY;

        for (int i = 1; i < Board.SIZE * Board.SIZE; i++) {
            ArrayList<int[]> nextMoves = new ArrayList<>();
            for (int k = 0; k < 8; k++) {
                int nx = x + KnightMove.moveX[k];
                int ny = y + KnightMove.moveY[k];
                if (board.isSafe(nx, ny)) {
                    nextMoves.add(new int[]{nx, ny, getDegree(nx, ny)});
                }
            }

            if (nextMoves.isEmpty()) {
                endTime = System.nanoTime();
                return false;
            }

            nextMoves.sort(Comparator.comparingInt(a -> a[2]));
            x = nextMoves.get(0)[0];
            y = nextMoves.get(0)[1];
            board.board[x][y] = i;
        }

        endTime = System.nanoTime();
        return true;
    }

    public long getExecutionTimeMillis() {
        return (endTime - startTime) / 1_000_000;
    }
}

