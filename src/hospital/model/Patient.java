package hospital.model;

public class Patient {
    private String id;
    private String name;
    private int age;
    private String disease;

    public Patient(String id, String name, int age, String disease) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.disease = disease;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public String getDisease() { return disease; }
    public void setDisease(String disease) { this.disease = disease; }

    @Override
    public String toString() {
        return String.format("| %-10s | %-20s | %-5d | %-20s |", id, name, age, disease);
    }
}
