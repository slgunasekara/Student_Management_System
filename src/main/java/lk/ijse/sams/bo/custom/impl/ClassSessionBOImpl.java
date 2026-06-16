package lk.ijse.sams.bo.custom.impl;

import lk.ijse.sams.bo.custom.ClassSessionBO;
import lk.ijse.sams.bo.exception.NotFoundException;
import lk.ijse.sams.bo.exception.ValidationException;
import lk.ijse.sams.dao.DAOFactory;
import lk.ijse.sams.dao.DAOTypes;
import lk.ijse.sams.dao.custom.ClassSessionDAO;
import lk.ijse.sams.dao.custom.LecturerDAO;
import lk.ijse.sams.dao.custom.SubjectDAO;
import lk.ijse.sams.dto.ClassSessionDTO;
import lk.ijse.sams.entity.ClassSession;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ClassSessionBOImpl implements ClassSessionBO {

    private final ClassSessionDAO sessionDAO = DAOFactory.getInstance().getDAO(DAOTypes.CLASS_SESSION);
    private final SubjectDAO      subjectDAO = DAOFactory.getInstance().getDAO(DAOTypes.SUBJECT);
    private final LecturerDAO     lecturerDAO= DAOFactory.getInstance().getDAO(DAOTypes.LECTURER);

    @Override
    public void saveSession(ClassSessionDTO dto) {
        if (dto.getSessionDate() == null) throw new ValidationException("Session date is required!");
        if (dto.getSubjectId() == null)   throw new ValidationException("Subject is required!");
        if (dto.getLecturerId() == null)  throw new ValidationException("Lecturer is required!");
        sessionDAO.save(toEntity(dto));
    }

    @Override
    public void updateSession(ClassSessionDTO dto) {
        if (!sessionDAO.update(toEntity(dto))) throw new NotFoundException("Class session not found!");
    }

    @Override
    public void deleteSession(String id) {
        if (!sessionDAO.delete(id)) throw new NotFoundException("Class session not found!");
    }

    @Override
    public List<ClassSessionDTO> getAllSessions() {
        return sessionDAO.getAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<ClassSessionDTO> getSessionsBySubject(String subjectId) {
        return sessionDAO.findBySubjectId(subjectId).stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public Optional<ClassSessionDTO> findById(String id) {
        return sessionDAO.findById(id).map(this::toDTO);
    }

    @Override
    public String generateNextId() {
        String last = sessionDAO.getLastId();
        if (last == null) return "SES001";
        int num = Integer.parseInt(last.substring(3)) + 1;
        return String.format("SES%03d", num);
    }

    private ClassSession toEntity(ClassSessionDTO dto) {
        ClassSession cs = new ClassSession();
        cs.setSessionId(dto.getSessionId());
        cs.setSessionDate(dto.getSessionDate());
        cs.setStartTime(dto.getStartTime());
        cs.setEndTime(dto.getEndTime());
        cs.setVenue(dto.getVenue());
        cs.setNotes(dto.getNotes());
        if (dto.getSubjectId() != null)
            subjectDAO.findById(dto.getSubjectId()).ifPresent(cs::setSubject);
        if (dto.getLecturerId() != null)
            lecturerDAO.findById(dto.getLecturerId()).ifPresent(cs::setLecturer);
        return cs;
    }

    private ClassSessionDTO toDTO(ClassSession cs) {
        ClassSessionDTO dto = new ClassSessionDTO();
        dto.setSessionId(cs.getSessionId());
        dto.setSessionDate(cs.getSessionDate());
        dto.setStartTime(cs.getStartTime());
        dto.setEndTime(cs.getEndTime());
        dto.setVenue(cs.getVenue());
        dto.setNotes(cs.getNotes());
        if (cs.getSubject() != null) {
            dto.setSubjectId(cs.getSubject().getSubjectId());
            dto.setSubjectName(cs.getSubject().getSubjectName());
        }
        if (cs.getLecturer() != null) {
            dto.setLecturerId(cs.getLecturer().getLecturerId());
            dto.setLecturerName(cs.getLecturer().getName());
        }
        return dto;
    }
}
