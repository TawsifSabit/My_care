package com.mycare.view;

import com.mycare.dao.AppointmentDAO;
import com.mycare.dao.DoctorDAO;
import com.mycare.dao.PatientDAO;
import com.mycare.dao.PrescriptionDAO;
import com.mycare.model.Appointment;
import com.mycare.model.Doctor;
import com.mycare.model.Patient;
import com.mycare.model.Prescription;
import com.mycare.util.UIUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;

public class PrescriptionManagementView extends JFrame {
    private AppointmentDAO appointmentDAO;
    private PrescriptionDAO prescriptionDAO;
    private PatientDAO patientDAO;
    private DoctorDAO doctorDAO;
    private JComboBox<Appointment> appointmentCombo;
    private JTextArea medicinesArea;
    private JTextField dosageField;
    private JTextArea notesArea;
    private DefaultTableModel tableModel;
    private JTable prescriptionTable;

    public PrescriptionManagementView() {
        UIUtil.initModernTheme();
        appointmentDAO = new AppointmentDAO();
        patientDAO = new PatientDAO();
        doctorDAO = new DoctorDAO();
        prescriptionDAO = new PrescriptionDAO();
        initializeUI();
        loadAppointments();
        loadPrescriptions();
    }

    private void initializeUI() {
        setTitle("Prescription Management");
        setSize(920, 640);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        UIUtil.styleFrame(this);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(245, 247, 250));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Appointment:"), gbc);
        gbc.gridx = 1;
        appointmentCombo = new JComboBox<>();
        appointmentCombo.setPreferredSize(new Dimension(500, 30));
        formPanel.add(appointmentCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Medicines (comma-separated):"), gbc);
        gbc.gridx = 1;
        medicinesArea = new JTextArea(3, 40);
        UIUtil.styleTextArea(medicinesArea);
        formPanel.add(new JScrollPane(medicinesArea), gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Dosage Instructions:"), gbc);
        gbc.gridx = 1;
        dosageField = new JTextField(40);
        UIUtil.styleTextField(dosageField);
        formPanel.add(dosageField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Notes:"), gbc);
        gbc.gridx = 1;
        notesArea = new JTextArea(3, 40);
        UIUtil.styleTextArea(notesArea);
        formPanel.add(new JScrollPane(notesArea), gbc);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setOpaque(false);
        JButton saveButton = UIUtil.createPrimaryButton("Save Prescription");
        saveButton.addActionListener(new SavePrescriptionAction());
        JButton refreshButton = UIUtil.createSecondaryButton("Refresh");
        refreshButton.addActionListener(e -> loadPrescriptions());
        buttonPanel.add(saveButton);
        buttonPanel.add(refreshButton);
        formPanel.add(buttonPanel, gbc);

        String[] columns = {"ID", "Appointment", "Patient", "Doctor", "Medicines", "Dosage", "Notes"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        prescriptionTable = new JTable(tableModel);
        UIUtil.styleTable(prescriptionTable);
        JScrollPane tableScroll = new JScrollPane(prescriptionTable);

        mainPanel.add(formPanel, BorderLayout.NORTH);
        mainPanel.add(tableScroll, BorderLayout.CENTER);
        add(mainPanel);
    }

    private void loadAppointments() {
        try {
            appointmentCombo.removeAllItems();
            List<Appointment> appointments = appointmentDAO.getAllAppointments();
            for (Appointment appointment : appointments) {
                appointmentCombo.addItem(appointment);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Unable to load appointments: " + e.getMessage());
        }
    }

    private void loadPrescriptions() {
        try {
            tableModel.setRowCount(0);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            for (Prescription prescription : prescriptionDAO.getAllPrescriptions()) {
                Appointment appointment = appointmentDAO.getAppointmentById(prescription.getAppointmentId());
                String patientName = appointment != null ? getPatientName(appointment.getPatientId()) : "Unknown";
                String doctorName = appointment != null ? getDoctorName(appointment.getDoctorId()) : "Unknown";
                String appointmentLabel = appointment != null ? "#" + appointment.getId() + " " + sdf.format(appointment.getAppointmentDate()) : "Unknown";
                tableModel.addRow(new Object[]{prescription.getId(), appointmentLabel, patientName, doctorName,
                        prescription.getMedicines(), prescription.getDosage(), prescription.getNotes()});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Unable to load prescriptions: " + e.getMessage());
        }
    }

    private String getPatientName(int id) {
        try {
            Patient patient = patientDAO.getPatientById(id);
            return patient != null ? patient.getName() : "Unknown";
        } catch (SQLException e) {
            return "Error";
        }
    }

    private String getDoctorName(int id) {
        try {
            Doctor doctor = doctorDAO.getDoctorById(id);
            return doctor != null ? doctor.getName() : "Unknown";
        } catch (SQLException e) {
            return "Error";
        }
    }

    private class SavePrescriptionAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            Appointment appointment = (Appointment) appointmentCombo.getSelectedItem();
            if (appointment == null) {
                JOptionPane.showMessageDialog(PrescriptionManagementView.this, "Please select an appointment.");
                return;
            }
            String medicines = medicinesArea.getText().trim();
            String dosage = dosageField.getText().trim();
            String notes = notesArea.getText().trim();
            if (medicines.isEmpty() || dosage.isEmpty()) {
                JOptionPane.showMessageDialog(PrescriptionManagementView.this, "Medicines and dosage are required.");
                return;
            }
            try {
                if (prescriptionDAO.hasPrescriptionForAppointment(appointment.getId())) {
                    JOptionPane.showMessageDialog(PrescriptionManagementView.this, "A prescription already exists for this appointment.");
                    return;
                }
                Prescription prescription = new Prescription();
                prescription.setAppointmentId(appointment.getId());
                prescription.setMedicines(medicines);
                prescription.setDosage(dosage);
                prescription.setNotes(notes);
                if (prescriptionDAO.savePrescription(prescription)) {
                    JOptionPane.showMessageDialog(PrescriptionManagementView.this, "Prescription saved successfully.");
                    loadPrescriptions();
                    medicinesArea.setText("");
                    dosageField.setText("");
                    notesArea.setText("");
                } else {
                    JOptionPane.showMessageDialog(PrescriptionManagementView.this, "Failed to save prescription.");
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(PrescriptionManagementView.this, "Error saving prescription: " + ex.getMessage());
            }
        }
    }
}
