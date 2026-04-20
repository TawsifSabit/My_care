package com.mycare.dao;

import com.mycare.model.Appointment;
import com.mycare.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class AppointmentDAO {

    public boolean bookAppointment(Appointment appointment) throws SQLException {
        String sql = "INSERT INTO appointments (patient_id, doctor_id, appointment_date, status, notes) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, appointment.getPatientId());
            stmt.setInt(2, appointment.getDoctorId());
            stmt.setTimestamp(3, appointment.getAppointmentDate());
            stmt.setString(4, appointment.getStatus());
            stmt.setString(5, appointment.getNotes());
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean updateAppointment(Appointment appointment) throws SQLException {
        String sql = "UPDATE appointments SET patient_id=?, doctor_id=?, appointment_date=?, status=?, notes=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, appointment.getPatientId());
            stmt.setInt(2, appointment.getDoctorId());
            stmt.setTimestamp(3, appointment.getAppointmentDate());
            stmt.setString(4, appointment.getStatus());
            stmt.setString(5, appointment.getNotes());
            stmt.setInt(6, appointment.getId());
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean cancelAppointment(int id) throws SQLException {
        String sql = "UPDATE appointments SET status='cancelled' WHERE id=? AND status!='cancelled'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean deleteAppointment(int id) throws SQLException {
        String sql = "DELETE FROM appointments WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    public List<Appointment> getFilteredAppointments(Integer patientId, Integer doctorId, String status) throws SQLException {
        List<Appointment> appointments = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM appointments");
        boolean hasWhere = false;

        if (patientId != null) {
            sql.append(hasWhere ? " AND" : " WHERE").append(" patient_id=?");
            hasWhere = true;
        }
        if (doctorId != null) {
            sql.append(hasWhere ? " AND" : " WHERE").append(" doctor_id=?");
            hasWhere = true;
        }
        if (status != null && !status.isEmpty()) {
            sql.append(hasWhere ? " AND" : " WHERE").append(" status=?");
            hasWhere = true;
        }
        sql.append(" ORDER BY appointment_date");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            int index = 1;
            if (patientId != null) {
                stmt.setInt(index++, patientId);
            }
            if (doctorId != null) {
                stmt.setInt(index++, doctorId);
            }
            if (status != null && !status.isEmpty()) {
                stmt.setString(index++, status);
            }
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                appointments.add(new Appointment(rs.getInt("id"), rs.getInt("patient_id"), rs.getInt("doctor_id"),
                                               rs.getTimestamp("appointment_date"), rs.getString("status"), rs.getString("notes")));
            }
        }
        return appointments;
    }

    public List<Appointment> getAppointmentsByDoctor(int doctorId) throws SQLException {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT * FROM appointments WHERE doctor_id=? ORDER BY appointment_date";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, doctorId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                appointments.add(new Appointment(rs.getInt("id"), rs.getInt("patient_id"), rs.getInt("doctor_id"),
                                               rs.getTimestamp("appointment_date"), rs.getString("status"), rs.getString("notes")));
            }
        }
        return appointments;
    }

    public List<Appointment> getAllAppointments() throws SQLException {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT * FROM appointments ORDER BY appointment_date";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                appointments.add(new Appointment(rs.getInt("id"), rs.getInt("patient_id"), rs.getInt("doctor_id"),
                                               rs.getTimestamp("appointment_date"), rs.getString("status"), rs.getString("notes")));
            }
        }
        return appointments;
    }

    public List<Appointment> getAppointmentsByPatient(int patientId) throws SQLException {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT * FROM appointments WHERE patient_id=? ORDER BY appointment_date";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, patientId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                appointments.add(new Appointment(rs.getInt("id"), rs.getInt("patient_id"), rs.getInt("doctor_id"),
                                               rs.getTimestamp("appointment_date"), rs.getString("status"), rs.getString("notes")));
            }
        }
        return appointments;
    }

    public Appointment getAppointmentById(int id) throws SQLException {
        String sql = "SELECT * FROM appointments WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Appointment(rs.getInt("id"), rs.getInt("patient_id"), rs.getInt("doctor_id"),
                        rs.getTimestamp("appointment_date"), rs.getString("status"), rs.getString("notes"));
            }
        }
        return null;
    }

    public boolean isDoctorAvailable(int doctorId, Timestamp dateTime) throws SQLException {
        String sql = "SELECT COUNT(*) FROM appointments WHERE doctor_id=? AND appointment_date=? AND status!='cancelled'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, doctorId);
            stmt.setTimestamp(2, dateTime);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) == 0;
            }
        }
        return false;
    }
}