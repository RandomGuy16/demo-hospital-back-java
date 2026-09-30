package com.evergreen.generalhospital.security;

import java.io.IOException;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import com.evergreen.generalhospital.models.useraccount.UserAccount;
import com.evergreen.generalhospital.repositories.UserAccountRepository;
import com.evergreen.generalhospital.services.JwtService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


// magic class to handle google OAuth2 authentication
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    
    private final UserAccountRepository userAccountRepository;
    private final JwtService jwtService;

    public OAuth2AuthenticationSuccessHandler(UserAccountRepository userAccountRepository,
                                              JwtService jwtService) {
        this.userAccountRepository = userAccountRepository;
        this.jwtService = jwtService;
    }

    // override methods

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        // get the oauth user
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        // get its info
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String googleSub = oAuth2User.getAttribute("sub");
        
        // check if this email belongs to an existing user
        Optional<UserAccount> existingUser = userAccountRepository.findByEmail(email);

        if (existingUser.isPresent()) {
            // case 1: if this email belongs to an existing user, then enchain its info

            UserAccount user = existingUser.get();
            user.setProvider("google");
            user.setProviderSubject(googleSub);
            userAccountRepository.save(user);

            // create the jwt
            String token = jwtService.generateToken(user);
            // redirect the user
            getRedirectStrategy().sendRedirect(
                request,
                response,
                "http://localhost:3000/auth/callback?token=" + token);
        } else {
            // case 2: brand new user, redirect to an onboarding panel
            String onboardingToken = jwtService.generateOnboardingToken(email, name, googleSub);
            getRedirectStrategy().sendRedirect(
                request,
                response,
                "http://localhost:3000/auth/complete-profile?token?" + onboardingToken);
        }
    }
}

