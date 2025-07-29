package ru.mazegen.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.RequiredTypeException;
import lombok.*;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;
import ru.mazegen.model.User;

import java.util.HashMap;

/**
 * Class that represents information about user stored in JWT
 */
@Data
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class JWTUserInfo {

    private final long userId;
    private final User.Role role;
    private final String nickname;


    public static JWTUserInfo fromUser(User user) {
        return new JWTUserInfo(user.getId(), user.getRole(), user.getNickname());
    }

    public HashMap<String, String> getClaims() {
        var claims = new HashMap<String, String>();
        claims.put("user_id", Long.toString(userId));
        claims.put("role", role.toString());
        claims.put("nickname", nickname);
        return claims;
    }

    public static JWTUserInfo fromClaims(@NonNull Claims claims) {
        try {
            Long userId = claims.get("user_id", Long.class);
            User.Role role = User.Role.valueOf(claims.get("role", String.class));
            String nickname = claims.get("nickname", String.class);
            if (userId == null || nickname == null) return null;
            return new JWTUserInfo(userId, role, nickname);
        } catch (RequiredTypeException | IllegalArgumentException e) {
            return null;
        }
    }
}
