package com.evergreen.generalhospital.services;

import com.evergreen.generalhospital.dto.useraccount.UserAccountRequest;
import com.evergreen.generalhospital.dto.useraccount.UserAccountRegisterRequest;
import com.evergreen.generalhospital.errors.RepeatedUsernameException;
import com.evergreen.generalhospital.errors.UnclearUserRoleException;
import com.evergreen.generalhospital.errors.PatientIdentityMismatchException;
import com.evergreen.generalhospital.models.patient.Patient;
import com.evergreen.generalhospital.models.practitioner.Practitioner;
import com.evergreen.generalhospital.models.useraccount.Role;
import com.evergreen.generalhospital.models.useraccount.UserAccount;
import com.evergreen.generalhospital.repositories.PatientRepository;
import com.evergreen.generalhospital.repositories.PractitionerRepository;
import com.evergreen.generalhospital.repositories.UserAccountRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;
import java.util.List;

@Service
@Transactional
public class UserAccountService implements UserDetailsService {
    private final UserAccountRepository userAccountRepository;
    private final PractitionerRepository practitionerRepository;
    private final PatientRepository patientRepository;
    private final PatientService patientService;
    private final PasswordEncoder passwordEncoder;

    /**
     * Creates the user-account service dependencies.
     *
     * @param userAccountRepository  repository for user-account persistence.
     * @param practitionerRepository repository used to resolve practitioner links.
     * @param patientRepository      repository used to resolve patient links.
     * @param patientService         service used to resolve or create patients
     *                               during registration.
     * @param passwordEncoder        encoder used to hash local passwords before
     *                               persistence.
     */
    public UserAccountService(UserAccountRepository userAccountRepository,
            PractitionerRepository practitionerRepository,
            PatientRepository patientRepository,
            PatientService patientService,
            PasswordEncoder passwordEncoder) {
        this.userAccountRepository = userAccountRepository;
        this.practitionerRepository = practitionerRepository;
        this.patientRepository = patientRepository;
        this.patientService = patientService;
        this.passwordEncoder = passwordEncoder;
    }

    private record UserAccountRefs(
            Patient patient,
            Practitioner practitioner) {
    };

    /**
     * Resolves the optional patient and practitioner links for a user-account
     * request.
     *
     * @param request incoming user-account payload.
     * @return resolved patient/practitioner references.
     * @throws UnclearUserRoleException if a referenced entity does not exist or the
     *                                  role/link pair is invalid.
     */
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

    /**
     * Validates that the selected role matches the attached domain links.
     *
     * @param role         requested application role.
     * @param patient      optional patient link.
     * @param practitioner optional practitioner link.
     * @throws UnclearUserRoleException if the role and links describe an invalid
     *                                  combination.
     */
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

    /**
     * Lists every stored user account.
     *
     * @return all persisted user accounts.
     */
    public List<UserAccount> getAllUserAccounts() {
        return userAccountRepository.findAll();
    }

    /**
     * Fetches a user account by its identifier.
     *
     * @param id user-account identifier.
     * @return optional user account.
     */
    public Optional<UserAccount> getUserAccountById(UUID id) {
        return userAccountRepository.findById(id);
    }

    /**
     * Fetches a user account by email.
     *
     * @param email unique login email.
     * @return optional user account.
     */
    public Optional<UserAccount> getUserAccountByEmail(String email) {
        return userAccountRepository.findByEmail(email);
    }

