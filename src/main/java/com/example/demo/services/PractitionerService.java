package com.example.demo.services;

import com.example.demo.dto.PractitionerRequest;
import com.example.demo.errors.ImmutableFieldException;
import com.example.demo.errors.RepeatedIdNumberException;
import com.example.demo.models.practitioner.Practitioner;
import com.example.demo.repositories.PractitionerRepository;
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

    public PractitionerService(PractitionerRepository practitionerRepository) {
        this.practitionerRepository = practitionerRepository;
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

    public Page<Practitioner> getAllPractitioners(Pageable pageable) {
        return practitionerRepository.findAll(pageable);
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
