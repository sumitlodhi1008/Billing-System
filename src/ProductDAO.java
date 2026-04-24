import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * Data Access Object for Product operations.
 * Handles all CRUD operations for the product table.
 */
public class ProductDAO {

    /**
     * Add a new product to the database.
     */
    public boolean addProduct(Product product) {
        String sql = "INSERT INTO product (name, price, quantity) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, product.getName());
            ps.setDouble(2, product.getPrice());
            ps.setInt(3, product.getQuantity());
            int rows = ps.executeUpdate();
            LoggerUtil.getLogger().info("Product added: " + product.getName());
            return rows > 0;
        } catch (SQLException e) {
            LoggerUtil.getLogger().log(Level.SEVERE, "Error adding product", e);
            return false;
        }
    }

    /**
     * Update an existing product.
     */
    public boolean updateProduct(Product product) {
        String sql = "UPDATE product SET name = ?, price = ?, quantity = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, product.getName());
            ps.setDouble(2, product.getPrice());
            ps.setInt(3, product.getQuantity());
            ps.setInt(4, product.getId());
            int rows = ps.executeUpdate();
            LoggerUtil.getLogger().info("Product updated: ID " + product.getId());
            return rows > 0;
        } catch (SQLException e) {
            LoggerUtil.getLogger().log(Level.SEVERE, "Error updating product", e);
            return false;
        }
    }

    /**
     * Delete a product by ID.
     */
    public boolean deleteProduct(int id) {
        String sql = "DELETE FROM product WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            int rows = ps.executeUpdate();
            LoggerUtil.getLogger().info("Product deleted: ID " + id);
            return rows > 0;
        } catch (SQLException e) {
            LoggerUtil.getLogger().log(Level.SEVERE, "Error deleting product", e);
            return false;
        }
    }

    /**
     * Get all products from the database.
     */
    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM product ORDER BY name";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Product p = new Product(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getDouble("price"),
                    rs.getInt("quantity")
                );
                products.add(p);
            }
        } catch (SQLException e) {
            LoggerUtil.getLogger().log(Level.SEVERE, "Error fetching products", e);
        }
        return products;
    }

    /**
     * Get a single product by ID.
     */
    public Product getProductById(int id) {
        String sql = "SELECT * FROM product WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Product(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getDouble("price"),
                    rs.getInt("quantity")
                );
            }
        } catch (SQLException e) {
            LoggerUtil.getLogger().log(Level.SEVERE, "Error fetching product by ID", e);
        }
        return null;
    }

    /**
     * Update product quantity after billing (reduce stock).
     */
    public boolean reduceStock(int productId, int qty) {
        String sql = "UPDATE product SET quantity = quantity - ? WHERE id = ? AND quantity >= ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, qty);
            ps.setInt(2, productId);
            ps.setInt(3, qty);
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            LoggerUtil.getLogger().log(Level.SEVERE, "Error reducing stock", e);
            return false;
        }
    }
}
