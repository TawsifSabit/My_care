package com.mycare.model;

public class Bill {
    private int id;
    private int patientId;
    private double totalAmount;
    private String status;

    public Bill() {}

    public Bill(int id, int patientId, double totalAmount, String status) {
        this.id = id;
        this.patientId = patientId;
        this.totalAmount = totalAmount;
        this.status = status;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getPatientId() { return patientId; }
    public void setPatientId(int patientId) { this.patientId = patientId; }
    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
