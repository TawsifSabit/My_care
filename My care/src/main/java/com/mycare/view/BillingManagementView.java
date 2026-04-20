package com.mycare.view;

import com.mycare.dao.BillingDAO;
import com.mycare.dao.PatientDAO;
import com.mycare.model.Bill;
import com.mycare.model.BillItem;
import com.mycare.model.Patient;
import com.mycare.util.UIUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;

public class BillingManagementView extends JFrame {
    private BillingDAO billingDAO;
    private PatientDAO patientDAO;
    private JComboBox<Patient> patientCombo;
    private JComboBox<String> itemTypeCombo;
    private JTextField descriptionField;
    private JTextField amountField;
    private DefaultTableModel billTableModel;
    private JTable billTable;
    private DefaultTableModel itemTableModel;
    private JTable itemTable;

    public BillingManagementView() {
        UIUtil.initModernTheme();
        billingDAO = new BillingDAO();
        patientDAO = new PatientDAO();
        initializeUI();
        loadPatients();
        loadBills();
    }

    private void initializeUI() {
        setTitle("Billing & Payment");
        setSize(1020, 680);
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
        formPanel.add(new JLabel("Patient:"), gbc);
        gbc.gridx = 1;
        patientCombo = new JComboBox<>();
        patientCombo.setPreferredSize(new Dimension(300, 30));
        formPanel.add(patientCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Item Type:"), gbc);
        gbc.gridx = 1;
        itemTypeCombo = new JComboBox<>(new String[]{"doctor_fee", "room_charge", "medicine"});
        itemTypeCombo.setPreferredSize(new Dimension(300, 30));
        formPanel.add(itemTypeCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1;
        descriptionField = new JTextField(30);
        UIUtil.styleTextField(descriptionField);
        formPanel.add(descriptionField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Amount:"), gbc);
        gbc.gridx = 1;
        amountField = new JTextField(12);
        UIUtil.styleTextField(amountField);
        formPanel.add(amountField, gbc);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton createBillBtn = UIUtil.createPrimaryButton("Create Bill");
        createBillBtn.addActionListener(new CreateBillAction());
        JButton addItemBtn = UIUtil.createSecondaryButton("Add Item");
        addItemBtn.addActionListener(new AddItemAction());
        JButton payBtn = UIUtil.createDangerButton("Mark Paid");
        payBtn.addActionListener(new MarkPaidAction());
        JButton refreshBtn = UIUtil.createSecondaryButton("Refresh");
        refreshBtn.addActionListener(e -> loadBills());
        buttonPanel.add(createBillBtn);
        buttonPanel.add(addItemBtn);
        buttonPanel.add(payBtn);
        buttonPanel.add(refreshBtn);
        formPanel.add(buttonPanel, gbc);

        String[] billColumns = {"Bill ID", "Patient", "Total", "Status"};
        billTableModel = new DefaultTableModel(billColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        billTable = new JTable(billTableModel);
        UIUtil.styleTable(billTable);
        billTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        billTable.getSelectionModel().addListSelectionListener(e -> loadItemsForSelectedBill());

        String[] itemColumns = {"Item ID", "Type", "Description", "Amount"};
        itemTableModel = new DefaultTableModel(itemColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        itemTable = new JTable(itemTableModel);
        UIUtil.styleTable(itemTable);

        JPanel tablesPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        tablesPanel.add(new JScrollPane(billTable));
        tablesPanel.add(new JScrollPane(itemTable));

        mainPanel.add(formPanel, BorderLayout.NORTH);
        mainPanel.add(tablesPanel, BorderLayout.CENTER);
        add(mainPanel);
    }

    private void loadPatients() {
        try {
            patientCombo.removeAllItems();
            for (Patient patient : patientDAO.getAllPatients()) {
                patientCombo.addItem(patient);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Unable to load patients: " + e.getMessage());
        }
    }

    private void loadBills() {
        try {
            billTableModel.setRowCount(0);
            for (Bill bill : billingDAO.getAllBills()) {
                String patientName = "Unknown";
                Patient patient = patientDAO.getPatientById(bill.getPatientId());
                if (patient != null) {
                    patientName = patient.getName();
                }
                billTableModel.addRow(new Object[]{bill.getId(), patientName, bill.getTotalAmount(), bill.getStatus()});
            }
            itemTableModel.setRowCount(0);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Unable to load bills: " + e.getMessage());
        }
    }

    private void loadItemsForSelectedBill() {
        int selectedRow = billTable.getSelectedRow();
        itemTableModel.setRowCount(0);
        if (selectedRow < 0) {
            return;
        }
        int billId = (Integer) billTableModel.getValueAt(selectedRow, 0);
        try {
            for (BillItem item : billingDAO.getBillItems(billId)) {
                itemTableModel.addRow(new Object[]{item.getId(), item.getItemType(), item.getDescription(), item.getAmount()});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Unable to load bill items: " + e.getMessage());
        }
    }

    private class CreateBillAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            Patient patient = (Patient) patientCombo.getSelectedItem();
            if (patient == null) {
                JOptionPane.showMessageDialog(BillingManagementView.this, "Please select a patient.");
                return;
            }
            try {
                Bill bill = new Bill();
                bill.setPatientId(patient.getId());
                bill.setTotalAmount(0.0);
                bill.setStatus("pending");
                int billId = billingDAO.createBill(bill);
                if (billId > 0) {
                    JOptionPane.showMessageDialog(BillingManagementView.this, "Bill created with ID " + billId);
                    loadBills();
                } else {
                    JOptionPane.showMessageDialog(BillingManagementView.this, "Unable to create bill.");
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(BillingManagementView.this, "Error creating bill: " + ex.getMessage());
            }
        }
    }

    private class AddItemAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedRow = billTable.getSelectedRow();
            if (selectedRow < 0) {
                JOptionPane.showMessageDialog(BillingManagementView.this, "Please select a bill.");
                return;
            }
            String type = (String) itemTypeCombo.getSelectedItem();
            String description = descriptionField.getText().trim();
            String amountText = amountField.getText().trim();
            if (description.isEmpty() || amountText.isEmpty()) {
                JOptionPane.showMessageDialog(BillingManagementView.this, "Description and amount are required.");
                return;
            }
            try {
                double amount = Double.parseDouble(amountText);
                int billId = (Integer) billTableModel.getValueAt(selectedRow, 0);
                BillItem item = new BillItem();
                item.setBillId(billId);
                item.setItemType(type);
                item.setDescription(description);
                item.setAmount(amount);
                if (billingDAO.addBillItem(item)) {
                    JOptionPane.showMessageDialog(BillingManagementView.this, "Item added successfully.");
                    descriptionField.setText("");
                    amountField.setText("");
                    loadBills();
                    loadItemsForSelectedBill();
                } else {
                    JOptionPane.showMessageDialog(BillingManagementView.this, "Unable to add item.");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(BillingManagementView.this, "Amount must be a number.");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(BillingManagementView.this, "Error adding item: " + ex.getMessage());
            }
        }
    }

    private class MarkPaidAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedRow = billTable.getSelectedRow();
            if (selectedRow < 0) {
                JOptionPane.showMessageDialog(BillingManagementView.this, "Please select a bill.");
                return;
            }
            int billId = (Integer) billTableModel.getValueAt(selectedRow, 0);
            try {
                if (billingDAO.markBillPaid(billId)) {
                    JOptionPane.showMessageDialog(BillingManagementView.this, "Bill marked as paid.");
                    loadBills();
                } else {
                    JOptionPane.showMessageDialog(BillingManagementView.this, "Unable to update bill status.");
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(BillingManagementView.this, "Error marking bill paid: " + ex.getMessage());
            }
        }
    }
}
