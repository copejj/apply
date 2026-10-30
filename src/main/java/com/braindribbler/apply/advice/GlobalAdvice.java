package com.braindribbler.apply.advice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.info.GitProperties;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.braindribbler.apply.config.EnvironmentConfig;
import com.braindribbler.apply.models.admin.Config;
import com.braindribbler.apply.repositories.admin.ConfigRepository;
import com.braindribbler.apply.service.admin.LabelService;

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GlobalAdvice {

    @Value("${app.api.base-url}")
    private String externalUrl;

    @Value("${app.config.environment}")
    private EnvironmentConfig currentEnv;

    private final ConfigRepository configRepository;
    private final LabelService labelService;
    private GitProperties gitProperties;

    public GlobalAdvice(GitProperties gitProperties, ConfigRepository configRepository, LabelService labelService) {
        this.configRepository = configRepository;
        this.labelService = labelService;
        this.gitProperties = gitProperties;
    }

    @ModelAttribute
    public void addSiteInfo(Model model) {
        model.addAttribute("siteName", "Brain Dribbler Apply");
        model.addAttribute("externalUrl", externalUrl);

        if (gitProperties != null) {
            String tag = gitProperties.get("closest.tag.name");
            String branch = gitProperties.getBranch();
            String fullHash = gitProperties.get("commit.id");
            String shortHash = gitProperties.getShortCommitId();
            String describe = gitProperties.get("commit.id.describe");

            String displayVersion = "Unknown";

            // 1. Separate your environments cleanly at the top level
            if (currentEnv == EnvironmentConfig.DEV) {
                // --- LOCAL DEV SUITE ROUTING ---
                if (branch != null && !branch.isEmpty() && !branch.equals(fullHash)) {
                    displayVersion = branch; // Force your branch name (e.g., version-info)
                } else if (shortHash != null && !shortHash.isEmpty()) {
                    displayVersion = shortHash; // Fallback to short hash if detached
                }
            } else {
                // --- NON DEV SERVERS ROUTING ---
                if (tag != null && !tag.isEmpty()) {
                    displayVersion = tag; // Always prioritize the release tag (e.g., v1.1.86)
                } else if (describe != null && !describe.isEmpty()) {
                    displayVersion = describe; // Fall back to the full describe pattern
                } else {
                    displayVersion = shortHash; // Ultimate production safety fallback
                }
            }

            model.addAttribute("gitBranch", displayVersion);
            model.addAttribute("gitHash", shortHash);
            model.addAttribute("showGitInfo", true);
        } else {
            model.addAttribute("showGitInfo", false);
        }

    }

    @ModelAttribute("appSettings")
    public Map<String, String> addConfigToModel() {
        // 1. Fetch records for BOTH the current env (PROD/DEV) and 'ANY'
        List<EnvironmentConfig> targetEnvs = List.of(currentEnv, EnvironmentConfig.ANY);
        List<Config> configs = configRepository.findByEnvironmentIn(targetEnvs);

        // 2. Build the map with override logic
        Map<String, String> settings = new HashMap<>();

        // 1. Defaults (ANY)
        configs.stream()
                .filter(c -> c.getEnvironment() == EnvironmentConfig.ANY)
                .forEach(c -> settings.put(c.getName(), c.getValue()));

        // 2. Overrides (Specific Env)
        configs.stream()
                .filter(c -> c.getEnvironment() == currentEnv)
                .forEach(c -> settings.put(c.getName(), c.getValue()));

        return settings;
    }

    @ModelAttribute("labelSettings")
    public Map<String, String> addLabelsToModel() {
        return labelService.getAllLabelsAsMap();
    }

}
