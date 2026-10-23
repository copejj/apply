package com.braindribbler.apply.controllers.admin;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.braindribbler.apply.config.EnvironmentConfig;
import com.braindribbler.apply.models.admin.Config;
import com.braindribbler.apply.service.admin.ConfigService;

@Controller
@RequestMapping("/admin/config") // All routes now start with /admin/configs
public class ConfigController {
    @Value("${app.config.environment:production}")
    private String environment;

    private final ConfigService configService;

    public ConfigController(ConfigService configService) {
        this.configService = configService;
    }

    @GetMapping
    public String listConfigs(Model model) {
        model.addAttribute("title", "Config Settings: " + environment);
        model.addAttribute("location", "Admin: Config");
        model.addAttribute("environments", EnvironmentConfig.values());

        model.addAttribute("configs", configService.getAllConfigs());
        model.addAttribute("newConfig", new Config());
        return "admin/config/list";
    }

    @PostMapping("/save")
    public String saveConfig(@ModelAttribute Config config) {
        configService.saveConfig(config);
        return "redirect:/admin/config";
    }

    @PostMapping("/delete/{id}")
    public String deleteConfig(@PathVariable Long id) {
        configService.deleteConfig(id);
        return "redirect:/admin/config";
    }
}
