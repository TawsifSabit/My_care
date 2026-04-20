package com.mycare.model;

import java.sql.Date;

public class Room {
    private int id;
    private String roomNumber;
    private String bedNumber;
    private String status;
    private Integer patientId;
    private Date assignedDate;
    private Date dischargeDate;

    public Room() {}

    public Room(int id, String roomNumber, String bedNumber, String status, Integer patientId, Date assignedDate, Date dischargeDate) {
        this.id = id;
        this.roomNumber = roomNumber;
        this.bedNumber = bedNumber;
        this.status = status;
        this.patientId = patientId;
        this.assignedDate = assignedDate;
        this.dischargeDate = dischargeDate;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }
    public String getBedNumber() { return bedNumber; }
    public void setBedNumber(String bedNumber) { this.bedNumber = bedNumber; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Integer getPatientId() { return patientId; }
    public void setPatientId(Integer patientId) { this.patientId = patientId; }
    public Date getAssignedDate() { return assignedDate; }
    public void setAssignedDate(Date assignedDate) { this.assignedDate = assignedDate; }
    public Date getDischargeDate() { return dischargeDate; }
    public void setDischargeDate(Date dischargeDate) { this.dischargeDate = dischargeDate; }
}
