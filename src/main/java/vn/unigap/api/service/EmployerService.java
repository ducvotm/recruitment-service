package vn.unigap.api.service;

import vn.unigap.api.entity.Employer;

import java.util.List;
import java.util.Optional;

public interface EmployerService {
    Employer createEmployer(Employer employer);
    Optional<Employer> getEmployerById(Long id);
    List<Employer> getAllEmployers();
    Employer updateEmployer(Long id, Employer employer);
    void deleteEmployer(Long id);
}
