package test;

import game.*;
import game.model.BacktrackingSolver;
import game.model.Board;
import game.model.WarnsdorffSolver;

public class KnightsTourTest {
    public static void main(String[] args) {
        Board board = new Board();
        BacktrackingSolver backSolver = new BacktrackingSolver(board);
        WarnsdorffSolver warnSolver = new WarnsdorffSolver(board);

        boolean b1 = backSolver.solve(0, 0);
        boolean b2 = warnSolver.solve(0, 0);

        assert b1 : "Backtracking failed!";
        assert b2 : "Warnsdorff's Rule failed!";
        System.out.println("✅ All basic knight tour tests passed.");
    }
}
