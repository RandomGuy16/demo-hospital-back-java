package com.evergreen.generalhospital.services;

import com.evergreen.generalhospital.dto.practitioner.PractitionerRequest;
import com.evergreen.generalhospital.errors.ImmutableFieldException;
import com.evergreen.generalhospital.errors.RepeatedIdNumberException;
import com.evergreen.generalhospital.errors.ResourceNotFoundException;
import com.evergreen.generalhospital.models.practitioner.Practitioner;
import com.evergreen.generalhospital.repositories.DepartmentRepository;
import com.evergreen.generalhospital.repositories.PractitionerRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class PractitionerService {
    private final PractitionerRepository practitionerRepository;
    private final DepartmentRepository departmentRepository;

    public PractitionerService(PractitionerRepository practitionerRepository,
                               DepartmentRepository departmentRepository
    ) {
        this.practitionerRepository = practitionerRepository;
        this.departmentRepository = departmentRepository;
    }

    public Practitioner createPractitioner(PractitionerRequest request) {
        // check if idNumber already exists, that can't be repeated
        // person being globally sets two different idNumber columns, which agrees to reality
        // a doctor/practitioner when they are sick, they become patients and someone else attends them
        if (practitionerRepository.existsByIdNumber(request.idNumber())) {
            throw new RepeatedIdNumberException("Practitioner with idNumber " + request.idNumber() + " already exists");
        }

        Practitioner practitioner = new Practitioner(
                request.firstName(),
                request.lastName(),
                request.idNumber(),
                request.dateOfBirth(),
                request.gender(),
                request.phoneNumber(),
                request.contacts());
        practitioner.setSpecialties(request.specialties() == null ? new ArrayList<>() : new ArrayList<>(request.specialties()));
        return practitionerRepository.save(practitioner);
    }

    private Page<Practitioner> getAllPractitioners(Pageable pageable) {
        return practitionerRepository.findAll(pageable);
    }

    private Page<Practitioner> getPractitionersByDepartment(Pageable pageable, UUID departmentId) {
        return practitionerRepository.findByDepartments_DepartmentId(departmentId, pageable);
    }

    private Page<Practitioner> getPractitionersBySpecialty(Pageable pageable, String specialty) {
        String trimmed = specialty.trim();
        if (trimmed.length() > 100)
            throw new IllegalArgumentException("Specialty filter must not exceed 100 characters");

        return practitionerRepository.findBySpecialtiesContaining(trimmed, pageable);
    }

    public Page<Practitioner> getPractitioners(Pageable pageable, UUID departmentId, String specialty) {
        boolean hasDept = departmentId != null;
        boolean hasSpecialty = specialty != null && !specialty.isBlank();

        // logic, fall gracefully if any parameter isnt present
        if (hasDept) {
            if (!departmentRepository.existsById(departmentId)) {
                throw new ResourceNotFoundException("Department not found with id: " + departmentId);
            }
            if (hasSpecialty) {
                String trimmed = specialty.trim();
                if (trimmed.length() > 100) {
                    throw new IllegalArgumentException("Specialty filter must not exceed 100 characters");
                }
                return practitionerRepository.findByDepartmentAndSpecialty(departmentId, trimmed, pageable);
            }
            return getPractitionersByDepartment(pageable, departmentId);
        } else if (hasSpecialty) {
            return getPractitionersBySpecialty(pageable, specialty);
        }
        return getAllPractitioners(pageable);
    }


    public Optional<Practitioner> getPractitionerById(UUID id) {
        return practitionerRepository.findById(id);
    }

    public Optional<Practitioner> updatePractitioner(UUID id, PractitionerRequest request) {
        return practitionerRepository.findById(id)
                .map(practitioner -> {
                    if (!practitioner.getIdNumber().equals(request.idNumber())) {
                        throw new ImmutableFieldException("Practitioner idNumber cannot be changed");
                    }
                    practitioner.setFirstName(request.firstName());
                    practitioner.setLastName(request.lastName());
                    practitioner.setDateOfBirth(request.dateOfBirth());
                    practitioner.setGender(request.gender());
                    practitioner.setPhoneNumber(request.phoneNumber());
                    practitioner.setContacts(request.contacts());
                    practitioner.setSpecialties(
                            request.specialties() == null ? new ArrayList<>() : new ArrayList<>(request.specialties()));
                    return practitionerRepository.save(practitioner);
                });
    }

    public Optional<Practitioner> deletePractitioner(UUID id) {
        return practitionerRepository.findById(id)
                .map(practitioner -> {
                    practitioner.getSpecialties().size();
                    practitioner.getDepartments().size();
                    practitionerRepository.delete(practitioner);
                    return practitioner;
                });
    }
}
