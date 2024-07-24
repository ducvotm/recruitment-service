package vn.unigap.api.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.unigap.api.entity.Employer;
import vn.unigap.api.repository.EmployerRepository;

import java.util.Optional;

@Service
public class EmployerServiceImpl implements EmployerService {

    @Autowired
    private EmployerRepository employerRepository;

    @Override
    public Employer createEmployer(Employer employer) {
        return employerRepository.save(employer);
    }

    @Override
    public Employer updateEmployer(Long id, Employer employer) {
        // Check if the employer with the given ID exists
        Optional<Employer> existingEmployerOpt = employerRepository.findById(id);

        if (existingEmployerOpt.isPresent()) {
            Employer existingEmployer = existingEmployerOpt.get();

            // Update fields of the existing employer with values from the provided employer object
            existingEmployer.setName(employer.getName());

            //Update another field as needed
            return employerRepository.save(existingEmployer);

        } else {
            // Handle the case where the employer with the given ID does not exist
            throw new EntityNotFoundException("Employer with ID " + id + " not found.");
        }
    }

    @Override
    public Optional<Employer> getEmployerById(Long id) {
        return employerRepository.findById(id);
    }

    @Override
    public getPageOfEmployer() {
        pending
    }

    @Override
    public void deleteEmployer(Long id) {
        employerRepository.deleteById(id);
    }

}
