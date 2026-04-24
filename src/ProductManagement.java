import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * Product Management - FULL SCREEN.
 * Big visible ADD / UPDATE / DELETE buttons.
 * Top section: Add/Edit form. Bottom section: Products table.
 */
public class ProductManagement extends JFrame {

    private JTable productTable;
    private DefaultTableModel tableModel;
    private JTextField nameField, priceField, quantityField;
    private JButton addBtn, updateBtn, deleteBtn, clearBtn;
    private JLabel formTitleLabel;
    private ProductDAO productDAO;
    private Dashboard parentDashboard;
    private int selectedProductId = -1;

    public ProductManagement(Dashboard parent) {
        this.parentDashboard = parent;
        this.productDAO = new ProductDAO();
        setTitle("Product Management - Add / Edit / Delete Products");
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
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 30, 10, 30));

        JLabel titleLabel = new JLabel("Product Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(16, 185, 129));
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JButton backBtn = makeBtn("< Back to Dashboard", new Color(71, 85, 105), Color.WHITE, 13);
        backBtn.addActionListener(e -> dispose());
        headerPanel.add(backBtn, BorderLayout.EAST);

        // ===== ADD/EDIT PRODUCT SECTION =====
        JPanel addSection = new JPanel(new BorderLayout());
        addSection.setBackground(new Color(30, 41, 59));
        addSection.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(5, 30, 10, 30),
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(16, 185, 129), 2),
                BorderFactory.createEmptyBorder(18, 25, 18, 25)
            )
        ));

        // Section title
        formTitleLabel = new JLabel("ADD NEW PRODUCT");
        formTitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        formTitleLabel.setForeground(new Color(16, 185, 129));

        // Fields in a row
        JPanel fieldsRow = new JPanel();
        fieldsRow.setLayout(new BoxLayout(fieldsRow, BoxLayout.X_AXIS));
        fieldsRow.setBackground(new Color(30, 41, 59));
        fieldsRow.setBorder(BorderFactory.createEmptyBorder(12, 0, 12, 0));

        fieldsRow.add(makeFieldGroup("Product Name:", nameField = makeTextField(18)));
        fieldsRow.add(Box.createHorizontalStrut(20));
        fieldsRow.add(makeFieldGroup("Price (Rs.):", priceField = makeTextField(10)));
        fieldsRow.add(Box.createHorizontalStrut(20));
        fieldsRow.add(makeFieldGroup("Stock Qty:", quantityField = makeTextField(8)));

        // Buttons row - BIG and COLORED
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        btnRow.setBackground(new Color(30, 41, 59));
        btnRow.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

        addBtn = makeBtn("   ADD PRODUCT   ", new Color(16, 185, 129), Color.WHITE, 15);
        addBtn.setPreferredSize(new Dimension(200, 45));
        addBtn.addActionListener(e -> addProduct());
        addBtn.setEnabled(true);
        btnRow.add(addBtn);

        updateBtn = makeBtn("   UPDATE   ", new Color(56, 189, 248), Color.WHITE, 15);
        updateBtn.setPreferredSize(new Dimension(150, 45));
        updateBtn.addActionListener(e -> updateProduct());
        updateBtn.setEnabled(false);
        btnRow.add(updateBtn);

        deleteBtn = makeBtn("   DELETE   ", new Color(220, 38, 38), Color.WHITE, 15);
        deleteBtn.setPreferredSize(new Dimension(150, 45));
        deleteBtn.addActionListener(e -> deleteProduct());
        deleteBtn.setEnabled(false);
        btnRow.add(deleteBtn);

        clearBtn = makeBtn("   CLEAR   ", new Color(100, 116, 139), Color.WHITE, 15);
        clearBtn.setPreferredSize(new Dimension(130, 45));
        clearBtn.addActionListener(e -> clearForm());
        btnRow.add(clearBtn);

        // Assemble add section
        JPanel addContent = new JPanel();
        addContent.setLayout(new BoxLayout(addContent, BoxLayout.Y_AXIS));
        addContent.setBackground(new Color(30, 41, 59));
        addContent.add(formTitleLabel);
        addContent.add(fieldsRow);
        addContent.add(btnRow);
        addSection.add(addContent, BorderLayout.CENTER);

        // ===== PRODUCT TABLE =====
        JPanel tableSection = new JPanel(new BorderLayout());
        tableSection.setBackground(new Color(15, 23, 42));
        tableSection.setBorder(BorderFactory.createEmptyBorder(10, 30, 20, 30));

        JLabel tableLbl = new JLabel("ALL PRODUCTS IN SHOP   (Click any row to Edit or Delete)");
        tableLbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
        tableLbl.setForeground(new Color(148, 163, 184));
        tableLbl.setBorder(BorderFactory.createEmptyBorder(0, 5, 10, 0));
        tableSection.add(tableLbl, BorderLayout.NORTH);

        String[] columns = {"ID", "Product Name", "Price (Rs.)", "Stock Qty"};
        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        productTable = new JTable(tableModel);
        styleTable(productTable);

        // Row click -> fill form
        productTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = productTable.getSelectedRow();
                if (row >= 0) {
                    selectedProductId = (int) tableModel.getValueAt(row, 0);
                    nameField.setText((String) tableModel.getValueAt(row, 1));
                    priceField.setText(String.valueOf(tableModel.getValueAt(row, 2)));
                    quantityField.setText(String.valueOf(tableModel.getValueAt(row, 3)));
                    updateBtn.setEnabled(true);
                    deleteBtn.setEnabled(true);
                    addBtn.setEnabled(false);
                    formTitleLabel.setText("EDITING: " + tableModel.getValueAt(row, 1));
                    formTitleLabel.setForeground(new Color(56, 189, 248));
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(productTable);
        scrollPane.getViewport().setBackground(new Color(30, 41, 59));
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(51, 65, 85), 1));
        tableSection.add(scrollPane, BorderLayout.CENTER);

        // Help text at bottom
        JPanel helpPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        helpPanel.setBackground(new Color(15, 23, 42));
        helpPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 0));
        JLabel helpLbl = new JLabel("Add New Product | Delete/Update Product");
        helpLbl.setForeground(new Color(100, 116, 139));
        helpLbl.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        helpPanel.add(helpLbl);
        tableSection.add(helpPanel, BorderLayout.SOUTH);

        // ===== ASSEMBLE MAIN =====
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBackground(new Color(15, 23, 42));
        topPanel.add(headerPanel);
        topPanel.add(addSection);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(tableSection, BorderLayout.CENTER);

        setContentPane(mainPanel);
    }

    // ===== HELPER METHODS =====

    private JTextField makeTextField(int cols) {
        JTextField field = new JTextField(cols);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        field.setBackground(new Color(51, 65, 85));
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(71, 85, 105), 1),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        field.setPreferredSize(new Dimension(0, 42));
        return field;
    }

    private JPanel makeFieldGroup(String label, JTextField field) {
        JPanel group = new JPanel();
        group.setLayout(new BoxLayout(group, BoxLayout.Y_AXIS));
        group.setBackground(new Color(30, 41, 59));

        JLabel lbl = new JLabel(label);
        lbl.setForeground(new Color(203, 213, 225));
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        lbl.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));

        field.setAlignmentX(Component.LEFT_ALIGNMENT);

        group.add(lbl);
        group.add(field);
        return group;
    }

    private JButton makeBtn(String text, Color bg, Color fg, int fontSize) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, fontSize));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(bg.darker(), 1),
            BorderFactory.createEmptyBorder(8, 18, 8, 18)
        ));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        Color hover = bg.brighter();
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { if (btn.isEnabled()) btn.setBackground(hover); }
            public void mouseExited(MouseEvent e) { if (btn.isEnabled()) btn.setBackground(bg); }
        });
        return btn;
    }

    private void styleTable(JTable table) {
        table.setBackground(new Color(30, 41, 59));
        table.setForeground(Color.WHITE);
        table.setGridColor(new Color(51, 65, 85));
        table.setSelectionBackground(new Color(56, 189, 248));
        table.setSelectionForeground(new Color(15, 23, 42));
        table.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        table.setRowHeight(35);
        table.setShowGrid(true);
        table.setIntercellSpacing(new Dimension(1, 1));

        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(51, 65, 85));
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 15));
        header.setPreferredSize(new Dimension(0, 40));

        table.getColumnModel().getColumn(0).setPreferredWidth(60);
        table.getColumnModel().getColumn(0).setMaxWidth(80);
        table.getColumnModel().getColumn(1).setPreferredWidth(350);
        table.getColumnModel().getColumn(2).setPreferredWidth(130);
        table.getColumnModel().getColumn(3).setPreferredWidth(120);
    }

    private void loadProducts() {
        tableModel.setRowCount(0);
        List<Product> products = productDAO.getAllProducts();
        for (Product p : products) {
            tableModel.addRow(new Object[]{p.getId(), p.getName(), p.getPrice(), p.getQuantity()});
        }
    }

    private boolean validateInput() {
        if (nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Product Name is required!", "Validation Error", JOptionPane.WARNING_MESSAGE);
            nameField.requestFocus();
            return false;
        }
        try {
            double price = Double.parseDouble(priceField.getText().trim());
            if (price < 0) { JOptionPane.showMessageDialog(this, "Price cannot be negative!", "Error", JOptionPane.WARNING_MESSAGE); return false; }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Price must be a valid number! (e.g. 55.00)", "Error", JOptionPane.WARNING_MESSAGE);
            priceField.requestFocus();
            return false;
        }
        try {
            int qty = Integer.parseInt(quantityField.getText().trim());
            if (qty < 0) { JOptionPane.showMessageDialog(this, "Quantity cannot be negative!", "Error", JOptionPane.WARNING_MESSAGE); return false; }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Quantity must be a whole number! (e.g. 50)", "Error", JOptionPane.WARNING_MESSAGE);
            quantityField.requestFocus();
            return false;
        }
        return true;
    }

    private void addProduct() {
        if (!validateInput()) return;
        Product p = new Product(nameField.getText().trim(),
            Double.parseDouble(priceField.getText().trim()),
            Integer.parseInt(quantityField.getText().trim()));
        if (productDAO.addProduct(p)) {
            JOptionPane.showMessageDialog(this,
                "Product Added Successfully!\n\nName: " + p.getName() +
                "\nPrice: Rs. " + String.format("%.2f", p.getPrice()) +
                "\nStock: " + p.getQuantity(),
                "Product Added", JOptionPane.INFORMATION_MESSAGE);
            loadProducts();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to add product! Check database.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateProduct() {
        if (selectedProductId == -1) { JOptionPane.showMessageDialog(this, "Select a product from table first!"); return; }
        if (!validateInput()) return;
        Product p = new Product(selectedProductId, nameField.getText().trim(),
            Double.parseDouble(priceField.getText().trim()),
            Integer.parseInt(quantityField.getText().trim()));
        if (productDAO.updateProduct(p)) {
            JOptionPane.showMessageDialog(this, "Product Updated!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadProducts();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteProduct() {
        if (selectedProductId == -1) { JOptionPane.showMessageDialog(this, "Select a product from table first!"); return; }
        int confirm = JOptionPane.showConfirmDialog(this,
            "DELETE this product?\n\n" + nameField.getText().trim(),
            "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            if (productDAO.deleteProduct(selectedProductId)) {
                JOptionPane.showMessageDialog(this, "Product Deleted!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadProducts();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Failed! Product may be in existing bills.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearForm() {
        nameField.setText("");
        priceField.setText("");
        quantityField.setText("");
        selectedProductId = -1;
        productTable.clearSelection();
        updateBtn.setEnabled(false);
        deleteBtn.setEnabled(false);
        addBtn.setEnabled(true);
        formTitleLabel.setText("ADD NEW PRODUCT");
        formTitleLabel.setForeground(new Color(16, 185, 129));
    }
}
