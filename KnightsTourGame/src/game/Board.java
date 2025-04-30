package game;

public class Board {
    public static final int SIZE = 8;
    public int[][] board;

    public Board() {
        board = new int[SIZE][SIZE];
        resetBoard();
    }

    public void resetBoard() {
        for (int i = 0; i < SIZE; i++)
            for (int j = 0; j < SIZE; j++)
                board[i][j] = -1;
    }

    public boolean isSafe(int x, int y) {
        return (x >= 0 && x < SIZE && y >= 0 && y < SIZE && board[x][y] == -1);
    }

    public void printBoard() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                System.out.printf("%2d ", board[i][j]);
            }
            System.out.println();
        }
    }
}

