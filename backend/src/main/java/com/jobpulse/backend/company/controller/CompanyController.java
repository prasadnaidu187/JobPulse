package com.jobpulse.backend.company.controller;

import com.jobpulse.backend.company.entity.Company;
import com.jobpulse.backend.company.repository.CompanyRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/companies")
@CrossOrigin(origins = "*")
public class CompanyController {

    private final CompanyRepository companyRepository;

    public CompanyController(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    // Get all companies
    @GetMapping
    public ResponseEntity<List<Company>> getAllCompanies() {

        List<Company> companies = companyRepository.findAll();

        return ResponseEntity.ok(companies);
    }

    // Get company by ID
    @GetMapping("/{id}")
    public ResponseEntity<Company> getCompanyById(
            @PathVariable Long id) {

        return companyRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Get companies by industry
    @GetMapping("/industry")
    public ResponseEntity<List<Company>> getCompaniesByIndustry(
            @RequestParam String name) {

        List<Company> companies =
                companyRepository.findByIndustryIgnoreCase(name.trim());

        return ResponseEntity.ok(companies);
    }

    // Create a new company
    @PostMapping
    public ResponseEntity<?> createCompany(
            @RequestBody Company company) {

        if (company.getName() == null ||
                company.getName().trim().isEmpty()) {

            return ResponseEntity.badRequest()
                    .body("Company name is required");
        }

        company.setName(company.getName().trim());

        if (companyRepository.existsByNameIgnoreCase(company.getName())) {

            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("A company with this name already exists");
        }

        Company savedCompany = companyRepository.save(company);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(savedCompany);
    }

    // Update an existing company
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCompany(
            @PathVariable Long id,
            @RequestBody Company updatedCompany) {

        Optional<Company> optionalCompany =
                companyRepository.findById(id);

        if (optionalCompany.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        if (updatedCompany.getName() == null ||
                updatedCompany.getName().trim().isEmpty()) {

            return ResponseEntity.badRequest()
                    .body("Company name is required");
        }

        String newName = updatedCompany.getName().trim();

        Optional<Company> existingCompany =
                companyRepository.findByNameIgnoreCase(newName);

        if (existingCompany.isPresent() &&
                !existingCompany.get().getId().equals(id)) {

            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Another company already uses this name");
        }

        Company company = optionalCompany.get();

        company.setName(newName);
        company.setIndustry(updatedCompany.getIndustry());
        company.setWebsite(updatedCompany.getWebsite());
        company.setLocation(updatedCompany.getLocation());
        company.setDescription(updatedCompany.getDescription());

        Company savedCompany = companyRepository.save(company);

        return ResponseEntity.ok(savedCompany);
    }

    // Delete a company
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCompany(
            @PathVariable Long id) {

        if (!companyRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        companyRepository.deleteById(id);

        return ResponseEntity.noContent().build();
    }
}