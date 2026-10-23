package com.braindribbler.apply.service.logs;

import java.util.List;

import com.braindribbler.apply.models.logs.Status;

public interface StatusService {
	Status getStatusById(Long statusId);

	List<Status> getAllStatuses();
}
