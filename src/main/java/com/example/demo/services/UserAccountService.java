package com.example.demo.services;

import com.example.demo.dto.UserAccountRequest;
import com.example.demo.dto.UserAccountRegisterRequest;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;
import java.util.List;

@Service
@Transactional
public class UserAccountService implements UserDetailsService {
    private final UserAccountRepository userAccountRepository;
    private final PractitionerRepository practitionerRepository;
    private final PatientRepository patientRepository;
    private final PasswordEncoder passwordEncoder;

    public UserAccountService(UserAccountRepository userAccountRepository,
                              PractitionerRepository practitionerRepository,
                              PatientRepository patientRepository,
                              PasswordEncoder passwordEncoder) {
        this.userAccountRepository = userAccountRepository;
        this.practitionerRepository = practitionerRepository;
        this.patientRepository = patientRepository;
        this.passwordEncoder = passwordEncoder;
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

    public Optional<UserAccount> getUserAccountByEmail(String email) {
        return userAccountRepository.findByEmail(email);
    }

    // local registration defaults to the internal provider and uses email as the stable subject.
    public UserAccount registerUserAccount(UserAccountRegisterRequest request) {
        return createUserAccount(new UserAccountRequest(
                request.displayName(),
                request.username(),
                request.practitionerId(),
                request.patientId(),
                "local",
                request.email(),
                request.role(),
                request.email(),
                request.password()
        ));
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
            request.email(),
            request.password() == null ? null : passwordEncoder.encode(request.password())
        );
        return userAccountRepository.save(newUser);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserAccount userAccount = userAccountRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User with email " + email + " not found"));

        return User.withUsername(userAccount.getEmail())
                .password(userAccount.getPassword())
                .authorities(new SimpleGrantedAuthority(userAccount.getRole().name()))
                .build();
    }
}
