package com.mycare.view;

import com.mycare.dao.EmergencyDAO;
import com.mycare.model.EmergencyPatient;
import com.mycare.util.UIUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class EmergencyManagementView extends JFrame {
    private EmergencyDAO emergencyDAO;
    private JTable emergencyTable;
    private DefaultTableModel tableModel;
    private JButton addPatientBtn, updateStatusBtn, dischargeBtn, refreshBtn;
    private JTextField nameField, ageField, arrivalTimeField;
    private JTextArea conditionField;
    private JComboBox<String> priorityComboBox;
    private JLabel statusLabel, patientCountLabel;

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public EmergencyManagementView() {
        UIUtil.initModernTheme();
        emergencyDAO = new EmergencyDAO();
        initializeUI();
        loadEmergencyPatients();
    }

    private void initializeUI() {
        setTitle("MyCare - Emergency Management System");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        UIUtil.styleFrame(this);

        // Main panel with BorderLayout
        setLayout(new BorderLayout());

        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(25, 118, 210));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel titleLabel = new JLabel("Emergency Management System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);

        refreshBtn = UIUtil.createPrimaryButton("ðŸ”„ Refresh");
        refreshBtn.addActionListener(e -> refreshTable());

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(refreshBtn, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // Center panel with table and form
        JPanel centerPanel = new JPanel(new BorderLayout());

        // Left panel - Table
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 10));

        JLabel tableTitle = new JLabel("Emergency Patients");
        tableTitle.setFont(new Font("Arial", Font.BOLD, 18));
        tableTitle.setForeground(new Color(25, 118, 210));

        // Action buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        addPatientBtn = UIUtil.createPrimaryButton("âž• Add Emergency Case");
        updateStatusBtn = UIUtil.createSecondaryButton("ðŸ“ Update Status");
        dischargeBtn = UIUtil.createDangerButton("ðŸ¥ Discharge Patient");

        addPatientBtn.addActionListener(e -> showAddPatientDialog());
        updateStatusBtn.addActionListener(e -> updatePatientStatus());
        dischargeBtn.addActionListener(e -> dischargePatient());

        buttonPanel.add(addPatientBtn);
        buttonPanel.add(updateStatusBtn);
        buttonPanel.add(dischargeBtn);

        // Table setup
        String[] columnNames = {"ID", "Name", "Age", "Condition", "Priority", "Arrival Time", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        emergencyTable = new JTable(tableModel);
        UIUtil.styleTable(emergencyTable);
        emergencyTable.setRowHeight(30);

        // Custom cell renderer for priority and status highlighting
        emergencyTable.getColumnModel().getColumn(4).setCellRenderer(new PriorityCellRenderer());
        emergencyTable.getColumnModel().getColumn(6).setCellRenderer(new StatusCellRenderer());

        JScrollPane scrollPane = new JScrollPane(emergencyTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        tablePanel.add(tableTitle, BorderLayout.NORTH);
        tablePanel.add(buttonPanel, BorderLayout.CENTER);
        tablePanel.add(scrollPane, BorderLayout.SOUTH);

        // Right panel - Form
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            "Register Emergency Patient"));
        formPanel.setPreferredSize(new Dimension(350, 400));

        JPanel formContent = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Name field
        gbc.gridx = 0; gbc.gridy = 0;
        formContent.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        nameField = new JTextField(15);
        UIUtil.styleTextField(nameField);
        formContent.add(nameField, gbc);

        // Age field
        gbc.gridx = 0; gbc.gridy = 1;
        formContent.add(new JLabel("Age:"), gbc);
        gbc.gridx = 1;
        ageField = new JTextField(15);
        UIUtil.styleTextField(ageField);
        formContent.add(ageField, gbc);

        // Condition field
        gbc.gridx = 0; gbc.gridy = 2;
        formContent.add(new JLabel("Condition:"), gbc);
        gbc.gridx = 1;
        conditionField = new JTextArea(3, 15);
        conditionField.setLineWrap(true);
        conditionField.setWrapStyleWord(true);
        UIUtil.styleTextArea(conditionField);
        JScrollPane conditionScroll = new JScrollPane(conditionField);
        formContent.add(conditionScroll, gbc);

        // Priority combo box
        gbc.gridx = 0; gbc.gridy = 3;
        formContent.add(new JLabel("Priority Level:"), gbc);
        gbc.gridx = 1;
        priorityComboBox = new JComboBox<>(new String[]{"Critical", "High", "Medium", "Low"});
        priorityComboBox.setSelectedItem("Medium");
        formContent.add(priorityComboBox, gbc);

        // Arrival time field
        gbc.gridx = 0; gbc.gridy = 4;
        formContent.add(new JLabel("Arrival Time:"), gbc);
        gbc.gridx = 1;
        arrivalTimeField = new JTextField(15);
        arrivalTimeField.setEditable(false);
        UIUtil.styleTextField(arrivalTimeField);
        arrivalTimeField.setText(LocalDateTime.now().format(formatter));
        formContent.add(arrivalTimeField, gbc);

        // Submit button
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton submitBtn = createStyledButton("ðŸš‘ Register Emergency Patient", new Color(255, 107, 53));
        submitBtn.addActionListener(e -> registerEmergencyPatient());
        formContent.add(submitBtn, gbc);

        formPanel.add(formContent);

        centerPanel.add(tablePanel, BorderLayout.CENTER);
        centerPanel.add(formPanel, BorderLayout.EAST);

        add(centerPanel, BorderLayout.CENTER);

        // Status bar
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        statusPanel.setBackground(Color.LIGHT_GRAY);

        statusLabel = new JLabel("Ready");
        patientCountLabel = new JLabel("Total Patients: 0");

        statusPanel.add(statusLabel, BorderLayout.WEST);
        statusPanel.add(patientCountLabel, BorderLayout.EAST);

        add(statusPanel, BorderLayout.SOUTH);

        // Auto-update arrival time
        Timer timer = new Timer(1000, e -> updateArrivalTime());
        timer.start();
    }

    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(backgroundColor.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(backgroundColor);
            }
        });

        return button;
    }

    private void updateArrivalTime() {
        arrivalTimeField.setText(LocalDateTime.now().format(formatter));
    }

    private void refreshTable() {
        loadEmergencyPatients();
        statusLabel.setText("Table refreshed");
    }

    private void showAddPatientDialog() {
        clearForm();
        updateArrivalTime();
        statusLabel.setText("Ready to add new emergency patient");
    }

    private void registerEmergencyPatient() {
        if (!validateForm()) {
            return;
        }

        try {
            String name = nameField.getText().trim();
            int age = Integer.parseInt(ageField.getText().trim());
            String condition = conditionField.getText().trim();
            String priority = (String) priorityComboBox.getSelectedItem();
            LocalDateTime arrivalTime = LocalDateTime.parse(arrivalTimeField.getText(), formatter);

            EmergencyPatient patient = new EmergencyPatient(name, age, condition, priority, arrivalTime, "Waiting");

            if (emergencyDAO.addEmergencyPatient(patient)) {
                loadEmergencyPatients();
                clearForm();
                statusLabel.setText("Emergency patient registered successfully");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to register emergency patient", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to save emergency patient: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid age", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updatePatientStatus() {
        int selectedRow = emergencyTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a patient to update status", "Selection Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int patientId = (Integer) tableModel.getValueAt(selectedRow, 0);
        String currentStatus = (String) tableModel.getValueAt(selectedRow, 6);

        String[] options = {"Waiting", "In Treatment", "Discharged"};
        String newStatus = (String) JOptionPane.showInputDialog(this,
            "Update status for patient ID: " + patientId,
            "Update Patient Status",
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            currentStatus);

        if (newStatus != null && !newStatus.equals(currentStatus)) {
            try {
                if (emergencyDAO.updatePatientStatus(patientId, newStatus)) {
                    loadEmergencyPatients();
                    statusLabel.setText("Patient status updated successfully");
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update patient status", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Failed to update status: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void dischargePatient() {
        int selectedRow = emergencyTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a patient to discharge", "Selection Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int patientId = (Integer) tableModel.getValueAt(selectedRow, 0);
        String patientName = (String) tableModel.getValueAt(selectedRow, 1);

        int result = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to discharge " + patientName + "?",
            "Confirm Discharge",
            JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.YES_OPTION) {
            try {
                if (emergencyDAO.dischargePatient(patientId)) {
                    loadEmergencyPatients();
                    statusLabel.setText("Patient discharged successfully");
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to discharge patient", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Failed to discharge patient: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void loadEmergencyPatients() {
        try {
            List<EmergencyPatient> patients = emergencyDAO.getAllEmergencyPatients();
            tableModel.setRowCount(0);

            for (EmergencyPatient patient : patients) {
                Object[] row = {
                    patient.getId(),
                    patient.getName(),
                    patient.getAge(),
                    patient.getCondition(),
                    patient.getPriorityLevel(),
                    patient.getArrivalTime().format(formatter),
                    patient.getStatus()
                };
                tableModel.addRow(row);
            }

            updatePatientCount();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to load emergency patients: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updatePatientCount() {
        patientCountLabel.setText("Total Patients: " + tableModel.getRowCount());
    }

    private void clearForm() {
        nameField.setText("");
        ageField.setText("");
        conditionField.setText("");
        priorityComboBox.setSelectedItem("Medium");
        updateArrivalTime();
    }

    private boolean validateForm() {
        if (nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Patient name is required", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (ageField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Patient age is required", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (conditionField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Condition description is required", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    // Custom cell renderer for priority highlighting
    private class PriorityCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (!isSelected) {
                String priority = (String) value;
                switch (priority) {
                    case "Critical":
                        c.setBackground(new Color(255, 235, 238)); // Light red
                        c.setForeground(new Color(198, 40, 40)); // Dark red
                        break;
                    case "High":
                        c.setBackground(new Color(255, 243, 224)); // Light orange
                        c.setForeground(new Color(239, 108, 0)); // Dark orange
                        break;
                    case "Medium":
                        c.setBackground(new Color(243, 229, 245)); // Light purple
                        c.setForeground(new Color(123, 31, 162)); // Dark purple
                        break;
                    case "Low":
                        c.setBackground(new Color(232, 245, 233)); // Light green
                        c.setForeground(new Color(46, 125, 50)); // Dark green
                        break;
                    default:
                        c.setBackground(Color.WHITE);
                        c.setForeground(Color.BLACK);
                }
            }

            return c;
        }
    }

    // Custom cell renderer for status highlighting
    private class StatusCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (!isSelected) {
                String status = (String) value;
                switch (status) {
                    case "Waiting":
                        c.setBackground(new Color(255, 243, 224)); // Light orange
                        c.setForeground(new Color(239, 108, 0)); // Dark orange
                        break;
                    case "In Treatment":
                        c.setBackground(new Color(227, 242, 253)); // Light blue
                        c.setForeground(new Color(25, 118, 210)); // Dark blue
                        break;
                    case "Discharged":
                        c.setBackground(new Color(232, 245, 233)); // Light green
                        c.setForeground(new Color(46, 125, 50)); // Dark green
                        break;
                    default:
                        c.setBackground(Color.WHITE);
                        c.setForeground(Color.BLACK);
                }
            }

            return c;
        }
    }
}