package hospital;

import hospital.model.Appointment;
import hospital.model.Doctor;
import hospital.model.Patient;
import hospital.model.Prescription;
import hospital.service.HospitalService;
import java.util.List;
import java.util.Scanner;

public class HospitalApp {
    private static final Scanner scanner = new Scanner(System.in);
    private static final HospitalService service = new HospitalService();

    public static void main(String[] args) {
        showWelcomeMessage();
        while (true) {
            System.out.println("\n--- LOGIN SYSTEM ---");
            System.out.println("1. Admin Login");
            System.out.println("2. Doctor Login");
            System.out.println("3. Patient Self-Registration & Booking");
            System.out.println("4. Exit");
            System.out.print("Select an option: ");
            
            String choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    adminLogin();
                    break;
                case "2":
                    doctorLogin();
                    break;
                case "3":
                    patientSelfBooking();
                    break;
                case "4":
                    System.out.println("Thank you for using Hospital Management System. Goodbye!");
                    System.exit(0);
                default:
                    System.out.println("Invalid choice! Please try again.");
            }
        }
    }

    private static void showWelcomeMessage() {
        System.out.println("==================================================");
        System.out.println("     HOSPITAL MANAGEMENT SYSTEM (CLI)     ");
        System.out.println("==================================================");
        System.out.println("Developed for University Semester Project");
    }

    private static void patientSelfBooking() {
        System.out.println("\n--- PATIENT SELF-REGISTRATION ---");
        
        // Auto-generate Patient ID
        String newPatientId = "P" + String.format("%03d", service.getAllPatients().size() + 1);
        
        System.out.print("Enter your Name: ");
        String name = scanner.nextLine();
        
        System.out.print("Enter your Age: ");
        int age;
        try {
            age = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid age! Registration failed.");
            return;
        }
        
        System.out.print("Enter your Disease/Reason for visit: ");
        String disease = scanner.nextLine();
        
        Patient newPatient = new Patient(newPatientId, name, age, disease);
        service.addPatient(newPatient);
        
        System.out.println("\n✅ Registration successful!");
        System.out.println("Your Auto-Generated Patient ID is: " + newPatientId);
        
        System.out.print("\nDo you want to book an appointment now? (yes/no): ");
        if (scanner.nextLine().trim().equalsIgnoreCase("yes")) {
            System.out.println("\n--- AVAILABLE DOCTORS ---");
            viewAllDoctors();
            
            System.out.print("Enter the ID of the Doctor you want to see: ");
            String docId = scanner.nextLine();
            Doctor doc = service.findDoctorById(docId);
            
            if (doc != null) {
                System.out.print("Enter preferred Date (DD/MM/YYYY): ");
                String date = scanner.nextLine();
                System.out.print("Enter preferred Time (HH:MM): ");
                String time = scanner.nextLine();
                
                // Book appointment and generate a Token
                String appointmentToken = "TKN-" + (service.getAllAppointments().size() + 101);
                service.scheduleAppointment(new Appointment(newPatientId, docId, date, time));
                
                System.out.println("\n✅ Appointment Booked Successfully!");
                System.out.println("★ Your Appointment Token/No is: " + appointmentToken + " ★");
                System.out.println("Please keep this token for your reference.");
            } else {
                System.out.println("❌ Doctor not found. Appointment booking cancelled.");
            }
        }
    }

    private static void adminLogin() {
        System.out.print("Enter Admin Username: ");
        String username = scanner.nextLine();
        System.out.print("Enter Admin Password: ");
        String password = scanner.nextLine();

        if (username.equals("admin") && password.equals("admin123")) {
            adminMenu();
        } else {
            System.out.println("Invalid credentials!");
        }
    }

    private static void doctorLogin() {
        System.out.print("Enter Doctor ID (e.g., D001): ");
        String doctorId = scanner.nextLine();
        Doctor doctor = service.findDoctorById(doctorId);

        if (doctor != null) {
            doctorMenu(doctor);
        } else {
            System.out.println("Doctor ID not found!");
        }
    }

    private static void adminMenu() {
        while (true) {
            System.out.println("\n--- ADMIN DASHBOARD ---");
            System.out.println("1. Add New Doctor");
            System.out.println("2. View All Doctors");
            System.out.println("3. Add New Patient");
            System.out.println("4. View All Patients");
            System.out.println("5. Search Patient");
            System.out.println("6. Logout");
            System.out.print("Select an option: ");

            String choice = scanner.nextLine();
            switch (choice) {
                case "1": addDoctor(); break;
                case "2": viewAllDoctors(); break;
                case "3": addPatient(); break;
                case "4": viewAllPatients(); break;
                case "5": searchPatient(); break;
                case "6": return;
                default: System.out.println("Invalid choice!");
            }
        }
    }

    private static void doctorMenu(Doctor doctor) {
        while (true) {
            System.out.println("\n--- DOCTOR DASHBOARD (Welcome " + doctor.getName() + ") ---");
            System.out.println("1. Book Appointment");
            System.out.println("2. View Scheduled Appointments");
            System.out.println("3. Add Prescription");
            System.out.println("4. View Patient Medical Records");
            System.out.println("5. Logout");
            System.out.print("Select an option: ");

            String choice = scanner.nextLine();
            switch (choice) {
                case "1": bookAppointment(doctor); break;
                case "2": viewAppointments(); break;
                case "3": addPrescription(doctor); break;
                case "4": viewPatientRecords(); break;
                case "5": return;
                default: System.out.println("Invalid choice!");
            }
        }
    }

    // Admin Functions
    private static void addDoctor() {
        System.out.print("Enter Doctor ID: ");
        String id = scanner.nextLine();
        System.out.print("Enter Name: ");
        String name = scanner.nextLine();
        System.out.print("Enter Specialization: ");
        String spec = scanner.nextLine();
        service.addDoctor(new Doctor(id, name, spec));
        System.out.println("Doctor added successfully!");
    }

    private static void viewAllDoctors() {
        List<Doctor> doctors = service.getAllDoctors();
        System.out.println("\n-------------------------------------------------------------");
        System.out.println("| ID         | Name                 | Specialization       |");
        System.out.println("-------------------------------------------------------------");
        for (Doctor d : doctors) System.out.println(d);
        System.out.println("-------------------------------------------------------------");
    }

    private static void addPatient() {
        System.out.print("Enter Patient ID: ");
        String id = scanner.nextLine();
        System.out.print("Enter Name: ");
        String name = scanner.nextLine();
        System.out.print("Enter Age: ");
        int age = Integer.parseInt(scanner.nextLine());
        System.out.print("Enter Disease/Reason: ");
        String disease = scanner.nextLine();
        service.addPatient(new Patient(id, name, age, disease));
        System.out.println("Patient record created!");
    }

    private static void viewAllPatients() {
        List<Patient> patients = service.getAllPatients();
        System.out.println("\n-------------------------------------------------------------------------");
        System.out.println("| ID         | Name                 | Age   | Disease              |");
        System.out.println("-------------------------------------------------------------------------");
        for (Patient p : patients) System.out.println(p);
        System.out.println("-------------------------------------------------------------------------");
    }

    private static void searchPatient() {
        System.out.print("Enter Name or ID to search: ");
        String query = scanner.nextLine();
        Patient p = service.findPatientById(query);
        if (p != null) {
            System.out.println("Patient Found: " + p);
        } else {
            List<Patient> results = service.searchPatientByName(query);
            if (results.isEmpty()) {
                System.out.println("No patient found matching query.");
            } else {
                for (Patient rp : results) System.out.println(rp);
            }
        }
    }

    // Doctor Functions
    private static void bookAppointment(Doctor doctor) {
        System.out.print("Enter Patient ID: ");
        String pId = scanner.nextLine();
        Patient p = service.findPatientById(pId);
        if (p == null) {
            System.out.println("Patient not found!");
            return;
        }
        System.out.print("Enter Date (DD/MM/YYYY): ");
        String date = scanner.nextLine();
        System.out.print("Enter Time (HH:MM): ");
        String time = scanner.nextLine();
        service.scheduleAppointment(new Appointment(pId, doctor.getId(), date, time));
        System.out.println("Appointment booked for " + p.getName());
    }

    private static void viewAppointments() {
        List<Appointment> apps = service.getAllAppointments();
        System.out.println("\n------------------------------------------------------");
        System.out.println("| Patient ID | Doctor ID  | Date         | Time     |");
        System.out.println("------------------------------------------------------");
        for (Appointment a : apps) System.out.println(a);
        System.out.println("------------------------------------------------------");
    }

    private static void addPrescription(Doctor doctor) {
        System.out.print("Enter Patient ID: ");
        String pId = scanner.nextLine();
        Patient p = service.findPatientById(pId);
        if (p == null) {
            System.out.println("Patient not found!");
            return;
        }
        System.out.print("Enter Medicine: ");
        String med = scanner.nextLine();
        System.out.print("Enter Dosage: ");
        String dosage = scanner.nextLine();
        service.addPrescription(new Prescription(pId, doctor.getId(), med, dosage));
        System.out.println("Prescription added successfully!");
    }

    private static void viewPatientRecords() {
        System.out.print("Enter Patient ID: ");
        String pId = scanner.nextLine();
        Patient p = service.findPatientById(pId);
        if (p == null) {
            System.out.println("Patient not found!");
            return;
        }
        System.out.println("\n--- Patient Details ---");
        System.out.println(p);
        System.out.println("\n--- Prescriptions ---");
        List<Prescription> pres = service.getPrescriptionsByPatientId(pId);
        if (pres.isEmpty()) {
            System.out.println("No prescriptions found.");
        } else {
            for (Prescription pr : pres) System.out.println(pr);
        }
    }
}
