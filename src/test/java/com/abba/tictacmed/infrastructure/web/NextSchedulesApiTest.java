package com.abba.tictacmed.infrastructure.web;

import com.abba.tictacmed.application.scheduling.command.NextSchedulesResult;
import com.abba.tictacmed.application.scheduling.usecases.CreateMedicationScheduleUseCase;
import com.abba.tictacmed.application.scheduling.usecases.GetNextSchedulesUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {SchedulingController.class})
class NextSchedulesApiTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    CreateMedicationScheduleUseCase createMedicationScheduleUseCase;
    @MockitoBean
    GetNextSchedulesUseCase getNextSchedulesUseCase;

    @Test
    void get_next_schedules_by_patient() throws Exception {
        UUID patientId = UUID.randomUUID();
        OffsetDateTime now = OffsetDateTime.now();
        var dto = new NextSchedulesResult(UUID.randomUUID(), patientId, "Ibuprofen", now.plusMinutes(5));
        when(getNextSchedulesUseCase.execute(any(), any(), any())).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/schedules/next")
                        .param("patientId", patientId.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].patientId").value(patientId.toString()))
                .andExpect(jsonPath("$[0].medicineName").value("Ibuprofen"));
    }
}
