package com.jobpulse.backend.job.service;

import com.jobpulse.backend.company.entity.Company;
import com.jobpulse.backend.company.repository.CompanyRepository;
import com.jobpulse.backend.job.dto.JobRequest;
import com.jobpulse.backend.job.dto.JobResponse;
import com.jobpulse.backend.job.entity.Job;
import com.jobpulse.backend.job.repository.JobRepository;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class JobService {

    private final JobRepository jobRepository;
    private final CompanyRepository companyRepository;

    public JobService(
            JobRepository jobRepository,
            CompanyRepository companyRepository
    ) {
        this.jobRepository = jobRepository;
        this.companyRepository = companyRepository;
    }

    @Transactional(readOnly = true)
    public List<JobResponse> getAllJobs() {
        return jobRepository
                .findAll(Sort.by(Sort.Direction.DESC, "createdAt"))
                .stream()
                .map(JobResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public JobResponse getJobById(Long id) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Job not found with ID: " + id));

        return JobResponse.fromEntity(job);
    }

    @Transactional
    public JobResponse createJob(JobRequest request) {
        Company company = companyRepository.findById(request.getCompanyId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Company not found with ID: " + request.getCompanyId()
                        ));

        validateSalary(request);

        Job job = new Job();

        updateJobFields(job, request, company);

        Job savedJob = jobRepository.save(job);

        return JobResponse.fromEntity(savedJob);
    }

    @Transactional
    public JobResponse updateJob(Long id, JobRequest request) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Job not found with ID: " + id));

        Company company = companyRepository.findById(request.getCompanyId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Company not found with ID: " + request.getCompanyId()
                        ));

        validateSalary(request);

        updateJobFields(job, request, company);

        Job updatedJob = jobRepository.save(job);

        return JobResponse.fromEntity(updatedJob);
    }

    @Transactional
    public void deleteJob(Long id) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Job not found with ID: " + id));

        jobRepository.delete(job);
    }

    @Transactional(readOnly = true)
    public List<JobResponse> searchJobs(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return getAllJobs();
        }

        return jobRepository.searchJobs(keyword.trim())
                .stream()
                .map(JobResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<JobResponse> getJobsByLocation(String location) {
        return jobRepository
                .findByLocationContainingIgnoreCase(location)
                .stream()
                .map(JobResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<JobResponse> getJobsByEmploymentType(String employmentType) {
        return jobRepository
                .findByEmploymentTypeIgnoreCase(employmentType)
                .stream()
                .map(JobResponse::fromEntity)
                .toList();
    }

    private void updateJobFields(
            Job job,
            JobRequest request,
            Company company
    ) {
        job.setTitle(request.getTitle().trim());
        job.setCompany(company);
        job.setLocation(request.getLocation());
        job.setDescription(request.getDescription());
        job.setEmploymentType(request.getEmploymentType());
        job.setSkills(request.getSkills());
        job.setExperienceRequired(request.getExperienceRequired());
        job.setSalaryMin(request.getSalaryMin());
        job.setSalaryMax(request.getSalaryMax());
        job.setSourceName(request.getSourceName());
        job.setSourceUrl(request.getSourceUrl());
        job.setPostedAt(
                request.getPostedAt() != null
                        ? request.getPostedAt()
                        : LocalDate.now()
        );
    }

    private void validateSalary(JobRequest request) {
        if (request.getSalaryMin() != null
                && request.getSalaryMax() != null
                && request.getSalaryMin().compareTo(request.getSalaryMax()) > 0) {
            throw new IllegalArgumentException(
                    "Minimum salary cannot be greater than maximum salary"
            );
        }
    }
}