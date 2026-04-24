import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Calendar;

/**
 * Sales Report - FULL SCREEN.
 * Shows all bills with customer name, items, totals.
 */
public class SalesReport extends JFrame {

    private JTable billTable;
    private DefaultTableModel billModel;
    private JLabel totalSalesLabel, billCountLabel;
    private JComboBox<String> filterCombo;
    private BillDAO billDAO;
    private Dashboard parentDashboard;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");

    public SalesReport(Dashboard parent) {
        this.parentDashboard = parent;
        this.billDAO = new BillDAO();
        setTitle("Sales Records - Customer Purchase History");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(900, 600));
        initUI();
        loadAllBills();
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(15, 23, 42));

        // ===== HEADER =====
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(15, 23, 42));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 30, 8, 30));

        JLabel titleLabel = new JLabel("Sales Records - Customer Purchases");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(251, 191, 36));
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JButton backBtn = makeBtn("< Back to Dashboard", new Color(71, 85, 105), Color.WHITE, 13);
        backBtn.addActionListener(e -> dispose());
        headerPanel.add(backBtn, BorderLayout.EAST);

        // ===== FILTER BAR =====
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 8));
        filterPanel.setBackground(new Color(30, 41, 59));
        filterPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(0, 30, 10, 30),
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(51, 65, 85), 1),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)
            )
        ));

        JLabel filterLbl = new JLabel("Filter:");
        filterLbl.setForeground(new Color(203, 213, 225));
        filterLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        filterPanel.add(filterLbl);

        filterCombo = new JComboBox<>(new String[]{"All Bills", "Today", "Last 7 Days", "Last 30 Days"});
        filterCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        filterCombo.setBackground(new Color(51, 65, 85));
        filterCombo.setForeground(Color.WHITE);
        filterCombo.addActionListener(e -> applyFilter());
        filterPanel.add(filterCombo);

        filterPanel.add(Box.createHorizontalStrut(30));

        billCountLabel = new JLabel("Bills: 0");
        billCountLabel.setForeground(new Color(56, 189, 248));
        billCountLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        filterPanel.add(billCountLabel);

        filterPanel.add(Box.createHorizontalStrut(25));

        totalSalesLabel = new JLabel("Total Sales: Rs. 0.00");
        totalSalesLabel.setForeground(new Color(16, 185, 129));
        totalSalesLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        filterPanel.add(totalSalesLabel);

        // ===== TABLE =====
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(new Color(15, 23, 42));
        tablePanel.setBorder(BorderFactory.createEmptyBorder(5, 30, 10, 30));

        String[] cols = {"Bill ID", "Customer Name", "Items", "Subtotal (Rs.)", "GST (Rs.)", "Grand Total (Rs.)", "Date & Time"};
        billModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        billTable = new JTable(billModel);
        styleTable(billTable);

        billTable.getColumnModel().getColumn(0).setPreferredWidth(70);
        billTable.getColumnModel().getColumn(0).setMaxWidth(90);
        billTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        billTable.getColumnModel().getColumn(2).setPreferredWidth(60);
        billTable.getColumnModel().getColumn(2).setMaxWidth(80);
        billTable.getColumnModel().getColumn(3).setPreferredWidth(120);
        billTable.getColumnModel().getColumn(4).setPreferredWidth(100);
        billTable.getColumnModel().getColumn(5).setPreferredWidth(130);
        billTable.getColumnModel().getColumn(6).setPreferredWidth(160);

        billTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) viewBillDetails();
            }
        });

        JScrollPane scrollPane = new JScrollPane(billTable);
        scrollPane.getViewport().setBackground(new Color(30, 41, 59));
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(51, 65, 85), 1));
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        // Bottom buttons
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 5));
        bottomPanel.setBackground(new Color(15, 23, 42));

        JButton viewBtn = makeBtn("  VIEW BILL DETAILS  ", new Color(56, 189, 248), Color.WHITE, 14);
        viewBtn.addActionListener(e -> viewBillDetails());
        bottomPanel.add(viewBtn);

        JButton refreshBtn = makeBtn("  REFRESH  ", new Color(100, 116, 139), Color.WHITE, 14);
        refreshBtn.addActionListener(e -> applyFilter());
        bottomPanel.add(refreshBtn);

        JLabel hint = new JLabel("    (Double-click a bill to see what the customer purchased)");
        hint.setForeground(new Color(100, 116, 139));
        hint.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        bottomPanel.add(hint);

        tablePanel.add(bottomPanel, BorderLayout.SOUTH);

        // ===== ASSEMBLE =====
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBackground(new Color(15, 23, 42));
        topPanel.add(headerPanel);
        topPanel.add(filterPanel);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(tablePanel, BorderLayout.CENTER);

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
            public void mouseEntered(MouseEvent e) { btn.setBackground(hover); }
            public void mouseExited(MouseEvent e) { btn.setBackground(bg); }
        });
        return btn;
    }

    private void styleTable(JTable table) {
        table.setBackground(new Color(30, 41, 59));
        table.setForeground(Color.WHITE);
        table.setGridColor(new Color(51, 65, 85));
        table.setSelectionBackground(new Color(251, 191, 36));
        table.setSelectionForeground(new Color(15, 23, 42));
        table.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        table.setRowHeight(35);

        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(51, 65, 85));
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 15));
        header.setPreferredSize(new Dimension(0, 40));
    }

    private void loadBills(List<Bill> bills) {
        billModel.setRowCount(0);
        double totalSales = 0;
        for (Bill b : bills) {
            List<BillItem> items = billDAO.getBillItems(b.getId());
            int itemCount = 0;
            for (BillItem item : items) itemCount += item.getQuantity();

            String dateStr = b.getDateTime() != null ? dateFormat.format(b.getDateTime()) : "";

            billModel.addRow(new Object[]{
                b.getId(),
                b.getCustomerName() != null ? b.getCustomerName() : "Walk-in",
                itemCount,
                String.format("%.2f", b.getTotalAmount()),
                String.format("%.2f", b.getGstAmount()),
                String.format("%.2f", b.getGrandTotal()),
                dateStr
            });
            totalSales += b.getGrandTotal();
        }
        billCountLabel.setText("Bills: " + bills.size());
        totalSalesLabel.setText("Total Sales: Rs. " + String.format("%.2f", totalSales));
    }

    private void loadAllBills() { loadBills(billDAO.getAllBills()); }

    private void applyFilter() {
        String filter = (String) filterCombo.getSelectedItem();
        if ("All Bills".equals(filter)) { loadAllBills(); return; }

        Calendar cal = Calendar.getInstance();
        Timestamp to = new Timestamp(System.currentTimeMillis());
        Timestamp from;
        switch (filter) {
            case "Today":
                cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0); cal.set(Calendar.SECOND, 0);
                from = new Timestamp(cal.getTimeInMillis()); break;
            case "Last 7 Days":
                cal.add(Calendar.DAY_OF_MONTH, -7);
                from = new Timestamp(cal.getTimeInMillis()); break;
            case "Last 30 Days":
                cal.add(Calendar.DAY_OF_MONTH, -30);
                from = new Timestamp(cal.getTimeInMillis()); break;
            default: loadAllBills(); return;
        }
        loadBills(billDAO.getBillsByDateRange(from, to));
    }

    private void viewBillDetails() {
        int row = billTable.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select a bill first!"); return; }

        int billId = (int) billModel.getValueAt(row, 0);
        String customerName = (String) billModel.getValueAt(row, 1);
        List<BillItem> items = billDAO.getBillItems(billId);

        StringBuilder sb = new StringBuilder();
        sb.append("================================================\n");
        sb.append("              BILL DETAILS - #").append(billId).append("\n");
        sb.append("================================================\n");
        sb.append("Customer  : ").append(customerName).append("\n");
        sb.append("Date      : ").append(billModel.getValueAt(row, 6)).append("\n");
        sb.append("------------------------------------------------\n");
        sb.append(String.format("%-22s %5s %10s %10s\n", "Item", "Qty", "Price", "Total"));
        sb.append("------------------------------------------------\n");

        int totalItems = 0;
        for (BillItem item : items) {
            String name = item.getProductName();
            if (name.length() > 22) name = name.substring(0, 20) + "..";
            sb.append(String.format("%-22s %5d %10.2f %10.2f\n",
                name, item.getQuantity(), item.getPrice(), item.getItemTotal()));
            totalItems += item.getQuantity();
        }

        sb.append("------------------------------------------------\n");
        sb.append("Total Items: ").append(totalItems).append("\n");
        sb.append("------------------------------------------------\n");
        sb.append(String.format("%-39s %10s\n", "Subtotal:", billModel.getValueAt(row, 3)));
        sb.append(String.format("%-39s %10s\n", "GST (18%):", billModel.getValueAt(row, 4)));
        sb.append("================================================\n");
        sb.append(String.format("%-39s %10s\n", "GRAND TOTAL:", billModel.getValueAt(row, 5)));
        sb.append("================================================\n");
        //sb.append("\n").append(customerName).append(" ne ").append(totalItems).append(" items purchase kiye\n");

        JTextArea area = new JTextArea(sb.toString());
        area.setFont(new Font("Courier New", Font.PLAIN, 13));
        area.setEditable(false);
        area.setBackground(new Color(30, 41, 59));
        area.setForeground(Color.WHITE);
        JScrollPane scroll = new JScrollPane(area);
        scroll.setPreferredSize(new Dimension(520, 420));
        JOptionPane.showMessageDialog(this, scroll, "Bill #" + billId + " - " + customerName, JOptionPane.PLAIN_MESSAGE);
    }
}
