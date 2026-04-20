package com.mycare.model;

import java.sql.Timestamp;

public class Prescription {
    private int id;
    private int appointmentId;
    private String medicines;
    private String dosage;
    private String notes;
    private Timestamp createdAt;

    public Prescription() {}

    public Prescription(int id, int appointmentId, String medicines, String dosage, String notes, Timestamp createdAt) {
        this.id = id;
        this.appointmentId = appointmentId;
        this.medicines = medicines;
        this.dosage = dosage;
        this.notes = notes;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getAppointmentId() { return appointmentId; }
    public void setAppointmentId(int appointmentId) { this.appointmentId = appointmentId; }
    public String getMedicines() { return medicines; }
    public void setMedicines(String medicines) { this.medicines = medicines; }
    public String getDosage() { return dosage; }
    public void setDosage(String dosage) { this.dosage = dosage; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
