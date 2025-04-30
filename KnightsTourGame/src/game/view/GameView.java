package game.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;
import javax.imageio.ImageIO;

public class GameView extends JFrame {
    private final JButton[][] cells = new JButton[8][8];
    private final JLabel statusLabel = new JLabel("Welcome to Knight's Tour!");
    private final JLabel moveLabel = new JLabel("Moves: 0");
    private int moveCount = 0;

    private final DefaultComboBoxModel<String> playerModel = new DefaultComboBoxModel<>();
    private final JComboBox<String> playerDropdown = new JComboBox<>(playerModel);
    private final DefaultTableModel tableModel = new DefaultTableModel(
            new String[]{"Name", "Algorithm", "StartX", "StartY", "Success", "Time(ms)"}, 0);

    private ImageIcon knightIcon;
    private JPanel boardPanel, controlPanel, gameTabPanel, resultsPanel;
    private boolean isDarkTheme = false;
    private long startTime;

    private Consumer<String> startListener;
    private Runnable resetListener;
    private Runnable themeToggleListener;

    private int startX, startY; // ✅ Track actual start position
    private String currentAlgorithm; // ✅ Track actual algorithm

    public GameView() {
        setTitle("Knight's Tour Game");
        setSize(750, 850);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        loadKnightIcon();

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Game", createGameTab());
        tabs.addTab("Results", createResultsTab());

        add(tabs);

        loadPlayersFromDB();
        setVisible(true);
    }

