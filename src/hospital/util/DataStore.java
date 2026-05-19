package hospital.util;

import java.io.*;
import java.util.*;
import hospital.model.*;

public class DataStore implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private List<Patient> patients = new ArrayList<>();
    private List<Doctor> doctors = new ArrayList<>();
    private List<Appointment> appointments = new ArrayList<>();
    private List<Prescription> prescriptions = new ArrayList<>();
    
    private String lastTokenDate = "";
    private int lastTokenNumber = 0;

    public static DataStore load() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("hospital_data.dat"))) {
            return (DataStore) ois.readObject();
        } catch (Exception e) {
            DataStore ds = new DataStore();
            ds.doctors.add(new Doctor("D001", "Dr. Ali", "ali@hospital.com", "pass123", "Cardiology", true));
            return ds;
        }
    }

    public void save() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("hospital_data.dat"))) {
            oos.writeObject(this);
        } catch (Exception e) {
            System.out.println("Error saving data: " + e.getMessage());
        }
    }
    
    public void exportPrescription(Prescription p) {
        File dir = new File("uploads/prescriptions");
        if (!dir.exists()) dir.mkdirs();
        try (PrintWriter writer = new PrintWriter(new FileWriter("uploads/prescriptions/" + p.getId() + ".txt"))) {
            writer.println(p.toString());
        } catch (IOException e) {
            System.out.println("Could not export prescription: " + e.getMessage());
        }
    }

    public List<Patient> getPatients() { return patients; }
    public List<Doctor> getDoctors() { return doctors; }
    public List<Appointment> getAppointments() { return appointments; }
    public List<Prescription> getPrescriptions() { return prescriptions; }

    public String getLastTokenDate() { return lastTokenDate; }
    public void setLastTokenDate(String lastTokenDate) { this.lastTokenDate = lastTokenDate; }
    public int getLastTokenNumber() { return lastTokenNumber; }
    public void setLastTokenNumber(int lastTokenNumber) { this.lastTokenNumber = lastTokenNumber; }
}
