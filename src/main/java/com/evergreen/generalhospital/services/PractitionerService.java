package com.evergreen.generalhospital.services;

import com.evergreen.generalhospital.dto.practitioner.PractitionerFreeTimeSlot;
import com.evergreen.generalhospital.dto.practitioner.PractitionerRequest;
import com.evergreen.generalhospital.errors.ImmutableFieldException;
import com.evergreen.generalhospital.errors.RepeatedIdNumberException;
import com.evergreen.generalhospital.errors.ResourceNotFoundException;
import com.evergreen.generalhospital.models.appointment.Appointment;
import com.evergreen.generalhospital.models.practitioner.Practitioner;
import com.evergreen.generalhospital.repositories.AppointmentRepository;
import com.evergreen.generalhospital.repositories.DepartmentRepository;
import com.evergreen.generalhospital.repositories.PractitionerRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
@Transactional
public class PractitionerService {
    private final PractitionerRepository practitionerRepository;
    private final DepartmentRepository departmentRepository;
    private final AppointmentRepository appointmentRepository;

    public PractitionerService(PractitionerRepository practitionerRepository,
                               DepartmentRepository departmentRepository,
                               AppointmentRepository appointmentRepository
    ) {
        this.practitionerRepository = practitionerRepository;
        this.departmentRepository = departmentRepository;
        this.appointmentRepository = appointmentRepository;
    }

    public Practitioner createPractitioner(PractitionerRequest request) {
        // check if idNumber already exists, that can't be repeated
        // person being globally sets two different idNumber columns, which agrees to reality
        // a doctor/practitioner when they are sick, they become patients and someone else attends them
        if (practitionerRepository.existsByIdNumber(request.idNumber())) {
            throw new RepeatedIdNumberException("Practitioner with idNumber " + request.idNumber() + " already exists");
        }

        Practitioner practitioner = new Practitioner(
                request.firstName(),
                request.lastName(),
                request.idNumber(),
                request.dateOfBirth(),
                request.gender(),
                request.phoneNumber(),
                request.contacts());
        practitioner.setSpecialties(request.specialties() == null ? new ArrayList<>() : new ArrayList<>(request.specialties()));
        return practitionerRepository.save(practitioner);
    }

    private Page<Practitioner> getAllPractitioners(Pageable pageable) {
        return practitionerRepository.findAll(pageable);
    }

    private Page<Practitioner> getPractitionersByDepartment(Pageable pageable, UUID departmentId) {
        return practitionerRepository.findByDepartments_DepartmentId(departmentId, pageable);
    }

    private Page<Practitioner> getPractitionersBySpecialty(Pageable pageable, String specialty) {
        String trimmed = specialty.trim();
        if (trimmed.length() > 100)
            throw new IllegalArgumentException("Specialty filter must not exceed 100 characters");

        return practitionerRepository.findBySpecialtiesContaining(trimmed, pageable);
    }

    public Page<Practitioner> getPractitioners(Pageable pageable, UUID departmentId, String specialty) {
        boolean hasDept = departmentId != null;
        boolean hasSpecialty = specialty != null && !specialty.isBlank();

        // logic, fall gracefully if any parameter isnt present
        if (hasDept) {
            if (!departmentRepository.existsById(departmentId)) {
                throw new ResourceNotFoundException("Department not found with id: " + departmentId);
            }
            if (hasSpecialty) {
                String trimmed = specialty.trim();
                if (trimmed.length() > 100) {
                    throw new IllegalArgumentException("Specialty filter must not exceed 100 characters");
                }
                return practitionerRepository.findByDepartmentAndSpecialty(departmentId, trimmed, pageable);
            }
            return getPractitionersByDepartment(pageable, departmentId);
        } else if (hasSpecialty) {
            return getPractitionersBySpecialty(pageable, specialty);
        }
        return getAllPractitioners(pageable);
    }


    public Optional<Practitioner> getPractitionerById(UUID id) {
        return practitionerRepository.findById(id);
    }


    // APPOINTMENT TIME LOGIC
    // define shift start time, end time and appointment duration
    public static final LocalTime DEFAULT_SHIFT_START = LocalTime.of(9, 0);
    public static final LocalTime DEFAULT_SHIFT_END = LocalTime.of(17, 0);
    public static final Duration  DEFAULT_SLOT_DURATION = Duration.ofMinutes(30);

    /**
     * Returns the practitioner availability for a given date using default shift hours (09:00 - 17:00)
     * and 30-minute appointment slots.
     *
     * @param id   practitioner UUID
     * @param date target date
     * @return list of available, discrete time slots
     */
    public List<PractitionerFreeTimeSlot> getPractitionerAvailability(UUID id, LocalDate date) {
        // call the private method
        return getPractitionerAvailability(id, date, DEFAULT_SHIFT_START, DEFAULT_SHIFT_END, DEFAULT_SLOT_DURATION);
    }

