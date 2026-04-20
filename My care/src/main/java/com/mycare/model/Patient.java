package com.mycare.model;

public class Patient {
    private int id;
    private String name;
    private int age;
    private String gender;
    private String contact;
    private String email;
    private String address;
    private String medicalHistory;

    public Patient() {}

    public Patient(int id, String name, int age, String gender, String contact, String email, String address, String medicalHistory) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.contact = contact;
        this.email = email;
        this.address = address;
        this.medicalHistory = medicalHistory;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getMedicalHistory() { return medicalHistory; }
    public void setMedicalHistory(String medicalHistory) { this.medicalHistory = medicalHistory; }

    @Override
    public String toString() {
        return name != null ? name : "Patient #" + id;
    }
}