package com.braindribbler.apply.repositories.admin;

import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.braindribbler.apply.config.EnvironmentConfig;
import com.braindribbler.apply.models.admin.Config;

public interface ConfigRepository extends JpaRepository<Config, Long> {
	@Cacheable(value = "configCache", key = "#envs")
	List<Config> findByEnvironmentIn(List<EnvironmentConfig> envs);
}
