package com.braindribbler.apply.service.logs.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.braindribbler.apply.dto.logs.WeekDTO;
import com.braindribbler.apply.models.logs.Week;
import com.braindribbler.apply.repositories.logs.WeekProjection;
import com.braindribbler.apply.repositories.logs.WeekRepository;
import com.braindribbler.apply.service.logs.WeekService;

@Service
public class WeekServiceImpl implements WeekService {
    private final WeekRepository weekRepository;

    public WeekServiceImpl(WeekRepository weekRepository) {
        this.weekRepository = weekRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public WeekDTO getWeekDtoById(Long weekId) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<Week> getAll() {
        return weekRepository.findAll();
    }

    public List<WeekDTO> getWeeksByUserId(Long userId) {
        List<WeekProjection> results = weekRepository.findActiveWeeks(userId);

        return results.stream()
                .map(row -> new WeekDTO(
                        row.getWeekId(),
                        row.getStartDate(),
                        row.getEndDate(),
                        row.getLogCounts()))
                .toList();
    }
}
