package com.evergreen.generalhospital.repositories;

import com.evergreen.generalhospital.models.practitioner.Practitioner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PractitionerRepository extends JpaRepository<Practitioner, UUID> {
    @Override
    Optional<Practitioner> findById(UUID uuid);

    boolean existsByIdNumber(@NonNull String idNumber);
}
