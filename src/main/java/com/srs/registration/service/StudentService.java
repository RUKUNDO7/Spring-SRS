package com.srs.registration.service;

import com.srs.registration.domain.AppUser;
import com.srs.registration.domain.Student;
import com.srs.registration.repository.StudentAuditRepository;
import com.srs.registration.repository.StudentHqlRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class StudentService {

    private final StudentHqlRepository studentHqlRepository;
    private final StudentAuditRepository studentAuditRepository;
    private final AppUserService appUserService;

    public StudentService(StudentHqlRepository studentHqlRepository,
                          StudentAuditRepository studentAuditRepository,
                          AppUserService appUserService) {
        this.studentHqlRepository = studentHqlRepository;
        this.studentAuditRepository = studentAuditRepository;
        this.appUserService = appUserService;
    }

    @Transactional(readOnly = true)
    public List<Student> listStudents(String keyword, String currentUserEmail, boolean isAdmin) {
        if (keyword == null || keyword.isBlank()) {
            return isAdmin ? studentHqlRepository.findAll() : studentHqlRepository.findAllByOwnerEmail(currentUserEmail);
        }
        return isAdmin
                ? studentHqlRepository.search(keyword.trim())
                : studentHqlRepository.searchByOwnerEmail(keyword.trim(), currentUserEmail);
    }

    @Transactional(readOnly = true)
    public Student getStudent(Long id) {
        return studentHqlRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));
    }

    public Student create(Student student, String createdByEmail) {
        if (studentHqlRepository.existsByRegistrationNumber(student.getRegistrationNumber())) {
            throw new DuplicateRegistrationException("Registration number already exists");
        }
        AppUser owner = appUserService.getByEmail(createdByEmail);
        student.setCreatedBy(owner);
        Student created = studentHqlRepository.save(student);
        studentAuditRepository.log("CREATE", created.getId(), created.getRegistrationNumber(), createdByEmail);
        return created;
    }

    public Student update(Long id, Student update, String actorEmail) {
        Student existing = getStudent(id);

        if (studentHqlRepository.existsByRegistrationNumberAndIdNot(update.getRegistrationNumber(), id)) {
            throw new DuplicateRegistrationException("Registration number already exists");
        }

        existing.setRegistrationNumber(update.getRegistrationNumber());
        existing.setFirstName(update.getFirstName());
        existing.setLastName(update.getLastName());
        existing.setEmail(update.getEmail());
        existing.setPhone(update.getPhone());
        existing.setDateOfBirth(update.getDateOfBirth());
        existing.setDepartment(update.getDepartment());

        Student saved = studentHqlRepository.save(existing);
        studentAuditRepository.log("UPDATE", saved.getId(), saved.getRegistrationNumber(), actorEmail);
        return saved;
    }

    public void delete(Long id, String actorEmail) {
        Student student = getStudent(id);
        studentAuditRepository.log("DELETE", student.getId(), student.getRegistrationNumber(), actorEmail);
        studentHqlRepository.delete(student);
    }
}

