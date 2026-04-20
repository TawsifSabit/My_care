package com.mycare.dao;

import com.mycare.model.Bill;
import com.mycare.model.BillItem;
import com.mycare.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class BillingDAO {

    public int createBill(Bill bill) throws SQLException {
        String sql = "INSERT INTO bills (patient_id, total_amount, status) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, bill.getPatientId());
            stmt.setDouble(2, bill.getTotalAmount());
            stmt.setString(3, bill.getStatus());
            int affected = stmt.executeUpdate();
            if (affected > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return -1;
    }

    public boolean addBillItem(BillItem item) throws SQLException {
        String sql = "INSERT INTO bill_items (bill_id, item_type, description, amount) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, item.getBillId());
            stmt.setString(2, item.getItemType());
            stmt.setString(3, item.getDescription());
            stmt.setDouble(4, item.getAmount());
            boolean inserted = stmt.executeUpdate() > 0;
            if (inserted) {
                updateBillTotal(item.getBillId());
            }
            return inserted;
        }
    }

    public boolean markBillPaid(int billId) throws SQLException {
        String sql = "UPDATE bills SET status='paid' WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, billId);
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean updateBillTotal(int billId) throws SQLException {
        String sql = "UPDATE bills SET total_amount = (SELECT IFNULL(SUM(amount), 0) FROM bill_items WHERE bill_id=?) WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, billId);
            stmt.setInt(2, billId);
            return stmt.executeUpdate() > 0;
        }
    }

    public List<Bill> getAllBills() throws SQLException {
        List<Bill> bills = new ArrayList<>();
        String sql = "SELECT * FROM bills ORDER BY created_at DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                bills.add(new Bill(rs.getInt("id"), rs.getInt("patient_id"), rs.getDouble("total_amount"), rs.getString("status")));
            }
        }
        return bills;
    }

    public List<BillItem> getBillItems(int billId) throws SQLException {
        List<BillItem> items = new ArrayList<>();
        String sql = "SELECT * FROM bill_items WHERE bill_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, billId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                items.add(new BillItem(rs.getInt("id"), rs.getInt("bill_id"), rs.getString("item_type"),
                                       rs.getString("description"), rs.getDouble("amount")));
            }
        }
        return items;
    }
}
