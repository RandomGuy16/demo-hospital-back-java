package com.evergreen.generalhospital.services;

import com.evergreen.generalhospital.dto.patient.PatientPatchRequest;
import com.evergreen.generalhospital.dto.patient.PatientRequest;
import com.evergreen.generalhospital.errors.ImmutableFieldException;
import com.evergreen.generalhospital.errors.PatientIdentityMismatchException;
import com.evergreen.generalhospital.errors.RepeatedIdNumberException;
import com.evergreen.generalhospital.models.patient.Patient;
import com.evergreen.generalhospital.models.useraccount.UserAccount;
import com.evergreen.generalhospital.repositories.PatientRepository;
import com.evergreen.generalhospital.repositories.UserAccountRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class PatientService {
    private final PatientRepository patientRepository;
    private final UserAccountRepository userAccountRepository;

    // once I had a problem because LSP didn't find the "bean" of PatientRepository,
    // it meant that spring didnt recognize it, that's why we use decorators
    /*
     * @Component - Generic bean
     * 
     * @Service - Business logic layer (like your services)
     * 
     * @Repository - Data access layer (your repositories)
     * 
     * @Controller / @RestController - Web layer (your API controllers)
     * 
     * @Configuration - Configuration classes
     */

    public PatientService(PatientRepository patientRepository, UserAccountRepository userAccountRepository) {
        this.patientRepository = patientRepository;
        this.userAccountRepository = userAccountRepository;
    }

    private String generateMrn(String idNumber) {
        String random = UUID.randomUUID().toString().replace(" ", "").substring(0, 8).toUpperCase();
        return "ShokoIeiri-" + idNumber + random;
    }

    public Patient createPatient(PatientRequest patient) {
        // first validation: check unique idNumber
        if (patientRepository.existsByIdNumber(patient.idNumber())) {
            throw new RepeatedIdNumberException("Patient with idNumber " + patient.idNumber() + " already exists");
        }

        Patient newPatient = new Patient(
                patient.firstName(),
                patient.lastName(),
                patient.idNumber(),
                patient.dateOfBirth(),
                patient.gender(),
                patient.phoneNumber(),
                patient.contacts(),
                generateMrn(patient.idNumber()),
                patient.address());
        return patientRepository.save(newPatient);
    }

    /**
     * Resolves the patient shell created by a guest booking, creating one when no
     * patient exists for the national idNumber yet.
     *
     * <p>Guest patients only carry identity and contact details; full demographics
     * are filled in later when the person registers.</p>
     *
     * @param idNumber   national idNumber identifying the guest.
     * @param phoneNumber guest contact phone number.
     * @param contacts    guest contact reference (email).
     * @param address     booking location, used as the patient address.
     * @return the existing or newly created guest patient.
     */
    public Patient findOrCreateGuestPatient(String idNumber, String phoneNumber, String contacts, String address) {
        return patientRepository.findByIdNumber(idNumber)
                .orElseGet(() -> patientRepository.save(new Patient(
                        null,
                        null,
                        idNumber,
                        null,
                        null,
                        phoneNumber,
                        contacts,
                        generateMrn(idNumber),
                        address)));
    }

    /**
     * Finds a patient by national idNumber and fills in the demographics provided
     * during self-service registration, creating the patient when none exists.
     *
     * <p>When a patient already exists under the idNumber, the provided name and
     * date of birth are verified against the stored values (anti-hijacking): a
     * mismatch is rejected so nobody can claim a stranger's identity. A shell
     * patient created by a guest booking has no demographics yet, so it is simply
     * completed.</p>
     *
     * @param idNumber    national idNumber being registered.
     * @param firstName   registered first name.
     * @param lastName    registered last name.
     * @param dateOfBirth registered date of birth.
     * @param gender      registered gender.
     * @param phoneNumber registered phone number.
     * @param contacts    registered contact reference (emergency contact).
     * @param address     registered address.
     * @return the existing or newly created patient.
     * @throws PatientIdentityMismatchException when an existing patient under the
     *                                          idNumber has different demographics.
     */
    public Patient resolveOrCreateByIdNumber(String idNumber,
                                             String firstName,
                                             String lastName,
                                             LocalDate dateOfBirth,
                                             String gender,
                                             String phoneNumber,
                                             String contacts,
                                             String address) {
        return patientRepository.findByIdNumber(idNumber)
                .map(existing -> {
                    boolean identityPresent = existing.getFirstName() != null
                            && existing.getLastName() != null
                            && existing.getDateOfBirth() != null;

                    if (identityPresent
                            && (!existing.getFirstName().equalsIgnoreCase(firstName)
                                || !existing.getLastName().equalsIgnoreCase(lastName)
                                || !existing.getDateOfBirth().equals(dateOfBirth))) {
                        throw new PatientIdentityMismatchException(
                                "Patient identity for idNumber " + idNumber + " does not match the provided information");
                    }

                    if (existing.getFirstName() == null) existing.setFirstName(firstName);
                    if (existing.getLastName() == null) existing.setLastName(lastName);
                    if (existing.getDateOfBirth() == null) existing.setDateOfBirth(dateOfBirth);
                    existing.setGender(gender);
                    existing.setPhoneNumber(phoneNumber);
                    existing.setContacts(contacts);
                    existing.setAddress(address);
                    return patientRepository.save(existing);
                })
                .orElseGet(() -> patientRepository.save(new Patient(
                        firstName,
                        lastName,
                        idNumber,
                        dateOfBirth,
                        gender,
                        phoneNumber,
                        contacts,
                        generateMrn(idNumber),
                        address)));
    }

    public Optional<Patient> getPatientById(UUID id) {
        return patientRepository.findById(id);
    }

    public Optional<Patient> getPatientByMRN(String mrn) {
        return patientRepository.findByMrn(mrn);
    }

    public Optional<Patient> getPatientByEmail(String email) {
        return userAccountRepository.findByEmail(email)
                .map(UserAccount::getPatient);
    }

    public Optional<Patient> updatePatient(UUID id, PatientRequest pRequest) {

        return patientRepository.findById(id)
                .map(p -> {
                    if (!p.getIdNumber().equals(pRequest.idNumber())) {
                        throw new ImmutableFieldException("Patient idNumber cannot be changed");
                    }
                    p.setFirstName(pRequest.firstName());
                    p.setLastName(pRequest.lastName());
                    p.setDateOfBirth(pRequest.dateOfBirth());
                    p.setGender(pRequest.gender());
                    p.setPhoneNumber(pRequest.phoneNumber());
                    p.setContacts(pRequest.contacts());
                    p.setAddress(pRequest.address());
                    return patientRepository.save(p);
                });
    }

    public Optional<Patient> patchPatient(UUID id, PatientPatchRequest pRequest) {
        return patientRepository.findById(id)
            .map(p -> {
                if (pRequest.idNumber() != null && !p.getIdNumber().equals(pRequest.idNumber())) {
                    throw new ImmutableFieldException("Patient idNumber cannot be changed");
                }
                if (pRequest.firstName() != null) p.setFirstName(pRequest.firstName());

                if (pRequest.lastName() != null) p.setLastName(pRequest.lastName());

                if (pRequest.dateOfBirth() != null) p.setDateOfBirth(pRequest.dateOfBirth());

                if (pRequest.gender() != null) p.setGender(pRequest.gender());

                if (pRequest.phoneNumber() != null) p.setPhoneNumber(pRequest.phoneNumber());

                if (pRequest.contacts() != null) p.setContacts(pRequest.contacts());

                if (pRequest.address() != null) p.setAddress(pRequest.address());

                return patientRepository.save(p);
            });
    }

    // In order to implement pagination, at least at a basic level
    // we need to use the Pageable interface from springboot
    // we return a Page<Patient> instead of a List<Patient>
    // this is because the pageable interface returns a page of objects
    // and we pass a Pageable object to the repository method
    // to get the page of objects
    // theres no need to add something to the repository, since it inherits from
    // JpaRepository

    public Page<Patient> getAllPatients(Pageable pageable) {
        return patientRepository.findAll(pageable);
    }

    public Optional<Patient> deletePatientById(UUID id) {
        return patientRepository.findById(id)
                .map(patient -> {
                    patientRepository.delete(patient);
                    return patient;
                });
    }
}
