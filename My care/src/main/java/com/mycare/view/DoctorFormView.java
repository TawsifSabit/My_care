package com.mycare.view;

import com.mycare.dao.DoctorDAO;
import com.mycare.model.Doctor;
import com.mycare.util.UIUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class DoctorFormView extends JDialog {
    private DoctorDAO doctorDAO;
    private Doctor doctor;
    private DoctorManagementView parentView;

    private JTextField nameField, contactField, emailField;
    private JComboBox<String> specializationCombo;
    private JTextArea scheduleArea;
    private JButton saveBtn, cancelBtn;

    public DoctorFormView(Doctor doctor, DoctorManagementView parent) {
        UIUtil.initModernTheme();
        this.doctor = doctor;
        this.parentView = parent;
        doctorDAO = new DoctorDAO();
        initializeUI();
        if (doctor != null) {
            populateFields();
        }
    }

    private void initializeUI() {
        setTitle(doctor == null ? "Add Doctor" : "Edit Doctor");
        setSize(520, 420);
        setModal(true);
        setLocationRelativeTo(parentView);
        UIUtil.styleWindow(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(245, 247, 250));
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Name
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        nameField = new JTextField(20);
        UIUtil.styleTextField(nameField);
        panel.add(nameField, gbc);

        // Specialization
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Specialization:"), gbc);
        gbc.gridx = 1;
        specializationCombo = new JComboBox<>(new String[]{"Cardiology", "Neurology", "Orthopedics", "Pediatrics", "Dermatology"});
        specializationCombo.setPreferredSize(new Dimension(250, 30));
        panel.add(specializationCombo, gbc);

        // Contact
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Contact:"), gbc);
        gbc.gridx = 1;
        contactField = new JTextField(20);
        UIUtil.styleTextField(contactField);
        panel.add(contactField, gbc);

        // Email
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        emailField = new JTextField(20);
        UIUtil.styleTextField(emailField);
        panel.add(emailField, gbc);

        // Schedule
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("Schedule:"), gbc);
        gbc.gridx = 1;
        scheduleArea = new JTextArea(3, 20);
        UIUtil.styleTextArea(scheduleArea);
        JScrollPane scheduleScroll = new JScrollPane(scheduleArea);
        panel.add(scheduleScroll, gbc);

        // Buttons
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel(new FlowLayout());
        saveBtn = UIUtil.createPrimaryButton("Save");
        saveBtn.addActionListener(new SaveActionListener());
        cancelBtn = UIUtil.createSecondaryButton("Cancel");
        cancelBtn.addActionListener(e -> dispose());
        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);
        panel.add(buttonPanel, gbc);

        add(panel);
    }

    private void populateFields() {
        nameField.setText(doctor.getName());
        specializationCombo.setSelectedItem(doctor.getSpecialization());
        contactField.setText(doctor.getContact());
        emailField.setText(doctor.getEmail());
        scheduleArea.setText(doctor.getSchedule());
    }

    private class SaveActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                String name = nameField.getText().trim();
                String specialization = (String) specializationCombo.getSelectedItem();
                String contact = contactField.getText().trim();
                String email = emailField.getText().trim();
                String schedule = scheduleArea.getText().trim();

                Doctor d = new Doctor();
                d.setName(name);
                d.setSpecialization(specialization);
                d.setContact(contact);
                d.setEmail(email);
                d.setSchedule(schedule);

                boolean success;
                if (doctor == null) {
                    success = doctorDAO.addDoctor(d);
                } else {
                    d.setId(doctor.getId());
                    success = doctorDAO.updateDoctor(d);
                }

                if (success) {
                    JOptionPane.showMessageDialog(DoctorFormView.this, "Doctor saved successfully!");
                    parentView.refreshTable();
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(DoctorFormView.this, "Failed to save doctor.");
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(DoctorFormView.this, "Database error: " + ex.getMessage());
            }
        }
    }
}