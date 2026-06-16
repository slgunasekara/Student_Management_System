package lk.ijse.sams.bo.custom.impl;

import lk.ijse.sams.bo.custom.StudentBO;
import lk.ijse.sams.bo.exception.DuplicateException;
import lk.ijse.sams.bo.exception.NotFoundException;
import lk.ijse.sams.bo.exception.ValidationException;
import lk.ijse.sams.dao.DAOFactory;
import lk.ijse.sams.dao.DAOTypes;
import lk.ijse.sams.dao.custom.CourseDAO;
import lk.ijse.sams.dao.custom.StudentDAO;
import lk.ijse.sams.dto.StudentDTO;
import lk.ijse.sams.entity.Student;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class StudentBOImpl implements StudentBO {

    private final StudentDAO studentDAO = DAOFactory.getInstance().getDAO(DAOTypes.STUDENT);
    private final CourseDAO  courseDAO  = DAOFactory.getInstance().getDAO(DAOTypes.COURSE);

    @Override
    public void saveStudent(StudentDTO dto) {
        if (dto.getName() == null || dto.getName().isBlank())
            throw new ValidationException("Student name is required!");
        if (dto.getPhone() == null || !dto.getPhone().matches("0\\d{9}"))
            throw new ValidationException("Invalid phone! Use format: 0771234567");
        if (dto.getEmail() != null && !dto.getEmail().isBlank()
                && !dto.getEmail().matches("^[\\w._%+\\-]+@[\\w.\\-]+\\.[a-zA-Z]{2,}$"))
            throw new ValidationException("Invalid email format!");

        if (studentDAO.existsByRegNumber(dto.getRegNumber()))
            throw new DuplicateException("Registration number '" + dto.getRegNumber() + "' already exists!");
        if (dto.getEmail() != null && !dto.getEmail().isBlank() && studentDAO.existsByEmail(dto.getEmail()))
            throw new DuplicateException("Email '" + dto.getEmail() + "' already registered!");

        Student student = toEntity(dto);
        student.setEnrollmentDate(Date.valueOf(LocalDate.now()));
        studentDAO.save(student);
    }

    @Override
    public void updateStudent(StudentDTO dto) {
        if (!studentDAO.update(toEntity(dto))) throw new NotFoundException("Student not found!");
    }

    @Override
    public void deleteStudent(String id) {
        if (!studentDAO.delete(id)) throw new NotFoundException("Student not found!");
    }

    @Override
    public List<StudentDTO> getAllStudents() {
        return studentDAO.getAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public Optional<StudentDTO> findById(String id) {
        return studentDAO.findById(id).map(this::toDTO);
    }

    @Override
    public String generateNextId() {
        String last = studentDAO.getLastId();
        if (last == null) return "STU001";
        int num = Integer.parseInt(last.substring(3)) + 1;
        return String.format("STU%03d", num);
    }

    @Override
    public String generateNextRegNumber() {
        int year = LocalDate.now().getYear();
        String lastId = studentDAO.getLastId(); // e.g. "STU003"
        if (lastId == null) return year + "/STU/001";
        try {
            int num = Integer.parseInt(lastId.replaceAll("[^0-9]", "")) + 1;
            return year + "/STU/" + String.format("%03d", num);
        } catch (NumberFormatException e) {
            return year + "/STU/001";
        }
    }

    private Student toEntity(StudentDTO dto) {
        Student s = new Student();
        s.setStudentId(dto.getStudentId());
        s.setRegNumber(dto.getRegNumber());
        s.setName(dto.getName());
        s.setEmail(dto.getEmail());
        s.setPhone(dto.getPhone());
        s.setDateOfBirth(dto.getDateOfBirth());
        s.setAddress(dto.getAddress());
        s.setEnrollmentDate(dto.getEnrollmentDate() != null ? dto.getEnrollmentDate() : Date.valueOf(LocalDate.now()));
        if (dto.getCourseId() != null) {
            courseDAO.findById(dto.getCourseId()).ifPresent(s::setCourse);
        }
        return s;
    }

    private StudentDTO toDTO(Student s) {
        StudentDTO dto = new StudentDTO();
        dto.setStudentId(s.getStudentId());
        dto.setRegNumber(s.getRegNumber());
        dto.setName(s.getName());
        dto.setEmail(s.getEmail());
        dto.setPhone(s.getPhone());
        dto.setDateOfBirth(s.getDateOfBirth());
        dto.setAddress(s.getAddress());
        dto.setEnrollmentDate(s.getEnrollmentDate());
        if (s.getCourse() != null) {
            dto.setCourseId(s.getCourse().getCourseId());
            dto.setCourseName(s.getCourse().getCourseName());
        }
        return dto;
    }
}
