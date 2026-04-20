# MyCare Hospital Management System

A comprehensive hospital management system built with Java Swing and MySQL database.

## Features

### Core Modules
- **Patient Management**: Add, update, delete, and search patient records
- **Doctor Management**: Manage doctor profiles with specializations and schedules
- **Appointment System**: Book, cancel, and reschedule appointments with availability checking
- **Prescription System**: Manage prescriptions and medical records
- **Room/Bed Management**: Track room and bed availability
- **Billing & Payment**: Generate bills and manage payments

### Advanced Features
- **Role-Based Login**: Admin, Doctor, Receptionist access levels
- **Dashboard & Reports**: Analytics and reporting
- **Search & Filter**: Advanced search capabilities
- **Medical Records Management**: Store and manage patient medical history

## Prerequisites

- Java 8 or higher
- MySQL 5.7 or higher
- MySQL Connector/J (JDBC driver)

## Setup Instructions

1. **Install MySQL** and create a database user with appropriate permissions.

2. **Download MySQL Connector/J**:
   - Download from https://dev.mysql.com/downloads/connector/j/
   - Place the JAR file in the `lib` directory

3. **Create Database**:
   - Run the SQL script in `sql/schema.sql` to create the database and tables
   - Update database connection details in `DBConnection.java` if needed

4. **Compile and Run**:
   ```bash
   # Compile
   javac -cp "lib/mysql-connector-java-*.jar" -d bin src/main/java/com/mycare/**/*.java

   # Run
   java -cp "bin:lib/mysql-connector-java-*.jar" com.mycare.view.LoginView
   ```

## Default Login Credentials

- **Admin**: username: `admin`, password: `admin123`
- **Doctor**: username: `doctor1`, password: `doc123`
- **Receptionist**: username: `receptionist1`, password: `rec123`

## Project Structure

```
MyCare/
├── src/main/java/com/mycare/
│   ├── model/          # Data models
│   ├── dao/            # Database access objects
│   ├── service/        # Business logic
│   ├── util/           # Utilities
│   └── view/           # GUI components
├── lib/                # External libraries
├── sql/                # Database scripts
└── README.md
```

## Technologies Used

- **Java Swing**: GUI framework
- **MySQL**: Database
- **JDBC**: Database connectivity
- **MVC Pattern**: Architecture

## Future Enhancements

- Complete implementation of all modules
- Advanced reporting with charts
- Email notifications
- Mobile app integration
- Cloud deployment

## License

This project is for educational purposes.