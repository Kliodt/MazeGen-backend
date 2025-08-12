package ru.mazegen.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.Authentication;
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
            @NotNull HttpServletRequest request,
            @NotNull HttpServletResponse response,
            @NotNull FilterChain filterChain
    ) throws ServletException, IOException {

        String accessToken = jwtService.getAccessToken(request);
        Authentication auth;

        if (accessToken == null) {
            auth = new JwtAuthenticationImpl(null, false);

        } else {
            var parseResult = jwtService.parseToken(accessToken);

            if (parseResult.isValid() || !parseResult.isExpired()) {
                auth = new JwtAuthenticationImpl(parseResult.jwtUserInfo(), true);
            } else {
                auth = new JwtAuthenticationImpl(null, false);
            }
        }

        SecurityContextHolder.getContext().setAuthentication(auth);

        log.info("Request from user {}", auth.getPrincipal());
        log.info("Request url {}", request.getRequestURL());
        log.info("Request origin {}", request.getHeader("Origin"));
        log.info("Request host {}", request.getHeader("Host"));

        filterChain.doFilter(request, response);
    }
}
