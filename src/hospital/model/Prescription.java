package hospital.model;

public class Prescription {
    private String patientId;
    private String doctorId;
    private String medicine;
    private String dosage;

    public Prescription(String patientId, String doctorId, String medicine, String dosage) {
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.medicine = medicine;
        this.dosage = dosage;
    }

    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }

    public String getDoctorId() { return doctorId; }
    public void setDoctorId(String doctorId) { this.doctorId = doctorId; }

    public String getMedicine() { return medicine; }
    public void setMedicine(String medicine) { this.medicine = medicine; }

    public String getDosage() { return dosage; }
    public void setDosage(String dosage) { this.dosage = dosage; }

    @Override
    public String toString() {
        return String.format("| %-10s | %-10s | %-20s | %-15s |", patientId, doctorId, medicine, dosage);
    }
}
