package com.example.demo.repositories;

import com.example.demo.models.Practitioner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PractitionerRepository extends JpaRepository<Practitioner, UUID> {
    @Override
    Optional<Practitioner> findById(UUID uuid);
}
