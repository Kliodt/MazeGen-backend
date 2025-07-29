package ru.mazegen.services;

import com.nimbusds.jwt.JWTParser;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.Nonnull;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.mazegen.model.User;
import ru.mazegen.security.JWTUserInfo;

import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JWTService {

    public record JWTParseResult(boolean isExpired, JWTUserInfo jwtUserInfo){}

    @Value("${spring.security.jwt.secret_key}")
    private String secretKey;

    @Value("${spring.security.jwt.access_token_lifetime_ms}")
    private long accessTokenLifetimeMs;

    @Value("${spring.security.jwt.refresh_token_lifetime_ms}")
    private long refreshTokenLifetimeMs;


    @NonNull
    public String generateToken(@NonNull JWTUserInfo user, boolean isRefresh) {
        long lifetimeMs = isRefresh ? refreshTokenLifetimeMs : accessTokenLifetimeMs;
        return Jwts.builder()
                .setClaims(user.getClaims())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + lifetimeMs))
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)))
                .compact();
    }


    @NonNull
    public JWTParseResult parseToken(@NonNull String token) {
        JwtParser parser = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)))
                .build();

        try {
            var claims = parser.parseClaimsJwt(token).getBody();
            return new JWTParseResult(false, JWTUserInfo.fromClaims(claims));
        } catch (ExpiredJwtException e) {
            var claims = e.getClaims();
            return new JWTParseResult(true, JWTUserInfo.fromClaims(claims));
        } catch (Exception e) { // invalid token
            return new JWTParseResult(true, null);
        }
    }

//    public void setAccessToken(@NonNull HttpServletResponse response, @NonNull String token) {
//        response.setHeader("Authorization", "Bearer " + token);
//    }
//
//    public String getAccessToken(HttpServletRequest request) {
//        String auth = request.getHeader("Authorization");
//        if (auth != null && auth.startsWith("Bearer ")) {
//            return auth.substring("Bearer ".length());
//        }
//        return null;
//    }

    public void setToken(HttpServletResponse response, String token, boolean isRefresh) {
        Cookie cookie = new Cookie(isRefresh ? "refresh_token" : "access_token", token);
        cookie.setHttpOnly(true);
//        cookie.setSecure(true); todo: after migrating from http to https
        cookie.setPath("/"); // visible for all pages
        cookie.setMaxAge(5 * 7 * 24 * 60 * 60); // 5 weeks
        response.addCookie(cookie);
    }

    public String getToken(HttpServletRequest request, boolean isRefresh) {
        var cookies = request.getCookies();
        if (cookies == null) return null;
        var name = isRefresh ? "refresh_token" : "access_token";
        for (var c : cookies) {
            if (name.equals(c.getName())) {
                return c.getValue();
            }
        }
        return null;
    }

}
