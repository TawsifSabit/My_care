package com.mycare.view;

import com.mycare.util.DBConnection;
import com.mycare.util.UIUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ReportsView extends JFrame {
    private JLabel totalPatientsLabel;
    private JLabel totalDoctorsLabel;
    private JLabel totalAppointmentsLabel;
    private JLabel upcomingAppointmentsLabel;
    private JLabel cancelledAppointmentsLabel;
    private JLabel availableRoomsLabel;
    private JLabel totalRevenueLabel;

    public ReportsView() {
        UIUtil.initModernTheme();
        initializeUI();
        loadMetrics();
    }

    private void initializeUI() {
        setTitle("Reports & Dashboard");
        setSize(820, 560);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        UIUtil.styleFrame(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(245, 247, 250));
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        totalPatientsLabel = new JLabel("Total Patients: 0");
        totalDoctorsLabel = new JLabel("Total Doctors: 0");
        totalAppointmentsLabel = new JLabel("Total Appointments: 0");
        upcomingAppointmentsLabel = new JLabel("Scheduled Appointments: 0");
        cancelledAppointmentsLabel = new JLabel("Cancelled Appointments: 0");
        availableRoomsLabel = new JLabel("Available Rooms: 0");
        totalRevenueLabel = new JLabel("Total Revenue: 0.00");

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(totalPatientsLabel, gbc);
        gbc.gridy = 1;
        panel.add(totalDoctorsLabel, gbc);
        gbc.gridy = 2;
        panel.add(totalAppointmentsLabel, gbc);
        gbc.gridy = 3;
        panel.add(upcomingAppointmentsLabel, gbc);
        gbc.gridy = 4;
        panel.add(cancelledAppointmentsLabel, gbc);
        gbc.gridy = 5;
        panel.add(availableRoomsLabel, gbc);
        gbc.gridy = 6;
        panel.add(totalRevenueLabel, gbc);

        gbc.gridy = 7;
        JButton refreshButton = UIUtil.createPrimaryButton("Refresh Metrics");
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadMetrics();
            }
        });
        panel.add(refreshButton, gbc);

        add(panel);
    }

    private void loadMetrics() {
        try (Connection conn = DBConnection.getConnection()) {
            totalPatientsLabel.setText("Total Patients: " + count(conn, "SELECT COUNT(*) FROM patients"));
            totalDoctorsLabel.setText("Total Doctors: " + count(conn, "SELECT COUNT(*) FROM doctors"));
            totalAppointmentsLabel.setText("Total Appointments: " + count(conn, "SELECT COUNT(*) FROM appointments"));
            upcomingAppointmentsLabel.setText("Scheduled Appointments: " + count(conn, "SELECT COUNT(*) FROM appointments WHERE status='scheduled'"));
            cancelledAppointmentsLabel.setText("Cancelled Appointments: " + count(conn, "SELECT COUNT(*) FROM appointments WHERE status='cancelled'"));
            availableRoomsLabel.setText("Available Rooms: " + count(conn, "SELECT COUNT(*) FROM rooms WHERE status='available'"));
            totalRevenueLabel.setText(String.format("Total Revenue: %.2f", sum(conn, "SELECT IFNULL(SUM(total_amount), 0) FROM bills WHERE status='paid'")));
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Unable to load reports: " + e.getMessage());
        }
    }

    private int count(Connection conn, String sql) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    private double sum(Connection conn, String sql) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getDouble(1);
            }
        }
        return 0.0;
    }
}
