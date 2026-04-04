package com.evergreen.generalhospital.testsupport.factories;

import com.evergreen.generalhospital.models.useraccount.Role;
import com.evergreen.generalhospital.models.useraccount.UserAccount;
import com.evergreen.generalhospital.repositories.PatientRepository;
import com.evergreen.generalhospital.repositories.PractitionerRepository;
import com.evergreen.generalhospital.repositories.UserAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

@TestComponent
public class UserAccountFactory {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PractitionerRepository practitionerRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private UserAccountRepository userAccountRepository;

    public UserAccount saveUserAccount() {
        return saveUserAccount(
            "John Doe",
            "john.doe",
            null,
            null,
            "subject-" + System.nanoTime(),
            Role.ROLE_ADMIN,
            "john.doe@example.com",
            "strong-password"
        );
    }

    public UserAccount saveUserAccount(String displayName,
                                          String username,
                                          UUID practitionerId,
                                          UUID patientId,
                                          String providerSubject,
                                          Role role,
                                          String email,
                                          String password) {

        return userAccountRepository.save(new UserAccount(
            practitionerId == null ? null : practitionerRepository.findById(practitionerId).orElse(null),
            patientId == null ? null : patientRepository.findById(patientId).orElse(null),
            "local",
            providerSubject,
            role,
            displayName,
            username,
            email,
            passwordEncoder.encode(password)
        ));
    }
}
