package com.braindribbler.apply.controllers.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.braindribbler.apply.models.admin.Label;
import com.braindribbler.apply.service.admin.LabelService;

@Controller
@RequestMapping("/admin/labels")
public class LabelController {
    private final LabelService labelService;

    public LabelController(LabelService labelService) {
        this.labelService = labelService;
    }

    @GetMapping
    public String listLabels(Model model) {
        model.addAttribute("title", "Labels List");
        model.addAttribute("location", "Admin: Labels");
        model.addAttribute("labels", labelService.getAllLabelsAsList());
        model.addAttribute("newLabel", new Label());
        return "admin/labels/list";
    }

    @PostMapping("/save")
    public String saveLabel(@ModelAttribute("labelObject") Label label) {
        labelService.saveLabel(label);
        return "redirect:/admin/labels";
    }

    @PostMapping("/delete/{id}")
    public String deleteLabel(@PathVariable Long id) {
        labelService.deleteLabel(id);
        return "redirect:/admin/labels";
    }
}
