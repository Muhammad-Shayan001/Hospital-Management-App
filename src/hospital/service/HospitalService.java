package hospital.service;

import hospital.model.Appointment;
import hospital.model.Doctor;
import hospital.model.Patient;
import hospital.model.Prescription;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HospitalService {
    private List<Patient> patients;
    private List<Doctor> doctors;
    private List<Appointment> appointments;
    private List<Prescription> prescriptions;

    public HospitalService() {
        this.patients = new ArrayList<>();
        this.doctors = new ArrayList<>();
        this.appointments = new ArrayList<>();
        this.prescriptions = new ArrayList<>();
        
        // Add some initial data for testing
        initializeData();
    }

    private void initializeData() {
        doctors.add(new Doctor("D001", "Dr. ALi", "Cardiology"));
        doctors.add(new Doctor("D002", "Dr. Sarah", "Pediatrics"));
        
        patients.add(new Patient("P001", "Zain", 30, "Heart Checkup"));
    }

    // Patient Management
    public void addPatient(Patient patient) {
        patients.add(patient);
    }

    public List<Patient> getAllPatients() {
        return patients;
    }

    public Patient findPatientById(String id) {
        return patients.stream()
                .filter(p -> p.getId().equalsIgnoreCase(id))
                .findFirst()
                .orElse(null);
    }

    public List<Patient> searchPatientByName(String name) {
        return patients.stream()
                .filter(p -> p.getName().toLowerCase().contains(name.toLowerCase()))
                .collect(Collectors.toList());
    }

    // Doctor Management
    public void addDoctor(Doctor doctor) {
        doctors.add(doctor);
    }

    public List<Doctor> getAllDoctors() {
        return doctors;
    }

    public Doctor findDoctorById(String id) {
        return doctors.stream()
                .filter(d -> d.getId().equalsIgnoreCase(id))
                .findFirst()
                .orElse(null);
    }

    // Appointment Management
    public void scheduleAppointment(Appointment appointment) {
        appointments.add(appointment);
    }

    public List<Appointment> getAllAppointments() {
        return appointments;
    }

    // Prescription Management
    public void addPrescription(Prescription prescription) {
        prescriptions.add(prescription);
    }

    public List<Prescription> getPrescriptionsByPatientId(String patientId) {
        return prescriptions.stream()
                .filter(pr -> pr.getPatientId().equalsIgnoreCase(patientId))
                .collect(Collectors.toList());
    }
    
    public List<Prescription> getAllPrescriptions() {
        return prescriptions;
    }
}
