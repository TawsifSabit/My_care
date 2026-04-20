package com.mycare.dao;

import com.mycare.model.Patient;
import com.mycare.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PatientDAO {

    public boolean addPatient(Patient patient) throws SQLException {
        String sql = "INSERT INTO patients (name, age, gender, contact, email, address, medical_history) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, patient.getName());
            stmt.setInt(2, patient.getAge());
            stmt.setString(3, patient.getGender());
            stmt.setString(4, patient.getContact());
            stmt.setString(5, patient.getEmail());
            stmt.setString(6, patient.getAddress());
            stmt.setString(7, patient.getMedicalHistory());
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean updatePatient(Patient patient) throws SQLException {
        String sql = "UPDATE patients SET name=?, age=?, gender=?, contact=?, email=?, address=?, medical_history=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, patient.getName());
            stmt.setInt(2, patient.getAge());
            stmt.setString(3, patient.getGender());
            stmt.setString(4, patient.getContact());
            stmt.setString(5, patient.getEmail());
            stmt.setString(6, patient.getAddress());
            stmt.setString(7, patient.getMedicalHistory());
            stmt.setInt(8, patient.getId());
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean deletePatient(int id) throws SQLException {
        String sql = "DELETE FROM patients WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    public Patient getPatientById(int id) throws SQLException {
        String sql = "SELECT * FROM patients WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Patient(rs.getInt("id"), rs.getString("name"), rs.getInt("age"),
                                 rs.getString("gender"), rs.getString("contact"), rs.getString("email"),
                                 rs.getString("address"), rs.getString("medical_history"));
            }
        }
        return null;
    }

    public List<Patient> getAllPatients() throws SQLException {
        List<Patient> patients = new ArrayList<>();
        String sql = "SELECT * FROM patients";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                patients.add(new Patient(rs.getInt("id"), rs.getString("name"), rs.getInt("age"),
                                       rs.getString("gender"), rs.getString("contact"), rs.getString("email"),
                                       rs.getString("address"), rs.getString("medical_history")));
            }
        }
        return patients;
    }

    public List<Patient> searchPatients(String name) throws SQLException {
        List<Patient> patients = new ArrayList<>();
        String sql = "SELECT * FROM patients WHERE name LIKE ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + name + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                patients.add(new Patient(rs.getInt("id"), rs.getString("name"), rs.getInt("age"),
                                       rs.getString("gender"), rs.getString("contact"), rs.getString("email"),
                                       rs.getString("address"), rs.getString("medical_history")));
            }
        }
        return patients;
    }
}