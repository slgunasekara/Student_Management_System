package lk.ijse.sams.bo.custom.impl;

import lk.ijse.sams.bo.custom.LecturerBO;
import lk.ijse.sams.bo.exception.DuplicateException;
import lk.ijse.sams.bo.exception.NotFoundException;
import lk.ijse.sams.bo.exception.ValidationException;
import lk.ijse.sams.dao.DAOFactory;
import lk.ijse.sams.dao.DAOTypes;
import lk.ijse.sams.dao.custom.LecturerDAO;
import lk.ijse.sams.dto.LecturerDTO;
import lk.ijse.sams.entity.Lecturer;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class LecturerBOImpl implements LecturerBO {

    private final LecturerDAO lecturerDAO = DAOFactory.getInstance().getDAO(DAOTypes.LECTURER);

    @Override
    public void saveLecturer(LecturerDTO dto) {
        if (dto.getName() == null || dto.getName().isBlank())
            throw new ValidationException("Lecturer name is required!");
        if (dto.getEmail() == null || !dto.getEmail().matches("^[\\w._%+\\-]+@[\\w.\\-]+\\.[a-zA-Z]{2,}$"))
            throw new ValidationException("Invalid email format!");
        if (dto.getPhone() == null || !dto.getPhone().matches("0\\d{9}"))
            throw new ValidationException("Invalid phone! Use format: 0771234567");

        if (lecturerDAO.existsByEmail(dto.getEmail()))
            throw new DuplicateException("Email '" + dto.getEmail() + "' already registered!");

        lecturerDAO.save(toEntity(dto));
    }

    @Override
    public void updateLecturer(LecturerDTO dto) {
        if (!lecturerDAO.update(toEntity(dto))) throw new NotFoundException("Lecturer not found!");
    }

    @Override
    public void deleteLecturer(String id) {
        if (!lecturerDAO.delete(id)) throw new NotFoundException("Lecturer not found!");
    }

    @Override
    public List<LecturerDTO> getAllLecturers() {
        return lecturerDAO.getAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public Optional<LecturerDTO> findById(String id) {
        return lecturerDAO.findById(id).map(this::toDTO);
    }

    @Override
    public String generateNextId() {
        String last = lecturerDAO.getLastId();
        if (last == null) return "L001";
        int num = Integer.parseInt(last.substring(1)) + 1;
        return String.format("L%03d", num);
    }

    private Lecturer toEntity(LecturerDTO dto) {
        Lecturer l = new Lecturer();
        l.setLecturerId(dto.getLecturerId());
        l.setName(dto.getName());
        l.setEmail(dto.getEmail());
        l.setPhone(dto.getPhone());
        l.setQualification(dto.getQualification());
        l.setDepartment(dto.getDepartment());
        l.setActive(dto.isActive());
        return l;
    }

    private LecturerDTO toDTO(Lecturer l) {
        LecturerDTO dto = new LecturerDTO();
        dto.setLecturerId(l.getLecturerId());
        dto.setName(l.getName());
        dto.setEmail(l.getEmail());
        dto.setPhone(l.getPhone());
        dto.setQualification(l.getQualification());
        dto.setDepartment(l.getDepartment());
        dto.setActive(l.isActive());
        return dto;
    }
}
