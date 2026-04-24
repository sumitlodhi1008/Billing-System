import java.sql.Timestamp;

/**
 * Model class representing a Bill.
 */
public class Bill {
    private int id;
    private String customerName;
    private double totalAmount;
    private double gstAmount;
    private double grandTotal;
    private Timestamp dateTime;

    public Bill() {}

    public Bill(String customerName, double totalAmount, double gstAmount, double grandTotal) {
        this.customerName = customerName;
        this.totalAmount = totalAmount;
        this.gstAmount = gstAmount;
        this.grandTotal = grandTotal;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public double getGstAmount() { return gstAmount; }
    public void setGstAmount(double gstAmount) { this.gstAmount = gstAmount; }

    public double getGrandTotal() { return grandTotal; }
    public void setGrandTotal(double grandTotal) { this.grandTotal = grandTotal; }

    public Timestamp getDateTime() { return dateTime; }
    public void setDateTime(Timestamp dateTime) { this.dateTime = dateTime; }
}
