package com.mycare.view;

import com.mycare.dao.PatientDAO;
import com.mycare.dao.RoomDAO;
import com.mycare.model.Patient;
import com.mycare.model.Room;
import com.mycare.util.UIUtil;

import javax.swing.*;
import javax.swing.DefaultListCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.sql.Date;
import java.util.List;

public class RoomManagementView extends JFrame {
    private RoomDAO roomDAO;
    private PatientDAO patientDAO;
    private DefaultTableModel tableModel;
    private JTable roomTable;
    private JComboBox<Patient> patientCombo;
    private JComboBox<Patient> filterPatientCombo;
    private JComboBox<String> filterStatusCombo;
    private JTextField roomNumberField;
    private JTextField bedNumberField;
    private JTextField filterRoomNumberField;
    private JTextField filterBedNumberField;
    private JButton deleteBtn;
    private JButton filterBtn;
    private JButton clearFilterBtn;

    public RoomManagementView() {
        UIUtil.initModernTheme();
        roomDAO = new RoomDAO();
        patientDAO = new PatientDAO();
        initializeUI();
        loadRooms();
        loadPatients();
    }

    private void initializeUI() {
        setTitle("Room/Bed Management");
        setSize(920, 620);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        UIUtil.styleFrame(this);

        JPanel content = new JPanel(new BorderLayout(10, 10));
        content.setBackground(new Color(245, 247, 250));
        content.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Room Number:"), gbc);
        gbc.gridx = 1;
        roomNumberField = new JTextField(20);
        UIUtil.styleTextField(roomNumberField);
        formPanel.add(roomNumberField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Bed Number:"), gbc);
        gbc.gridx = 1;
        bedNumberField = new JTextField(20);
        UIUtil.styleTextField(bedNumberField);
        formPanel.add(bedNumberField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Patient:"), gbc);
        gbc.gridx = 1;
        patientCombo = new JComboBox<>();
        patientCombo.setPreferredSize(new Dimension(300, 30));
        formPanel.add(patientCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addRoomBtn = UIUtil.createPrimaryButton("Add Room");
        addRoomBtn.addActionListener(new AddRoomAction());
        JButton assignBtn = UIUtil.createSecondaryButton("Assign Room");
        assignBtn.addActionListener(new AssignRoomAction());
        JButton releaseBtn = UIUtil.createSecondaryButton("Release Room");
        releaseBtn.addActionListener(new ReleaseRoomAction());
        deleteBtn = UIUtil.createDangerButton("Delete Room");
        deleteBtn.addActionListener(new DeleteRoomAction());
        JButton refreshBtn = UIUtil.createSecondaryButton("Refresh");
        refreshBtn.addActionListener(e -> loadRooms());
        buttonPanel.add(addRoomBtn);
        buttonPanel.add(assignBtn);
        buttonPanel.add(releaseBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(refreshBtn);
        formPanel.add(buttonPanel, gbc);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 1;
        formPanel.add(new JLabel("Filter Room:"), gbc);
        gbc.gridx = 1;
        filterRoomNumberField = new JTextField(15);
        UIUtil.styleTextField(filterRoomNumberField);
        formPanel.add(filterRoomNumberField, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("Filter Bed:"), gbc);
        gbc.gridx = 1;
        filterBedNumberField = new JTextField(15);
        UIUtil.styleTextField(filterBedNumberField);
        formPanel.add(filterBedNumberField, gbc);

        gbc.gridx = 0; gbc.gridy = 6;
        formPanel.add(new JLabel("Filter Patient:"), gbc);
        gbc.gridx = 1;
        filterPatientCombo = new JComboBox<>();
        filterPatientCombo.setPreferredSize(new Dimension(300, 30));
        formPanel.add(filterPatientCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 7;
        formPanel.add(new JLabel("Filter Status:"), gbc);
        gbc.gridx = 1;
        filterStatusCombo = new JComboBox<>(new String[]{"All", "available", "occupied"});
        filterStatusCombo.setPreferredSize(new Dimension(300, 30));
        formPanel.add(filterStatusCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 8; gbc.gridwidth = 2;
        JPanel filterButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterButtonPanel.setOpaque(false);
        filterBtn = UIUtil.createPrimaryButton("Apply Filter");
        filterBtn.addActionListener(new FilterRoomAction());
        clearFilterBtn = UIUtil.createSecondaryButton("Clear Filter");
        clearFilterBtn.addActionListener(new ClearRoomFilterAction());
        filterButtonPanel.add(filterBtn);
        filterButtonPanel.add(clearFilterBtn);
        formPanel.add(filterButtonPanel, gbc);

        String[] columns = {"ID", "Room", "Bed", "Status", "Patient ID", "Assigned Date", "Discharge Date"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        roomTable = new JTable(tableModel);
        UIUtil.styleTable(roomTable);
        JScrollPane tableScroll = new JScrollPane(roomTable);

        content.add(formPanel, BorderLayout.NORTH);
        content.add(tableScroll, BorderLayout.CENTER);
        add(content);
    }

    private void loadPatients() {
        try {
            patientCombo.removeAllItems();
            filterPatientCombo.removeAllItems();
            filterPatientCombo.addItem(null);
            List<Patient> patients = patientDAO.getAllPatients();
            for (Patient patient : patients) {
                patientCombo.addItem(patient);
                filterPatientCombo.addItem(patient);
            }
            filterPatientCombo.setRenderer(new DefaultListCellRenderer() {
                @Override
                public java.awt.Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    setText(value == null ? "All Patients" : value.toString());
                    return this;
                }
            });
            filterPatientCombo.setSelectedIndex(0);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Could not load patients: " + e.getMessage());
        }
    }

    private void loadRooms() {
        loadRooms(null, null, "All", null);
    }

    private void loadRooms(String roomNumber, String bedNumber, String status, Integer patientId) {
        try {
            tableModel.setRowCount(0);
            for (Room room : roomDAO.getFilteredRooms(roomNumber, bedNumber, "All".equals(status) ? null : status, patientId)) {
                tableModel.addRow(new Object[]{room.getId(), room.getRoomNumber(), room.getBedNumber(), room.getStatus(),
                        room.getPatientId(), room.getAssignedDate(), room.getDischargeDate()});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Could not load rooms: " + e.getMessage());
        }
    }

    private class AddRoomAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String roomNumber = roomNumberField.getText().trim();
            String bedNumber = bedNumberField.getText().trim();
            if (roomNumber.isEmpty() || bedNumber.isEmpty()) {
                JOptionPane.showMessageDialog(RoomManagementView.this, "Room number and bed number are required.");
                return;
            }
            try {
                Room room = new Room();
                room.setRoomNumber(roomNumber);
                room.setBedNumber(bedNumber);
                room.setStatus("available");
                if (roomDAO.addRoom(room)) {
                    JOptionPane.showMessageDialog(RoomManagementView.this, "Room added successfully.");
                    roomNumberField.setText("");
                    bedNumberField.setText("");
                    loadRooms();
                } else {
                    JOptionPane.showMessageDialog(RoomManagementView.this, "Unable to add room.");
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(RoomManagementView.this, "Error adding room: " + ex.getMessage());
            }
        }
    }

    private class AssignRoomAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedRow = roomTable.getSelectedRow();
            Patient patient = (Patient) patientCombo.getSelectedItem();
            if (selectedRow < 0) {
                JOptionPane.showMessageDialog(RoomManagementView.this, "Please select a room to assign.");
                return;
            }
            if (patient == null) {
                JOptionPane.showMessageDialog(RoomManagementView.this, "Please select a patient.");
                return;
            }
            int roomId = (Integer) tableModel.getValueAt(selectedRow, 0);
            try {
                if (roomDAO.assignRoom(roomId, patient.getId())) {
                    JOptionPane.showMessageDialog(RoomManagementView.this, "Room assigned successfully.");
                    loadRooms();
                } else {
                    JOptionPane.showMessageDialog(RoomManagementView.this, "Room could not be assigned. Ensure it is available.");
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(RoomManagementView.this, "Error assigning room: " + ex.getMessage());
            }
        }
    }

    private class ReleaseRoomAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedRow = roomTable.getSelectedRow();
            if (selectedRow < 0) {
                JOptionPane.showMessageDialog(RoomManagementView.this, "Please select a room to release.");
                return;
            }
            int roomId = (Integer) tableModel.getValueAt(selectedRow, 0);
            try {
                if (roomDAO.releaseRoom(roomId)) {
                    JOptionPane.showMessageDialog(RoomManagementView.this, "Room released successfully.");
                    loadRooms();
                } else {
                    JOptionPane.showMessageDialog(RoomManagementView.this, "Room could not be released. Ensure it is occupied.");
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(RoomManagementView.this, "Error releasing room: " + ex.getMessage());
            }
        }
    }

    private class DeleteRoomAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedRow = roomTable.getSelectedRow();
            if (selectedRow < 0) {
                JOptionPane.showMessageDialog(RoomManagementView.this, "Please select a room to delete.");
                return;
            }
            int confirmed = JOptionPane.showConfirmDialog(RoomManagementView.this,
                    "Are you sure you want to delete the selected room/bed record?",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION);
            if (confirmed != JOptionPane.YES_OPTION) {
                return;
            }
            int roomId = (Integer) tableModel.getValueAt(selectedRow, 0);
            try {
                if (roomDAO.deleteRoom(roomId)) {
                    JOptionPane.showMessageDialog(RoomManagementView.this, "Room deleted successfully.");
                    loadRooms();
                } else {
                    JOptionPane.showMessageDialog(RoomManagementView.this, "Room could not be deleted.");
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(RoomManagementView.this, "Error deleting room: " + ex.getMessage());
            }
        }
    }

    private class FilterRoomAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            Integer patientId = getSelectedFilterPatientId();
            String status = (String) filterStatusCombo.getSelectedItem();
            loadRooms(filterRoomNumberField.getText(), filterBedNumberField.getText(), status, patientId);
        }
    }

    private class ClearRoomFilterAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            filterRoomNumberField.setText("");
            filterBedNumberField.setText("");
            filterPatientCombo.setSelectedIndex(0);
            filterStatusCombo.setSelectedIndex(0);
            loadRooms();
        }
    }

    private Integer getSelectedFilterPatientId() {
        Patient patient = (Patient) filterPatientCombo.getSelectedItem();
        return patient != null ? patient.getId() : null;
    }
}
