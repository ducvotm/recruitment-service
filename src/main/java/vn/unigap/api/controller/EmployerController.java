package vn.unigap.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import vn.unigap.api.dto.in.EmployerDtoIn;
import vn.unigap.api.dto.out.EmployerDtoOut;
import vn.unigap.api.entity.Employer;
import vn.unigap.api.service.EmployerService;

import java.util.Optional;

@RestController
@RequestMapping("/employer")
public class EmployerController {

    @Autowired
    private EmployerService employerService;

    /*Create employer*/
    @PostMapping()
    public Employer createEmployer(@RequestBody Employer employer) {
        return employerService.createEmployer(employer);
    }

    /*Update employer*/
    @PutMapping("/update")
    public EmployerDtoOut updateEmployer(EmployerDtoIn employer) {
        return employer.convertToDtoOut();
    }

    /*Read employer*/
    @GetMapping("{id}")
    public Optional<Employer> getEmployerById(@PathVariable Long id) {
        return employerService.getEmployerById(id);
    }

    @GetMapping(pending)
    public EmployerDtoOut getPageOfEmployer(Pending) {
        return employer.convertToDtoOut();
    }

    /*Delete employer*/
    @DeleteMapping("/delete/{id}")
    public void deleteEmployerById(@PathVariable Long id) {
        employerService.deleteEmployer(id);
    }

}





