package hospital.model;

public class Appointment {
    private String patientId;
    private String doctorId;
    private String date;
    private String time;

    public Appointment(String patientId, String doctorId, String date, String time) {
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.date = date;
        this.time = time;
    }

    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }

    public String getDoctorId() { return doctorId; }
    public void setDoctorId(String doctorId) { this.doctorId = doctorId; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    @Override
    public String toString() {
        return String.format("| %-10s | %-10s | %-12s | %-8s |", patientId, doctorId, date, time);
    }
}
