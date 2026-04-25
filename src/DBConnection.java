import java.sql.Connection;
import java.sql.Statement;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;

/**
 * Centralized database connection handler.
 * Manages JDBC connection to MySQL database.
 */
public class DBConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/shop_billing";
    private static final String USER = "root";
    private static final String PASSWORD = "";


    /**
     * Returns a new connection to the MySQL database.
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void initializeDatabase() {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/",USER,PASSWORD);
            Statement stmt = conn.createStatement()) {

            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS shop_billing");
            stmt.executeUpdate("use shop_billing");
            // Product Table
            stmt.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS product (" +
                            "id INT PRIMARY KEY AUTO_INCREMENT," +
                            "name VARCHAR(255) NOT NULL," +
                            "price DOUBLE NOT NULL," +
                            "quantity INT NOT NULL)");

            // Bill Table
            stmt.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS bill (" +
                            "id INT PRIMARY KEY AUTO_INCREMENT," +
                            "customer_name VARCHAR(255) NOT NULL DEFAULT 'Walk-in Customer'," +
                            "total_amount DOUBLE NOT NULL," +
                            "gst_amount DOUBLE NOT NULL," +
                            "grand_total DOUBLE NOT NULL," +
                            "date_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");

            // Bill Items Table
            stmt.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS bill_items (" +
                            "id INT PRIMARY KEY AUTO_INCREMENT," +
                            "bill_id INT NOT NULL," +
                            "product_id INT NOT NULL," +
                            "product_name VARCHAR(255) NOT NULL," +
                            "quantity INT NOT NULL," +
                            "price DOUBLE NOT NULL," +
                            "item_total DOUBLE NOT NULL," +
                            "FOREIGN KEY (bill_id) REFERENCES bill(id) ON DELETE CASCADE," +
                            "FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE CASCADE)");

            LoggerUtil.getLogger().info("Database initialized successfully.");

        } catch (SQLException e) {
            LoggerUtil.getLogger().log(Level.SEVERE, "Database initialization failed", e);
        }
    }

    /**
     * Safely closes a connection.
     */
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                LoggerUtil.getLogger().log(Level.WARNING, "Error closing connection", e);
            }
        }
    }
}
