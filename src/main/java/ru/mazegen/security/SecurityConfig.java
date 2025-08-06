package ru.mazegen.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import ru.mazegen.model.User;
import ru.mazegen.services.JWTService;
import ru.mazegen.services.UserService;
import java.io.IOException;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
@Slf4j
public class SecurityConfig {

    private UserService userService;
    private JWTService jwtService;


    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:3000")
                        .allowedMethods("*")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

        return httpSecurity
                .authorizeHttpRequests(authorizeHttpRequests ->
                        authorizeHttpRequests
                                .requestMatchers("/**").permitAll()
                )
                .oauth2Login(oauth2Login ->
                        oauth2Login
                                .successHandler(this::onGoogleAuthSuccess)
                                .loginPage("/oauth2/authorization/google")
                )
                .sessionManagement(sessionManagement ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .csrf(AbstractHttpConfigurer::disable) // todo: разобраться с этим
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .build();
    }


    /**
     * Get tokens from Google Token api
     */
    private void onGoogleAuthSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        String googleId = oAuth2User.getAttribute("sub");
        String name = oAuth2User.getAttribute("name");
        String picture = oAuth2User.getAttribute("picture");
        // String email = oAuth2User.getAttribute("email");

        if (googleId == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        User user = userService.getOrCreateUserWithGoogleId(googleId, name, picture);

        // give the identified user new refresh and access tokens

        JWTUserInfo userInfo = JWTUserInfo.fromUser(user);

        var refreshToken = jwtService.createRefreshToken(userInfo);

        userService.addRefreshTokenForUser(refreshToken, userInfo.getUserId());

        jwtService.setRefreshToken(response, refreshToken);

        // clear expired and most likely dangling tokens
        userService.filterUserRefreshTokens(
                token -> !jwtService.parseToken(token).isExpired(),
                userInfo.getUserId()
        );


        response.sendRedirect("http://localhost:3000/"); // todo

//        var headerNames = request.getHeaderNames();
//        while (headerNames.hasMoreElements()) {
//            String headerName = headerNames.nextElement();
//            log.info("request header {} --- {}", headerName, request.getHeader(headerName));
//
//        }

        // todo: deal with google's session id
    }
}