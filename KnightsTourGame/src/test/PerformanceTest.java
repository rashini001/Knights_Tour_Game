package test;

import game.model.BacktrackingSolver;
import game.model.Board;
import game.model.WarnsdorffSolver;
import java.util.Random;

public class PerformanceTest {
    private static final int TEST_ROUNDS = 10;
    private static final Random random = new Random();

    public static void main(String[] args) {
        System.out.println("Performance Test Results (in milliseconds)");
        System.out.println("Round\tBacktracking\tWarnsdorff");
        System.out.println("----------------------------------------");

        for (int round = 1; round <= TEST_ROUNDS; round++) {
            Board board = new Board();
            int startX = random.nextInt(Board.SIZE);
            int startY = random.nextInt(Board.SIZE);

            BacktrackingSolver backSolver = new BacktrackingSolver(board);
            WarnsdorffSolver warnSolver = new WarnsdorffSolver(board);

            backSolver.solve(startX, startY);
            warnSolver.solve(startX, startY);

            System.out.printf("%d\t%d\t\t%d%n",
                round,
                backSolver.getExecutionTimeMillis(),
                warnSolver.getExecutionTimeMillis());
        }
    }
} 