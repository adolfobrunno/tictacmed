package com.abba.tictacmed.infrastructure.web;

import com.abba.tictacmed.application.patient.command.RegisterPatientResult;
import com.abba.tictacmed.application.patient.service.RegisterPatientUseCase;
import com.abba.tictacmed.application.scheduling.command.CreateMedicationScheduleResult;
import com.abba.tictacmed.application.scheduling.service.CreateMedicationScheduleUseCase;
import com.abba.tictacmed.application.scheduling.service.GetNextSchedulesUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.ZonedDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {PatientController.class, SchedulingController.class})
class ApiIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    RegisterPatientUseCase registerPatientUseCase;

    @MockitoBean
    CreateMedicationScheduleUseCase createMedicationScheduleUseCase;

    @MockitoBean
    GetNextSchedulesUseCase getNextSchedulesUseCase;

    @Test
    void registerPatient_and_createSchedule_via_api() throws Exception {
        // Arrange stubs
        var generatedPatientId = UUID.randomUUID();
        var patientResult = new RegisterPatientResult(generatedPatientId, "John Doe", "+123456789");
        java.time.Duration every10m = java.time.Duration.ofMinutes(10);
        var start = ZonedDateTime.now().plusMinutes(1);
        var end = start.plusHours(2);
        var generatedScheduleId = UUID.randomUUID();
        var scheduleResult = new CreateMedicationScheduleResult(
                generatedScheduleId,
                generatedPatientId,
                "Ibuprofen",
                start,
                end,
                every10m
        );

        when(registerPatientUseCase.execute(any()))
                .thenReturn(patientResult);
        when(createMedicationScheduleUseCase.execute(any()))
                .thenReturn(scheduleResult);

        // Register patient
        var registerPayload = new PatientController.RegisterPatientRequest("John Doe", "+123456789");
        var registerResponse = mockMvc.perform(post("/api/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerPayload)))
                .andExpect(status().isCreated())
                .andReturn();

        RegisterPatientResult patient = objectMapper.readValue(registerResponse.getResponse().getContentAsString(), RegisterPatientResult.class);
        assertThat(patient.id()).isEqualTo(generatedPatientId);

        // Create schedule
        var schedulePayload = new SchedulingController.CreateScheduleRequest(
                patient.id(),
                "Ibuprofen",
                start,
                end,
                "10m"
        );

        var scheduleResponse = mockMvc.perform(post("/api/schedules")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(schedulePayload)))
                .andExpect(status().isCreated())
                .andReturn();

        CreateMedicationScheduleResult schedule = objectMapper.readValue(scheduleResponse.getResponse().getContentAsString(), CreateMedicationScheduleResult.class);
        assertThat(schedule.id()).isEqualTo(generatedScheduleId);
        assertThat(schedule.patientId()).isEqualTo(patient.id());
        assertThat(schedule.medicineName()).isEqualTo("Ibuprofen");
    }
}
