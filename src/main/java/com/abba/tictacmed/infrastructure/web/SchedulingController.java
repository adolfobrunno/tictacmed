package com.abba.tictacmed.infrastructure.web;

import com.abba.tictacmed.application.scheduling.command.CreateMedicationScheduleCommand;
import com.abba.tictacmed.application.scheduling.command.CreateMedicationScheduleResult;
import com.abba.tictacmed.application.scheduling.command.NextSchedulesResult;
import com.abba.tictacmed.application.scheduling.usecases.CreateMedicationScheduleUseCase;
import com.abba.tictacmed.application.scheduling.usecases.GetNextSchedulesUseCase;
import com.abba.tictacmed.domain.scheduling.model.MedicationSchedule;
import com.abba.tictacmed.domain.scheduling.repository.MedicationScheduleRepository;
import com.abba.tictacmed.domain.scheduling.service.SchedulingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static com.abba.tictacmed.infrastructure.utils.Durations.parseFriendlyDurationToSeconds;

@RestController
@RequestMapping("/api/schedules")
public class SchedulingController {

    private final CreateMedicationScheduleUseCase createMedicationScheduleUseCase;
    private final GetNextSchedulesUseCase getNextSchedulesUseCase;
    private final SchedulingService schedulingService;
    private final MedicationScheduleRepository scheduleRepository;

    public SchedulingController(CreateMedicationScheduleUseCase createMedicationScheduleUseCase,
                                GetNextSchedulesUseCase getNextSchedulesUseCase,
                                SchedulingService schedulingService,
                                MedicationScheduleRepository scheduleRepository) {
        this.createMedicationScheduleUseCase = createMedicationScheduleUseCase;
        this.getNextSchedulesUseCase = getNextSchedulesUseCase;
        this.schedulingService = schedulingService;
        this.scheduleRepository = scheduleRepository;
    }

    public record CreateScheduleRequest(String patientId, String medicineName, OffsetDateTime startAt,
                                        OffsetDateTime endAt,
                                        String frequency, boolean recurring) {
    }

    public record NotifyResponse(UUID scheduleId, OffsetDateTime scheduledAt) {
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

    @PostMapping("/{id}/notify")
    public ResponseEntity<?> notifySchedule(@PathVariable("id") UUID id) {
        Optional<MedicationSchedule> opt = scheduleRepository.findById(id);
        if (opt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        MedicationSchedule schedule = opt.get();
        OffsetDateTime notifiedAt = schedulingService.notifyNextDue(schedule, OffsetDateTime.now());
        if (notifiedAt == null) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.ok(new NotifyResponse(schedule.getId(), notifiedAt));
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
