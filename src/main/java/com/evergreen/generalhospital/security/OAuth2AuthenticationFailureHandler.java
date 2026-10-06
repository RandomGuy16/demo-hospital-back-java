package com.evergreen.generalhospital.security;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class OAuth2AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    public OAuth2AuthenticationFailureHandler() {

    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest req,
                                        HttpServletResponse res,
                                        AuthenticationException exception) throws IOException, ServletException {
        // we dont expose the bare message to the page
        String error = URLEncoder.encode("google_auth_failed", StandardCharsets.UTF_8);
    
        // let the callback page handle the error
        String redirectUrl = "http://localhost:3000/auth/callback?error=" + error;

        res.sendRedirect(redirectUrl);
    }
}


