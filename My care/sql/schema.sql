-- MyCare Hospital Management System Database Schema

CREATE DATABASE IF NOT EXISTS mycare_hospital;
USE mycare_hospital;

-- Users table for login system
CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role ENUM('admin', 'doctor', 'receptionist', 'patient') NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Patients table
CREATE TABLE patients (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    age INT NOT NULL,
    gender ENUM('Male', 'Female', 'Other') NOT NULL,
    contact VARCHAR(15),
    email VARCHAR(100),
    address TEXT,
    medical_history TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Doctors table
CREATE TABLE doctors (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    specialization VARCHAR(100) NOT NULL,
    contact VARCHAR(15),
    email VARCHAR(100),
    schedule TEXT, -- JSON or comma-separated availability
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Appointments table
CREATE TABLE appointments (
    id INT PRIMARY KEY AUTO_INCREMENT,
    patient_id INT NOT NULL,
    doctor_id INT NOT NULL,
    appointment_date DATETIME NOT NULL,
    status ENUM('scheduled', 'completed', 'cancelled') DEFAULT 'scheduled',
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES patients(id),
    FOREIGN KEY (doctor_id) REFERENCES doctors(id)
);

-- Prescriptions table
CREATE TABLE prescriptions (
    id INT PRIMARY KEY AUTO_INCREMENT,
    appointment_id INT NOT NULL,
    medicines TEXT NOT NULL, -- JSON array of medicines
    dosage TEXT,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (appointment_id) REFERENCES appointments(id)
);

-- Rooms/Beds table
CREATE TABLE rooms (
    id INT PRIMARY KEY AUTO_INCREMENT,
    room_number VARCHAR(10) NOT NULL,
    bed_number VARCHAR(10) NOT NULL,
    status ENUM('available', 'occupied') DEFAULT 'available',
    patient_id INT,
    assigned_date DATE,
    discharge_date DATE,
    FOREIGN KEY (patient_id) REFERENCES patients(id)
);

-- Bills table
CREATE TABLE bills (
    id INT PRIMARY KEY AUTO_INCREMENT,
    patient_id INT NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    status ENUM('pending', 'paid') DEFAULT 'pending',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES patients(id)
);

-- Bill items table
CREATE TABLE bill_items (
    id INT PRIMARY KEY AUTO_INCREMENT,
    bill_id INT NOT NULL,
    item_type ENUM('doctor_fee', 'room_charge', 'medicine') NOT NULL,
    description VARCHAR(255),
    amount DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (bill_id) REFERENCES bills(id)
);

-- Medical Records table
CREATE TABLE medical_records (
    id INT PRIMARY KEY AUTO_INCREMENT,
    patient_id INT NOT NULL,
    record_type VARCHAR(50), -- e.g., 'X-ray', 'Blood Test'
    file_path VARCHAR(255),
    diagnosis TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES patients(id)
);

-- Emergency Patients table
CREATE TABLE emergency_patients (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    age INT NOT NULL,
    condition_description TEXT NOT NULL,
    priority_level ENUM('Critical', 'High', 'Medium', 'Low') NOT NULL,
    arrival_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status ENUM('Waiting', 'In Treatment', 'Discharged') DEFAULT 'Waiting',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insert sample data
INSERT INTO users (username, password, role) VALUES
('admin', 'admin123', 'admin'),
('doctor1', 'doc123', 'doctor'),
('receptionist1', 'rec123', 'receptionist');

INSERT INTO doctors (name, specialization, contact, email, schedule) VALUES
('Dr. John Smith', 'Cardiology', '1234567890', 'john@hospital.com', 'Mon-Fri 9AM-5PM'),
('Dr. Jane Doe', 'Neurology', '0987654321', 'jane@hospital.com', 'Tue-Sat 10AM-6PM');

INSERT INTO patients (name, age, gender, contact, medical_history) VALUES
('Alice Johnson', 30, 'Female', '1112223333', 'No known allergies'),
('Bob Wilson', 45, 'Male', '4445556666', 'Hypertension');

-- Insert sample emergency patients
INSERT INTO emergency_patients (name, age, condition_description, priority_level, status) VALUES
('Emergency Patient 1', 25, 'Severe chest pain', 'Critical', 'Waiting'),
('Emergency Patient 2', 40, 'Broken leg', 'High', 'In Treatment'),
('Emergency Patient 3', 60, 'Fever and cough', 'Medium', 'Waiting');
INSERT INTO users (username, password, role) VALUES
('admin', 'admin123', 'admin'),
('doctor1', 'doc123', 'doctor'),
('receptionist1', 'rec123', 'receptionist');

INSERT INTO doctors (name, specialization, contact, email, schedule) VALUES
('Dr. John Smith', 'Cardiology', '1234567890', 'john@hospital.com', 'Mon-Fri 9AM-5PM'),
('Dr. Jane Doe', 'Neurology', '0987654321', 'jane@hospital.com', 'Tue-Sat 10AM-6PM');

INSERT INTO patients (name, age, gender, contact, medical_history) VALUES
('Alice Johnson', 30, 'Female', '1112223333', 'No known allergies'),
('Bob Wilson', 45, 'Male', '4445556666', 'Hypertension');