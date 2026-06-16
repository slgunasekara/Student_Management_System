package lk.ijse.sams.dto.tm;
import javafx.beans.property.*;
public class AttendanceTM {
    private final SimpleStringProperty attendanceId, studentId, studentName, status, remarks;
    public AttendanceTM(String attendanceId, String studentId, String studentName, String status, String remarks) {
        this.attendanceId = new SimpleStringProperty(attendanceId);
        this.studentId = new SimpleStringProperty(studentId);
        this.studentName = new SimpleStringProperty(studentName);
        this.status = new SimpleStringProperty(status);
        this.remarks = new SimpleStringProperty(remarks);
    }
    public String getAttendanceId() { return attendanceId.get(); }
    public String getStudentId() { return studentId.get(); }
    public String getStudentName() { return studentName.get(); }
    public String getStatus() { return status.get(); }
    public String getRemarks() { return remarks.get(); }
    public SimpleStringProperty attendanceIdProperty() { return attendanceId; }
    public SimpleStringProperty studentIdProperty() { return studentId; }
    public SimpleStringProperty studentNameProperty() { return studentName; }
    public SimpleStringProperty statusProperty() { return status; }
    public SimpleStringProperty remarksProperty() { return remarks; }
}