    /**
     * Returns the practitioner availability broken into discrete appointment slots for the given shift.
     *
     * @param id           practitioner UUID
     * @param date         target date
     * @param shiftStart   shift start time
     * @param shiftEnd     shift end time
     * @param slotDuration duration of each discrete slot
     * @return list of available discrete time slots
     */
    public List<PractitionerFreeTimeSlot> getPractitionerAvailability(UUID id, LocalDate date,
                                                                      LocalTime shiftStart,
                                                                      LocalTime shiftEnd,
                                                                      Duration slotDuration) {
        // guards
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }
        if (shiftStart == null || shiftEnd == null) {
            throw new IllegalArgumentException("Shift start and end times cannot be null");
        }
        if (!shiftStart.isBefore(shiftEnd)) {
            throw new IllegalArgumentException("Shift start must be before shift end");
        }
        if (slotDuration == null || slotDuration.isZero() || slotDuration.isNegative()) {
            throw new IllegalArgumentException("Slot duration must be a positive duration");
        }

        if (!practitionerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Practitioner not found with id: " + id);
        }

        LocalDateTime shiftStartTime = date.atTime(shiftStart);
        LocalDateTime shiftEndTime = date.atTime(shiftEnd);

        // get all colliding appointments
        List<Appointment> bookings = appointmentRepository.findActiveAppointmentsByPractitionerAndRange(
                id, shiftStartTime, shiftEndTime);

        return calcPractitionerFreeTimeSlots(shiftStartTime, shiftEndTime, slotDuration, bookings);
    }

    /**
     * Calculates colliding time slots by the appointments in the date requested
     *
     * @param shiftStartTime shift start
     * @param shiftEndTime   shift end
     * @param slotDuration   duration of each duration slot
     * @param bookings       appointments in the date requested
     * @return free time slots
     */
    private List<PractitionerFreeTimeSlot> calcPractitionerFreeTimeSlots(
            LocalDateTime shiftStartTime,
            LocalDateTime shiftEndTime,
            Duration slotDuration,
            List<Appointment> bookings)
    {
        // define return variable and traveler
        List<PractitionerFreeTimeSlot> availableSlots = new ArrayList<>();
        LocalDateTime slotStart = shiftStartTime;

        // while the time slot window isnt out of bounds
        while (!slotStart.plus(slotDuration).isAfter(shiftEndTime)) {
            LocalDateTime slotEnd = slotStart.plus(slotDuration);
            // if time range doesn't have colliding bookings, add a free time slot
            if (!isSlotColliding(slotStart, slotEnd, bookings)) {
                availableSlots.add(new PractitionerFreeTimeSlot(slotStart, slotEnd));
            }
            slotStart = slotEnd;
        }

        return availableSlots;
    }

    /**
     * Calculates if there are bookings in a certain time window
     *
     * @param slotStart time range start
     * @param slotEnd   time range end
     * @param bookings  appointments in the date requested
     * @return true if appointments do collide, false if free time range
     */
    private boolean isSlotColliding(LocalDateTime slotStart, LocalDateTime slotEnd, List<Appointment> bookings) {
        for (Appointment booking : bookings) {
            if (slotStart.isBefore(booking.getEnd()) && slotEnd.isAfter(booking.getStart())) {
                return true;
            }
        }
        return false;
    }


    public Optional<Practitioner> updatePractitioner(UUID id, PractitionerRequest request) {
        return practitionerRepository.findById(id)
                .map(practitioner -> {
                    if (!practitioner.getIdNumber().equals(request.idNumber())) {
                        throw new ImmutableFieldException("Practitioner idNumber cannot be changed");
                    }
                    practitioner.setFirstName(request.firstName());
                    practitioner.setLastName(request.lastName());
                    practitioner.setDateOfBirth(request.dateOfBirth());
                    practitioner.setGender(request.gender());
                    practitioner.setPhoneNumber(request.phoneNumber());
                    practitioner.setContacts(request.contacts());
                    practitioner.setSpecialties(
                            request.specialties() == null ? new ArrayList<>() : new ArrayList<>(request.specialties()));
                    return practitionerRepository.save(practitioner);
                });
    }

    public Optional<Practitioner> deletePractitioner(UUID id) {
        return practitionerRepository.findById(id)
                .map(practitioner -> {
                    practitionerRepository.delete(practitioner);
                    return practitioner;
                });
    }
}
