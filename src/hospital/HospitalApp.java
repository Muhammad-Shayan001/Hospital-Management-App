package hospital;

import hospital.model.*;
import hospital.service.HospitalService;
import hospital.util.AIHelper;

import java.util.List;
import java.util.Scanner;
import java.util.UUID;
import java.util.stream.Collectors;

public class HospitalApp {
    private static final Scanner scanner = new Scanner(System.in);
    private static final HospitalService service = new HospitalService();
    private static final String ADMIN_EMAIL = "admin@gmail.com";
    private static String adminPassword = "Admin123"; // Default admin password

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("     HOSPITAL MANAGEMENT SYSTEM (CLI)     ");
        System.out.println("==================================================");

        while (true) {
            System.out.println("\n--- MAIN MENU ---");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. AI Medical Assistant");
            System.out.println("4. Exit");
            System.out.print("Select an option: ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    loginMenu();
                    break;
                case "2":
                    registerMenu();
                    break;
                case "3":
                    AIHelper.startChat(scanner);
                    break;
                case "4":
                    System.out.println("Thank you for using the Hospital Management System. Goodbye!");
                    System.exit(0);
                default:
                    System.out.println("Invalid choice! Please try again.");
            }
        }
    }

    private static void loginMenu() {
        System.out.println("\n--- LOGIN ---");
        System.out.println("1. Admin Login");
        System.out.println("2. Doctor Login");
        System.out.println("3. Patient Login");
        System.out.println("4. Back to Main Menu");
        System.out.print("Select role: ");

        String choice = scanner.nextLine().trim();
        if (choice.equals("4"))
            return;

        System.out.print("Enter Email: ");
        String email = scanner.nextLine().trim();
        System.out.print("Enter Password: ");
        String password = scanner.nextLine().trim();

        if (choice.equals("1")) {
            if (email.equalsIgnoreCase(ADMIN_EMAIL) && password.equals(adminPassword)) {
                System.out.println("\nLogin Successful!");
                adminMenu();
            } else {
                System.out.println("Invalid Admin Credentials!");
            }
        } else if (choice.equals("2")) {
            Doctor doc = service.authenticateDoctor(email, password);
            if (doc != null) {
                if (!doc.isApproved()) {
                    System.out.println("Account pending admin approval.");
                } else {
                    System.out.println("\nLogin Successful!");
                    doctorMenu(doc);
                }
            } else {
                System.out.println("Invalid Doctor Credentials!");
            }
        } else if (choice.equals("3")) {
            Patient pat = service.authenticatePatient(email, password);
            if (pat != null) {
                System.out.println("\nLogin Successful!");
                patientMenu(pat);
            } else {
                System.out.println("Invalid Patient Credentials!");
            }
        } else {
            System.out.println("Invalid choice.");
        }
    }

    private static void registerMenu() {
        System.out.println("\n--- REGISTRATION ---");
        System.out.println("1. Doctor Registration");
        System.out.println("2. Patient Registration");
        System.out.println("3. Back to Main Menu");
        System.out.print("Select option: ");

        String choice = scanner.nextLine().trim();
        if (choice.equals("1")) {
            System.out.print("Enter Name: ");
            String name = scanner.nextLine();
            System.out.print("Enter Email: ");
            String email = scanner.nextLine();
            System.out.print("Enter Password: ");
            String pass = scanner.nextLine();
            System.out.print("Enter Specialization: ");
            String spec = scanner.nextLine();
            String id = "D" + UUID.randomUUID().toString().substring(0, 4).toUpperCase();

            Doctor doc = new Doctor(id, name, email, pass, spec, false);
            service.addDoctor(doc);
            System.out.println("Registration successful. Waiting for admin approval.");

        } else if (choice.equals("2")) {
            System.out.print("Enter Name: ");
            String name = scanner.nextLine();
            System.out.print("Enter Email: ");
            String email = scanner.nextLine();
            System.out.print("Enter Password: ");
            String pass = scanner.nextLine();
            System.out.print("Enter Age: ");
            int age = Integer.parseInt(scanner.nextLine());
            System.out.print("Enter Disease/Symptom: ");
            String disease = scanner.nextLine();
            String id = "P" + UUID.randomUUID().toString().substring(0, 4).toUpperCase();

            Patient pat = new Patient(id, name, email, pass, age, disease);
            service.addPatient(pat);
            System.out.println("Registration successful. You can now login.");
        }
    }

    // ================= ADMIN MENU =================
    private static void adminMenu() {
        while (true) {
            System.out.println("\n--- ADMIN DASHBOARD ---");
            System.out.println("1. Manage Doctors");
            System.out.println("2. Manage Patients");
            System.out.println("3. Approve Doctor Registrations");
            System.out.println("4. Reset User Password");
            System.out.println("5. View All Appointments");
            System.out.println("6. View All Prescriptions");
            System.out.println("7. Logout");
            System.out.print("Select an option: ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    manageDoctors();
                    break;
                case "2":
                    managePatients();
                    break;
                case "3":
                    approveDoctors();
                    break;
                case "4":
                    resetPassword();
                    break;
                case "5":
                    viewAllAppointments();
                    break;
                case "6":
                    viewAllPrescriptions();
                    break;
                case "7":
                    return;
                default:
                    System.out.println("Invalid choice!");
            }
        }
    }

    private static void manageDoctors() {
        System.out.println("\n--- Manage Doctors ---");
        System.out.println("1. Add Doctor");
        System.out.println("2. Edit Doctor");
        System.out.println("3. Delete Doctor");
        System.out.println("4. View All Doctors");
        System.out.print("Select: ");
        String choice = scanner.nextLine().trim();

        if (choice.equals("1")) {
            System.out.print("Enter Name: ");
            String name = scanner.nextLine();
            System.out.print("Enter Email: ");
            String email = scanner.nextLine();
            System.out.print("Enter Password: ");
            String pass = scanner.nextLine();
            System.out.print("Enter Specialization: ");
            String spec = scanner.nextLine();
            String id = "D" + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
            service.addDoctor(new Doctor(id, name, email, pass, spec, true));
            System.out.println("Doctor added successfully!");
        } else if (choice.equals("2")) {
            if (service.getAllDoctors().isEmpty()) {
                System.out.println("No doctors are registered in the system yet.");
                return;
            }
            System.out.println("\n--- Registered Doctors ---");
            for (Doctor doc : service.getAllDoctors()) System.out.println(doc);
            System.out.println("--------------------------");
            System.out.print("Enter Doctor ID to edit: ");
            Doctor d = service.findDoctorById(scanner.nextLine().trim());
            if (d != null) {
                System.out.print("Enter New Name (leave blank to keep current): ");
                String name = scanner.nextLine();
                if (!name.isEmpty())
                    d.setName(name);
                System.out.print("Enter New Specialization (leave blank to keep current): ");
                String spec = scanner.nextLine();
                if (!spec.isEmpty())
                    d.setSpecialization(spec);
                service.saveData();
                System.out.println("Doctor updated.");
            } else {
                    System.out.println("Doctor not found.");
            }
        } else if (choice.equals("3")) {
            if (service.getAllDoctors().isEmpty()) {
                    System.out.println("No doctors are registered in the system yet.");
                return;
            }
            System.out.println("\n--- Registered Doctors ---");
            for (Doctor doc : service.getAllDoctors()) System.out.println(doc);
            System.out.println("--------------------------");
            System.out.print("Enter Doctor ID to delete: ");
            String delId = scanner.nextLine().trim();
            if (service.findDoctorById(delId) == null) {
                System.out.println("Doctor with ID '" + delId + "' not found.");
            } else {
                service.deleteDoctor(delId);
                System.out.println("Doctor deleted successfully.");
            }
        } else if (choice.equals("4")) {
            if (service.getAllDoctors().isEmpty()) {
                System.out.println("No doctors are registered in the system yet.");
            } else {
                for (Doctor d : service.getAllDoctors()) System.out.println(d);
            }
        }
    }

    private static void managePatients() {
        System.out.println("\n--- Manage Patients ---");
        System.out.println("1. Add Patient");
        System.out.println("2. Edit Patient");
        System.out.println("3. Delete Patient");
        System.out.println("4. View All Patients");
        System.out.print("Select: ");
        String choice = scanner.nextLine().trim();

        if (choice.equals("1")) {
            System.out.print("Enter Name: ");
            String name = scanner.nextLine();
            System.out.print("Enter Email: ");
            String email = scanner.nextLine();
            System.out.print("Enter Password: ");
            String pass = scanner.nextLine();
            System.out.print("Enter Age: ");
            int age = Integer.parseInt(scanner.nextLine());
            System.out.print("Enter Disease: ");
            String disease = scanner.nextLine();
            String id = "P" + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
            service.addPatient(new Patient(id, name, email, pass, age, disease));
            System.out.println("Patient added successfully!");
        } else if (choice.equals("2")) {
            if (service.getAllPatients().isEmpty()) {
                System.out.println("No patients are registered in the system yet.");
                return;
            }
            System.out.println("\n--- Registered Patients ---");
            for (Patient pat : service.getAllPatients()) System.out.println(pat);
            System.out.println("---------------------------");
            System.out.print("Enter Patient ID to edit: ");
            Patient p = service.findPatientById(scanner.nextLine().trim());
            if (p != null) {
                System.out.print("Enter New Name (leave blank to keep current): ");
                String name = scanner.nextLine();
                if (!name.isEmpty())
                    p.setName(name);
                System.out.print("Enter New Disease (leave blank to keep current): ");
                String disease = scanner.nextLine();
                if (!disease.isEmpty())
                    p.setDisease(disease);
                service.saveData();
                System.out.println("Patient updated.");
            } else {
                System.out.println("Patient not found.");
            }
        } else if (choice.equals("3")) {
            if (service.getAllPatients().isEmpty()) {
                System.out.println("No patients are registered in the system yet.");
                return;
            }
            System.out.println("\n--- Registered Patients ---");
            for (Patient pat : service.getAllPatients()) System.out.println(pat);
            System.out.println("---------------------------");
            System.out.print("Enter Patient ID to delete: ");
            String delId = scanner.nextLine().trim();
            if (service.findPatientById(delId) == null) {
                System.out.println("Patient with ID '" + delId + "' not found.");
            } else {
                service.deletePatient(delId);
                System.out.println("Patient deleted successfully.");
            }
        } else if (choice.equals("4")) {
            if (service.getAllPatients().isEmpty()) {
                System.out.println("No patients are registered in the system yet.");
            } else {
                for (Patient p : service.getAllPatients()) System.out.println(p);
            }
        }
    }

    private static void approveDoctors() {
        List<Doctor> pending = service.getAllDoctors().stream().filter(d -> !d.isApproved())
                .collect(Collectors.toList());
        if (pending.isEmpty()) {
            System.out.println("No pending doctor registrations.");
            return;
        }
        for (Doctor d : pending) {
            System.out.println(d);
            System.out.print("Approve this doctor? (yes/no): ");
            if (scanner.nextLine().equalsIgnoreCase("yes")) {
                d.setApproved(true);
                service.saveData();
                System.out.println("Approved!");
            }
        }
    }

    private static void resetPassword() {
        System.out.print("Enter user email to reset password: ");
        String email = scanner.nextLine().trim();
        System.out.print("Enter new password: ");
        String newPass = scanner.nextLine().trim();

        if (email.equalsIgnoreCase(ADMIN_EMAIL)) {
            adminPassword = newPass;
            System.out.println("Admin password reset.");
            return;
        }

        for (Doctor d : service.getAllDoctors()) {
            if (d.getEmail().equalsIgnoreCase(email)) {
                d.setPassword(newPass);
                service.saveData();
                System.out.println("Doctor password reset.");
                return;
            }
        }
        for (Patient p : service.getAllPatients()) {
            if (p.getEmail().equalsIgnoreCase(email)) {
                p.setPassword(newPass);
                service.saveData();
                System.out.println("Patient password reset.");
                return;
            }
        }
        System.out.println("Email not found.");
    }

    private static void viewAllAppointments() {
        for (Appointment a : service.getAllAppointments()) {
            System.out.println(a);
        }
    }

    private static void viewAllPrescriptions() {
        for (Prescription p : service.getAllPrescriptions()) {
            System.out.println(p);
        }
    }

    // ================= DOCTOR MENU =================
    private static void doctorMenu(Doctor doc) {
        while (true) {
            System.out.println("\n--- DOCTOR DASHBOARD (Welcome " + doc.getName() + ") ---");
            System.out.println("1. View My Appointments");
            System.out.println("2. Approve/Reject Appointments");
            System.out.println("3. Create Prescription");
            System.out.println("4. Logout");
            System.out.print("Select an option: ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    for (Appointment a : service.getAppointmentsByDoctorId(doc.getId()))
                        System.out.println(a);
                    break;
                case "2":
                    manageAppointments(doc);
                    break;
                case "3":
                    createPrescription(doc);
                    break;
                case "4":
                    return;
                default:
                    System.out.println("Invalid choice!");
            }
        }
    }

    private static void manageAppointments(Doctor doc) {
        List<Appointment> pending = service.getAppointmentsByDoctorId(doc.getId()).stream()
                .filter(a -> a.getStatus().equals("PENDING")).collect(Collectors.toList());
        if (pending.isEmpty()) {
            System.out.println("No pending appointments.");
            return;
        }
        for (Appointment a : pending) {
            System.out.println(a);
            System.out.print("Approve (A) / Reject (R) / Skip (S): ");
            String action = scanner.nextLine().toUpperCase();
            if (action.equals("A")) {
                service.approveAppointment(a);
                System.out.println("Approved. Token Number: " + a.getTokenNumber());
            } else if (action.equals("R")) {
                service.rejectAppointment(a);
                System.out.println("Rejected.");
            }
        }
    }

    private static void createPrescription(Doctor doc) {
        System.out.println("\n--- Your Assigned Patients ---");
        List<Appointment> apps = service.getAppointmentsByDoctorId(doc.getId());
        if (apps.isEmpty()) {
            System.out.println("You have no assigned patients to write a prescription for.");
            return;
        }
        for (Appointment a : apps) {
            Patient pt = service.findPatientById(a.getPatientId());
            if (pt != null) {
                System.out.println("Patient ID: " + pt.getId() + " | Name: " + pt.getName() + " | Appointment Status: " + a.getStatus());
            }
        }
        System.out.println("------------------------------");
        System.out.print("Enter Patient ID from the list above: ");
        String patId = scanner.nextLine();
        Patient p = service.findPatientById(patId);
        if (p == null) {
            System.out.println("Patient not found.");
            return;
        }
        System.out.print("Enter Diagnosis: ");
        String diag = scanner.nextLine();
        System.out.print("Enter Medicine: ");
        String med = scanner.nextLine();
        System.out.print("Enter Dosage: ");
        String dos = scanner.nextLine();
        System.out.print("Enter Instructions: ");
        String inst = scanner.nextLine();

        String preId = "PR" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        service.addPrescription(new Prescription(preId, patId, doc.getId(), diag, med, dos, inst));
        System.out.println("Prescription saved and exported to file.");
    }

    // ================= PATIENT MENU =================
    private static void patientMenu(Patient pat) {
        while (true) {
            System.out.println("\n--- PATIENT DASHBOARD (Welcome " + pat.getName() + ") ---");
            System.out.println("1. Book Appointment");
            System.out.println("2. View My Appointments (Tokens & Status)");
            System.out.println("3. View My Prescriptions");
            System.out.println("4. Logout");
            System.out.print("Select an option: ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    bookAppointment(pat);
                    break;
                case "2":
                    for (Appointment a : service.getAppointmentsByPatientId(pat.getId()))
                        System.out.println(a);
                    break;
                case "3":
                    for (Prescription p : service.getPrescriptionsByPatientId(pat.getId()))
                        System.out.println(p);
                    break;
                case "4":
                    return;
                default:
                    System.out.println("Invalid choice!");
            }
        }
    }

    private static void bookAppointment(Patient pat) {
        System.out.println("\n--- Available Doctors ---");
        for (Doctor d : service.getApprovedDoctors()) {
            System.out.println(d);
        }
        System.out.print("Enter Doctor ID to book: ");
        String docId = scanner.nextLine();
        if (service.findDoctorById(docId) == null) {
            System.out.println("Invalid Doctor ID.");
            return;
        }
        System.out.print("Enter Date (YYYY-MM-DD): ");
        String date = scanner.nextLine();
        System.out.print("Enter Time (HH:MM): ");
        String time = scanner.nextLine();

        String appId = "A" + UUID.randomUUID().toString().substring(0, 5).toUpperCase();
        service.scheduleAppointment(new Appointment(appId, pat.getId(), docId, date, time));
        System.out.println("Appointment requested. Pending doctor approval.");
    }
}
