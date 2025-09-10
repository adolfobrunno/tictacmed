package com.abba.tictacmed.infrastructure.web;

import com.abba.tictacmed.application.scheduling.command.CreateMedicationScheduleCommand;
import com.abba.tictacmed.application.scheduling.command.CreateMedicationScheduleResult;
import com.abba.tictacmed.application.scheduling.service.CreateMedicationScheduleUseCase;
import com.abba.tictacmed.application.scheduling.service.GetNextSchedulesUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

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

    public record CreateScheduleRequest(UUID patientId, String medicineName, ZonedDateTime startAt, ZonedDateTime endAt,
                                        String frequency) {
    }

    @PostMapping
    public ResponseEntity<CreateMedicationScheduleResult> create(@RequestBody CreateScheduleRequest request) {
        long frequencySeconds = parseFriendlyDurationToSeconds(request.frequency());
        CreateMedicationScheduleResult result = createMedicationScheduleUseCase.execute(
                new CreateMedicationScheduleCommand(
                        request.patientId(),
                        request.medicineName(),
                        request.startAt(),
                        request.endAt(),
                        frequencySeconds
                )
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    private long parseFriendlyDurationToSeconds(String text) {
        if (text == null || text.isBlank()) throw new IllegalArgumentException("frequency is required");
        String s = text.trim().toLowerCase();
        // Backward compatibility: if it is purely digits, treat as seconds
        if (s.chars().allMatch(Character::isDigit)) {
            return Long.parseLong(s);
        }
        long total = 0;
        int i = 0;
        int n = s.length();
        while (i < n) {
            // read number
            int j = i;
            while (j < n && Character.isDigit(s.charAt(j))) j++;
            if (j == i) throw new IllegalArgumentException("Invalid duration segment at position " + i + ": " + s);
            long value = Long.parseLong(s.substring(i, j));
            if (j >= n) throw new IllegalArgumentException("Missing unit after number in duration: " + s);
            char unit = s.charAt(j);
            long factor = switch (unit) {
                case 's' -> 1;
                case 'm' -> 60;
                case 'h' -> 3600;
                case 'd' -> 86400;
                default -> throw new IllegalArgumentException("Unsupported duration unit '" + unit + "' in: " + s);
            };
            total += value * factor;
            j++;
            i = j;
        }
        if (total <= 0) throw new IllegalArgumentException("frequency must be positive");
        return total;
    }

    @GetMapping("/next")
    public ResponseEntity<List<GetNextSchedulesUseCase.NextScheduleDto>> next(
            @RequestParam("patientId") UUID patientId,
            @RequestParam(value = "from", required = false) ZonedDateTime from,
            @RequestParam(value = "to", required = false) ZonedDateTime to) {
        List<GetNextSchedulesUseCase.NextScheduleDto> list = getNextSchedulesUseCase.execute(patientId, from, to);
        return ResponseEntity.ok(list);
    }
}
