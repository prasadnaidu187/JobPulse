package com.jobpulse.backend.job.repository;

import com.jobpulse.backend.job.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {

    List<Job> findByTitleContainingIgnoreCase(String title);

    List<Job> findByLocationContainingIgnoreCase(String location);

    List<Job> findByEmploymentTypeIgnoreCase(String employmentType);

    List<Job> findByCompanyId(Long companyId);

    @Query("""
           SELECT j FROM Job j
           WHERE LOWER(j.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
              OR LOWER(j.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
              OR LOWER(j.skills) LIKE LOWER(CONCAT('%', :keyword, '%'))
           """)
    List<Job> searchJobs(@Param("keyword") String keyword);
}