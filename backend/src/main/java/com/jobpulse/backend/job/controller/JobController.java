package com.jobpulse.backend.job.controller;

import com.jobpulse.backend.job.dto.JobRequest;
import com.jobpulse.backend.job.dto.JobResponse;
import com.jobpulse.backend.job.service.JobService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
@CrossOrigin(origins = "*")
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    // Get all jobs
    @GetMapping
    public ResponseEntity<List<JobResponse>> getAllJobs() {
        return ResponseEntity.ok(jobService.getAllJobs());
    }

    // Get job by ID
    @GetMapping("/{id}")
    public ResponseEntity<JobResponse> getJobById(@PathVariable Long id) {
        return ResponseEntity.ok(jobService.getJobById(id));
    }

    // Create a job
    @PostMapping
    public ResponseEntity<JobResponse> createJob(
            @Valid @RequestBody JobRequest request
    ) {
        JobResponse response = jobService.createJob(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Update a job
    @PutMapping("/{id}")
    public ResponseEntity<JobResponse> updateJob(
            @PathVariable Long id,
            @Valid @RequestBody JobRequest request
    ) {
        return ResponseEntity.ok(jobService.updateJob(id, request));
    }

    // Delete a job
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteJob(@PathVariable Long id) {
        jobService.deleteJob(id);

        return ResponseEntity.noContent().build();
    }

    // Search jobs by title, description, or skills
    @GetMapping("/search")
    public ResponseEntity<List<JobResponse>> searchJobs(
            @RequestParam String keyword
    ) {
        return ResponseEntity.ok(jobService.searchJobs(keyword));
    }

    // Filter jobs by location
    @GetMapping("/location")
    public ResponseEntity<List<JobResponse>> getJobsByLocation(
            @RequestParam String location
    ) {
        return ResponseEntity.ok(jobService.getJobsByLocation(location));
    }

    // Filter jobs by employment type
    @GetMapping("/employment-type")
    public ResponseEntity<List<JobResponse>> getJobsByEmploymentType(
            @RequestParam String type
    ) {
        return ResponseEntity.ok(
                jobService.getJobsByEmploymentType(type)
        );
    }
}