package com.abba.tictacmed.infrastructure.persistence;

import com.abba.tictacmed.TestcontainersConfiguration;
import com.abba.tictacmed.domain.patient.model.Patient;
import com.abba.tictacmed.domain.patient.repository.PatientRepository;
import com.abba.tictacmed.domain.scheduling.model.MedicationSchedule;
import com.abba.tictacmed.domain.scheduling.repository.MedicationScheduleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
@DirtiesContext
class PersistenceIntegrationTest {

    @Autowired
    PatientRepository patientRepository;
    @Autowired
    MedicationScheduleRepository scheduleRepository;

    @Test
    void should_persist_and_load_patient_and_schedule() {
        Patient patient = Patient.register("Maria Silva", "558299990000");
        patient = patientRepository.save(patient);
        assertNotNull(patient.getId());

        ZonedDateTime start = ZonedDateTime.of(2025, 9, 9, 8, 0, 0, 0, ZoneId.of("UTC"));
        ZonedDateTime end = start.plusDays(1);
        MedicationSchedule schedule = MedicationSchedule.create(patient, "Dipyrone", start, end, Duration.ofHours(8));
        // confirm one dose
        schedule.confirmAdministration(start, start.plusMinutes(10));

        schedule = scheduleRepository.save(schedule);

        var loadedOpt = scheduleRepository.findById(schedule.getId());
        assertTrue(loadedOpt.isPresent());
        var loaded = loadedOpt.get();
        assertEquals(schedule.getId(), loaded.getId());
        assertEquals(patient.getId(), loaded.getPatient().getId());
        assertEquals(1, loaded.getAdministrations().size());
        assertTrue(loaded.isAdministrationConfirmedAt(start));

        var byPatient = scheduleRepository.findByPatientId(patient.getId());
        assertFalse(byPatient.isEmpty());
        assertEquals(schedule.getId(), byPatient.getFirst().getId());
    }
}
