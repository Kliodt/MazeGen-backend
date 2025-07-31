package ru.mazegen.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.mazegen.services.JWTService;

import java.io.IOException;


/**
 * Preprocess requests. Check access token, set Authentication principle depending
 * on the token validness. Principle could be obtained later in controllers using
 * {@link org.springframework.security.core.annotation.AuthenticationPrincipal}
 * annotation in method parameters. Principle's type in this project is {@link JWTUserInfo}.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    private final JWTService jwtService;

    @Override
    protected void doFilterInternal(
            @AuthenticationPrincipal
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        String accessToken = jwtService.getAccessToken(request);
        JwtAuthenticationImpl auth;

        if (accessToken == null) {
            auth = new JwtAuthenticationImpl(null, false);

        } else {
            var parseResult = jwtService.parseToken(accessToken);

            if (!parseResult.isValid()) {
                auth = new JwtAuthenticationImpl(null, false);
                log.warn("Request form user with invalid token {}", accessToken);
            } else if (parseResult.isExpired()) {
                auth = new JwtAuthenticationImpl(parseResult.jwtUserInfo(), false);
            } else {
                auth = new JwtAuthenticationImpl(parseResult.jwtUserInfo(), true);
            }
        }

        SecurityContextHolder.getContext().setAuthentication(auth);

        log.info("Request form user: {}", auth.getPrincipal());

        filterChain.doFilter(request, response);
    }
}
