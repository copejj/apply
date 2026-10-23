package com.braindribbler.apply.service.admin;

import java.util.List;

import com.braindribbler.apply.config.EnvironmentConfig;
import com.braindribbler.apply.models.admin.Config;

public interface ConfigService {
    List<Config> getAllConfigs();

    List<Config> getAllActiveConfigs(EnvironmentConfig env);

    Config saveConfig(Config config);

    void deleteConfig(Long configId);
}
