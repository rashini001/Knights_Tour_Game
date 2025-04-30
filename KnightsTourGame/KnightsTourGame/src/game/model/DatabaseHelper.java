package game.model;

import java.sql.*;

public class DatabaseHelper {
    private static final String URL = "jdbc:mysql://localhost:3306/knights_tour?serverTimezone=UTC";
    private static final String USERNAME = "root"; // <--- Your MySQL username
    private static final String PASSWORD = "";     // <--- Your MySQL password

    static {
        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            Statement stmt = conn.createStatement();

            String sql = "CREATE TABLE IF NOT EXISTS knight_results (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "name VARCHAR(255) NOT NULL," +
                    "algorithm VARCHAR(255) NOT NULL," +
                    "start_x INT NOT NULL," +
                    "start_y INT NOT NULL," +
                    "completed BOOLEAN NOT NULL," +
                    "time_taken DOUBLE," +
                    "timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";
            stmt.execute(sql);

            System.out.println("✅ Connected to DB and checked/created table knight_results.");
        } catch (SQLException e) {
            System.err.println("❌ ERROR during table creation: " + e.getMessage());
        }
    }

    public static void saveResult(String name, String algorithm, int x, int y, boolean completed, double time) {
        String sql = "INSERT INTO knight_results (name, algorithm, start_x, start_y, completed, time_taken) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {

            System.out.println("✅ Connected to Database: " + URL);

            conn.setAutoCommit(false); // 🔥 Force manual commit

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                System.out.println("📋 Saving to database:");
                System.out.println("Name: " + name);
                System.out.println("Algorithm: " + algorithm);
                System.out.println("StartX: " + x);
                System.out.println("StartY: " + y);
                System.out.println("Completed: " + completed);
                System.out.println("TimeTaken: " + time);

                pstmt.setString(1, name);
                pstmt.setString(2, algorithm);
                pstmt.setInt(3, x);
                pstmt.setInt(4, y);
                pstmt.setBoolean(5, completed);
                pstmt.setDouble(6, time);

                pstmt.executeUpdate();
                conn.commit(); // 🔥 Explicit COMMIT after insert
                System.out.println("✅ Data saved and committed successfully.");

            } catch (SQLException e) {
                conn.rollback(); // ❌ If error, rollback
                System.err.println("❌ ERROR inserting data (rolled back): " + e.getMessage());
            }

        } catch (SQLException e) {
            System.err.println("❌ ERROR connecting to database: " + e.getMessage());
        }
    }
}
