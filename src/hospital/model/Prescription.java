package hospital.model;

import java.io.Serializable;

public class Prescription implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private String patientId;
    private String doctorId;
    private String diagnosis;
    private String medicine;
    private String dosage;
    private String instructions;

    public Prescription(String id, String patientId, String doctorId, String diagnosis, String medicine, String dosage, String instructions) {
        this.id = id;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.diagnosis = diagnosis;
        this.medicine = medicine;
        this.dosage = dosage;
        this.instructions = instructions;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }
    public String getDoctorId() { return doctorId; }
    public void setDoctorId(String doctorId) { this.doctorId = doctorId; }
    public String getDiagnosis() { return diagnosis; }
    public void setDiagnosis(String diagnosis) { this.diagnosis = diagnosis; }
    public String getMedicine() { return medicine; }
    public void setMedicine(String medicine) { this.medicine = medicine; }
    public String getDosage() { return dosage; }
    public void setDosage(String dosage) { this.dosage = dosage; }
    public String getInstructions() { return instructions; }
    public void setInstructions(String instructions) { this.instructions = instructions; }

    @Override
    public String toString() {
        return String.format("Prescription ID: %s | Patient: %s | Doctor: %s\nDiagnosis: %s\nMedicine: %s | Dosage: %s\nInstructions: %s\n---------------------------------------------------------", 
            id, patientId, doctorId, diagnosis, medicine, dosage, instructions);
    }
}
