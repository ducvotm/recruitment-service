package vn.unigap.api.repository.jpa;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.unigap.api.entity.jpa.Job;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface JobRepository extends JpaRepository<Job, Long> {

    @Query("SELECT j FROM Job j, Employer e WHERE j.employerId = e.id ORDER BY j.expiredAt DESC, e.name ASC")
    Page<Job> findAllJobsOrderedByExpiredAtAndEmployerName(Pageable pageable);

    @Query("SELECT DATE(j.createdAt), COUNT(j.id) FROM Job j WHERE j.createdAt between :start and :end GROUP BY DATE(j.createdAt)")
    List<Object[]> countJobByDate(@Param("start") LocalDateTime start,
                                        @Param("end") LocalDateTime end);
}
