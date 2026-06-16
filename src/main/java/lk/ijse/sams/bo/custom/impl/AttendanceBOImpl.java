package lk.ijse.sams.bo.custom.impl;

import lk.ijse.sams.bo.custom.AttendanceBO;
import lk.ijse.sams.bo.exception.NotFoundException;
import lk.ijse.sams.dao.DAOFactory;
import lk.ijse.sams.dao.DAOTypes;
import lk.ijse.sams.dao.custom.AttendanceDAO;
import lk.ijse.sams.dao.custom.ClassSessionDAO;
import lk.ijse.sams.dao.custom.StudentDAO;
import lk.ijse.sams.dto.AttendanceDTO;
import lk.ijse.sams.entity.Attendance;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class AttendanceBOImpl implements AttendanceBO {

    private final AttendanceDAO  attendanceDAO = DAOFactory.getInstance().getDAO(DAOTypes.ATTENDANCE);
    private final StudentDAO     studentDAO    = DAOFactory.getInstance().getDAO(DAOTypes.STUDENT);
    private final ClassSessionDAO sessionDAO   = DAOFactory.getInstance().getDAO(DAOTypes.CLASS_SESSION);

    @Override
    public void markAttendance(AttendanceDTO dto) {
        // If record already exists for this student+session, update instead
        Optional<Attendance> existing = attendanceDAO.findByStudentAndSession(
                dto.getStudentId(), dto.getSessionId());
        if (existing.isPresent()) {
            existing.get().setStatus(dto.getStatus());
            existing.get().setRemarks(dto.getRemarks());
            attendanceDAO.update(existing.get());
        } else {
            Attendance attendance = toEntity(dto);
            attendance.setMarkedDate(Date.valueOf(LocalDate.now()));
            attendanceDAO.save(attendance);
        }
    }

    @Override
    public void updateAttendance(AttendanceDTO dto) {
        if (!attendanceDAO.update(toEntity(dto))) throw new NotFoundException("Attendance record not found!");
    }

    @Override
    public List<AttendanceDTO> getAttendanceBySession(String sessionId) {
        return attendanceDAO.findBySessionId(sessionId).stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<AttendanceDTO> getAttendanceByStudent(String studentId) {
        return attendanceDAO.findByStudentId(studentId).stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<AttendanceDTO> getAttendanceByStudentDateRange(String studentId, Date from, Date to) {
        return attendanceDAO.findByStudentAndDateRange(studentId, from, to)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public double getAttendancePercentage(String studentId, String subjectId) {
        long total   = attendanceDAO.countTotalByStudentAndSubject(studentId, subjectId);
        long present = attendanceDAO.countPresentByStudentAndSubject(studentId, subjectId);
        if (total == 0) return 0.0;
        return (double) present / total * 100.0;
    }

    @Override
    public String generateNextId() {
        String last = attendanceDAO.getLastId();
        if (last == null) return "ATT001";
        int num = Integer.parseInt(last.substring(3)) + 1;
        return String.format("ATT%03d", num);
    }

    private Attendance toEntity(AttendanceDTO dto) {
        Attendance a = new Attendance();
        a.setAttendanceId(dto.getAttendanceId());
        a.setStatus(dto.getStatus());
        a.setRemarks(dto.getRemarks());
        a.setMarkedDate(dto.getMarkedDate() != null ? dto.getMarkedDate() : Date.valueOf(LocalDate.now()));
        if (dto.getStudentId() != null)
            studentDAO.findById(dto.getStudentId()).ifPresent(a::setStudent);
        if (dto.getSessionId() != null)
            sessionDAO.findById(dto.getSessionId()).ifPresent(a::setClassSession);
        return a;
    }

    private AttendanceDTO toDTO(Attendance a) {
        AttendanceDTO dto = new AttendanceDTO();
        dto.setAttendanceId(a.getAttendanceId());
        dto.setStatus(a.getStatus());
        dto.setRemarks(a.getRemarks());
        dto.setMarkedDate(a.getMarkedDate());
        if (a.getStudent() != null) {
            dto.setStudentId(a.getStudent().getStudentId());
            dto.setStudentName(a.getStudent().getName());
        }
        if (a.getClassSession() != null) {
            dto.setSessionId(a.getClassSession().getSessionId());
        }
        return dto;
    }
}
