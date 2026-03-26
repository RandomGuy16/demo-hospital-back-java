package com.example.demo.services;

import com.example.demo.dto.UserAccountRequest;
import com.example.demo.errors.RepeatedUsernameException;
import com.example.demo.errors.UnclearUserRoleException;
import com.example.demo.models.patient.Patient;
import com.example.demo.models.practitioner.Practitioner;
import com.example.demo.models.useraccount.Role;
import com.example.demo.models.useraccount.UserAccount;
import com.example.demo.repositories.PatientRepository;
import com.example.demo.repositories.PractitionerRepository;
import com.example.demo.repositories.UserAccountRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;
import java.util.List;

@Service
@Transactional
public class UserAccountService {
    private final UserAccountRepository userAccountRepository;
    private final PractitionerRepository practitionerRepository;
    private final PatientRepository patientRepository;

    public UserAccountService(UserAccountRepository userAccountRepository,
                              PractitionerRepository practitionerRepository,
                              PatientRepository patientRepository) {
        this.userAccountRepository = userAccountRepository;
        this.practitionerRepository = practitionerRepository;
        this.patientRepository = patientRepository;
    }

    private record UserAccountRefs (
        Patient patient,
        Practitioner practitioner
    ) {};

    private UserAccountRefs resolveUserAccountRefs(UserAccountRequest request) {
        Patient patient = request.patientId() == null
                ? null
                : patientRepository.findById(request.patientId())
                    .orElseThrow(() -> new UnclearUserRoleException("Patient link does not exist"));

        Practitioner practitioner = request.practitionerId() == null
                ? null
                : practitionerRepository.findById(request.practitionerId())
                    .orElseThrow(() -> new UnclearUserRoleException("Practitioner link does not exist"));

        validateRoleLink(request.role(), patient, practitioner);

        return new UserAccountRefs(patient, practitioner);
    }

    private void validateRoleLink(Role role, Patient patient, Practitioner practitioner) {
        boolean hasPatient = patient != null;
        boolean hasPractitioner = practitioner != null;

        boolean valid = switch (role) {
            case ROLE_PATIENT -> hasPatient && !hasPractitioner;
            case ROLE_PRACTITIONER -> hasPractitioner && !hasPatient;
            case ROLE_ADMIN, ROLE_RECEPTIONIST -> !hasPatient && !hasPractitioner;
        };

        if (!valid) {
            throw new UnclearUserRoleException("Role does not match the linked patient/practitioner reference");
        }
    }

    public List<UserAccount> getAllUserAccounts() {
        return userAccountRepository.findAll();
    }

    public Optional<UserAccount> getUserAccountById(UUID id) {
        return userAccountRepository.findById(id);
    }

    public UserAccount createUserAccount(UserAccountRequest request) {
        if (userAccountRepository.existsByUsername(request.username())) {
            throw new RepeatedUsernameException("User with username " + request.username() + " already exists");
        }

        if (userAccountRepository.existsByEmail(request.email())) {
            throw new RepeatedUsernameException("User with email " + request.email() + " already exists");
        }

        if (userAccountRepository.existsByProviderAndProviderSubject(request.provider(), request.providerSubject())) {
            throw new RepeatedUsernameException("User for provider subject already exists");
        }

        UserAccountRefs refs = resolveUserAccountRefs(request);

        UserAccount newUser = new UserAccount(
            refs.practitioner,
            refs.patient,
            request.provider(),
            request.providerSubject(),
            request.role(),
            request.displayName(),
            request.username(),
            request.email()
        );
        return userAccountRepository.save(newUser);
    }
}
