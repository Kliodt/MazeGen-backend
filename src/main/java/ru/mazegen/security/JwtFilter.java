package ru.mazegen.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
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
public class JwtFilter extends OncePerRequestFilter {

    private final JWTService jwtService;

    @Override
    protected void doFilterInternal(
            @AuthenticationPrincipal
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        String accessToken = jwtService.getToken(request, false);
        JwtAuthenticationImpl auth = null;

        if (accessToken != null) {
            var parseResult = jwtService.parseToken(accessToken);

            if (parseResult.isExpired() || parseResult.jwtUserInfo() == null)
                auth = new JwtAuthenticationImpl(parseResult.jwtUserInfo(), true);
        }

        if (auth == null) auth = new JwtAuthenticationImpl(null, false);

        SecurityContextHolder.getContext().setAuthentication(auth);

        filterChain.doFilter(request, response);
    }
}
