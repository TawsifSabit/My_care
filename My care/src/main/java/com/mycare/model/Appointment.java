package com.mycare.model;

import java.sql.Timestamp;

public class Appointment {
    private int id;
    private int patientId;
    private int doctorId;
    private Timestamp appointmentDate;
    private String status;
    private String notes;

    public Appointment() {}

    public Appointment(int id, int patientId, int doctorId, Timestamp appointmentDate, String status, String notes) {
        this.id = id;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.appointmentDate = appointmentDate;
        this.status = status;
        this.notes = notes;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getPatientId() { return patientId; }
    public void setPatientId(int patientId) { this.patientId = patientId; }

    public int getDoctorId() { return doctorId; }
    public void setDoctorId(int doctorId) { this.doctorId = doctorId; }

    public Timestamp getAppointmentDate() { return appointmentDate; }
    public void setAppointmentDate(Timestamp appointmentDate) { this.appointmentDate = appointmentDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    @Override
    public String toString() {
        return "Appointment #" + id + " (" + appointmentDate + ")";
    }
}