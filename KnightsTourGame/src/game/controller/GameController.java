package game.controller;

import game.model.Board;
import game.model.DatabaseHelper;
import game.view.GameView;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Random;

public class GameController {
    private final Board board;
    private final GameView view;
    private boolean isAnimating = false;

    public GameController(Board board, GameView view) {
        this.board = board;
        this.view = view;

        view.addStartListener(this::startGame);
        view.addResetListener(this::resetGame);
        view.addThemeToggleListener(this::toggleTheme);

        resetGame();
    }

    private void resetGame() {
        board.resetBoard();
        Random rand = new Random();
        int startX = rand.nextInt(8);
        int startY = rand.nextInt(8);
        view.resetBoard(startX, startY);
    }

    private void startGame(String algorithm) {
        if (isAnimating) return;
        board.resetBoard();
        view.resetMoveCounter();
        isAnimating = true;

        SwingWorker<Boolean, Point> worker = new SwingWorker<>() {
            boolean success;
            long time;

            @Override
            protected Boolean doInBackground() throws Exception {
                boolean solved;
                if ("Backtracking".equals(algorithm)) {
                    game.model.BacktrackingSolver solver = new game.model.BacktrackingSolver(board);
                    solved = solver.solve(view.getStartX(), view.getStartY());
                    time = solver.getExecutionTimeMillis();
                } else {
                    game.model.WarnsdorffSolver solver = new game.model.WarnsdorffSolver(board);
                    solved = solver.solve(view.getStartX(), view.getStartY());
                    time = solver.getExecutionTimeMillis();
                }

                if (solved) {
                    List<Point> path = view.getTourPath(board.board);
                    for (Point p : path) {
                        publish(p);
                        Thread.sleep(200);
                    }
                }
                return solved;
            }

            @Override
            protected void process(List<Point> chunks) {
                for (Point p : chunks) {
                    view.updateKnightPosition(p);
                    view.incrementMoveCounter();
                }
            }

            @Override
            protected void done() {
                isAnimating = false;
                try {
                    boolean success = get();
                    view.showResult(success);

                    String playerName = view.getPlayerName();
                    if (playerName != null && !playerName.isBlank()) {
                        DatabaseHelper.saveResult(
                                playerName,
                                view.getCurrentAlgorithm(), // ✅ Correct
                                view.getStartX(),            // ✅ Correct
                                view.getStartY(),            // ✅ Correct
                                success,
                                view.getElapsedTime()
                        );
                        view.reloadResults();
                    }

                    // Delay and Show Win/Loss Custom Popup
                    Timer timer = new Timer(500, e -> {
                        int moves = view.getMoveCount();
                        if (success) {
                            // Set custom Win Message style
                            UIManager.put("OptionPane.background", new Color(255, 0, 98));
                            UIManager.put("Panel.background", new Color(213, 76, 132));
                            Font largeFont = new Font("Arial", Font.BOLD, 20);
                            UIManager.put("Label.font", largeFont);
                            UIManager.put("OptionPane.messageFont", largeFont);
                            UIManager.put("OptionPane.okButtonText", "OK");
                            UIManager.put("Button.background", Color.RED);

                            JOptionPane optionPane = new JOptionPane(
                                    "Congratulations!\nYou completed the Knight's Tour!\nTotal Moves: " + moves,
                                    JOptionPane.INFORMATION_MESSAGE
                            );
                            JDialog dialog = optionPane.createDialog(view, "You Win!");
                            dialog.setSize(500, 200);
                            dialog.setVisible(true);
                        } else {
                            // Set custom Loss Message style
                            UIManager.put("OptionPane.background", new Color(255, 0, 98));
                            UIManager.put("Panel.background", new Color(213, 76, 132));
                            Font largeFont = new Font("Arial", Font.BOLD, 20);
                            UIManager.put("Label.font", largeFont);
                            UIManager.put("OptionPane.messageFont", largeFont);
                            UIManager.put("OptionPane.okButtonText", "OK");
                            UIManager.put("Button.background", Color.RED);

                            JOptionPane optionPane = new JOptionPane(
                                    "Game Over!\nKnight could not complete the tour.\nTotal Moves: " + moves,
                                    JOptionPane.ERROR_MESSAGE
                            );
                            JDialog dialog = optionPane.createDialog(view, "You Lost!");
                            dialog.setSize(500, 200);
                            dialog.setVisible(true);
                        }
                    });
                    timer.setRepeats(false);
                    timer.start();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    private void toggleTheme() {
        view.toggleTheme();
    }
}