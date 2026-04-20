package com.mycare.view;

import com.mycare.dao.DoctorDAO;
import com.mycare.model.Doctor;
import com.mycare.util.UIUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;

public class DoctorManagementView extends JFrame {
    private DoctorDAO doctorDAO;
    private JTable doctorTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> specializationFilter;
    private JButton addBtn, updateBtn, deleteBtn, filterBtn;

    public DoctorManagementView() {
        UIUtil.initModernTheme();
        doctorDAO = new DoctorDAO();
        initializeUI();
        loadDoctors();
    }

    private void initializeUI() {
        setTitle("Doctor Management");
        setSize(920, 620);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        UIUtil.styleFrame(this);

        JPanel mainPanel = new JPanel(new BorderLayout(0, 16));
        mainPanel.setBackground(new Color(245, 247, 250));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        // Filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.setOpaque(false);
        specializationFilter = new JComboBox<>(new String[]{"All", "Cardiology", "Neurology", "Orthopedics", "Pediatrics"});
        filterBtn = UIUtil.createPrimaryButton("Filter");
        filterBtn.addActionListener(new FilterActionListener());
        filterPanel.add(new JLabel("Specialization:"));
        filterPanel.add(specializationFilter);
        filterPanel.add(filterBtn);

        // Table
        String[] columns = {"ID", "Name", "Specialization", "Contact", "Email", "Schedule"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        doctorTable = new JTable(tableModel);
        UIUtil.styleTable(doctorTable);
        JScrollPane scrollPane = new JScrollPane(doctorTable);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setOpaque(false);
        addBtn = UIUtil.createPrimaryButton("Add Doctor");
        addBtn.addActionListener(new AddActionListener());
        updateBtn = UIUtil.createSecondaryButton("Update Doctor");
        updateBtn.addActionListener(new UpdateActionListener());
        deleteBtn = UIUtil.createDangerButton("Delete Doctor");
        deleteBtn.addActionListener(new DeleteActionListener());

        buttonPanel.add(addBtn);
        buttonPanel.add(updateBtn);
        buttonPanel.add(deleteBtn);

        mainPanel.add(filterPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void loadDoctors() {
        try {
            List<Doctor> doctors = doctorDAO.getAllDoctors();
            populateTable(doctors);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading doctors: " + e.getMessage());
        }
    }

    private void populateTable(List<Doctor> doctors) {
        tableModel.setRowCount(0);
        for (Doctor d : doctors) {
            tableModel.addRow(new Object[]{
                d.getId(), d.getName(), d.getSpecialization(),
                d.getContact(), d.getEmail(), d.getSchedule()
            });
        }
    }

    private class AddActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            new DoctorFormView(null, DoctorManagementView.this).setVisible(true);
        }
    }

    private class UpdateActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedRow = doctorTable.getSelectedRow();
            if (selectedRow >= 0) {
                int id = (Integer) tableModel.getValueAt(selectedRow, 0);
                try {
                    Doctor doctor = doctorDAO.getDoctorById(id);
                    new DoctorFormView(doctor, DoctorManagementView.this).setVisible(true);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(DoctorManagementView.this, "Error: " + ex.getMessage());
                }
            } else {
                JOptionPane.showMessageDialog(DoctorManagementView.this, "Please select a doctor to update.");
            }
        }
    }

    private class DeleteActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedRow = doctorTable.getSelectedRow();
            if (selectedRow >= 0) {
                int id = (Integer) tableModel.getValueAt(selectedRow, 0);
                int confirm = JOptionPane.showConfirmDialog(DoctorManagementView.this,
                    "Are you sure you want to delete this doctor?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        doctorDAO.deleteDoctor(id);
                        loadDoctors();
                        JOptionPane.showMessageDialog(DoctorManagementView.this, "Doctor deleted successfully!");
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(DoctorManagementView.this, "Error: " + ex.getMessage());
                    }
                }
            } else {
                JOptionPane.showMessageDialog(DoctorManagementView.this, "Please select a doctor to delete.");
            }
        }
    }

    private class FilterActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String specialization = (String) specializationFilter.getSelectedItem();
            if ("All".equals(specialization)) {
                loadDoctors();
            } else {
                try {
                    List<Doctor> doctors = doctorDAO.getDoctorsBySpecialization(specialization);
                    populateTable(doctors);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(DoctorManagementView.this, "Error: " + ex.getMessage());
                }
            }
        }
    }

    public void refreshTable() {
        loadDoctors();
    }
}