package com.braindribbler.apply.advice;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.braindribbler.apply.models.menus.MenuItem;
import com.braindribbler.apply.security.UserDetailsImpl;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class MenuAdvice {

    @ModelAttribute("menuItems")
    public List<MenuItem> menuItems(@AuthenticationPrincipal UserDetailsImpl user) {
        List<MenuItem> list = new ArrayList<>();

        // Links for everyone
        list.add(new MenuItem("/", "Home"));

        if (user != null) {
            boolean isAdmin = user.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

            list.add(new MenuItem("/companies", "Companies"));

            MenuItem logMenu = new MenuItem("/logs/list", "Jobs");
            logMenu.addChild(new MenuItem("/logs/list", "Applied"));
            logMenu.addChild(new MenuItem("/weeks/list", "Weeks"));
            list.add(logMenu);

            if (isAdmin) {
                MenuItem adminMenu = new MenuItem("/admin", "Admin");
                adminMenu.addChild(new MenuItem("/admin/config", "Config"));
                adminMenu.addChild(new MenuItem("/admin/invite", "Invites"));
                adminMenu.addChild(new MenuItem("/admin/labels", "Labels"));
                adminMenu.addChild(new MenuItem("/admin/migrations", "Migrations"));
                adminMenu.addChild(new MenuItem("/admin/users", "Users"));
                list.add(adminMenu);
            }
            list.add(new MenuItem("/admin/users/edit/" + user.getUserId(), "Profile", "align-right"));
            list.add(new MenuItem("/logout", "Logout", "align-right"));
        } else {
            list.add(new MenuItem("/login", "Login", "align-right"));
        }

        return list;
    }

    @ModelAttribute("currentUri")
    public String currentUri(HttpServletRequest request) {
        return request.getRequestURI();
    }
}
