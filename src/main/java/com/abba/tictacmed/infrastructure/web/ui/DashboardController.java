package com.abba.tictacmed.infrastructure.web.ui;

import com.abba.tictacmed.application.patient.command.ConfirmPatientCommand;
import com.abba.tictacmed.application.patient.command.RegisterPatientCommand;
import com.abba.tictacmed.application.patient.usecases.ConfirmPatientUseCase;
import com.abba.tictacmed.application.patient.usecases.RegisterPatientUseCase;
import com.abba.tictacmed.application.scheduling.command.CreateMedicationScheduleCommand;
import com.abba.tictacmed.application.scheduling.usecases.CreateMedicationScheduleUseCase;
import com.abba.tictacmed.infrastructure.utils.Durations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;

@Controller
@RequestMapping("/ui")
public class DashboardController {

    private final RegisterPatientUseCase registerPatientUseCase;
    private final ConfirmPatientUseCase confirmPatientUseCase;
    private final CreateMedicationScheduleUseCase createMedicationScheduleUseCase;

    public DashboardController(RegisterPatientUseCase registerPatientUseCase,
                               ConfirmPatientUseCase confirmPatientUseCase,
                               CreateMedicationScheduleUseCase createMedicationScheduleUseCase) {
        this.registerPatientUseCase = registerPatientUseCase;
        this.confirmPatientUseCase = confirmPatientUseCase;
        this.createMedicationScheduleUseCase = createMedicationScheduleUseCase;
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }

    @PostMapping("/patients")
    public String registerPatient(@RequestParam("name") String name,
                                  @RequestParam("contact") String contact,
                                  Model model) {
        registerPatientUseCase.execute(new RegisterPatientCommand(name, contact, false));
        model.addAttribute("patientMsg", "Paciente cadastrado. Código enviado por WhatsApp.");
        model.addAttribute("codeSent", true);
        model.addAttribute("contactPrefill", contact);
        return "dashboard";
    }

    @PostMapping("/patients/confirm")
    public String confirmPatient(@RequestParam("contact") String contact,
                                 @RequestParam("code") String code,
                                 Model model) {
        try {
            confirmPatientUseCase.execute(new ConfirmPatientCommand(contact, code));
            model.addAttribute("patientMsg", "Cadastro confirmado com sucesso. Paciente ativo.");
        } catch (IllegalArgumentException ex) {
            model.addAttribute("patientMsg", "Falha ao confirmar: " + ex.getMessage());
            model.addAttribute("codeSent", true);
            model.addAttribute("contactPrefill", contact);
        }
        return "dashboard";
    }

    @PostMapping("/schedules")
    public String createSchedule(@RequestParam("patientContact") String patientContact,
                                 @RequestParam("medicineName") String medicineName,
                                 @RequestParam("startAt") String startAtStr,
                                 @RequestParam("days") int days,
                                 @RequestParam("frequency") String frequencyLabel,
                                 Model model) {
        LocalDateTime ldt = LocalDateTime.parse(startAtStr);
        OffsetDateTime startAt = ldt.atZone(ZoneId.systemDefault()).toOffsetDateTime();
        OffsetDateTime endAt = startAt.plusDays(days);
        Duration frequency = Durations.parseFriendlyDurationToSeconds(frequencyLabel);
        try {
            createMedicationScheduleUseCase.execute(new CreateMedicationScheduleCommand(
                    patientContact,
                    medicineName,
                    startAt,
                    endAt,
                    frequency,
                    false
            ));
            model.addAttribute("scheduleMsg", "Agendamento criado com sucesso.");
            model.addAttribute("scheduleSuccess", true);
            model.addAttribute("homeUrl", "/");
        } catch (IllegalStateException ex) {
            model.addAttribute("scheduleMsg", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            // Patient not found or other validation errors
            model.addAttribute("scheduleError", true);
            model.addAttribute("scheduleErrorMsg", "Paciente não encontrado. Cadastre o paciente antes de criar agendamentos.");
        }
        return "dashboard";
    }
}
