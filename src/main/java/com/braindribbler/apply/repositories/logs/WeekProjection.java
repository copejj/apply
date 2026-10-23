package com.braindribbler.apply.repositories.logs;

import java.time.LocalDate;

public interface WeekProjection {
    Long getWeekId();

    LocalDate getStartDate();

    LocalDate getEndDate();

    Integer getLogCounts();
}
