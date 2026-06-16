package lk.ijse.sams.dto.tm;
import javafx.beans.property.*;
import lk.ijse.sams.entity.Attendance;
public class StudentTM {
    private final SimpleStringProperty studentId, regNumber, name, email, phone, courseName;
    public StudentTM(String studentId, String regNumber, String name, String email, String phone, String courseName) {
        this.studentId = new SimpleStringProperty(studentId);
        this.regNumber = new SimpleStringProperty(regNumber);
        this.name = new SimpleStringProperty(name);
        this.email = new SimpleStringProperty(email);
        this.phone = new SimpleStringProperty(phone);
        this.courseName = new SimpleStringProperty(courseName);
    }
    public String getStudentId() { return studentId.get(); }
    public String getRegNumber() { return regNumber.get(); }
    public String getName() { return name.get(); }
    public String getEmail() { return email.get(); }
    public String getPhone() { return phone.get(); }
    public String getCourseName() { return courseName.get(); }
    public SimpleStringProperty studentIdProperty() { return studentId; }
    public SimpleStringProperty regNumberProperty() { return regNumber; }
    public SimpleStringProperty nameProperty() { return name; }
    public SimpleStringProperty emailProperty() { return email; }
    public SimpleStringProperty phoneProperty() { return phone; }
    public SimpleStringProperty courseNameProperty() { return courseName; }
}
