package com.braindribbler.spring.service.admin;

import java.util.List;

import com.braindribbler.spring.config.EnvironmentConfig;
import com.braindribbler.spring.models.admin.Config;

public interface ConfigService {
    List<Config> getAllConfigs();

    List<Config> getAllActiveConfigs(EnvironmentConfig env);

    Config saveConfig(Config config);

    void deleteConfig(Long configId);
}
