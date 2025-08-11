package ru.mazegen.controllers.rest;

import com.nimbusds.jose.shaded.gson.JsonObject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.mazegen.security.JWTUserInfo;
import ru.mazegen.services.JWTService;
import ru.mazegen.services.UserService;

import java.util.HashMap;
import java.util.Map;

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
    public Map<String, String> updateTokens(HttpServletRequest request, HttpServletResponse response) {

        var oldRefreshToken = jwtService.getRefreshToken(request);

        // check that refresh token is present
        if (oldRefreshToken == null) {
            log.info("null refresh token");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return null;
        }

        var parseResult = jwtService.parseToken(oldRefreshToken);

        // check that refresh token is valid and not expired
        if (parseResult.isExpired() || !parseResult.isValid()) {
            log.info("expired or invalid refresh token");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return null;
        }

        JWTUserInfo userInfo = parseResult.jwtUserInfo();
        var newRefreshToken = jwtService.createRefreshToken(userInfo);

        // check that this token wasn't used before
        if (!userService.replaceRefreshTokenForUser(oldRefreshToken, newRefreshToken, userInfo.getUserId())) {
            log.info("token reused: {}", oldRefreshToken);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return null;
        }

        // give the user new tokens
        var newAccessToken = jwtService.createAccessToken(userInfo);

        jwtService.setAccessToken(response, newAccessToken);
        jwtService.setRefreshToken(response, newRefreshToken);

        return Map.of("access_token", newAccessToken); // json
    }


    /**
     * Invalidate current refresh token. (Access token still could be used until it expires)
     */
    @PostMapping("/logout")
    public void logoutUser(HttpServletRequest request, HttpServletResponse response, @AuthenticationPrincipal JWTUserInfo userInfo) {
        if (userInfo == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        var oldRefreshToken = jwtService.getRefreshToken(request);

        if (oldRefreshToken != null && userService.removeRefreshTokenForUser(oldRefreshToken, userInfo.getUserId())) {
            return;
        }

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }


//    @GetMapping("/me")
//    public String getMe(HttpServletResponse response, @AuthenticationPrincipal JWTUserInfo userInfo) {
//        if (userInfo == null) {
//            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//            return null;
//        }
//        log.info("me is {}", userInfo);
//        return "user: " + userInfo;
//    }
}



