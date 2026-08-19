package com.evergreen.generalhospital.repositories;

import com.evergreen.generalhospital.models.patient.Patient;
import com.evergreen.generalhospital.models.useraccount.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import java.util.Optional;

@Repository
public interface UserAccountRepository extends JpaRepository<UserAccount, UUID> {

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByProviderAndProviderSubject(String provider, String providerSubject);
    boolean existsByPatient(Patient patient);
    Optional<UserAccount> findByEmail(String email);
    Optional<UserAccount> findByProviderAndProviderSubject(String provider, String providerSubject);
}
