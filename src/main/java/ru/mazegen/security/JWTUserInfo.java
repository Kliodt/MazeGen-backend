package ru.mazegen.security;

import io.jsonwebtoken.Claims;
import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import ru.mazegen.model.User;

import java.util.HashMap;
import java.util.Map;

/**
 * Class that represents information about user stored in JWT
 */
@Data
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class JWTUserInfo {

    private final long userId;
    private final User.Role role;
    private final String nickname;


    public static JWTUserInfo fromUser(User user) {
        return new JWTUserInfo(user.getId(), user.getRole(), user.getNickname());
    }

    public Map<String, String> getClaims() {
        return Map.of(
                "user_id", Long.toString(userId),
                "role", role.toString(),
                "nickname", nickname
        );
    }

    public static JWTUserInfo fromClaims(@NonNull Claims claims) {
        try {
            long userId = Long.parseLong(claims.get("user_id", String.class));
            User.Role role = User.Role.valueOf(claims.get("role", String.class));
            String nickname = claims.get("nickname", String.class);

            return new JWTUserInfo(userId, role, nickname);

        } catch (Exception e) {
            log.warn("JWTUserInfo couldn't be created from claims: {}", claims, e);
            return null;
        }
    }
}
