package com.mycare.view;

import com.mycare.model.User;
import com.mycare.util.UIUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainDashboard extends JFrame {
    private User currentUser;
    private JButton patientBtn, doctorBtn, appointmentBtn, prescriptionBtn, roomBtn, billBtn, reportBtn, emergencyBtn;

    public MainDashboard(User user) {
        UIUtil.initModernTheme();
        this.currentUser = user;
        initializeUI();
    }

    private void initializeUI() {
        setTitle("MyCare Hospital - Dashboard");
        setSize(880, 620);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        UIUtil.styleFrame(this);

        JPanel panel = new JPanel(new GridLayout(3, 3, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        patientBtn = createNavButton("Patient Management");
        patientBtn.addActionListener(e -> new PatientManagementView().setVisible(true));

        doctorBtn = createNavButton("Doctor Management");
        doctorBtn.addActionListener(e -> new DoctorManagementView().setVisible(true));

        appointmentBtn = createNavButton("Appointment System");
        appointmentBtn.addActionListener(e -> new AppointmentManagementView().setVisible(true));

        prescriptionBtn = createNavButton("Prescription System");
        prescriptionBtn.addActionListener(e -> new PrescriptionManagementView().setVisible(true));

        roomBtn = createNavButton("Room/Bed Management");
        roomBtn.addActionListener(e -> new RoomManagementView().setVisible(true));

        billBtn = createNavButton("Billing & Payment");
        billBtn.addActionListener(e -> new BillingManagementView().setVisible(true));

        reportBtn = createNavButton("Reports & Dashboard");
        reportBtn.addActionListener(e -> new ReportsView().setVisible(true));

        emergencyBtn = createNavButton("ðŸš‘ Emergency Management");
        emergencyBtn.addActionListener(e -> new EmergencyManagementView().setVisible(true));

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(e -> {
            new LoginView().setVisible(true);
            dispose();
        });

        panel.add(patientBtn);
        panel.add(doctorBtn);
        panel.add(appointmentBtn);
        panel.add(prescriptionBtn);
        panel.add(roomBtn);
        panel.add(billBtn);
        panel.add(reportBtn);
        panel.add(emergencyBtn);
        panel.add(logoutBtn);

        panel.setBackground(new Color(245, 247, 250));

        // Disable buttons based on role
        if ("doctor".equals(currentUser.getRole())) {
            patientBtn.setEnabled(false);
            doctorBtn.setEnabled(false);
            roomBtn.setEnabled(false);
            billBtn.setEnabled(false);
        } else if ("receptionist".equals(currentUser.getRole())) {
            prescriptionBtn.setEnabled(false);
        }
        // Emergency management is available to all roles

        add(panel);
    }

    private JButton createNavButton(String text) {
        JButton button = UIUtil.createPrimaryButton(text);
        button.setPreferredSize(new Dimension(220, 70));
        return button;
    }
}