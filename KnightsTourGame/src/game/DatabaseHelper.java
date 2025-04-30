package game;

import java.sql.*;

public class DatabaseHelper {
    private static final String URL = "jdbc:mysql://localhost:3306/knights_tour";
    private static final String USERNAME = "root"; // <-- your MySQL username
    private static final String PASSWORD = "";     // <-- your MySQL password

    static {
        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
             Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS knight_results (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "name VARCHAR(255) NOT NULL," +
                    "algorithm VARCHAR(255) NOT NULL," +
                    "start_x INT NOT NULL," +
                    "start_y INT NOT NULL," +
                    "completed BOOLEAN NOT NULL," +
                    "time_taken DOUBLE," +
                    "timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void saveResult(String name, String algorithm, int x, int y, boolean completed, double time) {
        String sql = "INSERT INTO knight_results (name, algorithm, start_x, start_y, completed, time_taken) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, algorithm);
            pstmt.setInt(3, x);
            pstmt.setInt(4, y);
            pstmt.setBoolean(5, completed);
            pstmt.setDouble(6, time);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
