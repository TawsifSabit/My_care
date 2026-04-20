package com.mycare.dao;

import com.mycare.model.Prescription;
import com.mycare.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class PrescriptionDAO {

    public boolean savePrescription(Prescription prescription) throws SQLException {
        String sql = "INSERT INTO prescriptions (appointment_id, medicines, dosage, notes) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, prescription.getAppointmentId());
            stmt.setString(2, prescription.getMedicines());
            stmt.setString(3, prescription.getDosage());
            stmt.setString(4, prescription.getNotes());
            return stmt.executeUpdate() > 0;
        }
    }

    public List<Prescription> getAllPrescriptions() throws SQLException {
        List<Prescription> prescriptions = new ArrayList<>();
        String sql = "SELECT * FROM prescriptions ORDER BY created_at DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                prescriptions.add(new Prescription(rs.getInt("id"), rs.getInt("appointment_id"),
                                                   rs.getString("medicines"), rs.getString("dosage"),
                                                   rs.getString("notes"), rs.getTimestamp("created_at")));
            }
        }
        return prescriptions;
    }

    public boolean hasPrescriptionForAppointment(int appointmentId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM prescriptions WHERE appointment_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, appointmentId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }
}
