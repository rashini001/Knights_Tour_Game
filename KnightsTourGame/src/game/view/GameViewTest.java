package game.view;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.awt.Point;

class GameViewTest {

    private GameView gameView;

    @BeforeEach
    void setUp() {
        gameView = new GameView();
    }

    @Test
    void testResetMoveCounter() {
        gameView.incrementMoveCounter();
        gameView.incrementMoveCounter();
        gameView.resetMoveCounter();
        assertEquals(0, gameView.getMoveCount(), "Move count should reset to 0");
    }

    @Test
    void testIncrementMoveCounter() {
        gameView.incrementMoveCounter();
        assertEquals(1, gameView.getMoveCount(), "Move count should increment by 1");
    }

    @Test
    void testResetBoardStartPosition() {
        int startX = 2;
        int startY = 3;
        gameView.resetBoard(startX, startY);

        assertEquals(startX, gameView.getStartX(), "Start X should match reset position");
        assertEquals(startY, gameView.getStartY(), "Start Y should match reset position");
    }

    @Test
    void testTourPathOrder() {
        int[][] board = new int[8][8];
        // Simulate knight moves
        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++) {
                board[i][j] = i * 8 + j;
            }

        var path = gameView.getTourPath(board);
        assertEquals(64, path.size(), "Path should contain 64 points");
        assertEquals(new Point(0,0), path.get(0), "First move should be (0,0)");
        assertEquals(new Point(7,7), path.get(63), "Last move should be (7,7)");
    }

    // Note: getPlayerName() testing requires GUI interaction (hard to automate),
    //      So either Mock GUI component or test indirectly.

}
