package ru.mazegen.controllers;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mazegen.security.JWTUserInfo;
import ru.mazegen.services.JWTService;
import ru.mazegen.services.UserService;

@RestController
@AllArgsConstructor
@RequestMapping("/auth")
@Slf4j
public class AuthController {

    private JWTService jwtService;
    private UserService userService;

    /**
     * Update access and refresh tokens using refresh token
     * (usually first request when user enters the website, also when access token expires)
     */
    @PostMapping("/token")
    public void updateTokens(HttpServletRequest request, HttpServletResponse response) {

        var oldRefreshToken = jwtService.getRefreshToken(request);

        // check that refresh token is present
        if (oldRefreshToken == null) {
            log.info("null refresh token");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        var parseResult = jwtService.parseToken(oldRefreshToken);

        // check that refresh token is valid and not expired
        if (parseResult.isExpired() || !parseResult.isValid()) {
            log.info("expired or invalid refresh token");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        JWTUserInfo userInfo = parseResult.jwtUserInfo();
        var newRefreshToken = jwtService.createRefreshToken(userInfo);

        // check that this token wasn't used before
        if (!userService.replaceRefreshTokenForUser(oldRefreshToken, newRefreshToken, userInfo.getUserId())) {
            log.info("token reused: {}", oldRefreshToken);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // give the user new tokens
        var newAccessToken = jwtService.createAccessToken(userInfo);

        jwtService.setAccessToken(response, newAccessToken);
        jwtService.setRefreshToken(response, newRefreshToken);
    }


    @GetMapping("/me")
    public String getMe(@AuthenticationPrincipal JWTUserInfo userInfo) {
        return "user: " + userInfo;
    }
}



