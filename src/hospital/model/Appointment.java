package hospital.model;

import java.io.Serializable;

public class Appointment implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private String patientId;
    private String doctorId;
    private String date;
    private String time;
    private String status; // PENDING, APPROVED, REJECTED
    private int tokenNumber;

    public Appointment(String id, String patientId, String doctorId, String date, String time) {
        this.id = id;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.date = date;
        this.time = time;
        this.status = "PENDING";
        this.tokenNumber = 0;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }
    public String getDoctorId() { return doctorId; }
    public void setDoctorId(String doctorId) { this.doctorId = doctorId; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public int getTokenNumber() { return tokenNumber; }
    public void setTokenNumber(int tokenNumber) { this.tokenNumber = tokenNumber; }

    @Override
    public String toString() {
        return String.format("| %-8s | %-10s | %-10s | %-12s | %-8s | %-10s | Token: %d |", id, patientId, doctorId, date, time, status, tokenNumber);
    }
}
