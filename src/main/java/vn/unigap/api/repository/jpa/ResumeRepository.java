package vn.unigap.api.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.unigap.api.entity.jpa.Resume;

import java.time.LocalDateTime;
import java.util.List;

public interface ResumeRepository extends JpaRepository<Resume, Long> {

    @Query("SELECT DATE(r.createdAt), COUNT(r.id) FROM Resume r WHERE r.createdAt between :start and :end GROUP BY DATE(r.createdAt)")
    List<Object[]> countResumeByDate(@Param("start") LocalDateTime start,
                                     @Param("end") LocalDateTime end);


    @Query("SELECT r FROM Resume r WHERE r.salary <= :jobSalary")
    List<Resume> findResumesBySalary(@Param("jobSalary") Integer jobSalary);
}
