package com.braindribbler.apply.service.logs;

import java.util.List;

import com.braindribbler.apply.dto.logs.WeekDTO;
import com.braindribbler.apply.models.logs.Week;

public interface WeekService {
	WeekDTO getWeekDtoById(Long weekId);

	List<WeekDTO> getWeeksByUserId(Long userId);

	List<Week> getAll();
}
