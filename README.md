# Hospital Management System (Java CLI)

A professional Command Line Interface (CLI) application for managing hospital operations, built with Core Java and OOP principles.

## Features

### Role-Based Access
- **Admin**: Manage doctors, view patient records, and add new patients.
- **Doctor**: Book appointments, manage prescriptions, and view medical history.

### Modules
- **Patient Management**: Add, view, and search patients.
- **Doctor Management**: Register and list hospital staff.
- **Appointment System**: Schedule visits with specific doctors.
- **Prescription System**: Record medical advice and dosages.

## Technical Details
- **Language**: Java 8+
- **Data Storage**: In-memory `ArrayList` (Dynamic storage)
- **Architecture**: Modular design (Model-Service-App)
- **Concepts**: Encapsulation, List manipulation, Stream API.

## Login Credentials

| Role   | Username / ID | Password  |
|--------|---------------|-----------|
| Admin  | `admin`       | `admin123`|
| Doctor | `D001`        | (No PW)   |

*Note: You can add more doctors through the Admin menu.*

## How to Run

1. **Compile**:
   ```bash
   javac -d bin src/hospital/model/*.java src/hospital/service/*.java src/hospital/HospitalApp.java
   ```

2. **Run**:
   ```bash
   java -cp bin hospital.HospitalApp
   ```

## Project Structure
- `src/hospital/model/`: Data models (POJOs).
- `src/hospital/service/`: Business logic and data management.
- `src/hospital/HospitalApp.java`: Main entry point and CLI interface.
