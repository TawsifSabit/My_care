package com.mycare.model;

import java.time.LocalDateTime;

public class EmergencyPatient {
    private int id;
    private String name;
    private int age;
    private String condition;
    private String priorityLevel; // Critical, High, Medium, Low
    private LocalDateTime arrivalTime;
    private String status; // Waiting, In Treatment, Discharged

    public EmergencyPatient() {}

    public EmergencyPatient(int id, String name, int age, String condition, String priorityLevel, LocalDateTime arrivalTime, String status) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.condition = condition;
        this.priorityLevel = priorityLevel;
        this.arrivalTime = arrivalTime;
        this.status = status;
    }

    // Constructor for new emergency patients (without ID)
    public EmergencyPatient(String name, int age, String condition, String priorityLevel, LocalDateTime arrivalTime, String status) {
        this.name = name;
        this.age = age;
        this.condition = condition;
        this.priorityLevel = priorityLevel;
        this.arrivalTime = arrivalTime;
        this.status = status;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public String getCondition() { return condition; }
    public void setCondition(String condition) { this.condition = condition; }

    public String getPriorityLevel() { return priorityLevel; }
    public void setPriorityLevel(String priorityLevel) { this.priorityLevel = priorityLevel; }

    public LocalDateTime getArrivalTime() { return arrivalTime; }
    public void setArrivalTime(LocalDateTime arrivalTime) { this.arrivalTime = arrivalTime; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return "EmergencyPatient{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", condition='" + condition + '\'' +
                ", priorityLevel='" + priorityLevel + '\'' +
                ", arrivalTime=" + arrivalTime +
                ", status='" + status + '\'' +
                '}';
    }
}