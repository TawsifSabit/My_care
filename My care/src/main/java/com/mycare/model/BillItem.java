package com.mycare.model;

public class BillItem {
    private int id;
    private int billId;
    private String itemType;
    private String description;
    private double amount;

    public BillItem() {}

    public BillItem(int id, int billId, String itemType, String description, double amount) {
        this.id = id;
        this.billId = billId;
        this.itemType = itemType;
        this.description = description;
        this.amount = amount;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getBillId() { return billId; }
    public void setBillId(int billId) { this.billId = billId; }
    public String getItemType() { return itemType; }
    public void setItemType(String itemType) { this.itemType = itemType; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
}
