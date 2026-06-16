package lk.ijse.sams.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "subjects")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Subject {

    @Id
    @Column(name = "subject_id", length = 10)
    private String subjectId;

    @Column(name = "subject_name", nullable = false, length = 150)
    private String subjectName;

    @Column(name = "subject_code", nullable = false, unique = true, length = 20)
    private String subjectCode;

    @Column(name = "credits")
    private int credits;

    @Column(name = "description", length = 500)
    private String description;

    // Subject belongs to a course
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    // A subject can be taught by many lecturers (and lecturer teaches many subjects)
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinTable(
            name = "subject_lecturers",
            joinColumns = @JoinColumn(name = "subject_id"),
            inverseJoinColumns = @JoinColumn(name = "lecturer_id")
    )
    private List<Lecturer> lecturers = new ArrayList<>();

    // A subject can have many class sessions
    @OneToMany(mappedBy = "subject", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ClassSession> classSessions = new ArrayList<>();
}
