import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * Data Access Object for Bill operations.
 * Handles bill storage and retrieval including customer name.
 */
public class BillDAO {

    /**
     * Save a bill and its items to the database.
     * Returns the generated bill ID, or -1 on failure.
     */
    public int saveBill(Bill bill, List<BillItem> items) {
        String billSql = "INSERT INTO bill (customer_name, total_amount, gst_amount, grand_total) VALUES (?, ?, ?, ?)";
        String itemSql = "INSERT INTO bill_items (bill_id, product_id, product_name, quantity, price, item_total) VALUES (?, ?, ?, ?, ?, ?)";
        Connection conn = null;

        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // Insert bill with customer name
            PreparedStatement billPs = conn.prepareStatement(billSql, Statement.RETURN_GENERATED_KEYS);
            billPs.setString(1, bill.getCustomerName());
            billPs.setDouble(2, bill.getTotalAmount());
            billPs.setDouble(3, bill.getGstAmount());
            billPs.setDouble(4, bill.getGrandTotal());
            billPs.executeUpdate();

            ResultSet keys = billPs.getGeneratedKeys();
            int billId = -1;
            if (keys.next()) {
                billId = keys.getInt(1);
            }

            // Insert bill items
            PreparedStatement itemPs = conn.prepareStatement(itemSql);
            for (BillItem item : items) {
                itemPs.setInt(1, billId);
                itemPs.setInt(2, item.getProductId());
                itemPs.setString(3, item.getProductName());
                itemPs.setInt(4, item.getQuantity());
                itemPs.setDouble(5, item.getPrice());
                itemPs.setDouble(6, item.getItemTotal());
                itemPs.addBatch();

                // Reduce stock using same connection
                String stockSql = "UPDATE product SET quantity = quantity - ? WHERE id = ? AND quantity >= ?";
                PreparedStatement stockPs = conn.prepareStatement(stockSql);
                stockPs.setInt(1, item.getQuantity());
                stockPs.setInt(2, item.getProductId());
                stockPs.setInt(3, item.getQuantity());
                stockPs.executeUpdate();
                stockPs.close();
            }
            itemPs.executeBatch();

            conn.commit();
            LoggerUtil.getLogger().info("Bill saved successfully: ID " + billId + " for customer: " + bill.getCustomerName());
            return billId;

        } catch (SQLException e) {
            LoggerUtil.getLogger().log(Level.SEVERE, "Error saving bill", e);
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) {
                    LoggerUtil.getLogger().log(Level.SEVERE, "Rollback failed", ex);
                }
            }
            return -1;
        } finally {
            DBConnection.closeConnection(conn);
        }
    }

    /**
     * Get all bills (summary) including customer name.
     */
    public List<Bill> getAllBills() {
        List<Bill> bills = new ArrayList<>();
        String sql = "SELECT * FROM bill ORDER BY date_time DESC";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Bill b = new Bill();
                b.setId(rs.getInt("id"));
                b.setCustomerName(rs.getString("customer_name"));
                b.setTotalAmount(rs.getDouble("total_amount"));
                b.setGstAmount(rs.getDouble("gst_amount"));
                b.setGrandTotal(rs.getDouble("grand_total"));
                b.setDateTime(rs.getTimestamp("date_time"));
                bills.add(b);
            }
        } catch (SQLException e) {
            LoggerUtil.getLogger().log(Level.SEVERE, "Error fetching bills", e);
        }
        return bills;
    }

    /**
     * Get bill items for a specific bill.
     */
    public List<BillItem> getBillItems(int billId) {
        List<BillItem> items = new ArrayList<>();
        String sql = "SELECT * FROM bill_items WHERE bill_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, billId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                BillItem item = new BillItem();
                item.setId(rs.getInt("id"));
                item.setBillId(rs.getInt("bill_id"));
                item.setProductId(rs.getInt("product_id"));
                item.setProductName(rs.getString("product_name"));
                item.setQuantity(rs.getInt("quantity"));
                item.setPrice(rs.getDouble("price"));
                item.setItemTotal(rs.getDouble("item_total"));
                items.add(item);
            }
        } catch (SQLException e) {
            LoggerUtil.getLogger().log(Level.SEVERE, "Error fetching bill items", e);
        }
        return items;
    }

    /**
     * Get a single bill by ID.
     */
    public Bill getBillById(int billId) {
        String sql = "SELECT * FROM bill WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, billId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Bill b = new Bill();
                b.setId(rs.getInt("id"));
                b.setCustomerName(rs.getString("customer_name"));
                b.setTotalAmount(rs.getDouble("total_amount"));
                b.setGstAmount(rs.getDouble("gst_amount"));
                b.setGrandTotal(rs.getDouble("grand_total"));
                b.setDateTime(rs.getTimestamp("date_time"));
                return b;
            }
        } catch (SQLException e) {
            LoggerUtil.getLogger().log(Level.SEVERE, "Error fetching bill by ID", e);
        }
        return null;
    }

    /**
     * Get bills filtered by date range.
     */
    public List<Bill> getBillsByDateRange(Timestamp from, Timestamp to) {
        List<Bill> bills = new ArrayList<>();
        String sql = "SELECT * FROM bill WHERE date_time BETWEEN ? AND ? ORDER BY date_time DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, from);
            ps.setTimestamp(2, to);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Bill b = new Bill();
                b.setId(rs.getInt("id"));
                b.setCustomerName(rs.getString("customer_name"));
                b.setTotalAmount(rs.getDouble("total_amount"));
                b.setGstAmount(rs.getDouble("gst_amount"));
                b.setGrandTotal(rs.getDouble("grand_total"));
                b.setDateTime(rs.getTimestamp("date_time"));
                bills.add(b);
            }
        } catch (SQLException e) {
            LoggerUtil.getLogger().log(Level.SEVERE, "Error fetching bills by date range", e);
        }
        return bills;
    }

    /**
     * Get total sales amount.
     */
    public double getTotalSales() {
        String sql = "SELECT COALESCE(SUM(grand_total), 0) as total FROM bill";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getDouble("total");
            }
        } catch (SQLException e) {
            LoggerUtil.getLogger().log(Level.SEVERE, "Error calculating total sales", e);
        }
        return 0;
    }
}
