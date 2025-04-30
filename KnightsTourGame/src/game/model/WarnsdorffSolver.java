package game.model;

public class WarnsdorffSolver {
    private final Board board;
    private long startTime, endTime;
    private final int[][] moveCounts = new int[Board.SIZE][Board.SIZE];
    private final boolean[][] visited = new boolean[Board.SIZE][Board.SIZE];
    private static final int[] moveX = KnightMove.moveX;
    private static final int[] moveY = KnightMove.moveY;
    private static final int SIZE = Board.SIZE;
    private static final int SIZE_SQUARED = SIZE * SIZE;

    public WarnsdorffSolver(Board board) {
        this.board = board;
    }

    public boolean solve(int startX, int startY) {
        board.resetBoard();
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                visited[i][j] = false;
            }
        }
        int x = startX, y = startY;
        board.board[x][y] = 0;
        visited[x][y] = true;
        startTime = System.nanoTime();

        // Pre-calculate move counts for all positions
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                moveCounts[i][j] = countOnwardMoves(i, j);
            }
        }

        for (int move = 1; move < SIZE_SQUARED; move++) {
            int minMoves = Integer.MAX_VALUE;
            int bestX = -1, bestY = -1;
            int currentX = x;
            int currentY = y;

            // Unroll the loop for better performance
            int nextX, nextY;
            
            // Move 1
            nextX = currentX + moveX[0];
            nextY = currentY + moveY[0];
            if (isValidMove(nextX, nextY) && moveCounts[nextX][nextY] < minMoves) {
                minMoves = moveCounts[nextX][nextY];
                bestX = nextX;
                bestY = nextY;
            }
            
            // Move 2
            nextX = currentX + moveX[1];
            nextY = currentY + moveY[1];
            if (isValidMove(nextX, nextY) && moveCounts[nextX][nextY] < minMoves) {
                minMoves = moveCounts[nextX][nextY];
                bestX = nextX;
                bestY = nextY;
            }
            
            // Move 3
            nextX = currentX + moveX[2];
            nextY = currentY + moveY[2];
            if (isValidMove(nextX, nextY) && moveCounts[nextX][nextY] < minMoves) {
                minMoves = moveCounts[nextX][nextY];
                bestX = nextX;
                bestY = nextY;
            }
            
            // Move 4
            nextX = currentX + moveX[3];
            nextY = currentY + moveY[3];
            if (isValidMove(nextX, nextY) && moveCounts[nextX][nextY] < minMoves) {
                minMoves = moveCounts[nextX][nextY];
                bestX = nextX;
                bestY = nextY;
            }
            
            // Move 5
            nextX = currentX + moveX[4];
            nextY = currentY + moveY[4];
            if (isValidMove(nextX, nextY) && moveCounts[nextX][nextY] < minMoves) {
                minMoves = moveCounts[nextX][nextY];
                bestX = nextX;
                bestY = nextY;
            }
            
            // Move 6
            nextX = currentX + moveX[5];
            nextY = currentY + moveY[5];
            if (isValidMove(nextX, nextY) && moveCounts[nextX][nextY] < minMoves) {
                minMoves = moveCounts[nextX][nextY];
                bestX = nextX;
                bestY = nextY;
            }
            
            // Move 7
            nextX = currentX + moveX[6];
            nextY = currentY + moveY[6];
            if (isValidMove(nextX, nextY) && moveCounts[nextX][nextY] < minMoves) {
                minMoves = moveCounts[nextX][nextY];
                bestX = nextX;
                bestY = nextY;
            }
            
            // Move 8
            nextX = currentX + moveX[7];
            nextY = currentY + moveY[7];
            if (isValidMove(nextX, nextY) && moveCounts[nextX][nextY] < minMoves) {
                minMoves = moveCounts[nextX][nextY];
                bestX = nextX;
                bestY = nextY;
            }

            if (bestX == -1) {
                endTime = System.nanoTime();
                return false;
            }

            // Update move counts for affected positions
            updateMoveCounts(x, y);
            
            x = bestX;
            y = bestY;
            board.board[x][y] = move;
            visited[x][y] = true;
        }

        endTime = System.nanoTime();
        return true;
    }

    private boolean isValidMove(int x, int y) {
        return x >= 0 && x < SIZE && y >= 0 && y < SIZE && !visited[x][y];
    }

    private int countOnwardMoves(int x, int y) {
        int count = 0;
        int nextX, nextY;
        
        // Unroll the loop for better performance
        nextX = x + moveX[0];
        nextY = y + moveY[0];
        if (isValidMove(nextX, nextY)) count++;
        
        nextX = x + moveX[1];
        nextY = y + moveY[1];
        if (isValidMove(nextX, nextY)) count++;
        
        nextX = x + moveX[2];
        nextY = y + moveY[2];
        if (isValidMove(nextX, nextY)) count++;
        
        nextX = x + moveX[3];
        nextY = y + moveY[3];
        if (isValidMove(nextX, nextY)) count++;
        
        nextX = x + moveX[4];
        nextY = y + moveY[4];
        if (isValidMove(nextX, nextY)) count++;
        
        nextX = x + moveX[5];
        nextY = y + moveY[5];
        if (isValidMove(nextX, nextY)) count++;
        
        nextX = x + moveX[6];
        nextY = y + moveY[6];
        if (isValidMove(nextX, nextY)) count++;
        
        nextX = x + moveX[7];
        nextY = y + moveY[7];
        if (isValidMove(nextX, nextY)) count++;
        
        return count;
    }

    private void updateMoveCounts(int x, int y) {
        // Update move counts for positions that could be affected by the current move
        int nextX, nextY;
        
        nextX = x + moveX[0];
        nextY = y + moveY[0];
        if (nextX >= 0 && nextX < SIZE && nextY >= 0 && nextY < SIZE) {
            moveCounts[nextX][nextY] = countOnwardMoves(nextX, nextY);
        }
        
        nextX = x + moveX[1];
        nextY = y + moveY[1];
        if (nextX >= 0 && nextX < SIZE && nextY >= 0 && nextY < SIZE) {
            moveCounts[nextX][nextY] = countOnwardMoves(nextX, nextY);
        }
        
        nextX = x + moveX[2];
        nextY = y + moveY[2];
        if (nextX >= 0 && nextX < SIZE && nextY >= 0 && nextY < SIZE) {
            moveCounts[nextX][nextY] = countOnwardMoves(nextX, nextY);
        }
        
        nextX = x + moveX[3];
        nextY = y + moveY[3];
        if (nextX >= 0 && nextX < SIZE && nextY >= 0 && nextY < SIZE) {
            moveCounts[nextX][nextY] = countOnwardMoves(nextX, nextY);
        }
        
        nextX = x + moveX[4];
        nextY = y + moveY[4];
        if (nextX >= 0 && nextX < SIZE && nextY >= 0 && nextY < SIZE) {
            moveCounts[nextX][nextY] = countOnwardMoves(nextX, nextY);
        }
        
        nextX = x + moveX[5];
        nextY = y + moveY[5];
        if (nextX >= 0 && nextX < SIZE && nextY >= 0 && nextY < SIZE) {
            moveCounts[nextX][nextY] = countOnwardMoves(nextX, nextY);
        }
        
        nextX = x + moveX[6];
        nextY = y + moveY[6];
        if (nextX >= 0 && nextX < SIZE && nextY >= 0 && nextY < SIZE) {
            moveCounts[nextX][nextY] = countOnwardMoves(nextX, nextY);
        }
        
        nextX = x + moveX[7];
        nextY = y + moveY[7];
        if (nextX >= 0 && nextX < SIZE && nextY >= 0 && nextY < SIZE) {
            moveCounts[nextX][nextY] = countOnwardMoves(nextX, nextY);
        }
    }

    public long getExecutionTimeMillis() {
        return (endTime - startTime) / 1_000_000;
    }
}
