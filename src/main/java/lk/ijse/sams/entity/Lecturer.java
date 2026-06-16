package lk.ijse.sams.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "lecturers")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Lecturer {

    @Id
    @Column(name = "lecturer_id", length = 10)
    private String lecturerId;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "phone", nullable = false, length = 15)
    private String phone;

    @Column(name = "qualification", length = 200)
    private String qualification;

    @Column(name = "department", length = 100)
    private String department;

    @Column(name = "is_active")
    private boolean active = true;

    // Inverse side of ManyToMany with Subject
    @ManyToMany(mappedBy = "lecturers", fetch = FetchType.LAZY)
    private List<Subject> subjects = new ArrayList<>();

    // One lecturer takes many class sessions
    @OneToMany(mappedBy = "lecturer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ClassSession> classSessions = new ArrayList<>();
}
