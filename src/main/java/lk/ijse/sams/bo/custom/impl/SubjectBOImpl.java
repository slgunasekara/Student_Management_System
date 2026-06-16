package lk.ijse.sams.bo.custom.impl;

import lk.ijse.sams.bo.custom.SubjectBO;
import lk.ijse.sams.bo.exception.DuplicateException;
import lk.ijse.sams.bo.exception.NotFoundException;
import lk.ijse.sams.dao.DAOFactory;
import lk.ijse.sams.dao.DAOTypes;
import lk.ijse.sams.dao.custom.CourseDAO;
import lk.ijse.sams.dao.custom.SubjectDAO;
import lk.ijse.sams.dto.SubjectDTO;
import lk.ijse.sams.entity.Course;
import lk.ijse.sams.entity.Subject;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class SubjectBOImpl implements SubjectBO {

    private final SubjectDAO subjectDAO = DAOFactory.getInstance().getDAO(DAOTypes.SUBJECT);
    private final CourseDAO courseDAO   = DAOFactory.getInstance().getDAO(DAOTypes.COURSE);

    @Override
    public void saveSubject(SubjectDTO dto) {
        if (subjectDAO.existsBySubjectCode(dto.getSubjectCode())) {
            throw new DuplicateException("Subject code '" + dto.getSubjectCode() + "' already exists!");
        }
        subjectDAO.save(toEntity(dto));
    }

    @Override
    public void updateSubject(SubjectDTO dto) {
        if (!subjectDAO.update(toEntity(dto))) throw new NotFoundException("Subject not found!");
    }

    @Override
    public void deleteSubject(String id) {
        if (!subjectDAO.delete(id)) throw new NotFoundException("Subject not found!");
    }

    @Override
    public List<SubjectDTO> getAllSubjects() {
        return subjectDAO.getAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<SubjectDTO> getSubjectsByCourse(String courseId) {
        return subjectDAO.findByCourseId(courseId).stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public Optional<SubjectDTO> findById(String id) {
        return subjectDAO.findById(id).map(this::toDTO);
    }

    @Override
    public String generateNextId() {
        String last = subjectDAO.getLastId();
        if (last == null) return "SUB001";
        int num = Integer.parseInt(last.substring(3)) + 1;
        return String.format("SUB%03d", num);
    }

    private Subject toEntity(SubjectDTO dto) {
        Subject s = new Subject();
        s.setSubjectId(dto.getSubjectId());
        s.setSubjectName(dto.getSubjectName());
        s.setSubjectCode(dto.getSubjectCode());
        s.setCredits(dto.getCredits());
        s.setDescription(dto.getDescription());
        if (dto.getCourseId() != null) {
            courseDAO.findById(dto.getCourseId()).ifPresent(s::setCourse);
        }
        return s;
    }

    private SubjectDTO toDTO(Subject s) {
        SubjectDTO dto = new SubjectDTO();
        dto.setSubjectId(s.getSubjectId());
        dto.setSubjectName(s.getSubjectName());
        dto.setSubjectCode(s.getSubjectCode());
        dto.setCredits(s.getCredits());
        dto.setDescription(s.getDescription());
        if (s.getCourse() != null) {
            dto.setCourseId(s.getCourse().getCourseId());
            dto.setCourseName(s.getCourse().getCourseName());
        }
        return dto;
    }
}
