import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Billing Page - FULL SCREEN.
 * Customer name, product selection, cart, GST, bill generation.
 */
public class BillingPage extends JFrame {

    private static final double GST_RATE = 0.18;

    private JTextField customerNameField;
    private JComboBox<Product> productCombo;
    private JTextField qtyField;
    private JTable cartTable;
    private DefaultTableModel cartModel;
    private JLabel subtotalLabel, gstLabel, grandTotalLabel;
    private JButton addToCartBtn, removeFromCartBtn, generateBillBtn, clearCartBtn;
    private List<BillItem> cartItems;
    private ProductDAO productDAO;
    private BillDAO billDAO;
    private Dashboard parentDashboard;

    public BillingPage(Dashboard parent) {
        this.parentDashboard = parent;
        this.productDAO = new ProductDAO();
        this.billDAO = new BillDAO();
        this.cartItems = new ArrayList<>();
        setTitle("Billing System - Create Invoice");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(900, 700));
        initUI();
        loadProducts();
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(0, 0));
        mainPanel.setBackground(new Color(15, 23, 42));

        // ===== HEADER =====
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(15, 23, 42));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 30, 8, 30));

        JLabel titleLabel = new JLabel("Billing System");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(56, 189, 248));
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JButton backBtn = makeBtn("< Back to Dashboard", new Color(71, 85, 105), Color.WHITE, 13);
        backBtn.addActionListener(e -> dispose());
        headerPanel.add(backBtn, BorderLayout.EAST);

        // ===== CUSTOMER NAME =====
        JPanel custPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 8));
        custPanel.setBackground(new Color(30, 41, 59));
        custPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(0, 30, 5, 30),
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(251, 191, 36), 2),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)
            )
        ));

        JLabel custLbl = new JLabel("Customer Name:");
        custLbl.setForeground(new Color(251, 191, 36));
        custLbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
        custPanel.add(custLbl);

        customerNameField = new JTextField(25);
        customerNameField.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        customerNameField.setBackground(new Color(51, 65, 85));
        customerNameField.setForeground(Color.WHITE);
        customerNameField.setCaretColor(Color.WHITE);
        customerNameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(71, 85, 105), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        customerNameField.setPreferredSize(new Dimension(300, 40));
        custPanel.add(customerNameField);

        // ===== PRODUCT SELECTION =====
        JPanel selectPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        selectPanel.setBackground(new Color(30, 41, 59));
        selectPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(0, 30, 8, 30),
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(51, 65, 85), 1),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)
            )
        ));

        JLabel selLbl = new JLabel("Select Product:");
        selLbl.setForeground(new Color(203, 213, 225));
        selLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        selectPanel.add(selLbl);

        productCombo = new JComboBox<>();
        productCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        productCombo.setBackground(new Color(51, 65, 85));
        productCombo.setForeground(Color.WHITE);
        productCombo.setPreferredSize(new Dimension(320, 38));
        selectPanel.add(productCombo);

        JLabel qtyLbl = new JLabel("  Qty:");
        qtyLbl.setForeground(new Color(203, 213, 225));
        qtyLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        selectPanel.add(qtyLbl);

        qtyField = new JTextField("1", 5);
        qtyField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        qtyField.setBackground(new Color(51, 65, 85));
        qtyField.setForeground(Color.WHITE);
        qtyField.setCaretColor(Color.WHITE);
        qtyField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(71, 85, 105), 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        qtyField.setPreferredSize(new Dimension(70, 38));
        selectPanel.add(qtyField);

        addToCartBtn = makeBtn("  ADD TO CART  ", new Color(16, 185, 129), Color.WHITE, 14);
        addToCartBtn.setPreferredSize(new Dimension(170, 40));
        addToCartBtn.addActionListener(e -> addToCart());
        selectPanel.add(addToCartBtn);

        // ===== CART TABLE =====
        JPanel cartPanel = new JPanel(new BorderLayout());
        cartPanel.setBackground(new Color(15, 23, 42));
        cartPanel.setBorder(BorderFactory.createEmptyBorder(5, 30, 5, 30));

        String[] cols = {"#", "Product", "Price (Rs.)", "Qty", "Total (Rs.)"};
        cartModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        cartTable = new JTable(cartModel);
        styleTable(cartTable);

        JScrollPane scrollPane = new JScrollPane(cartTable);
        scrollPane.getViewport().setBackground(new Color(30, 41, 59));
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(51, 65, 85), 1));
        cartPanel.add(scrollPane, BorderLayout.CENTER);

        // Cart buttons
        JPanel cartBtnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 5));
        cartBtnPanel.setBackground(new Color(15, 23, 42));

        removeFromCartBtn = makeBtn("  REMOVE SELECTED  ", new Color(220, 38, 38), Color.WHITE, 13);
        removeFromCartBtn.addActionListener(e -> removeFromCart());
        cartBtnPanel.add(removeFromCartBtn);

        clearCartBtn = makeBtn("  CLEAR CART  ", new Color(100, 116, 139), Color.WHITE, 13);
        clearCartBtn.addActionListener(e -> clearCart());
        cartBtnPanel.add(clearCartBtn);

        cartPanel.add(cartBtnPanel, BorderLayout.SOUTH);

        // ===== SUMMARY + GENERATE =====
        JPanel summaryPanel = new JPanel(new BorderLayout());
        summaryPanel.setBackground(new Color(30, 41, 59));
        summaryPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(5, 30, 20, 30),
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(56, 189, 248), 2),
                BorderFactory.createEmptyBorder(15, 25, 15, 25)
            )
        ));

        JPanel totalsPanel = new JPanel(new GridLayout(3, 2, 15, 8));
        totalsPanel.setBackground(new Color(30, 41, 59));

        totalsPanel.add(makeLbl("Subtotal:", new Color(203, 213, 225), 15, false));
        subtotalLabel = makeLbl("Rs. 0.00", Color.WHITE, 15, true);
        totalsPanel.add(subtotalLabel);

        totalsPanel.add(makeLbl("GST (18%):", new Color(203, 213, 225), 15, false));
        gstLabel = makeLbl("Rs. 0.00", Color.WHITE, 15, true);
        totalsPanel.add(gstLabel);

        totalsPanel.add(makeLbl("GRAND TOTAL:", new Color(16, 185, 129), 18, false));
        grandTotalLabel = makeLbl("Rs. 0.00", new Color(16, 185, 129), 20, true);
        totalsPanel.add(grandTotalLabel);

        summaryPanel.add(totalsPanel, BorderLayout.CENTER);

        generateBillBtn = makeBtn("    GENERATE BILL    ", new Color(56, 189, 248), Color.WHITE, 17);
        generateBillBtn.setPreferredSize(new Dimension(250, 50));
        generateBillBtn.addActionListener(e -> generateBill());

        JPanel genPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        genPanel.setBackground(new Color(30, 41, 59));
        genPanel.add(generateBillBtn);
        summaryPanel.add(genPanel, BorderLayout.EAST);

        // ===== ASSEMBLE =====
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBackground(new Color(15, 23, 42));
        topPanel.add(headerPanel);
        topPanel.add(custPanel);
        topPanel.add(selectPanel);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(cartPanel, BorderLayout.CENTER);
        mainPanel.add(summaryPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    // ===== HELPERS =====

    private JButton makeBtn(String text, Color bg, Color fg, int fontSize) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, fontSize));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(bg.darker(), 1),
            BorderFactory.createEmptyBorder(8, 16, 8, 16)
        ));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        Color hover = bg.brighter();
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { if (btn.isEnabled()) btn.setBackground(hover); }
            public void mouseExited(MouseEvent e) { if (btn.isEnabled()) btn.setBackground(bg); }
        });
        return btn;
    }

    private JLabel makeLbl(String text, Color color, int fontSize, boolean rightAlign) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, fontSize));
        lbl.setForeground(color);
        if (rightAlign) lbl.setHorizontalAlignment(SwingConstants.RIGHT);
        return lbl;
    }

    private void styleTable(JTable table) {
        table.setBackground(new Color(30, 41, 59));
        table.setForeground(Color.WHITE);
        table.setGridColor(new Color(51, 65, 85));
        table.setSelectionBackground(new Color(56, 189, 248));
        table.setSelectionForeground(new Color(15, 23, 42));
        table.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        table.setRowHeight(35);

        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(51, 65, 85));
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 15));
        header.setPreferredSize(new Dimension(0, 38));

        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(0).setMaxWidth(60);
        table.getColumnModel().getColumn(1).setPreferredWidth(300);
        table.getColumnModel().getColumn(2).setPreferredWidth(120);
        table.getColumnModel().getColumn(3).setPreferredWidth(70);
        table.getColumnModel().getColumn(4).setPreferredWidth(130);
    }

    private void loadProducts() {
        productCombo.removeAllItems();
        List<Product> products = productDAO.getAllProducts();
        for (Product p : products) {
            if (p.getQuantity() > 0) productCombo.addItem(p);
        }
    }

    private void addToCart() {
        Product selected = (Product) productCombo.getSelectedItem();
        if (selected == null) { JOptionPane.showMessageDialog(this, "Please select a product!"); return; }

        int qty;
        try {
            qty = Integer.parseInt(qtyField.getText().trim());
            if (qty <= 0) { JOptionPane.showMessageDialog(this, "Quantity must be > 0!"); return; }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Enter a valid quantity!"); return;
        }

        int alreadyInCart = 0;
        for (BillItem item : cartItems)
            if (item.getProductId() == selected.getId()) alreadyInCart = item.getQuantity();

        if (qty + alreadyInCart > selected.getQuantity()) {
            JOptionPane.showMessageDialog(this, "Insufficient stock! Available: " + (selected.getQuantity() - alreadyInCart));
            return;
        }

        boolean found = false;
        for (BillItem item : cartItems) {
            if (item.getProductId() == selected.getId()) {
                item.setQuantity(item.getQuantity() + qty);
                item.setItemTotal(item.getPrice() * item.getQuantity());
                found = true; break;
            }
        }
        if (!found) cartItems.add(new BillItem(selected.getId(), selected.getName(), qty, selected.getPrice()));

        refreshCart();
        qtyField.setText("1");
    }

    private void removeFromCart() {
        int row = cartTable.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select an item to remove!"); return; }
        cartItems.remove(row);
        refreshCart();
    }

    private void clearCart() { cartItems.clear(); refreshCart(); }

    private void refreshCart() {
        cartModel.setRowCount(0);
        double subtotal = 0;
        int idx = 1;
        for (BillItem item : cartItems) {
            cartModel.addRow(new Object[]{idx++, item.getProductName(),
                String.format("%.2f", item.getPrice()), item.getQuantity(),
                String.format("%.2f", item.getItemTotal())});
            subtotal += item.getItemTotal();
        }
        double gst = subtotal * GST_RATE;
        double grandTotal = subtotal + gst;
        subtotalLabel.setText("Rs. " + String.format("%.2f", subtotal));
        gstLabel.setText("Rs. " + String.format("%.2f", gst));
        grandTotalLabel.setText("Rs. " + String.format("%.2f", grandTotal));
    }

    private void generateBill() {
        if (cartItems.isEmpty()) { JOptionPane.showMessageDialog(this, "Cart is empty! Add items first."); return; }

        String customerName = customerNameField.getText().trim();
        if (customerName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter customer name!");
            customerNameField.requestFocus(); return;
        }

        double subtotal = 0;
        for (BillItem item : cartItems) subtotal += item.getItemTotal();
        double gst = subtotal * GST_RATE;
        double grandTotal = subtotal + gst;

        Bill bill = new Bill(customerName, subtotal, gst, grandTotal);
        int billId = billDAO.saveBill(bill, cartItems);

        if (billId > 0) {
            StringBuilder sb = new StringBuilder();
            sb.append("================================================\n");
            sb.append("              SHOP BILLING SYSTEM               \n");
            sb.append("                 INVOICE BILL                   \n");
            sb.append("================================================\n");
            sb.append(String.format("Bill No   : %d\n", billId));
            sb.append(String.format("Customer  : %s\n", customerName));
            sb.append("------------------------------------------------\n");
            sb.append(String.format("%-22s %5s %10s %10s\n", "Item", "Qty", "Price", "Total"));
            sb.append("------------------------------------------------\n");
            for (BillItem item : cartItems) {
                String n = item.getProductName();
                if (n.length() > 22) n = n.substring(0, 20) + "..";
                sb.append(String.format("%-22s %5d %10.2f %10.2f\n", n,
                    item.getQuantity(), item.getPrice(), item.getItemTotal()));
            }
            sb.append("------------------------------------------------\n");
            sb.append(String.format("%-39s %10.2f\n", "Subtotal:", subtotal));
            sb.append(String.format("%-39s %10.2f\n", "GST (18%):", gst));
            sb.append("================================================\n");
            sb.append(String.format("%-39s %10.2f\n", "GRAND TOTAL:", grandTotal));
            sb.append("================================================\n");
            sb.append("\n          Thank you for shopping!              \n");
            sb.append("           Visit again, ").append(customerName).append("!\n");

            JTextArea billArea = new JTextArea(sb.toString());
            billArea.setFont(new Font("Courier New", Font.PLAIN, 13));
            billArea.setEditable(false);
            billArea.setBackground(new Color(30, 41, 59));
            billArea.setForeground(Color.WHITE);
            JScrollPane scroll = new JScrollPane(billArea);
            scroll.setPreferredSize(new Dimension(520, 400));
            JOptionPane.showMessageDialog(this, scroll, "Bill #" + billId + " - " + customerName, JOptionPane.PLAIN_MESSAGE);

            LoggerUtil.getLogger().info("Bill generated: ID " + billId + ", Customer: " + customerName);
            clearCart();
            customerNameField.setText("");
            loadProducts();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to save bill! Check database.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
