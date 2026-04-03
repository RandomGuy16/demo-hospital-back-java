package com.example.demo.testsupport.factories;

import com.example.demo.models.useraccount.Role;
import com.example.demo.models.useraccount.UserAccount;
import com.example.demo.repositories.PatientRepository;
import com.example.demo.repositories.PractitionerRepository;
import com.example.demo.repositories.UserAccountRepository;
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
