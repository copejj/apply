package com.braindribbler.apply.service.logs.impl;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.braindribbler.apply.dto.companies.CompanyAddressDTO;
import com.braindribbler.apply.dto.logs.LogDTO;
import com.braindribbler.apply.dto.logs.LogStatusDTO;
import com.braindribbler.apply.forms.logs.LogForm;
import com.braindribbler.apply.models.companies.Company;
import com.braindribbler.apply.models.logs.Log;
import com.braindribbler.apply.models.logs.LogStatus;
import com.braindribbler.apply.models.logs.Status;
import com.braindribbler.apply.models.logs.Week;
import com.braindribbler.apply.repositories.logs.LogRepository;
import com.braindribbler.apply.repositories.logs.LogStatusRepository;
import com.braindribbler.apply.repositories.logs.StatusRepository;
import com.braindribbler.apply.repositories.logs.WeekRepository;
import com.braindribbler.apply.service.logs.LogService;

@Service
public class LogServiceImpl implements LogService {

    private final LogRepository logRepository;
    private final LogStatusRepository logStatusRepository;
    private final StatusRepository statusRepository;
    private final WeekRepository weekRepository;

    public LogServiceImpl(LogRepository logRepository,
            LogStatusRepository logStatusRepository,
            StatusRepository statusRepository,
            WeekRepository weekRepository) {
        this.logRepository = logRepository;
        this.weekRepository = weekRepository;
        this.logStatusRepository = logStatusRepository;
        this.statusRepository = statusRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public LogDTO getLogDtoById(Long logId) {
        if (logId == null) {
            throw new IllegalArgumentException("Log ID must not be null");
        }
        return logRepository.findById(logId)
                .map(this::convertToDto)
                .orElseThrow(() -> new RuntimeException("Log not found with id: " + logId));
    }

    @Override
    @Transactional(readOnly = true)
    public LogDTO getLogDtoByIdAndUserId(Long logId, Long userId) {
        if (logId == null || userId == null) {
            throw new IllegalArgumentException("Log ID and User ID must not be null");
        }
        return logRepository.findByLogIdAndUserId(logId, userId)
                .map(this::convertToDto)
                .orElseThrow(() -> new RuntimeException("Log not found with id: " + logId));
    }

    @Override
    @Transactional
    public void updateLog(LogDTO dto) {
        Long logId = dto.logId();
        if (logId == null) {
            throw new IllegalArgumentException("Log ID must not be null");
        }
        Log log = logRepository.findById(logId)
                .orElseThrow(() -> new RuntimeException("Log not found with id: " + logId));
        updateEntityFromDto(log, dto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LogDTO> findLogs(Long userId) {
        return logRepository.findByUserId(userId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LogDTO> findLogs(Long userId, Long weekId, Long companyId) {
        if (weekId == null && companyId == null) {
            return findLogs(userId);
        }
        return logRepository.findFilteredLogs(userId, weekId, companyId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateStatus(Long logId, Long statusId) {
        if (logId == null || statusId == null) {
            throw new IllegalArgumentException("Log and Status ID must not be null");
        }

        Log log = logRepository.findById(logId)
                .orElseThrow(() -> new RuntimeException("Log not found with id: " + logId));

        if (statusId.equals(log.getLatestStatusId())) {
            return;
        }

        Status status = statusRepository.findById(statusId)
                .orElseThrow(() -> new RuntimeException("Status not found with id: " + statusId));

        LogStatus logStatus = new LogStatus();
        logStatus.setLog(log);
        logStatus.setStatus(status);
        logStatusRepository.save(logStatus);
    }

    @Override
    @Transactional
    public Long saveFromForm(LogForm form, Long userId) {
        Log log;
        if (form.getLogId() != null) {
            log = logRepository.findByLogId(form.getLogId())
                    .orElseThrow(() -> new RuntimeException("Log not found with id: " + form.getLogId()));
        } else {
            log = new Log();
            log.setUserId(userId);
        }

        log.setTitle(form.getTitle());
        log.setJobNumber(form.getJobNumber());
        log.setNextStep(form.getNextStep());
        log.setJobLink(form.getJobLink());
        log.setDescription(form.getDescription());
        log.setNotes(form.getNotes());
        log.setConfirmation(form.getConfirmation());
        log.setContact(form.getContact());
        log.setContactNumber(form.getContactNumber());
        log.setActionDate(form.getActionDate());
        log.setCompanyId(form.getCompanyId());

        Week week = weekRepository.findWeekByDate(form.getActionDate())
                .orElseGet(() -> createNewWeek(form.getActionDate()));
        log.setWeekId(week.getWeekId());

        Log saved = logRepository.save(log);

        if (form.getLatestStatusId() != null) {
            updateStatus(saved.getLogId(), form.getLatestStatusId());
        }

        return saved.getLogId();
    }

    @Override
    @Transactional
    public void deleteById(Long logId, Long userId) {
        if (logId == null || userId == null) {
            throw new IllegalArgumentException("Log ID and User ID must not be null");
        }

        Log log = logRepository.findById(logId)
                .orElseThrow(() -> new RuntimeException("Log not found with id: " + logId));

        if (log.getUserId() != null && !Long.valueOf(log.getUserId()).equals(userId)) {
            throw new SecurityException("You do not have permission to delete this log.");
        }

        logRepository.deleteById(logId);
    }

    private Week createNewWeek(LocalDate date) {
        LocalDate start = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
        LocalDate end = date.with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY));

        Week newWeek = new Week();
        newWeek.setStartDate(start);
        newWeek.setEndDate(end);
        return weekRepository.save(newWeek);
    }

    private LogDTO convertToDto(Log log) {
        Company company = log.getCompany();
        String companyEmail = null;
        String companyWebsite = null;
        CompanyAddressDTO companyFirstAddress = null;

        if (company != null) {
            companyEmail = company.getCompanyEmail();
            companyWebsite = company.getCompanyWebsite();
            var addresses = company.getCompanyAddresses();
            if (addresses != null && !addresses.isEmpty()) {
                companyFirstAddress = CompanyAddressDTO.fromEntity(addresses.get(0));
            }
        }

        List<LogStatusDTO> logStatusDtos = null;
        if (log.getLogStatuses() != null) {
            logStatusDtos = log.getLogStatuses().stream()
                    .map(ls -> new LogStatusDTO(
                            ls.getLogStatusId(),
                            ls.getStatusDate(),
                            ls.getStatus() != null ? ls.getStatus().getStatus() : null,
                            ls.getStatus() != null ? ls.getStatus().getStatusId() : null))
                    .collect(Collectors.toList());
        }

        LogStatusDTO latestStatusDto = null;
        if (log.getLatestStatus() != null) {
            LogStatus ls = log.getLatestStatus();
            latestStatusDto = new LogStatusDTO(
                    ls.getLogStatusId(),
                    ls.getStatusDate(),
                    ls.getStatus() != null ? ls.getStatus().getStatus() : null,
                    ls.getStatus() != null ? ls.getStatus().getStatusId() : null);
        }

        return new LogDTO(
                log.getLogId(),
                log.getCreatedDate(),
                log.getUserId() != null ? log.getUserId().longValue() : null,
                log.getTitle(),
                log.getJobNumber(),
                log.getNextStep(),
                log.getJobLink(),
                log.getDescription(),
                log.getNotes(),
                log.getConfirmation(),
                log.getContact(),
                log.getContactNumber(),
                log.getCompanyId(),
                company != null ? company.getCompanyName() : null,
                companyEmail,
                companyWebsite,
                companyFirstAddress,
                log.getActionDate(),
                log.getWeekId(),
                log.getWeek() != null ? log.getWeek().getStartDate() : null,
                log.getWeek() != null ? log.getWeek().getEndDate() : null,
                log.getLatestStatusId(),
                latestStatusDto,
                logStatusDtos);
    }

    private void updateEntityFromDto(Log log, LogDTO dto) {
        log.setTitle(dto.title());
        log.setJobNumber(dto.jobNumber());
        log.setNextStep(dto.nextStep());
        log.setJobLink(dto.jobLink());
        log.setDescription(dto.description());
        log.setNotes(dto.notes());
        log.setConfirmation(dto.confirmation());
        log.setContact(dto.contact());
        log.setContactNumber(dto.contactNumber());
        log.setActionDate(dto.actionDate());
        log.setCompanyId(dto.companyId());
        log.setWeekId(dto.weekId());
    }
}