    /**
     * Registers a local patient account from demographics and credentials.
     *
     * <p>
     * Self-service registration is patient-only: the payload carries the
     * person's demographics, the patient is resolved or created by national
     * idNumber, and the account is linked to it with the {@code ROLE_PATIENT}
     * role. The account username mirrors the email so login stays
     * email-based.
     * </p>
     *
     * @param request registration payload.
     * @return newly created user account.
     * @throws RepeatedUsernameException        if the email is already taken or
     *                                          the idNumber already owns an
     *                                          account.
     * @throws PatientIdentityMismatchException if the demographics do not match
     *                                          an existing patient under that
     *                                          idNumber
     */
    public UserAccount registerUserAccount(UserAccountRegisterRequest request) {
        // username is the same as email, to avoid complications
        String username = request.email();

        if (userAccountRepository.existsByEmail(request.email())) {
            throw new RepeatedUsernameException("User with email " + request.email() + " already exists");
        }
        if (userAccountRepository.existsByUsername(username)) {
            throw new RepeatedUsernameException("User with username " + username + " already exists");
        }

        Patient patient = patientService.resolveOrCreateByIdNumber(
                request.idNumber(),
                request.firstName(),
                request.lastName(),
                request.dateOfBirth(),
                request.gender(),
                request.phoneNumber(),
                request.emergencyContact(),
                request.address());

        // checks if the patient already has a user PRIOR to adding the new user
        if (userAccountRepository.existsByPerson(patient)) {
            throw new RepeatedUsernameException("A user account already exists for idNumber " + request.idNumber());
        }

        // create hashset to contain roles
        var roles = new HashSet<Role>();
        roles.add(Role.ROLE_PATIENT);

        // add the user
        UserAccount newUser = new UserAccount(
                "local",
                username,
                roles,
                request.firstName() + " " + request.lastName(),
                username,
                request.email(),
                passwordEncoder.encode(request.password())); // use BCrypt to encode the password
                                                             // this is defined in SecurityConfig
        newUser.setPerson(patient);
        return userAccountRepository.save(newUser);
    }

    /**
     * Future feature: Administrative account creation.
     * Reserved for administrative provisioning in the dedicated admin branch.
     * Creates a user account after enforcing uniqueness and role-link constraints.
     *
     * @param request user-account payload.
     * @return persisted user account with encoded password.
     * @throws RepeatedUsernameException if username, email, or provider subject is
     *                                   already taken.
     * @throws UnclearUserRoleException  if the role and linked entities are
     *                                   inconsistent.
     */
    public UserAccount adminCreateUserAccount(UserAccountRequest request) {
        if (userAccountRepository.existsByUsername(request.username())) {
            throw new RepeatedUsernameException("User with username " + request.username() + " already exists");
        }

        if (userAccountRepository.existsByEmail(request.email())) {
            throw new RepeatedUsernameException("User with email " + request.email() + " already exists");
        }

        if (userAccountRepository.existsByProviderAndProviderSubject(request.provider(), request.providerSubject())) {
            throw new RepeatedUsernameException("User for provider subject already exists");
        }

        // Resolve and validate the optional patient/practitioner ownership before
        // writing anything.
        UserAccountRefs refs = resolveUserAccountRefs(request);

        var roles = new HashSet<Role>();
        roles.add(request.role());

        // create the user account entity
        UserAccount newUser = new UserAccount(
                request.provider(),
                request.providerSubject(),
                roles,
                request.displayName(),
                request.username(),
                request.email(),
                request.password() == null ? null : passwordEncoder.encode(request.password()));
        if (refs.patient() != null) {
            newUser.setPerson(refs.patient());
        } else if (refs.practitioner() != null) {
            newUser.setPerson(refs.practitioner());
        }
        return userAccountRepository.save(newUser);
    }

    @Override
    /**
     * Loads Spring Security credentials for local email/password authentication.
     *
     * @param email email used as the username during login.
     * @return Spring Security user details with the stored password hash and role.
     * @throws UsernameNotFoundException if the email is not registered.
     */
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserAccount userAccount = userAccountRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User with email " + email + " not found"));

        // stream UserAccount roles and map them to SimpleGrantedAuthority
        return User.withUsername(userAccount.getEmail())
                .password(userAccount.getPassword())
                .authorities(userAccount.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority(role.name()))
                        .toList())
                .build();
    }
}
