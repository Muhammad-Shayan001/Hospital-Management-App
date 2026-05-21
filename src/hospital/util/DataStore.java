package hospital.util;

import java.io.*;
import java.util.*;
import hospital.model.*;

public class DataStore {

    private List<Patient> patients = new ArrayList<>();
    private List<Doctor> doctors = new ArrayList<>();
    private List<Appointment> appointments = new ArrayList<>();
    private List<Prescription> prescriptions = new ArrayList<>();

    private String lastTokenDate = "";
    private int lastTokenNumber = 0;

    private static final String FILE_NAME = "hospital_data.json";

    // ==================== LOAD ====================
    public static DataStore load() {
        DataStore ds = new DataStore();
        File file = new File(FILE_NAME);

        if (!file.exists()) {
            ds.doctors.add(new Doctor("D001", "Dr. Ali", "ali@hospital.com", "pass123", "Cardiology", true));
            return ds;
        }

        try {
            String json = readFile(file);
            json = json.trim();
            if (json.isEmpty() || json.equals("{}")) {
                ds.doctors.add(new Doctor("D001", "Dr. Ali", "ali@hospital.com", "pass123", "Cardiology", true));
                return ds;
            }

            // Parse meta
            ds.lastTokenDate = getJsonString(json, "lastTokenDate");
            ds.lastTokenNumber = getJsonInt(json, "lastTokenNumber");

            // Parse doctors
            String doctorsArr = getJsonArray(json, "doctors");
            if (doctorsArr != null) {
                for (String obj : splitJsonObjects(doctorsArr)) {
                    ds.doctors.add(new Doctor(
                        getJsonString(obj, "id"),
                        getJsonString(obj, "name"),
                        getJsonString(obj, "email"),
                        getJsonString(obj, "password"),
                        getJsonString(obj, "specialization"),
                        getJsonBool(obj, "isApproved")
                    ));
                }
            }

            // Parse patients
            String patientsArr = getJsonArray(json, "patients");
            if (patientsArr != null) {
                for (String obj : splitJsonObjects(patientsArr)) {
                    ds.patients.add(new Patient(
                        getJsonString(obj, "id"),
                        getJsonString(obj, "name"),
                        getJsonString(obj, "email"),
                        getJsonString(obj, "password"),
                        getJsonInt(obj, "age"),
                        getJsonString(obj, "disease")
                    ));
                }
            }

            // Parse appointments
            String appointmentsArr = getJsonArray(json, "appointments");
            if (appointmentsArr != null) {
                for (String obj : splitJsonObjects(appointmentsArr)) {
                    Appointment a = new Appointment(
                        getJsonString(obj, "id"),
                        getJsonString(obj, "patientId"),
                        getJsonString(obj, "doctorId"),
                        getJsonString(obj, "date"),
                        getJsonString(obj, "time")
                    );
                    a.setStatus(getJsonString(obj, "status"));
                    a.setTokenNumber(getJsonInt(obj, "tokenNumber"));
                    ds.appointments.add(a);
                }
            }

            // Parse prescriptions
            String prescriptionsArr = getJsonArray(json, "prescriptions");
            if (prescriptionsArr != null) {
                for (String obj : splitJsonObjects(prescriptionsArr)) {
                    ds.prescriptions.add(new Prescription(
                        getJsonString(obj, "id"),
                        getJsonString(obj, "patientId"),
                        getJsonString(obj, "doctorId"),
                        getJsonString(obj, "diagnosis"),
                        getJsonString(obj, "medicine"),
                        getJsonString(obj, "dosage"),
                        getJsonString(obj, "instructions")
                    ));
                }
            }

        } catch (Exception e) {
            System.out.println("Error loading JSON data: " + e.getMessage());
            ds.doctors.add(new Doctor("D001", "Dr. Ali", "ali@hospital.com", "pass123", "Cardiology", true));
        }
        return ds;
    }

