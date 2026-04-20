package com.mycare.dao;

import com.mycare.model.Doctor;
import com.mycare.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DoctorDAO {

    public boolean addDoctor(Doctor doctor) throws SQLException {
        String sql = "INSERT INTO doctors (name, specialization, contact, email, schedule) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, doctor.getName());
            stmt.setString(2, doctor.getSpecialization());
            stmt.setString(3, doctor.getContact());
            stmt.setString(4, doctor.getEmail());
            stmt.setString(5, doctor.getSchedule());
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean updateDoctor(Doctor doctor) throws SQLException {
        String sql = "UPDATE doctors SET name=?, specialization=?, contact=?, email=?, schedule=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, doctor.getName());
            stmt.setString(2, doctor.getSpecialization());
            stmt.setString(3, doctor.getContact());
            stmt.setString(4, doctor.getEmail());
            stmt.setString(5, doctor.getSchedule());
            stmt.setInt(6, doctor.getId());
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean deleteDoctor(int id) throws SQLException {
        String sql = "DELETE FROM doctors WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    public Doctor getDoctorById(int id) throws SQLException {
        String sql = "SELECT * FROM doctors WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Doctor(rs.getInt("id"), rs.getString("name"), rs.getString("specialization"),
                                rs.getString("contact"), rs.getString("email"), rs.getString("schedule"));
            }
        }
        return null;
    }

    public List<Doctor> getAllDoctors() throws SQLException {
        List<Doctor> doctors = new ArrayList<>();
        String sql = "SELECT * FROM doctors";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                doctors.add(new Doctor(rs.getInt("id"), rs.getString("name"), rs.getString("specialization"),
                                     rs.getString("contact"), rs.getString("email"), rs.getString("schedule")));
            }
        }
        return doctors;
    }

    public List<Doctor> getDoctorsBySpecialization(String specialization) throws SQLException {
        List<Doctor> doctors = new ArrayList<>();
        String sql = "SELECT * FROM doctors WHERE specialization = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, specialization);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                doctors.add(new Doctor(rs.getInt("id"), rs.getString("name"), rs.getString("specialization"),
                                     rs.getString("contact"), rs.getString("email"), rs.getString("schedule")));
            }
        }
        return doctors;
    }
}