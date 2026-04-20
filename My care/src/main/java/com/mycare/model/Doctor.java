package com.mycare.model;

public class Doctor {
    private int id;
    private String name;
    private String specialization;
    private String contact;
    private String email;
    private String schedule;

    public Doctor() {}

    public Doctor(int id, String name, String specialization, String contact, String email, String schedule) {
        this.id = id;
        this.name = name;
        this.specialization = specialization;
        this.contact = contact;
        this.email = email;
        this.schedule = schedule;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }

    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSchedule() { return schedule; }
    public void setSchedule(String schedule) { this.schedule = schedule; }

    @Override
    public String toString() {
        return name != null ? name : "Doctor #" + id;
    }
}