    // ==================== SAVE ====================
    public void save() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_NAME))) {
            StringBuilder sb = new StringBuilder();
            sb.append("{\n");

            // Meta
            sb.append("  \"lastTokenDate\": \"").append(esc(lastTokenDate)).append("\",\n");
            sb.append("  \"lastTokenNumber\": ").append(lastTokenNumber).append(",\n");

            // Doctors
            sb.append("  \"doctors\": [\n");
            for (int i = 0; i < doctors.size(); i++) {
                Doctor d = doctors.get(i);
                sb.append("    {\n");
                sb.append("      \"id\": \"").append(esc(d.getId())).append("\",\n");
                sb.append("      \"name\": \"").append(esc(d.getName())).append("\",\n");
                sb.append("      \"email\": \"").append(esc(d.getEmail())).append("\",\n");
                sb.append("      \"password\": \"").append(esc(d.getPassword())).append("\",\n");
                sb.append("      \"specialization\": \"").append(esc(d.getSpecialization())).append("\",\n");
                sb.append("      \"isApproved\": ").append(d.isApproved()).append("\n");
                sb.append("    }");
                if (i < doctors.size() - 1) sb.append(",");
                sb.append("\n");
            }
            sb.append("  ],\n");

            // Patients
            sb.append("  \"patients\": [\n");
            for (int i = 0; i < patients.size(); i++) {
                Patient p = patients.get(i);
                sb.append("    {\n");
                sb.append("      \"id\": \"").append(esc(p.getId())).append("\",\n");
                sb.append("      \"name\": \"").append(esc(p.getName())).append("\",\n");
                sb.append("      \"email\": \"").append(esc(p.getEmail())).append("\",\n");
                sb.append("      \"password\": \"").append(esc(p.getPassword())).append("\",\n");
                sb.append("      \"age\": ").append(p.getAge()).append(",\n");
                sb.append("      \"disease\": \"").append(esc(p.getDisease())).append("\"\n");
                sb.append("    }");
                if (i < patients.size() - 1) sb.append(",");
                sb.append("\n");
            }
            sb.append("  ],\n");

            // Appointments
            sb.append("  \"appointments\": [\n");
            for (int i = 0; i < appointments.size(); i++) {
                Appointment a = appointments.get(i);
                sb.append("    {\n");
                sb.append("      \"id\": \"").append(esc(a.getId())).append("\",\n");
                sb.append("      \"patientId\": \"").append(esc(a.getPatientId())).append("\",\n");
                sb.append("      \"doctorId\": \"").append(esc(a.getDoctorId())).append("\",\n");
                sb.append("      \"date\": \"").append(esc(a.getDate())).append("\",\n");
                sb.append("      \"time\": \"").append(esc(a.getTime())).append("\",\n");
                sb.append("      \"status\": \"").append(esc(a.getStatus())).append("\",\n");
                sb.append("      \"tokenNumber\": ").append(a.getTokenNumber()).append("\n");
                sb.append("    }");
                if (i < appointments.size() - 1) sb.append(",");
                sb.append("\n");
            }
            sb.append("  ],\n");

            // Prescriptions
            sb.append("  \"prescriptions\": [\n");
            for (int i = 0; i < prescriptions.size(); i++) {
                Prescription p = prescriptions.get(i);
                sb.append("    {\n");
                sb.append("      \"id\": \"").append(esc(p.getId())).append("\",\n");
                sb.append("      \"patientId\": \"").append(esc(p.getPatientId())).append("\",\n");
                sb.append("      \"doctorId\": \"").append(esc(p.getDoctorId())).append("\",\n");
                sb.append("      \"diagnosis\": \"").append(esc(p.getDiagnosis())).append("\",\n");
                sb.append("      \"medicine\": \"").append(esc(p.getMedicine())).append("\",\n");
                sb.append("      \"dosage\": \"").append(esc(p.getDosage())).append("\",\n");
                sb.append("      \"instructions\": \"").append(esc(p.getInstructions())).append("\"\n");
                sb.append("    }");
                if (i < prescriptions.size() - 1) sb.append(",");
                sb.append("\n");
            }
            sb.append("  ]\n");

            sb.append("}");
            pw.print(sb.toString());
        } catch (Exception e) {
            System.out.println("Error saving JSON data: " + e.getMessage());
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

    // ==================== GETTERS ====================
    public List<Patient> getPatients() { return patients; }
    public List<Doctor> getDoctors() { return doctors; }
    public List<Appointment> getAppointments() { return appointments; }
    public List<Prescription> getPrescriptions() { return prescriptions; }
    public String getLastTokenDate() { return lastTokenDate; }
    public void setLastTokenDate(String d) { this.lastTokenDate = d; }
    public int getLastTokenNumber() { return lastTokenNumber; }
    public void setLastTokenNumber(int n) { this.lastTokenNumber = n; }

    // ==================== JSON HELPERS ====================
    private static String esc(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t");
    }

    private static String readFile(File f) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) sb.append(line).append("\n");
        }
        return sb.toString();
    }

    private static String getJsonString(String json, String key) {
        String search = "\"" + key + "\"";
        int idx = json.indexOf(search);
        if (idx == -1) return "";
        int colon = json.indexOf(":", idx + search.length());
        if (colon == -1) return "";
        int start = json.indexOf("\"", colon + 1);
        if (start == -1) return "";
        start++;
        int end = start;
        while (end < json.length()) {
            if (json.charAt(end) == '"' && json.charAt(end - 1) != '\\') break;
            end++;
        }
        String val = json.substring(start, end);
        return val.replace("\\\"", "\"").replace("\\n", "\n").replace("\\t", "\t").replace("\\\\", "\\");
    }

    private static int getJsonInt(String json, String key) {
        String search = "\"" + key + "\"";
        int idx = json.indexOf(search);
        if (idx == -1) return 0;
        int colon = json.indexOf(":", idx + search.length());
        if (colon == -1) return 0;
        int start = colon + 1;
        while (start < json.length() && (json.charAt(start) == ' ' || json.charAt(start) == '\n')) start++;
        int end = start;
        while (end < json.length() && (Character.isDigit(json.charAt(end)) || json.charAt(end) == '-')) end++;
        try { return Integer.parseInt(json.substring(start, end)); } catch (Exception e) { return 0; }
    }

    private static boolean getJsonBool(String json, String key) {
        String search = "\"" + key + "\"";
        int idx = json.indexOf(search);
        if (idx == -1) return false;
        int colon = json.indexOf(":", idx + search.length());
        if (colon == -1) return false;
        String after = json.substring(colon + 1).trim();
        return after.startsWith("true");
    }

    private static String getJsonArray(String json, String key) {
        String search = "\"" + key + "\"";
        int idx = json.indexOf(search);
        if (idx == -1) return null;
        int bracketStart = json.indexOf("[", idx);
        if (bracketStart == -1) return null;
        int depth = 0;
        int end = bracketStart;
        for (int i = bracketStart; i < json.length(); i++) {
            if (json.charAt(i) == '[') depth++;
            else if (json.charAt(i) == ']') { depth--; if (depth == 0) { end = i; break; } }
        }
        return json.substring(bracketStart + 1, end);
    }

    private static List<String> splitJsonObjects(String arrayContent) {
        List<String> objects = new ArrayList<>();
        int depth = 0;
        int start = -1;
        for (int i = 0; i < arrayContent.length(); i++) {
            char c = arrayContent.charAt(i);
            if (c == '{') { if (depth == 0) start = i; depth++; }
            else if (c == '}') { depth--; if (depth == 0 && start != -1) { objects.add(arrayContent.substring(start, i + 1)); start = -1; } }
        }
        return objects;
    }
}
