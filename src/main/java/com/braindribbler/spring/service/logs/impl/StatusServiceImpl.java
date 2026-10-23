package com.braindribbler.spring.service.logs.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.braindribbler.spring.models.logs.Status;
import com.braindribbler.spring.repositories.logs.StatusRepository;
import com.braindribbler.spring.service.logs.StatusService;

@Service
public class StatusServiceImpl implements StatusService {
	private final StatusRepository statusRepository;

	public StatusServiceImpl(StatusRepository statusRepository) {
		this.statusRepository = statusRepository;
	}

	@Override
	public List<Status> getAllStatuses() {
		return statusRepository.findAllByOrderByOrderAsc();
	}

	@Override
	public Status getStatusById(Long statusId) {
		throw new UnsupportedOperationException("Unimplemented method 'getStatusById'");
	}

}
