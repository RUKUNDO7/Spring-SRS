package com.srs.registration.repository;

import com.srs.registration.domain.StudentAudit;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@Transactional
public class StudentAuditRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public void log(String action, Long studentId, String registrationNumber, String actorEmail) {
        StudentAudit audit = new StudentAudit();
        audit.setAction(action);
        audit.setStudentId(studentId);
        audit.setStudentRegistrationNumber(registrationNumber);
        audit.setActorEmail(actorEmail);
        audit.setCreatedAt(LocalDateTime.now());
        entityManager.persist(audit);
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public List<StudentAudit> findLatest(int max) {
        return entityManager.createQuery("from StudentAudit order by createdAt desc", StudentAudit.class)
                .setMaxResults(max)
                .getResultList();
    }
}
