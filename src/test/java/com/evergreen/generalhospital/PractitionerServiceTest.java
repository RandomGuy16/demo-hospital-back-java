package com.evergreen.generalhospital;

import com.evergreen.generalhospital.config.HospitalProperties;
import com.evergreen.generalhospital.dto.practitioner.PractitionerFreeTimeSlot;
import com.evergreen.generalhospital.errors.ResourceNotFoundException;
import com.evergreen.generalhospital.models.appointment.Appointment;
import com.evergreen.generalhospital.models.appointment.AppointmentStatus;
import com.evergreen.generalhospital.models.appointment.UrgencyLevel;
import com.evergreen.generalhospital.models.department.Department;
import com.evergreen.generalhospital.models.patient.Patient;
import com.evergreen.generalhospital.models.practitioner.Practitioner;
import com.evergreen.generalhospital.repositories.AppointmentRepository;
import com.evergreen.generalhospital.repositories.DepartmentRepository;
import com.evergreen.generalhospital.repositories.PractitionerRepository;
import com.evergreen.generalhospital.services.PractitionerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PractitionerServiceTest {

    @Mock
    private PractitionerRepository practitionerRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private AppointmentRepository appointmentRepository;

    // instead of injecting everything we build it ourselves
    private PractitionerService practitionerService;

    private UUID practitionerId;
    private LocalDate testDate;

    @BeforeEach
    void setUp() {
        // we build it ourselves
        HospitalProperties hospitalProperties = new HospitalProperties();
        hospitalProperties.getScheduling().setDefaultShiftStart(LocalTime.of(9, 0));
        hospitalProperties.getScheduling().setDefaultShiftEnd(LocalTime.of(17, 0));
        hospitalProperties.getScheduling().setDefaultSlotDuration(Duration.ofMinutes(30));

        practitionerService = new PractitionerService(
                practitionerRepository,
                departmentRepository,
                appointmentRepository,
                hospitalProperties
        );

        practitionerId = UUID.randomUUID();
        testDate = LocalDate.of(2026, 9, 1);
    }

    private Appointment createMockAppointment(LocalDateTime start, LocalDateTime end, AppointmentStatus status) {
        return new Appointment(
                new Patient(),
                new Practitioner(),
                new Department(),
                start,
                end,
                status,
                "Routine checkup",
                UrgencyLevel.ROUTINE,
                null
        );
    }

    @Test
    void getPractitionerAvailability_nullId_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> practitionerService.getPractitionerAvailability(null, testDate))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Id cannot be null");
    }

    @Test
    void getPractitionerAvailability_nullDate_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> practitionerService.getPractitionerAvailability(practitionerId, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Date cannot be null");
    }

    @Test
    void getPractitionerAvailability_invalidShiftOrDuration_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> practitionerService.getPractitionerAvailability(
                practitionerId, testDate, LocalTime.of(17, 0), LocalTime.of(9, 0), Duration.ofMinutes(30)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Shift start must be before shift end");

        assertThatThrownBy(() -> practitionerService.getPractitionerAvailability(
                practitionerId, testDate, LocalTime.of(9, 0), LocalTime.of(17, 0), Duration.ZERO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Slot duration must be a positive duration");
    }

    @Test
    void getPractitionerAvailability_practitionerNotFound_throwsResourceNotFoundException() {
        when(practitionerRepository.existsById(practitionerId)).thenReturn(false);

        assertThatThrownBy(() -> practitionerService.getPractitionerAvailability(practitionerId, testDate))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Practitioner not found");
    }

    @Test
    void getPractitionerAvailability_noAppointments_returnsAllDiscreteSlots() {
        when(practitionerRepository.existsById(practitionerId)).thenReturn(true);
        when(appointmentRepository.findActiveAppointmentsByPractitionerAndRange(
                eq(practitionerId),
                eq(testDate.atTime(9, 0)),
                eq(testDate.atTime(17, 0))))
                .thenReturn(Collections.emptyList());

        List<PractitionerFreeTimeSlot> slots = practitionerService.getPractitionerAvailability(practitionerId, testDate);

        // 09:00 to 17:00 (8 hours) / 30 mins = 16 slots
        assertThat(slots).hasSize(16);
        assertThat(slots.get(0)).isEqualTo(new PractitionerFreeTimeSlot(
                testDate.atTime(9, 0),
                testDate.atTime(9, 30)));
        assertThat(slots.get(15)).isEqualTo(new PractitionerFreeTimeSlot(
                testDate.atTime(16, 30),
                testDate.atTime(17, 0)));
    }

    @Test
    void getPractitionerAvailability_withBookings_omitsCollidingSlots() {
        when(practitionerRepository.existsById(practitionerId)).thenReturn(true);

        // Existing booking from 09:30 to 10:00
        Appointment booking = createMockAppointment(
                testDate.atTime(9, 30),
                testDate.atTime(10, 0),
                AppointmentStatus.SCHEDULED);

        when(appointmentRepository.findActiveAppointmentsByPractitionerAndRange(
                eq(practitionerId), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(booking));

        List<PractitionerFreeTimeSlot> slots = practitionerService.getPractitionerAvailability(practitionerId, testDate);

        // 16 total - 1 booked slot = 15 slots
        assertThat(slots).hasSize(15);
        // First slot (09:00 - 09:30) is present
        assertThat(slots.get(0)).isEqualTo(new PractitionerFreeTimeSlot(
                testDate.atTime(9, 0),
                testDate.atTime(9, 30)));
        // Next available slot is 10:00 - 10:30 (09:30 - 10:00 was excluded)
        assertThat(slots.get(1)).isEqualTo(new PractitionerFreeTimeSlot(
                testDate.atTime(10, 0),
                testDate.atTime(10, 30)));
    }

    @Test
    void getPractitionerAvailability_multiSlotBooking_omitsAllOverlappingSlots() {
        when(practitionerRepository.existsById(practitionerId)).thenReturn(true);

        // 90 minute booking from 10:00 to 11:30
        Appointment booking = createMockAppointment(
                testDate.atTime(10, 0),
                testDate.atTime(11, 30),
                AppointmentStatus.SCHEDULED);

        when(appointmentRepository.findActiveAppointmentsByPractitionerAndRange(
                eq(practitionerId), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(booking));

        List<PractitionerFreeTimeSlot> slots = practitionerService.getPractitionerAvailability(practitionerId, testDate);

        // 16 - 3 = 13 slots
        assertThat(slots).hasSize(13);
        assertThat(slots).noneMatch(s ->
                s.start().equals(testDate.atTime(10, 0)) ||
                s.start().equals(testDate.atTime(10, 30)) ||
                s.start().equals(testDate.atTime(11, 0)));
    }

    @Test
    void getPractitionerAvailability_partialOverlapBooking_omitsBothSpannedSlots() {
        when(practitionerRepository.existsById(practitionerId)).thenReturn(true);

        // Booking from 13:15 to 13:45 (overlaps 13:00-13:30 and 13:30-14:00)
        Appointment booking = createMockAppointment(
                testDate.atTime(13, 15),
                testDate.atTime(13, 45),
                AppointmentStatus.SCHEDULED);

        when(appointmentRepository.findActiveAppointmentsByPractitionerAndRange(
                eq(practitionerId), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(booking));

        List<PractitionerFreeTimeSlot> slots = practitionerService.getPractitionerAvailability(practitionerId, testDate);

        assertThat(slots).noneMatch(s -> s.start().equals(testDate.atTime(13, 0)));
        assertThat(slots).noneMatch(s -> s.start().equals(testDate.atTime(13, 30)));
        assertThat(slots).anyMatch(s -> s.start().equals(testDate.atTime(12, 30)));
        assertThat(slots).anyMatch(s -> s.start().equals(testDate.atTime(14, 0)));
    }

    @Test
    void getPractitionerAvailability_fullyBookedDay_returnsEmptyList() {
        when(practitionerRepository.existsById(practitionerId)).thenReturn(true);

        // Full day booking from 09:00 to 17:00
        Appointment booking = createMockAppointment(
                testDate.atTime(9, 0),
                testDate.atTime(17, 0),
                AppointmentStatus.SCHEDULED);

        when(appointmentRepository.findActiveAppointmentsByPractitionerAndRange(
                eq(practitionerId), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(booking));

        List<PractitionerFreeTimeSlot> slots = practitionerService.getPractitionerAvailability(practitionerId, testDate);

        assertThat(slots).isEmpty();
    }

    @Test
    void getPractitionerAvailability_customShiftAndDuration() {
        when(practitionerRepository.existsById(practitionerId)).thenReturn(true);

        LocalTime shiftStart = LocalTime.of(8, 0);
        LocalTime shiftEnd = LocalTime.of(12, 0);
        Duration duration = Duration.ofMinutes(60);

        when(appointmentRepository.findActiveAppointmentsByPractitionerAndRange(
                eq(practitionerId),
                eq(testDate.atTime(shiftStart)),
                eq(testDate.atTime(shiftEnd))))
                .thenReturn(Collections.emptyList());

        List<PractitionerFreeTimeSlot> slots = practitionerService.getPractitionerAvailability(
                practitionerId, testDate, shiftStart, shiftEnd, duration);

        // 4 hours with 60 min slots = 4 slots
        assertThat(slots).hasSize(4);
        assertThat(slots.get(0).start()).isEqualTo(testDate.atTime(8, 0));
        assertThat(slots.get(0).end()).isEqualTo(testDate.atTime(9, 0));
        assertThat(slots.get(3).start()).isEqualTo(testDate.atTime(11, 0));
        assertThat(slots.get(3).end()).isEqualTo(testDate.atTime(12, 0));
    }
}
