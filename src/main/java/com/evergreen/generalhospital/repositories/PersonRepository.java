package com.evergreen.generalhospital.repositories;

import com.evergreen.generalhospital.models.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PersonRepository extends JpaRepository<Person, UUID> {
    Optional<Person> findByIdNumber(String idNumber);

    boolean existsByIdNumber(@NonNull String idNumber);
}
