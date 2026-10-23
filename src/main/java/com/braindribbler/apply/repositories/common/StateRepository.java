package com.braindribbler.apply.repositories.common;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.braindribbler.apply.models.common.State;

public interface StateRepository extends JpaRepository<State, Integer> {
    // Used to find "ID" or "CA" from the form submission
    Optional<State> findByAbbr(String abbr);

    Optional<State> findByName(String abbr);
}
