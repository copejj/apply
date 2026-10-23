package com.braindribbler.apply.repositories.logs;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.braindribbler.apply.models.logs.Status;

public interface StatusRepository extends JpaRepository<Status, Long> {
	List<Status> findAllByOrderByOrderAsc();
}
