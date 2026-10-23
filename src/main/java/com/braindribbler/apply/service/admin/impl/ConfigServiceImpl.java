package com.braindribbler.apply.service.admin.impl;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.braindribbler.apply.config.EnvironmentConfig;
import com.braindribbler.apply.models.admin.Config;
import com.braindribbler.apply.repositories.admin.ConfigRepository;
import com.braindribbler.apply.service.admin.ConfigService;

import jakarta.transaction.Transactional;

@Service
public class ConfigServiceImpl implements ConfigService {

    private final ConfigRepository configRepository;

    public ConfigServiceImpl(ConfigRepository configRepository) {
        this.configRepository = configRepository;
    }

    @Override
    public List<Config> getAllConfigs() {
        return configRepository.findAll();
    }

    @Override
    @Cacheable(value = "configCache", key = "#env")
    public List<Config> getAllActiveConfigs(EnvironmentConfig env) {
        return configRepository.findByEnvironmentIn(
                List.of(EnvironmentConfig.ANY, env));
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    @CacheEvict(value = "configCache", allEntries = true)
    public Config saveConfig(Config config) {
        if (config == null) {
            throw new IllegalArgumentException("Config cannot be null");
        }
        return configRepository.save(config);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    @CacheEvict(value = "configCache", allEntries = true)
    public void deleteConfig(Long configId) {
        if (configId != null) {
            configRepository.deleteById(configId);
        }
    }
}
