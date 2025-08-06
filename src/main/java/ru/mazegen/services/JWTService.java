package ru.mazegen.services;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.mazegen.security.JWTUserInfo;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JWTService {

    public record JWTParseResult(boolean isValid, boolean isExpired, JWTUserInfo jwtUserInfo) {
    }

    @Value("${spring.security.jwt.secret_key}")
    private String secretKey;

    @Value("${spring.security.jwt.access_token_lifetime_ms}")
    private long accessTokenLifetimeMs;

    @Value("${spring.security.jwt.refresh_token_lifetime_ms}")
    private long refreshTokenLifetimeMs;


    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    @Nullable
    private String createToken(@NotNull JWTUserInfo user, boolean isRefresh) {
        long lifetimeMs = isRefresh ? refreshTokenLifetimeMs : accessTokenLifetimeMs;
        return Jwts.builder()
                .setClaims(user.getClaims())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + lifetimeMs))
                .signWith(getSecretKey())
                .compact();
    }


    @NotNull
    public JWTParseResult parseToken(@NotNull String token) {
        JwtParser parser = Jwts.parserBuilder()
                .setSigningKey(getSecretKey())
                .build();
        try {
            var claims = parser.parseClaimsJws(token).getBody();
            return new JWTParseResult(true, false, JWTUserInfo.fromClaims(claims));
        } catch (ExpiredJwtException e) {
            var claims = e.getClaims();
            return new JWTParseResult(true, true, JWTUserInfo.fromClaims(claims));
        } catch (Exception e) { // invalid token
            return new JWTParseResult(false, false, null);
        }
    }


    private void setTokenCookie(HttpServletResponse response, String token, boolean isRefresh) {
        Cookie cookie = new Cookie(isRefresh ? "refresh_token" : "access_token", token);
        cookie.setHttpOnly(true);
        //        cookie.setSecure(true); todo: after migrating from http to https
        cookie.setPath("/"); // visible for all pages
        cookie.setMaxAge(5 * 7 * 24 * 60 * 60); // 5 weeks
        response.addCookie(cookie);
    }


    private String getTokenCookie(HttpServletRequest request, boolean isRefresh) {
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


    private String getTokenFromAuthHeader(HttpServletRequest request) {
        var prefix = "Bearer ";
        var str = request.getHeader("Authorization");
        if (str == null || !str.startsWith(prefix)) return null;
        return str.substring(prefix.length());
    }


    public void setAccessToken(HttpServletResponse response, @NotNull String token) {
        setTokenCookie(response, token, false);
    }

    public void setRefreshToken(HttpServletResponse response, @NotNull String token) {
        setTokenCookie(response, token, true);
    }

    public String getAccessToken(HttpServletRequest request) {
        return getTokenFromAuthHeader(request);
    }

    public String getRefreshToken(HttpServletRequest request) {
        return getTokenCookie(request, true);
    }

    public String createAccessToken(JWTUserInfo userInfo) {
        return createToken(userInfo, false);
    }

    public String createRefreshToken(JWTUserInfo userInfo) {
        return createToken(userInfo, true);
    }
}
