package com.braindribbler.spring.repositories.common;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.braindribbler.spring.models.common.State;

public interface StateRepository extends JpaRepository<State, Integer> {
    // Used to find "ID" or "CA" from the form submission
    Optional<State> findByAbbr(String abbr);

    Optional<State> findByName(String abbr);
}
