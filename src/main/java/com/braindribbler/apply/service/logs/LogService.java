package com.braindribbler.apply.service.logs;

import java.util.List;

import com.braindribbler.apply.dto.logs.LogDTO;
import com.braindribbler.apply.forms.logs.LogForm;

public interface LogService {
	void updateLog(LogDTO dto);

	List<LogDTO> findLogs(Long userId);

	List<LogDTO> findLogs(Long userId, Long weekId, Long companyId);

	LogDTO getLogDtoById(Long logId);

	LogDTO getLogDtoByIdAndUserId(Long logId, Long userId);

	Long saveFromForm(LogForm logForm, Long userId);

	void updateStatus(Long logId, Long statusId);

	void deleteById(Long logId, Long userId);
}
