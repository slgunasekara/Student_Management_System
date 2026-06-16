package lk.ijse.sams.entity;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Date;

@Entity
@Table(name = "attendance")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Attendance {

    @Id
    @Column(name = "attendance_id", length = 10)
    private String attendanceId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AttendanceStatus status;

    @Column(name = "marked_date")
    private Date markedDate;

    @Column(name = "remarks", length = 300)
    private String remarks;

    // Attendance is for one student
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    // Attendance is for one class session
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private ClassSession classSession;

    public enum AttendanceStatus {
        PRESENT, ABSENT, LATE, EXCUSED
    }
}
