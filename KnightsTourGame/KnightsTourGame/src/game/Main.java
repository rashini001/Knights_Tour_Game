package game;

import game.controller.GameController;
import game.model.Board;
import game.view.GameView;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Board board = new Board();
            GameView view = new GameView();
            new GameController(board, view);
        });
    }
}
