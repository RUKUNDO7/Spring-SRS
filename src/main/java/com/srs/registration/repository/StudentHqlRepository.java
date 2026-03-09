package com.srs.registration.repository;

import com.srs.registration.domain.Student;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class StudentHqlRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public List<Student> findAll() {
        return entityManager.createQuery("from Student s order by s.createdAt desc", Student.class)
                .getResultList();
    }

    public List<Student> findAllByOwnerEmail(String email) {
        return entityManager.createQuery(
                        "from Student s where s.createdBy.email = :email order by s.createdAt desc",
                        Student.class)
                .setParameter("email", email)
                .getResultList();
    }

    public List<Student> search(String keyword) {
        return entityManager.createQuery(
                        "from Student s where lower(s.firstName) like lower(:keyword) " +
                                "or lower(s.lastName) like lower(:keyword) " +
                                "or lower(s.registrationNumber) like lower(:keyword) " +
                                "or lower(s.department) like lower(:keyword) " +
                                "order by s.createdAt desc",
                        Student.class)
                .setParameter("keyword", "%" + keyword + "%")
                .getResultList();
    }

    public List<Student> searchByOwnerEmail(String keyword, String email) {
        return entityManager.createQuery(
                        "from Student s where s.createdBy.email = :email and (" +
                                "lower(s.firstName) like lower(:keyword) " +
                                "or lower(s.lastName) like lower(:keyword) " +
                                "or lower(s.registrationNumber) like lower(:keyword) " +
                                "or lower(s.department) like lower(:keyword)) " +
                                "order by s.createdAt desc",
                        Student.class)
                .setParameter("email", email)
                .setParameter("keyword", "%" + keyword + "%")
                .getResultList();
    }

    public Optional<Student> findById(Long id) {
        return Optional.ofNullable(entityManager.find(Student.class, id));
    }

    public boolean existsByRegistrationNumber(String registrationNumber) {
        Long count = entityManager.createQuery(
                        "select count(s.id) from Student s where s.registrationNumber = :registrationNumber",
                        Long.class)
                .setParameter("registrationNumber", registrationNumber)
                .getSingleResult();
        return count != null && count > 0;
    }

    public boolean existsByRegistrationNumberAndIdNot(String registrationNumber, Long id) {
        Long count = entityManager.createQuery(
                        "select count(s.id) from Student s where s.registrationNumber = :registrationNumber and s.id <> :id",
                        Long.class)
                .setParameter("registrationNumber", registrationNumber)
                .setParameter("id", id)
                .getSingleResult();
        return count != null && count > 0;
    }

    public Student save(Student student) {
        if (student.getId() == null) {
            entityManager.persist(student);
            return student;
        }
        return entityManager.merge(student);
    }

    public void delete(Student student) {
        entityManager.remove(student);
    }
}

