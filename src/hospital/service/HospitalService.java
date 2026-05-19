package hospital.service;

import hospital.model.*;
import hospital.util.DataStore;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class HospitalService {
    private DataStore dataStore;

    public HospitalService() {
        this.dataStore = DataStore.load();
    }

    public void saveData() {
        dataStore.save();
    }

    // Patient Management
    public void addPatient(Patient patient) {
        dataStore.getPatients().add(patient);
        saveData();
    }
    
    public void deletePatient(String id) {
        dataStore.getPatients().removeIf(p -> p.getId().equalsIgnoreCase(id));
        saveData();
    }

    public List<Patient> getAllPatients() {
        return dataStore.getPatients();
    }

    public Patient findPatientById(String id) {
        return dataStore.getPatients().stream()
                .filter(p -> p.getId().equalsIgnoreCase(id))
                .findFirst()
                .orElse(null);
    }
    
    public Patient authenticatePatient(String email, String password) {
        return dataStore.getPatients().stream()
                .filter(p -> p.getEmail().equalsIgnoreCase(email) && p.getPassword().equals(password))
                .findFirst()
                .orElse(null);
    }

    // Doctor Management
    public void addDoctor(Doctor doctor) {
        dataStore.getDoctors().add(doctor);
        saveData();
    }
    
    public void deleteDoctor(String id) {
        dataStore.getDoctors().removeIf(d -> d.getId().equalsIgnoreCase(id));
        saveData();
    }

    public List<Doctor> getAllDoctors() {
        return dataStore.getDoctors();
    }
    
    public List<Doctor> getApprovedDoctors() {
        return dataStore.getDoctors().stream()
                .filter(Doctor::isApproved)
                .collect(Collectors.toList());
    }

    public Doctor findDoctorById(String id) {
        return dataStore.getDoctors().stream()
                .filter(d -> d.getId().equalsIgnoreCase(id))
                .findFirst()
                .orElse(null);
    }
    
    public Doctor authenticateDoctor(String email, String password) {
        return dataStore.getDoctors().stream()
                .filter(d -> d.getEmail().equalsIgnoreCase(email) && d.getPassword().equals(password))
                .findFirst()
                .orElse(null);
    }

    // Appointment Management
    public void scheduleAppointment(Appointment appointment) {
        dataStore.getAppointments().add(appointment);
        saveData();
    }

    public List<Appointment> getAllAppointments() {
        return dataStore.getAppointments();
    }

    public List<Appointment> getAppointmentsByDoctorId(String doctorId) {
        return dataStore.getAppointments().stream()
                .filter(a -> a.getDoctorId().equalsIgnoreCase(doctorId))
                .collect(Collectors.toList());
    }

    public List<Appointment> getAppointmentsByPatientId(String patientId) {
        return dataStore.getAppointments().stream()
                .filter(a -> a.getPatientId().equalsIgnoreCase(patientId))
                .collect(Collectors.toList());
    }
    
    public Appointment findAppointmentById(String id) {
        return dataStore.getAppointments().stream()
                .filter(a -> a.getId().equalsIgnoreCase(id))
                .findFirst()
                .orElse(null);
    }
    
    public void approveAppointment(Appointment app) {
        String today = LocalDate.now().toString();
        if (!today.equals(dataStore.getLastTokenDate())) {
            dataStore.setLastTokenDate(today);
            dataStore.setLastTokenNumber(0);
        }
        
        int newToken = dataStore.getLastTokenNumber() + 1;
        dataStore.setLastTokenNumber(newToken);
        
        app.setTokenNumber(newToken);
        app.setStatus("APPROVED");
        saveData();
    }
    
    public void rejectAppointment(Appointment app) {
        app.setStatus("REJECTED");
        app.setTokenNumber(0);
        saveData();
    }

    // Prescription Management
    public void addPrescription(Prescription prescription) {
        dataStore.getPrescriptions().add(prescription);
        dataStore.exportPrescription(prescription); // Save to uploads/
        saveData();
    }

    public List<Prescription> getPrescriptionsByPatientId(String patientId) {
        return dataStore.getPrescriptions().stream()
                .filter(pr -> pr.getPatientId().equalsIgnoreCase(patientId))
                .collect(Collectors.toList());
    }
    
    public List<Prescription> getAllPrescriptions() {
        return dataStore.getPrescriptions();
    }
}
