package lk.ijse.sams.bo.custom.impl;

import lk.ijse.sams.bo.custom.CourseBO;
import lk.ijse.sams.bo.exception.DuplicateException;
import lk.ijse.sams.bo.exception.NotFoundException;
import lk.ijse.sams.dao.DAOFactory;
import lk.ijse.sams.dao.DAOTypes;
import lk.ijse.sams.dao.custom.CourseDAO;
import lk.ijse.sams.dto.CourseDTO;
import lk.ijse.sams.entity.Course;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CourseBOImpl implements CourseBO {

    private final CourseDAO courseDAO = DAOFactory.getInstance().getDAO(DAOTypes.COURSE);

    @Override
    public void saveCourse(CourseDTO dto) {
        if (courseDAO.existsByCourseCode(dto.getCourseCode())) {
            throw new DuplicateException("Course code '" + dto.getCourseCode() + "' already exists!");
        }
        courseDAO.save(toEntity(dto));
    }

    @Override
    public void updateCourse(CourseDTO dto) {
        if (!courseDAO.update(toEntity(dto))) throw new NotFoundException("Course not found!");
    }

    @Override
    public void deleteCourse(String id) {
        if (!courseDAO.delete(id)) throw new NotFoundException("Course not found!");
    }

    @Override
    public List<CourseDTO> getAllCourses() {
        return courseDAO.getAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public Optional<CourseDTO> findById(String id) {
        return courseDAO.findById(id).map(this::toDTO);
    }

    @Override
    public String generateNextId() {
        String last = courseDAO.getLastId();
        if (last == null) return "C001";
        int num = Integer.parseInt(last.substring(1)) + 1;
        return String.format("C%03d", num);
    }

    private Course toEntity(CourseDTO dto) {
        Course c = new Course();
        c.setCourseId(dto.getCourseId());
        c.setCourseName(dto.getCourseName());
        c.setCourseCode(dto.getCourseCode());
        c.setDescription(dto.getDescription());
        c.setDurationMonths(dto.getDurationMonths());
        return c;
    }

    private CourseDTO toDTO(Course c) {
        CourseDTO dto = new CourseDTO();
        dto.setCourseId(c.getCourseId());
        dto.setCourseName(c.getCourseName());
        dto.setCourseCode(c.getCourseCode());
        dto.setDescription(c.getDescription());
        dto.setDurationMonths(c.getDurationMonths());
        return dto;
    }
}
