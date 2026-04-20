package com.mycare.dao;

import com.mycare.model.Room;
import com.mycare.util.DBConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RoomDAO {

    public boolean addRoom(Room room) throws SQLException {
        String sql = "INSERT INTO rooms (room_number, bed_number, status) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, room.getRoomNumber());
            stmt.setString(2, room.getBedNumber());
            stmt.setString(3, room.getStatus());
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean assignRoom(int roomId, int patientId) throws SQLException {
        String sql = "UPDATE rooms SET status='occupied', patient_id=?, assigned_date=CURDATE(), discharge_date=NULL WHERE id=? AND status='available'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, patientId);
            stmt.setInt(2, roomId);
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean releaseRoom(int roomId) throws SQLException {
        String sql = "UPDATE rooms SET status='available', patient_id=NULL, discharge_date=CURDATE() WHERE id=? AND status='occupied'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, roomId);
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean deleteRoom(int roomId) throws SQLException {
        String sql = "DELETE FROM rooms WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, roomId);
            return stmt.executeUpdate() > 0;
        }
    }

    public List<Room> getFilteredRooms(String roomNumber, String bedNumber, String status, Integer patientId) throws SQLException {
        List<Room> rooms = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM rooms");
        boolean hasWhere = false;

        if (roomNumber != null && !roomNumber.trim().isEmpty()) {
            sql.append(hasWhere ? " AND" : " WHERE").append(" room_number LIKE ?");
            hasWhere = true;
        }
        if (bedNumber != null && !bedNumber.trim().isEmpty()) {
            sql.append(hasWhere ? " AND" : " WHERE").append(" bed_number LIKE ?");
            hasWhere = true;
        }
        if (status != null && !status.trim().isEmpty()) {
            sql.append(hasWhere ? " AND" : " WHERE").append(" status=?");
            hasWhere = true;
        }
        if (patientId != null) {
            sql.append(hasWhere ? " AND" : " WHERE").append(" patient_id=?");
            hasWhere = true;
        }

        sql.append(" ORDER BY room_number, bed_number");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            int index = 1;
            if (roomNumber != null && !roomNumber.trim().isEmpty()) {
                stmt.setString(index++, "%" + roomNumber.trim() + "%");
            }
            if (bedNumber != null && !bedNumber.trim().isEmpty()) {
                stmt.setString(index++, "%" + bedNumber.trim() + "%");
            }
            if (status != null && !status.trim().isEmpty()) {
                stmt.setString(index++, status);
            }
            if (patientId != null) {
                stmt.setInt(index++, patientId);
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Integer assignedPatientId = rs.getObject("patient_id") != null ? rs.getInt("patient_id") : null;
                    rooms.add(new Room(rs.getInt("id"), rs.getString("room_number"), rs.getString("bed_number"),
                                       rs.getString("status"), assignedPatientId,
                                       rs.getDate("assigned_date"), rs.getDate("discharge_date")));
                }
            }
        }
        return rooms;
    }

    public List<Room> getAllRooms() throws SQLException {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT * FROM rooms ORDER BY room_number, bed_number";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Integer patientId = rs.getObject("patient_id") != null ? rs.getInt("patient_id") : null;
                rooms.add(new Room(rs.getInt("id"), rs.getString("room_number"), rs.getString("bed_number"),
                                   rs.getString("status"), patientId,
                                   rs.getDate("assigned_date"), rs.getDate("discharge_date")));
            }
        }
        return rooms;
    }
}
