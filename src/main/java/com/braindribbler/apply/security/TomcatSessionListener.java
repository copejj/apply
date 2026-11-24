package com.braindribbler.apply.security;

import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Component
public class TomcatSessionListener {

    private final HttpServletRequest request;

    public TomcatSessionListener(HttpServletRequest request) {
        this.request = request;
    }

    @EventListener
    public void handleAuthenticationSuccess(AuthenticationSuccessEvent event) {
        HttpSession session = request.getSession(true);
        session.setAttribute("userName", event.getAuthentication().getName());
        session.setAttribute("Locale", request.getLocale());
    }
}
