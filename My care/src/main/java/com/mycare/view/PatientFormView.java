package com.mycare.view;

import com.mycare.dao.PatientDAO;
import com.mycare.model.Patient;
import com.mycare.util.UIUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class PatientFormView extends JDialog {
    private PatientDAO patientDAO;
    private Patient patient;
    private PatientManagementView parentView;

    private JTextField nameField, ageField, contactField, emailField;
    private JTextArea addressArea, historyArea;
    private JComboBox<String> genderCombo;
    private JButton saveBtn, cancelBtn;

    public PatientFormView(Patient patient, PatientManagementView parent) {
        UIUtil.initModernTheme();
        this.patient = patient;
        this.parentView = parent;
        patientDAO = new PatientDAO();
        initializeUI();
        if (patient != null) {
            populateFields();
        }
    }

    private void initializeUI() {
        setTitle(patient == null ? "Add Patient" : "Edit Patient");
        setSize(520, 560);
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

        // Age
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Age:"), gbc);
        gbc.gridx = 1;
        ageField = new JTextField(20);
        UIUtil.styleTextField(ageField);
        panel.add(ageField, gbc);

        // Gender
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Gender:"), gbc);
        gbc.gridx = 1;
        genderCombo = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        genderCombo.setPreferredSize(new Dimension(220, 30));
        panel.add(genderCombo, gbc);

        // Contact
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Contact:"), gbc);
        gbc.gridx = 1;
        contactField = new JTextField(20);
        UIUtil.styleTextField(contactField);
        panel.add(contactField, gbc);

        // Email
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        emailField = new JTextField(20);
        UIUtil.styleTextField(emailField);
        panel.add(emailField, gbc);

        // Address
        gbc.gridx = 0; gbc.gridy = 5;
        panel.add(new JLabel("Address:"), gbc);
        gbc.gridx = 1;
        addressArea = new JTextArea(3, 20);
        UIUtil.styleTextArea(addressArea);
        JScrollPane addressScroll = new JScrollPane(addressArea);
        panel.add(addressScroll, gbc);

        // Medical History
        gbc.gridx = 0; gbc.gridy = 6;
        panel.add(new JLabel("Medical History:"), gbc);
        gbc.gridx = 1;
        historyArea = new JTextArea(3, 20);
        UIUtil.styleTextArea(historyArea);
        JScrollPane historyScroll = new JScrollPane(historyArea);
        panel.add(historyScroll, gbc);

        // Buttons
        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2;
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
        nameField.setText(patient.getName());
        ageField.setText(String.valueOf(patient.getAge()));
        genderCombo.setSelectedItem(patient.getGender());
        contactField.setText(patient.getContact());
        emailField.setText(patient.getEmail());
        addressArea.setText(patient.getAddress());
        historyArea.setText(patient.getMedicalHistory());
    }

    private class SaveActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                String name = nameField.getText().trim();
                int age = Integer.parseInt(ageField.getText().trim());
                String gender = (String) genderCombo.getSelectedItem();
                String contact = contactField.getText().trim();
                String email = emailField.getText().trim();
                String address = addressArea.getText().trim();
                String history = historyArea.getText().trim();

                Patient p = new Patient();
                p.setName(name);
                p.setAge(age);
                p.setGender(gender);
                p.setContact(contact);
                p.setEmail(email);
                p.setAddress(address);
                p.setMedicalHistory(history);

                boolean success;
                if (patient == null) {
                    success = patientDAO.addPatient(p);
                } else {
                    p.setId(patient.getId());
                    success = patientDAO.updatePatient(p);
                }

                if (success) {
                    JOptionPane.showMessageDialog(PatientFormView.this, "Patient saved successfully!");
                    parentView.refreshTable();
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(PatientFormView.this, "Failed to save patient.");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(PatientFormView.this, "Please enter a valid age.");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(PatientFormView.this, "Database error: " + ex.getMessage());
            }
        }
    }
}