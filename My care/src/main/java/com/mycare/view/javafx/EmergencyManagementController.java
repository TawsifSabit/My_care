package com.mycare.view.javafx;

import com.mycare.dao.EmergencyDAO;
import com.mycare.model.EmergencyPatient;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class EmergencyManagementController implements Initializable {

    @FXML private TableView<EmergencyPatient> emergencyTable;
    @FXML private TableColumn<EmergencyPatient, Integer> idColumn;
    @FXML private TableColumn<EmergencyPatient, String> nameColumn;
    @FXML private TableColumn<EmergencyPatient, Integer> ageColumn;
    @FXML private TableColumn<EmergencyPatient, String> conditionColumn;
    @FXML private TableColumn<EmergencyPatient, String> priorityColumn;
    @FXML private TableColumn<EmergencyPatient, String> arrivalTimeColumn;
    @FXML private TableColumn<EmergencyPatient, String> statusColumn;

    @FXML private Button addPatientBtn, updateStatusBtn, dischargeBtn, submitBtn, refreshBtn;
    @FXML private TextField nameField, ageField, arrivalTimeField;
    @FXML private TextArea conditionField;
    @FXML private ComboBox<String> priorityComboBox;
    @FXML private Label statusLabel, patientCountLabel;

    private EmergencyDAO emergencyDAO;
    private ObservableList<EmergencyPatient> emergencyPatients;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        emergencyDAO = new EmergencyDAO();
        emergencyPatients = FXCollections.observableArrayList();

        setupTable();
        setupForm();
        loadEmergencyPatients();

        // Set initial arrival time
        updateArrivalTime();
    }

    private void setupTable() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        ageColumn.setCellValueFactory(new PropertyValueFactory<>("age"));
        conditionColumn.setCellValueFactory(new PropertyValueFactory<>("condition"));
        priorityColumn.setCellValueFactory(new PropertyValueFactory<>("priorityLevel"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Custom cell factory for arrival time formatting
        arrivalTimeColumn.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getArrivalTime().format(formatter)
            )
        );

        // Custom cell factory for priority highlighting
        priorityColumn.setCellFactory(column -> new TableCell<EmergencyPatient, String>() {
            @Override
            protected void updateItem(String priority, boolean empty) {
                super.updateItem(priority, empty);
                if (empty || priority == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(priority);
                    if ("Critical".equals(priority)) {
                        setStyle("-fx-background-color: #ffebee; -fx-text-fill: #c62828; -fx-font-weight: bold;");
                    } else if ("High".equals(priority)) {
                        setStyle("-fx-background-color: #fff3e0; -fx-text-fill: #ef6c00;");
                    } else if ("Medium".equals(priority)) {
                        setStyle("-fx-background-color: #f3e5f5; -fx-text-fill: #7b1fa2;");
                    } else {
                        setStyle("-fx-background-color: #e8f5e8; -fx-text-fill: #2e7d32;");
                    }
                }
            }
        });

        // Custom cell factory for status
        statusColumn.setCellFactory(column -> new TableCell<EmergencyPatient, String>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(status);
                    switch (status) {
                        case "Waiting":
                            setStyle("-fx-background-color: #fff3e0; -fx-text-fill: #ef6c00;");
                            break;
                        case "In Treatment":
                            setStyle("-fx-background-color: #e3f2fd; -fx-text-fill: #1976d2;");
                            break;
                        case "Discharged":
                            setStyle("-fx-background-color: #e8f5e8; -fx-text-fill: #2e7d32;");
                            break;
                        default:
                            setStyle("");
                    }
                }
            }
        });

        emergencyTable.setItems(emergencyPatients);
    }

    private void setupForm() {
        // Set default priority
        priorityComboBox.setValue("Medium");

        // Auto-update arrival time every second
        Thread timeThread = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1000);
                    Platform.runLater(this::updateArrivalTime);
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        timeThread.setDaemon(true);
        timeThread.start();
    }

    private void updateArrivalTime() {
        arrivalTimeField.setText(LocalDateTime.now().format(formatter));
    }

    @FXML
    private void refreshTable() {
        loadEmergencyPatients();
        updateStatus("Table refreshed");
    }

    @FXML
    private void showAddPatientForm() {
        clearForm();
        updateArrivalTime();
        updateStatus("Ready to add new emergency patient");
    }

    @FXML
    private void registerEmergencyPatient() {
        if (!validateForm()) {
            return;
        }

        try {
            String name = nameField.getText().trim();
            int age = Integer.parseInt(ageField.getText().trim());
            String condition = conditionField.getText().trim();
            String priority = priorityComboBox.getValue();
            LocalDateTime arrivalTime = LocalDateTime.parse(arrivalTimeField.getText(), formatter);

            EmergencyPatient patient = new EmergencyPatient(name, age, condition, priority, arrivalTime, "Waiting");

            if (emergencyDAO.addEmergencyPatient(patient)) {
                loadEmergencyPatients();
                clearForm();
                updateStatus("Emergency patient registered successfully");
            } else {
                showAlert("Error", "Failed to register emergency patient");
            }
        } catch (SQLException e) {
            showAlert("Database Error", "Failed to save emergency patient: " + e.getMessage());
        } catch (NumberFormatException e) {
            showAlert("Input Error", "Please enter a valid age");
        }
    }

    @FXML
    private void updatePatientStatus() {
        EmergencyPatient selectedPatient = emergencyTable.getSelectionModel().getSelectedItem();
        if (selectedPatient == null) {
            showAlert("Selection Error", "Please select a patient to update status");
            return;
        }

        ChoiceDialog<String> dialog = new ChoiceDialog<>("Waiting", "Waiting", "In Treatment", "Discharged");
        dialog.setTitle("Update Patient Status");
        dialog.setHeaderText("Update status for " + selectedPatient.getName());
        dialog.setContentText("Select new status:");

        dialog.showAndWait().ifPresent(status -> {
            try {
                if (emergencyDAO.updatePatientStatus(selectedPatient.getId(), status)) {
                    loadEmergencyPatients();
                    updateStatus("Patient status updated successfully");
                } else {
                    showAlert("Error", "Failed to update patient status");
                }
            } catch (SQLException e) {
                showAlert("Database Error", "Failed to update status: " + e.getMessage());
            }
        });
    }

    @FXML
    private void dischargePatient() {
        EmergencyPatient selectedPatient = emergencyTable.getSelectionModel().getSelectedItem();
        if (selectedPatient == null) {
            showAlert("Selection Error", "Please select a patient to discharge");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Discharge");
        confirmation.setHeaderText("Discharge Patient");
        confirmation.setContentText("Are you sure you want to discharge " + selectedPatient.getName() + "?");

        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    if (emergencyDAO.dischargePatient(selectedPatient.getId())) {
                        loadEmergencyPatients();
                        updateStatus("Patient discharged successfully");
                    } else {
                        showAlert("Error", "Failed to discharge patient");
                    }
                } catch (SQLException e) {
                    showAlert("Database Error", "Failed to discharge patient: " + e.getMessage());
                }
            }
        });
    }

    private void loadEmergencyPatients() {
        try {
            List<EmergencyPatient> patients = emergencyDAO.getAllEmergencyPatients();
            emergencyPatients.clear();
            emergencyPatients.addAll(patients);
            updatePatientCount();
        } catch (SQLException e) {
            showAlert("Database Error", "Failed to load emergency patients: " + e.getMessage());
        }
    }

    private void updatePatientCount() {
        patientCountLabel.setText("Total Patients: " + emergencyPatients.size());
    }

    private void clearForm() {
        nameField.clear();
        ageField.clear();
        conditionField.clear();
        priorityComboBox.setValue("Medium");
        updateArrivalTime();
    }

    private boolean validateForm() {
        if (nameField.getText().trim().isEmpty()) {
            showAlert("Validation Error", "Patient name is required");
            return false;
        }
        if (ageField.getText().trim().isEmpty()) {
            showAlert("Validation Error", "Patient age is required");
            return false;
        }
        if (conditionField.getText().trim().isEmpty()) {
            showAlert("Validation Error", "Condition description is required");
            return false;
        }
        if (priorityComboBox.getValue() == null) {
            showAlert("Validation Error", "Priority level is required");
            return false;
        }
        return true;
    }

    private void updateStatus(String message) {
        statusLabel.setText(message);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}