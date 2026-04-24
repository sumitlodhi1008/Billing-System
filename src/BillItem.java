/**
 * Model class representing an item in a bill.
 */
public class BillItem {
    private int id;
    private int billId;
    private int productId;
    private String productName;
    private int quantity;
    private double price;
    private double itemTotal;

    public BillItem() {}

    public BillItem(int productId, String productName, int quantity, double price) {
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
        this.itemTotal = price * quantity;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getBillId() { return billId; }
    public void setBillId(int billId) { this.billId = billId; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public double getItemTotal() { return itemTotal; }
    public void setItemTotal(double itemTotal) { this.itemTotal = itemTotal; }
}
