package game;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.sql.*;
import java.util.*;
import javax.imageio.ImageIO;
import java.util.List;

public class GameGUI extends JFrame {
    private final JButton[][] cells = new JButton[8][8];
    private final JLabel statusLabel = new JLabel("Welcome to Knight's Tour!");
    private final Board board = new Board();
    private int startX, startY;

    private JComboBox<String> playerDropdown;
    private DefaultComboBoxModel<String> playerModel;

    private DefaultTableModel tableModel;
    private JPanel boardPanel, controlPanel, gameTabPanel, resultsPanel;

    private boolean isDarkTheme = false;
    private ImageIcon knightIcon;

    public GameGUI() {
        setTitle("Knight's Tour Game");
        setSize(700, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        loadKnightIcon();

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Game", createGameTab());
        tabs.addTab("Results", createResultsTab());

        add(tabs);
        loadPlayersFromDB();
        resetBoard();
        setVisible(true);
    }

    private void loadKnightIcon() {
        try {
            BufferedImage img = ImageIO.read(getClass().getResource("/resources/knight.png"));
            knightIcon = new ImageIcon(img.getScaledInstance(60, 60, Image.SCALE_SMOOTH));
        } catch (IOException | IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, "Knight image missing! Add 'knight.png' to resources folder.", "Error", JOptionPane.ERROR_MESSAGE);
            knightIcon = null;
        }
    }

    private JPanel createGameTab() {
        gameTabPanel = new JPanel(new BorderLayout());

        statusLabel.setFont(new Font("Arial", Font.BOLD, 16));
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gameTabPanel.add(statusLabel, BorderLayout.NORTH);

        boardPanel = new JPanel(new GridLayout(8, 8));
        Font cellFont = new Font("Arial", Font.BOLD, 14);
        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++) {
                JButton btn = new JButton();
                btn.setFont(cellFont);
                btn.setEnabled(false);
                btn.setHorizontalAlignment(SwingConstants.CENTER);
                btn.setVerticalAlignment(SwingConstants.CENTER);
                cells[i][j] = btn;
                boardPanel.add(btn);
            }

        gameTabPanel.add(boardPanel, BorderLayout.CENTER);

        controlPanel = new JPanel();
        JButton btnBacktrack = new JButton("Backtracking");
        JButton btnWarnsdorff = new JButton("Warnsdorff");
        JButton btnReset = new JButton("Reset");

        JButton btnTheme = new JButton("Toggle Theme");
        btnTheme.addActionListener(e -> {
            isDarkTheme = !isDarkTheme;
            applyTheme();
        });

        playerModel = new DefaultComboBoxModel<>();
        playerDropdown = new JComboBox<>(playerModel);
        playerDropdown.setEditable(true);
        playerDropdown.setPreferredSize(new Dimension(180, 30));

        controlPanel.add(new JLabel("Player:"));
        controlPanel.add(playerDropdown);
        controlPanel.add(btnBacktrack);
        controlPanel.add(btnWarnsdorff);
        controlPanel.add(btnReset);
        controlPanel.add(btnTheme);

        btnBacktrack.addActionListener(e -> solveTourWithAnimation("Backtracking"));
        btnWarnsdorff.addActionListener(e -> solveTourWithAnimation("Warnsdorff"));
        btnReset.addActionListener(e -> resetBoard());

        gameTabPanel.add(controlPanel, BorderLayout.SOUTH);

