package com.braindribbler.apply.controllers.logs;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.braindribbler.apply.dto.logs.WeekDTO;
import com.braindribbler.apply.security.UserDetailsImpl;
import com.braindribbler.apply.service.logs.WeekService;

@Controller
@RequestMapping("/weeks")
public class WeekController {
    private final WeekService weekService;

    public WeekController(WeekService weekService) {
        this.weekService = weekService;
    }

    @GetMapping("/list")
    public String viewWeeks(@AuthenticationPrincipal UserDetailsImpl userDetails, Model model) { // Model for passing
                                                                                                 // data to the template

        List<WeekDTO> weeks = weekService.getWeeksByUserId(userDetails.getUserId());

        model.addAttribute("location", "Weeks");
        model.addAttribute("title", "Applications By Week");
        model.addAttribute("weeks", weeks);

        return "weeks/list";
    }
}