    private void loadKnightIcon() {
        try {
            BufferedImage img = ImageIO.read(getClass().getResource("/resources/knight.png"));
            knightIcon = new ImageIcon(img.getScaledInstance(60, 60, Image.SCALE_SMOOTH));
        } catch (IOException | IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, "Knight image missing! Please add knight.png in resources!", "Error", JOptionPane.ERROR_MESSAGE);
            knightIcon = null;
        }
    }

    private JPanel createGameTab() {
        gameTabPanel = new JPanel(new BorderLayout());

        JPanel topPanel = new JPanel(new GridLayout(2, 1));
        statusLabel.setFont(new Font("Arial", Font.BOLD, 16));
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        moveLabel.setFont(new Font("Arial", Font.BOLD, 16));
        moveLabel.setHorizontalAlignment(SwingConstants.CENTER);

        topPanel.add(statusLabel);
        topPanel.add(moveLabel);
        gameTabPanel.add(topPanel, BorderLayout.NORTH);

        boardPanel = new JPanel(new GridLayout(8, 8));
        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++) {
                JButton btn = new JButton();
                btn.setEnabled(false);
                btn.setHorizontalAlignment(SwingConstants.CENTER);
                btn.setVerticalAlignment(SwingConstants.CENTER);
                cells[i][j] = btn;
                boardPanel.add(btn);
            }
        gameTabPanel.add(boardPanel, BorderLayout.CENTER);

        controlPanel = new JPanel();
        JButton btnBacktracking = new JButton("Backtracking");
        JButton btnWarnsdorff = new JButton("Warnsdorff");
        JButton btnReset = new JButton("Reset");
        JButton btnTheme = new JButton("Toggle Theme");

        Color brownColor = new Color(60, 31, 6);
        btnBacktracking.setBackground(brownColor);
        btnWarnsdorff.setBackground(brownColor);
        btnReset.setBackground(brownColor);
        btnTheme.setBackground(brownColor);

        btnBacktracking.setForeground(Color.WHITE);
        btnWarnsdorff.setForeground(Color.WHITE);
        btnReset.setForeground(Color.WHITE);
        btnTheme.setForeground(Color.WHITE);

        playerDropdown.setEditable(true);
        playerDropdown.setPreferredSize(new Dimension(180, 30));

        controlPanel.add(new JLabel("Player:"));
        controlPanel.add(playerDropdown);
        controlPanel.add(btnBacktracking);
        controlPanel.add(btnWarnsdorff);
        controlPanel.add(btnReset);
        controlPanel.add(btnTheme);

        btnBacktracking.addActionListener(e -> {
            startTime = System.currentTimeMillis();
            currentAlgorithm = "Backtracking"; // ✅ Save current algorithm
            if (startListener != null) startListener.accept("Backtracking");
        });

        btnWarnsdorff.addActionListener(e -> {
            startTime = System.currentTimeMillis();
            currentAlgorithm = "Warnsdorff"; // ✅ Save current algorithm
            if (startListener != null) startListener.accept("Warnsdorff");
        });

        btnReset.addActionListener(e -> {
            if (resetListener != null) resetListener.run();
        });

        btnTheme.addActionListener(e -> {
            if (themeToggleListener != null) themeToggleListener.run();
        });

        gameTabPanel.add(controlPanel, BorderLayout.SOUTH);
        return gameTabPanel;
    }

    private JPanel createResultsTab() {
        resultsPanel = new JPanel(new BorderLayout());
        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        JButton btnReload = new JButton("Reload Results");
        btnReload.addActionListener(e -> reloadResults());

        resultsPanel.add(scrollPane, BorderLayout.CENTER);
        resultsPanel.add(btnReload, BorderLayout.SOUTH);

        reloadResults();
        return resultsPanel;
    }

    public void addStartListener(Consumer<String> listener) {
        this.startListener = listener;
    }

    public void addResetListener(Runnable listener) {
        this.resetListener = listener;
    }

    public void addThemeToggleListener(Runnable listener) {
        this.themeToggleListener = listener;
    }

    public void resetBoard(int startX, int startY) {
        this.startX = startX;
        this.startY = startY;
        Color light = new Color(153, 120, 70);
        Color dark = new Color(89, 62, 42);
        Color startPosColor = new Color(255, 0, 98);

        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++) {
                JButton btn = cells[i][j];
                btn.setText("");
                btn.setIcon(null);
                btn.setBackground((i + j) % 2 == 0 ? light : dark);
            }

        if (knightIcon != null) {
            cells[startX][startY].setIcon(knightIcon);
        }
        cells[startX][startY].setBackground(startPosColor);

        statusLabel.setText("Knight starts at (" + startX + ", " + startY + ")");
        resetMoveCounter();
    }

    public void updateKnightPosition(Point p) {
        clearIcons();
        cells[p.x][p.y].setIcon(knightIcon);
    }

    private void clearIcons() {
        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++) {
                cells[i][j].setIcon(null);
            }
    }

    public void resetMoveCounter() {
        moveCount = 0;
        moveLabel.setText("Moves: 0");
    }

    public void incrementMoveCounter() {
        moveCount++;
        moveLabel.setText("Moves: " + moveCount);
    }

    public int getMoveCount() {
        return moveCount;
    }

    public void toggleTheme() {
        isDarkTheme = !isDarkTheme;
        applyTheme();
    }

    private void applyTheme() {
        Color light = new Color(153, 120, 70);
        Color dark = new Color(89, 62, 42);
        Color bg = isDarkTheme ? Color.DARK_GRAY : Color.LIGHT_GRAY;
        Color fg = isDarkTheme ? Color.WHITE : Color.BLACK;

        gameTabPanel.setBackground(bg);
        boardPanel.setBackground(bg);
        controlPanel.setBackground(bg);
        statusLabel.setForeground(fg);
        moveLabel.setForeground(fg);

        for (Component component : boardPanel.getComponents()) {
            JButton btn = (JButton) component;
            if (btn.getIcon() == null) {
                btn.setBackground((btn.getX() + btn.getY()) % 2 == 0 ? light : dark);
            }
            btn.setForeground(fg);
        }
    }

    public void showResult(boolean success) {
        long time = System.currentTimeMillis() - startTime;
        statusLabel.setText((success ? " Success!" : " Failed!") + " Time: " + time + " ms");
    }

    public List<Point> getTourPath(int[][] board) {
        List<Point> path = new ArrayList<>();
        Point[] map = new Point[64];
        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++) {
                int step = board[i][j];
                if (step >= 0 && step < 64) {
                    map[step] = new Point(i, j);
                }
            }
        for (int i = 0; i < 64; i++) {
            if (map[i] != null) path.add(map[i]);
        }
        return path;
    }

    public String getPlayerName() {
        String playerName = (String) playerDropdown.getEditor().getItem();
        if (playerName == null || playerName.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Player name cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        return playerName;
    }

    public long getElapsedTime() {
        return System.currentTimeMillis() - startTime;
    }

    public int getStartX() {
        return startX;
    }

    public int getStartY() {
        return startY;
    }

    public String getCurrentAlgorithm() {
        return currentAlgorithm;
    }

    public void reloadResults() {
        tableModel.setRowCount(0);
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/knights_tour", "root", "");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT name, algorithm, start_x, start_y, completed, time_taken FROM knight_results")) {
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getString("name"),
                        rs.getString("algorithm"),
                        rs.getInt("start_x"),
                        rs.getInt("start_y"),
                        rs.getBoolean("completed"),
                        rs.getLong("time_taken")
                });
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading results from the database.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadPlayersFromDB() {
        Set<String> names = new HashSet<>();
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/knights_tour", "root", "");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT DISTINCT name FROM knight_results")) {
            while (rs.next()) {
                names.add(rs.getString("name"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading players from the database.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
        for (String name : names) playerModel.addElement(name);
    }
}
