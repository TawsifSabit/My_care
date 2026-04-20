package com.mycare.dao;

import com.mycare.model.EmergencyPatient;
import com.mycare.util.DBConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EmergencyDAO {

    public boolean addEmergencyPatient(EmergencyPatient patient) throws SQLException {
        String sql = "INSERT INTO emergency_patients (name, age, condition_description, priority_level, arrival_time, status) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, patient.getName());
            stmt.setInt(2, patient.getAge());
            stmt.setString(3, patient.getCondition());
            stmt.setString(4, patient.getPriorityLevel());
            stmt.setTimestamp(5, Timestamp.valueOf(patient.getArrivalTime()));
            stmt.setString(6, patient.getStatus());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        patient.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
            return false;
        }
    }

    public boolean updateEmergencyPatient(EmergencyPatient patient) throws SQLException {
        String sql = "UPDATE emergency_patients SET name=?, age=?, condition_description=?, priority_level=?, status=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, patient.getName());
            stmt.setInt(2, patient.getAge());
            stmt.setString(3, patient.getCondition());
            stmt.setString(4, patient.getPriorityLevel());
            stmt.setString(5, patient.getStatus());
            stmt.setInt(6, patient.getId());
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean updatePatientStatus(int id, String status) throws SQLException {
        String sql = "UPDATE emergency_patients SET status=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, id);
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean dischargePatient(int id) throws SQLException {
        return updatePatientStatus(id, "Discharged");
    }

    public boolean deleteEmergencyPatient(int id) throws SQLException {
        String sql = "DELETE FROM emergency_patients WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    public EmergencyPatient getEmergencyPatientById(int id) throws SQLException {
        String sql = "SELECT * FROM emergency_patients WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToEmergencyPatient(rs);
                }
            }
        }
        return null;
    }

    public List<EmergencyPatient> getAllEmergencyPatients() throws SQLException {
        String sql = "SELECT * FROM emergency_patients ORDER BY " +
                    "CASE priority_level " +
                    "WHEN 'Critical' THEN 1 " +
                    "WHEN 'High' THEN 2 " +
                    "WHEN 'Medium' THEN 3 " +
                    "WHEN 'Low' THEN 4 " +
                    "END, arrival_time ASC";
        List<EmergencyPatient> patients = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                patients.add(mapResultSetToEmergencyPatient(rs));
            }
        }
        return patients;
    }

    public List<EmergencyPatient> getEmergencyPatientsByStatus(String status) throws SQLException {
        String sql = "SELECT * FROM emergency_patients WHERE status=? ORDER BY " +
                    "CASE priority_level " +
                    "WHEN 'Critical' THEN 1 " +
                    "WHEN 'High' THEN 2 " +
                    "WHEN 'Medium' THEN 3 " +
                    "WHEN 'Low' THEN 4 " +
                    "END, arrival_time ASC";
        List<EmergencyPatient> patients = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    patients.add(mapResultSetToEmergencyPatient(rs));
                }
            }
        }
        return patients;
    }

    public List<EmergencyPatient> getActiveEmergencyPatients() throws SQLException {
        String sql = "SELECT * FROM emergency_patients WHERE status IN ('Waiting', 'In Treatment') ORDER BY " +
                    "CASE priority_level " +
                    "WHEN 'Critical' THEN 1 " +
                    "WHEN 'High' THEN 2 " +
                    "WHEN 'Medium' THEN 3 " +
                    "WHEN 'Low' THEN 4 " +
                    "END, arrival_time ASC";
        List<EmergencyPatient> patients = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                patients.add(mapResultSetToEmergencyPatient(rs));
            }
        }
        return patients;
    }

    private EmergencyPatient mapResultSetToEmergencyPatient(ResultSet rs) throws SQLException {
        return new EmergencyPatient(
            rs.getInt("id"),
            rs.getString("name"),
            rs.getInt("age"),
            rs.getString("condition_description"),
            rs.getString("priority_level"),
            rs.getTimestamp("arrival_time").toLocalDateTime(),
            rs.getString("status")
        );
    }
}