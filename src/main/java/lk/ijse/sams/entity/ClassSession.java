package lk.ijse.sams.entity;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "class_sessions")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ClassSession {

    @Id
    @Column(name = "session_id", length = 10)
    private String sessionId;

    @Column(name = "session_date", nullable = false)
    private Date sessionDate;

    @Column(name = "start_time")
    private Time startTime;

    @Column(name = "end_time")
    private Time endTime;

    @Column(name = "venue", length = 100)
    private String venue;

    @Column(name = "notes", length = 500)
    private String notes;

    // Class session belongs to one subject
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    // Class session is conducted by one lecturer
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecturer_id", nullable = false)
    private Lecturer lecturer;

    // One class session has many attendance records
    @OneToMany(mappedBy = "classSession", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Attendance> attendances = new ArrayList<>();
}