        return gameTabPanel;
    }

    private JPanel createResultsTab() {
        resultsPanel = new JPanel(new BorderLayout());
        String[] cols = {"Name", "Algorithm", "StartX", "StartY", "Success", "Time (ms)"};
        tableModel = new DefaultTableModel(cols, 0);
        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        resultsPanel.add(scrollPane, BorderLayout.CENTER);

        JButton btnReload = new JButton("Reload Results");
        btnReload.addActionListener(e -> loadResultsIntoTable());
        resultsPanel.add(btnReload, BorderLayout.SOUTH);

        loadResultsIntoTable();
        return resultsPanel;
    }

    private void resetBoard() {
        board.resetBoard();
        Random rand = new Random();
        startX = rand.nextInt(8);
        startY = rand.nextInt(8);

        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++) {
                JButton btn = cells[i][j];
                btn.setText("");
                btn.setIcon(null);
            }

        applyTheme();
        cells[startX][startY].setBackground(Color.GREEN);
        cells[startX][startY].setIcon(knightIcon);
        statusLabel.setText("Knight starts at (" + startX + ", " + startY + ")");
    }

    private void applyTheme() {
        Color light = new Color(240, 217, 181);
        Color dark = new Color(181, 136, 99);
        Color bg = isDarkTheme ? Color.DARK_GRAY : Color.LIGHT_GRAY;
        Color fg = isDarkTheme ? Color.WHITE : Color.BLACK;

        gameTabPanel.setBackground(bg);
        boardPanel.setBackground(bg);
        controlPanel.setBackground(bg);
        statusLabel.setForeground(fg);
        controlPanel.setForeground(fg);

        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++) {
                JButton btn = cells[i][j];
                if (i == startX && j == startY && knightIcon != null) {
                    btn.setBackground(Color.GREEN);
                } else {
                    btn.setBackground((i + j) % 2 == 0 ? light : dark);
                }
                btn.setForeground(fg);
            }
    }

    private void solveTourWithAnimation(String algorithm) {
        board.resetBoard();

        SwingWorker<Boolean, Point> worker = new SwingWorker<>() {
            boolean success;
            long time;

            @Override
            protected Boolean doInBackground() throws Exception {
                long start = System.nanoTime();
                boolean solved;

                if (algorithm.equals("Backtracking")) {
                    BacktrackingSolver solver = new BacktrackingSolver(board);
                    solved = solver.solve(startX, startY);
                    time = solver.getExecutionTimeMillis();
                } else {
                    WarnsdorffSolver solver = new WarnsdorffSolver(board);
                    solved = solver.solve(startX, startY);
                    time = solver.getExecutionTimeMillis();
                }

                success = solved;
                if (success) {
                    List<Point> path = getTourPath(board);
                    for (Point p : path) {
                        publish(p);
                        Thread.sleep(200);
                    }
                }
                return success;
            }

            @Override
            protected void process(List<Point> chunks) {
                for (Point p : chunks) {
                    clearIcons();
                    cells[p.x][p.y].setIcon(knightIcon);
                }
            }

            @Override
            protected void done() {
                statusLabel.setText((success ? "✅ Success!" : "❌ Failed!") + " Time: " + time + " ms");

                String player = ((String) playerDropdown.getEditor().getItem()).trim();
                if (!player.isBlank()) {
                    if (!playerExists(player)) playerModel.addElement(player);
                    DatabaseHelper.saveResult(player, algorithm, startX, startY, success, time);
                }

                if (!success) {
                    JOptionPane.showMessageDialog(GameGUI.this,
                            "Knight could not complete the tour using " + algorithm,
                            "Failed", JOptionPane.WARNING_MESSAGE);
                }
            }
        };

        worker.execute();
    }

    private void clearIcons() {
        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++) {
                cells[i][j].setIcon(null);
            }
    }

    private List<Point> getTourPath(Board board) {
        List<Point> path = new ArrayList<>();
        int[][] moves = board.board;
        Point[] map = new Point[64];
        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++) {
                int step = moves[i][j];
                if (step >= 0 && step < 64) {
                    map[step] = new Point(i, j);
                }
            }
        for (int i = 0; i < 64; i++) {
            if (map[i] != null) path.add(map[i]);
        }
        return path;
    }

    private void loadResultsIntoTable() {
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
            JOptionPane.showMessageDialog(this, "DB Load Error: " + ex.getMessage());
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
            System.err.println("Error loading players: " + ex.getMessage());
        }
        for (String name : names) playerModel.addElement(name);
    }

    private boolean playerExists(String name) {
        for (int i = 0; i < playerModel.getSize(); i++) {
            if (playerModel.getElementAt(i).equalsIgnoreCase(name)) return true;
        }
        return false;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(GameGUI::new);
    }
}
