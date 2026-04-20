package com.mycare.view;

import com.mycare.dao.AppointmentDAO;
import com.mycare.dao.DoctorDAO;
import com.mycare.dao.PatientDAO;
import com.mycare.model.Appointment;
import com.mycare.model.Doctor;
import com.mycare.model.Patient;
import com.mycare.util.UIUtil;

import javax.swing.*;
import javax.swing.DefaultListCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class AppointmentManagementView extends JFrame {
    private AppointmentDAO appointmentDAO;
    private PatientDAO patientDAO;
    private DoctorDAO doctorDAO;
    private JTable appointmentTable;
    private DefaultTableModel tableModel;
    private JComboBox<Patient> patientCombo;
    private JComboBox<Doctor> doctorCombo;
    private JComboBox<Patient> filterPatientCombo;
    private JComboBox<Doctor> filterDoctorCombo;
    private JComboBox<String> filterStatusCombo;
    private JTextField dateField;
    private JButton bookBtn, cancelBtn, deleteBtn, filterBtn, clearFilterBtn;

    public AppointmentManagementView() {
        UIUtil.initModernTheme();
        appointmentDAO = new AppointmentDAO();
        patientDAO = new PatientDAO();
        doctorDAO = new DoctorDAO();
        initializeUI();
        loadAppointments();
        loadCombos();
    }

    private void initializeUI() {
        setTitle("Appointment Management");
        setSize(1020, 640);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        UIUtil.styleFrame(this);

        JPanel mainPanel = new JPanel(new BorderLayout(0, 16));
        mainPanel.setBackground(new Color(245, 247, 250));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        // Booking panel
        JPanel bookingPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0; gbc.gridy = 0;
        bookingPanel.add(new JLabel("Patient:"), gbc);
        gbc.gridx = 1;
        patientCombo = new JComboBox<>();
        bookingPanel.add(patientCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        bookingPanel.add(new JLabel("Doctor:"), gbc);
        gbc.gridx = 1;
        doctorCombo = new JComboBox<>();
        bookingPanel.add(doctorCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        bookingPanel.add(new JLabel("Date/Time (yyyy-MM-dd HH:mm):"), gbc);
        gbc.gridx = 1;
        dateField = new JTextField(20);
        UIUtil.styleTextField(dateField);
        bookingPanel.add(dateField, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setOpaque(false);
        bookBtn = UIUtil.createPrimaryButton("Book Appointment");
        bookBtn.addActionListener(new BookActionListener());
        cancelBtn = UIUtil.createSecondaryButton("Cancel Selected");
        cancelBtn.addActionListener(new CancelActionListener());
        deleteBtn = UIUtil.createDangerButton("Delete Selected");
        deleteBtn.addActionListener(new DeleteActionListener());
        buttonPanel.add(bookBtn);
        buttonPanel.add(cancelBtn);
        buttonPanel.add(deleteBtn);
        bookingPanel.add(buttonPanel, gbc);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 1;
        bookingPanel.add(new JLabel("Filter by Patient:"), gbc);
        gbc.gridx = 1;
        filterPatientCombo = new JComboBox<>();
        bookingPanel.add(filterPatientCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        bookingPanel.add(new JLabel("Filter by Doctor:"), gbc);
        gbc.gridx = 1;
        filterDoctorCombo = new JComboBox<>();
        bookingPanel.add(filterDoctorCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 6;
        bookingPanel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 1;
        filterStatusCombo = new JComboBox<>(new String[]{"All", "scheduled", "cancelled"});
        bookingPanel.add(filterStatusCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2;
        JPanel filterButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterButtonPanel.setOpaque(false);
        filterBtn = UIUtil.createPrimaryButton("Apply Filter");
        filterBtn.addActionListener(new FilterActionListener());
        clearFilterBtn = UIUtil.createSecondaryButton("Clear Filter");
        clearFilterBtn.addActionListener(new ClearFilterActionListener());
        filterButtonPanel.add(filterBtn);
        filterButtonPanel.add(clearFilterBtn);
        bookingPanel.add(filterButtonPanel, gbc);

        // Table
        String[] columns = {"ID", "Patient", "Doctor", "Date/Time", "Status", "Notes"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        appointmentTable = new JTable(tableModel);
        UIUtil.styleTable(appointmentTable);
        JScrollPane scrollPane = new JScrollPane(appointmentTable);

        mainPanel.add(bookingPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        add(mainPanel);
    }

    private void loadAppointments() {
        loadAppointments(null, null, "All");
    }

    private void loadAppointments(Integer patientId, Integer doctorId, String status) {
        try {
            List<Appointment> appointments = appointmentDAO.getFilteredAppointments(
                    patientId,
                    doctorId,
                    "All".equals(status) ? null : status
            );
            tableModel.setRowCount(0);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            for (Appointment a : appointments) {
                tableModel.addRow(new Object[]{
                    a.getId(),
                    getPatientName(a.getPatientId()),
                    getDoctorName(a.getDoctorId()),
                    sdf.format(a.getAppointmentDate()),
                    a.getStatus(),
                    a.getNotes()
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading appointments: " + e.getMessage());
        }
    }

    private void loadCombos() {
        try {
            filterPatientCombo.addItem(null);
            List<Patient> patients = patientDAO.getAllPatients();
            for (Patient p : patients) {
                patientCombo.addItem(p);
                filterPatientCombo.addItem(p);
            }
            filterPatientCombo.setRenderer(new DefaultListCellRenderer() {
                @Override
                public java.awt.Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                                      boolean isSelected, boolean cellHasFocus) {
                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    setText(value == null ? "All Patients" : value.toString());
                    return this;
                }
            });
            filterPatientCombo.setSelectedIndex(0);

            filterDoctorCombo.addItem(null);
            List<Doctor> doctors = doctorDAO.getAllDoctors();
            for (Doctor d : doctors) {
                doctorCombo.addItem(d);
                filterDoctorCombo.addItem(d);
            }
            filterDoctorCombo.setRenderer(new DefaultListCellRenderer() {
                @Override
                public java.awt.Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                                      boolean isSelected, boolean cellHasFocus) {
                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    setText(value == null ? "All Doctors" : value.toString());
                    return this;
                }
            });
            filterDoctorCombo.setSelectedIndex(0);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading data: " + e.getMessage());
        }
    }

    private String getPatientName(int id) {
        try {
            Patient p = patientDAO.getPatientById(id);
            return p != null ? p.getName() : "Unknown";
        } catch (SQLException e) {
            return "Error";
        }
    }

    private String getDoctorName(int id) {
        try {
            Doctor d = doctorDAO.getDoctorById(id);
            return d != null ? d.getName() : "Unknown";
        } catch (SQLException e) {
            return "Error";
        }
    }

    private class BookActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            Patient patient = (Patient) patientCombo.getSelectedItem();
            Doctor doctor = (Doctor) doctorCombo.getSelectedItem();
            String dateStr = dateField.getText().trim();

            if (patient == null || doctor == null || dateStr.isEmpty()) {
                JOptionPane.showMessageDialog(AppointmentManagementView.this, "Please fill all fields.");
                return;
            }

            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                Date date = sdf.parse(dateStr);
                Timestamp timestamp = new Timestamp(date.getTime());

                if (!appointmentDAO.isDoctorAvailable(doctor.getId(), timestamp)) {
                    JOptionPane.showMessageDialog(AppointmentManagementView.this, "Doctor is not available at this time.");
                    return;
                }

                Appointment appointment = new Appointment();
                appointment.setPatientId(patient.getId());
                appointment.setDoctorId(doctor.getId());
                appointment.setAppointmentDate(timestamp);
                appointment.setStatus("scheduled");

                if (appointmentDAO.bookAppointment(appointment)) {
                    JOptionPane.showMessageDialog(AppointmentManagementView.this, "Appointment booked successfully!");
                    loadAppointments();
                    dateField.setText("");
                } else {
                    JOptionPane.showMessageDialog(AppointmentManagementView.this, "Failed to book appointment.");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(AppointmentManagementView.this, "Invalid date format or error: " + ex.getMessage());
            }
        }
    }

    private class CancelActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedRow = appointmentTable.getSelectedRow();
            if (selectedRow >= 0) {
                try {
                    Object value = tableModel.getValueAt(selectedRow, 0);
                    int id = value instanceof Number ? ((Number) value).intValue() : Integer.parseInt(value.toString());
                    boolean cancelled = appointmentDAO.cancelAppointment(id);
                    if (cancelled) {
                        loadAppointments();
                        JOptionPane.showMessageDialog(AppointmentManagementView.this, "Appointment cancelled!");
                    } else {
                        JOptionPane.showMessageDialog(AppointmentManagementView.this, "Appointment could not be cancelled. It may already be cancelled.");
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(AppointmentManagementView.this, "Unable to read the selected appointment ID.");
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(AppointmentManagementView.this, "Error: " + ex.getMessage());
                }
            } else {
                JOptionPane.showMessageDialog(AppointmentManagementView.this, "Please select an appointment to cancel.");
            }
        }
    }

    private class DeleteActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedRow = appointmentTable.getSelectedRow();
            if (selectedRow >= 0) {
                int confirmed = JOptionPane.showConfirmDialog(AppointmentManagementView.this,
                        "Do you really want to delete the selected appointment?",
                        "Confirm Delete",
                        JOptionPane.YES_NO_OPTION);
                if (confirmed == JOptionPane.YES_OPTION) {
                    try {
                        Object value = tableModel.getValueAt(selectedRow, 0);
                        int id = value instanceof Number ? ((Number) value).intValue() : Integer.parseInt(value.toString());
                        boolean deleted = appointmentDAO.deleteAppointment(id);
                        if (deleted) {
                            loadAppointments();
                            JOptionPane.showMessageDialog(AppointmentManagementView.this, "Appointment deleted successfully.");
                        } else {
                            JOptionPane.showMessageDialog(AppointmentManagementView.this, "Appointment could not be deleted.");
                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(AppointmentManagementView.this, "Unable to read the selected appointment ID.");
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(AppointmentManagementView.this, "Error: " + ex.getMessage());
                    }
                }
            } else {
                JOptionPane.showMessageDialog(AppointmentManagementView.this, "Please select an appointment to delete.");
            }
        }
    }

    private class FilterActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            Integer patientId = getSelectedFilterPatientId();
            Integer doctorId = getSelectedFilterDoctorId();
            String status = (String) filterStatusCombo.getSelectedItem();
            loadAppointments(patientId, doctorId, status != null ? status : "All");
        }
    }

    private class ClearFilterActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            filterPatientCombo.setSelectedIndex(0);
            filterDoctorCombo.setSelectedIndex(0);
            filterStatusCombo.setSelectedIndex(0);
            loadAppointments();
        }
    }

    private Integer getSelectedFilterPatientId() {
        Patient selected = (Patient) filterPatientCombo.getSelectedItem();
        return selected != null ? selected.getId() : null;
    }

    private Integer getSelectedFilterDoctorId() {
        Doctor selected = (Doctor) filterDoctorCombo.getSelectedItem();
        return selected != null ? selected.getId() : null;
    }
}