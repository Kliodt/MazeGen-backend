package ru.mazegen.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
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
public class AuthController {

    private JWTService jwtService;
    private UserService userService;

    /**
     * Update access and refresh tokens using refresh token
     * (usually first request when user enters the website, also when access token expires)
     */
    @PostMapping("/token")
    public void updateTokens(HttpServletRequest request, HttpServletResponse response) {
        var oldRefreshToken = jwtService.getToken(request, true);
        if (oldRefreshToken == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // check that refresh token is valid and not expired
        var parseResult = jwtService.parseToken(oldRefreshToken);
        if (parseResult.isExpired() || parseResult.jwtUserInfo() == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        JWTUserInfo userInfo = parseResult.jwtUserInfo();
        var newRefreshToken = jwtService.generateToken(userInfo, true);

        // check that this token wasn't used before
        if (!userService.replaceRefreshTokenForUser(oldRefreshToken, newRefreshToken, userInfo.getUserId())) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // give the user new tokens
        var newAccessToken = jwtService.generateToken(userInfo, false);

        jwtService.setToken(response, newAccessToken, false);
        jwtService.setToken(response, newRefreshToken, true);
    }


    @GetMapping("/me")
    public String getMe(@AuthenticationPrincipal JWTUserInfo userInfo) {
        return "user: " + userInfo;
    }
}
