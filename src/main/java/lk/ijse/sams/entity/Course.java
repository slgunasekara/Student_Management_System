package lk.ijse.sams.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "courses")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Course {

    @Id
    @Column(name = "course_id", length = 10)
    private String courseId;

    @Column(name = "course_name", nullable = false, length = 150)
    private String courseName;

    @Column(name = "course_code", nullable = false, unique = true, length = 20)
    private String courseCode;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "duration_months")
    private int durationMonths;

    // One course has many subjects
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Subject> subjects = new ArrayList<>();

    // One course has many students enrolled
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Student> students = new ArrayList<>();
}
