package lk.ijse.sams.entity;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "students")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Student {

    @Id
    @Column(name = "student_id", length = 10)
    private String studentId; // STU001, STU002 etc.

    @Column(name = "reg_number", nullable = false, unique = true, length = 20)
    private String regNumber;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "email", unique = true, length = 100)
    private String email;

    @Column(name = "phone", nullable = false, length = 15)
    private String phone;

    @Column(name = "date_of_birth")
    private Date dateOfBirth;

    @Column(name = "address", length = 255)
    private String address;

    @Column(name = "enrollment_date", nullable = false)
    private Date enrollmentDate;

    // Student belongs to one course
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    // One student has many attendance records
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Attendance> attendances = new ArrayList<>();
}
