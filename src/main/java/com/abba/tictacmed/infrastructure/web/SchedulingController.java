package com.abba.tictacmed.infrastructure.web;

import com.abba.tictacmed.application.scheduling.command.CreateMedicationScheduleCommand;
import com.abba.tictacmed.application.scheduling.command.CreateMedicationScheduleResult;
import com.abba.tictacmed.application.scheduling.command.NextSchedulesResult;
import com.abba.tictacmed.application.scheduling.usecases.CreateMedicationScheduleUseCase;
import com.abba.tictacmed.application.scheduling.usecases.GetNextSchedulesUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.OffsetDateTime;

import static com.abba.tictacmed.infrastructure.utils.Durations.parseFriendlyDurationToSeconds;

@RestController
@RequestMapping("/api/schedules")
public class SchedulingController {

    private final CreateMedicationScheduleUseCase createMedicationScheduleUseCase;
    private final GetNextSchedulesUseCase getNextSchedulesUseCase;

    public SchedulingController(CreateMedicationScheduleUseCase createMedicationScheduleUseCase,
                                GetNextSchedulesUseCase getNextSchedulesUseCase) {
        this.createMedicationScheduleUseCase = createMedicationScheduleUseCase;
        this.getNextSchedulesUseCase = getNextSchedulesUseCase;
    }

    public record CreateScheduleRequest(String patientId, String medicineName, OffsetDateTime startAt,
                                        OffsetDateTime endAt,
                                        String frequency, boolean recurring) {
    }

    @PostMapping
    public ResponseEntity<CreateMedicationScheduleResult> create(@RequestBody CreateScheduleRequest request) {
        Duration frequency = parseFriendlyDurationToSeconds(request.frequency());
        CreateMedicationScheduleResult result = createMedicationScheduleUseCase.execute(
                new CreateMedicationScheduleCommand(
                        request.patientId(),
                        request.medicineName(),
                        request.startAt(),
                        request.endAt(),
                        frequency,
                        request.recurring()
                )
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @GetMapping("/next")
    public ResponseEntity<NextSchedulesResult> next(
            @RequestParam("patientId") String patientId,
            @RequestParam(value = "from", required = false) OffsetDateTime from,
            @RequestParam(value = "to", required = false) OffsetDateTime to) {
        NextSchedulesResult list = getNextSchedulesUseCase.execute(patientId, from, to);
        return ResponseEntity.ok(list);
    }
}
