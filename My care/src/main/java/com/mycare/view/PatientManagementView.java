package com.mycare.view;

import com.mycare.dao.PatientDAO;
import com.mycare.model.Patient;
import com.mycare.util.UIUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;

public class PatientManagementView extends JFrame {
    private PatientDAO patientDAO;
    private JTable patientTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JButton addBtn, updateBtn, deleteBtn, searchBtn;

    public PatientManagementView() {
        UIUtil.initModernTheme();
        patientDAO = new PatientDAO();
        initializeUI();
        loadPatients();
    }

    private void initializeUI() {
        setTitle("Patient Management");
        setSize(920, 620);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        UIUtil.styleFrame(this);

        JPanel mainPanel = new JPanel(new BorderLayout(0, 16));
        mainPanel.setBackground(new Color(245, 247, 250));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setOpaque(false);
        searchField = new JTextField(20);
        UIUtil.styleTextField(searchField);
        searchBtn = UIUtil.createPrimaryButton("Search");
        searchBtn.addActionListener(new SearchActionListener());
        searchPanel.add(new JLabel("Search by Name:"));
        searchPanel.add(searchField);
        searchPanel.add(searchBtn);

        // Table
        String[] columns = {"ID", "Name", "Age", "Gender", "Contact", "Email", "Medical History"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        patientTable = new JTable(tableModel);
        UIUtil.styleTable(patientTable);
        JScrollPane scrollPane = new JScrollPane(patientTable);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setOpaque(false);
        addBtn = UIUtil.createPrimaryButton("Add Patient");
        addBtn.addActionListener(new AddActionListener());
        updateBtn = UIUtil.createSecondaryButton("Update Patient");
        updateBtn.addActionListener(new UpdateActionListener());
        deleteBtn = UIUtil.createDangerButton("Delete Patient");
        deleteBtn.addActionListener(new DeleteActionListener());

        buttonPanel.add(addBtn);
        buttonPanel.add(updateBtn);
        buttonPanel.add(deleteBtn);

        mainPanel.add(searchPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void loadPatients() {
        try {
            List<Patient> patients = patientDAO.getAllPatients();
            tableModel.setRowCount(0);
            for (Patient p : patients) {
                tableModel.addRow(new Object[]{
                    p.getId(), p.getName(), p.getAge(), p.getGender(),
                    p.getContact(), p.getEmail(), p.getMedicalHistory()
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading patients: " + e.getMessage());
        }
    }

    private void searchPatients(String name) {
        try {
            List<Patient> patients = patientDAO.searchPatients(name);
            tableModel.setRowCount(0);
            for (Patient p : patients) {
                tableModel.addRow(new Object[]{
                    p.getId(), p.getName(), p.getAge(), p.getGender(),
                    p.getContact(), p.getEmail(), p.getMedicalHistory()
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error searching patients: " + e.getMessage());
        }
    }

    private class AddActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            new PatientFormView(null, PatientManagementView.this).setVisible(true);
        }
    }

    private class UpdateActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedRow = patientTable.getSelectedRow();
            if (selectedRow >= 0) {
                int id = (Integer) tableModel.getValueAt(selectedRow, 0);
                try {
                    Patient patient = patientDAO.getPatientById(id);
                    new PatientFormView(patient, PatientManagementView.this).setVisible(true);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(PatientManagementView.this, "Error: " + ex.getMessage());
                }
            } else {
                JOptionPane.showMessageDialog(PatientManagementView.this, "Please select a patient to update.");
            }
        }
    }

    private class DeleteActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedRow = patientTable.getSelectedRow();
            if (selectedRow >= 0) {
                int id = (Integer) tableModel.getValueAt(selectedRow, 0);
                int confirm = JOptionPane.showConfirmDialog(PatientManagementView.this,
                    "Are you sure you want to delete this patient?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        patientDAO.deletePatient(id);
                        loadPatients();
                        JOptionPane.showMessageDialog(PatientManagementView.this, "Patient deleted successfully!");
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(PatientManagementView.this, "Error: " + ex.getMessage());
                    }
                }
            } else {
                JOptionPane.showMessageDialog(PatientManagementView.this, "Please select a patient to delete.");
            }
        }
    }

    private class SearchActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String name = searchField.getText().trim();
            if (!name.isEmpty()) {
                searchPatients(name);
            } else {
                loadPatients();
            }
        }
    }

    public void refreshTable() {
        loadPatients();
    }
}