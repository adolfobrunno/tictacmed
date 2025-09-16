package com.abba.tictacmed.domain.scheduling.model;

import com.abba.tictacmed.domain.patient.model.Patient;
import lombok.Getter;

import java.time.ZonedDateTime;

@Getter
public class MedicationAdministrationEvent {

    private Patient patient;
    private MedicationSchedule schedule;
    private ZonedDateTime dateTime;
    private AdministrationStatus status;

    public MedicationAdministrationEvent(Patient patient, MedicationSchedule schedule, ZonedDateTime dateTime, AdministrationStatus status) {
        this.patient = patient;
        this.schedule = schedule;
        this.dateTime = dateTime;
        this.status = status;
    }


}
