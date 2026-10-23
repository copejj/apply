package com.braindribbler.apply.controllers.logs;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.braindribbler.apply.dto.logs.LogDTO;
import com.braindribbler.apply.forms.logs.LogForm;
import com.braindribbler.apply.security.UserDetailsImpl;
import com.braindribbler.apply.service.companies.CompanyService;
import com.braindribbler.apply.service.logs.LogService;
import com.braindribbler.apply.service.logs.StatusService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/logs")
public class LogController {
    private final LogService logService;
    private final CompanyService companyService;
    private final StatusService statusService;

    public LogController(LogService logService, CompanyService companyService, StatusService statusService) {
        this.logService = logService;
        this.companyService = companyService;
        this.statusService = statusService;
    }

    @GetMapping("/list")
    public String viewLogs(
            @AuthenticationPrincipal UserDetailsImpl userDetails, // User ID handled internally
            @RequestParam(required = false) Long weekId,
            @RequestParam(required = false) Long companyId,
            Model model) { // Model for passing data to the template

        List<LogDTO> logs = logService.findLogs(userDetails.getUserId(), weekId, companyId);

        model.addAttribute("location", "Logs");
        model.addAttribute("title", "Applications List");
        model.addAttribute("logs", logs);
        model.addAttribute("selectedWeek", weekId);
        model.addAttribute("selectedCompany", companyId);
        model.addAttribute("allStatuses", statusService.getAllStatuses());

        return "logs/list";
    }

    @PostMapping("/update-status")
    @ResponseBody
    public ResponseEntity<String> updateStatus(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam("logId") Long logId,
            @RequestParam(value = "statusId", required = false) Long statusId) {
        try {
            Long userId = userDetails.getUserId();
            LogDTO logDto = logService.getLogDtoByIdAndUserId(logId, userId);
            if (logDto == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized access to this log record.");
            }

            logService.updateStatus(logId, statusId);

            return ResponseEntity.ok("Status successfully updated");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating status: " + e.getMessage());
        }
    }

    @GetMapping("/details")
    public String viewLogDetails(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam(required = false) Long weekId,
            @RequestParam(required = false) Long companyId,
            Model model) {

        List<LogDTO> logs = logService.findLogs(userDetails.getUserId(), weekId, companyId);

        model.addAttribute("location", "Logs");
        model.addAttribute("title", "Applications Details");
        model.addAttribute("logs", logs);
        model.addAttribute("selectedWeek", weekId);
        model.addAttribute("selectedCompany", companyId);

        return "logs/details";
    }

    @GetMapping("/edit/{logId}")
    public String editLog(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long logId,
            Model model) {

        LogDTO dto = logService.getLogDtoById(logId);

        LogForm form = new LogForm();
        form.setLogId(dto.logId());
        form.setTitle(dto.title());
        form.setJobNumber(dto.jobNumber());
        form.setNextStep(dto.nextStep());
        form.setJobLink(dto.jobLink());
        form.setDescription(dto.description());
        form.setNotes(dto.notes());
        form.setConfirmation(dto.confirmation());
        form.setContact(dto.contact());
        form.setContactNumber(dto.contactNumber());
        form.setCompanyId(dto.companyId());
        form.setActionDate(dto.actionDate());
        form.setLatestStatusId(dto.latestStatusId());
        form.setLatestStatus(dto.latestStatus());
        form.setLogStatuses(dto.logStatuses());

        Long userId = userDetails.getUserId();

        model.addAttribute("log", form);
        model.addAttribute("title", "Application Information");
        model.addAttribute("statuses", statusService.getAllStatuses());
        model.addAttribute("location", "Edit Log");
        model.addAttribute("companies", companyService.getAll(userId));

        return "logs/edit";
    }

    @GetMapping("/create")
    public String createLogForm(
            @RequestParam(name = "companyId", required = false) Long companyId,
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            Model model) {

        LogForm form = new LogForm();
        if (companyId != null) {
            form.setCompanyId(companyId);
        }

        model.addAttribute("log", form);
        model.addAttribute("title", "Application Information");
        model.addAttribute("statuses", statusService.getAllStatuses());
        model.addAttribute("location", "New Log");
        model.addAttribute("companies", companyService.getAll(userDetails.getUserId()));

        return "logs/edit";
    }

    @PostMapping("/save")
    public String saveLog(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @ModelAttribute("log") LogForm logForm,
            BindingResult result,
            @RequestParam(value = "action", required = false) String action,
            RedirectAttributes redirectAttributes,
            Model model) {

        if ("deleteLog".equals(action)) {
            try {
                if (logForm.getLogId() != null) {
                    logService.deleteById(logForm.getLogId(), userDetails.getUserId());
                    redirectAttributes.addFlashAttribute("saveSuccess", "Log deleted successfully!");
                }
                // Redirect straight to your list view URL
                return "redirect:/logs/list";
            } catch (Exception e) {
                // If delete fails, stay on page and show error
                model.addAttribute("saveError", "Error deleting log: " + e.getMessage());
                model.addAttribute("title", "Application Information");
                model.addAttribute("location", "Edit Log");
                model.addAttribute("companies", companyService.getAll(userDetails.getUserId()));
                return "logs/edit";
            }
        }

        if (result.hasErrors()) {
            model.addAttribute("title", "Application Information");
            model.addAttribute("location", logForm.getLogId() == null ? "New Log" : "Edit Log");
            model.addAttribute("companies", companyService.getAll(userDetails.getUserId()));
            return "logs/edit";
        }

        try {
            Long savedId = logService.saveFromForm(logForm, userDetails.getUserId());
            redirectAttributes.addFlashAttribute("saveSuccess", "Log saved successfully!");

            if ("saveAndAdd".equals(action)) {
                return "redirect:/logs/create?companyId=" + logForm.getCompanyId();
            }

            return "redirect:/logs/edit/" + savedId;

        } catch (Exception e) {
            model.addAttribute("saveError", "Error: " + e.getMessage());
            model.addAttribute("companies", companyService.getAll(userDetails.getUserId()));
            return "logs/edit";
        }
    }
}
