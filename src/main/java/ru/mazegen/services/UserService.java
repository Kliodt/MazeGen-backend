package ru.mazegen.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;
import ru.mazegen.model.User;
import ru.mazegen.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.function.Predicate;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepo;


    /**
     * Replaces user's refresh token with another one.
     * If oldToken not exists, or user not exists, nothing happens and method returns false
     * @return true, on success, false if something went wrong
     */
    @Transactional
    public boolean replaceRefreshTokenForUser(@NotNull String oldToken, @NotNull String newToken, long userId) {
        var user = userRepo.findById(userId);
        if (user.isEmpty()) return false;
        var tokens = user.get().getRefreshTokens();
        if (tokens.remove(oldToken)) {
            tokens.add(newToken);
            return true;
        }
        return false;
    }


    /**
     * Give a new refresh token to a user
     */
    @Transactional
    public void addRefreshTokenForUser(@NotNull String token, long userId) {
        var user = userRepo.findById(userId).orElse(null);
        if (user == null) {
            log.warn("Tried to add refresh token for non existent user {}", userId);
            return;
        }
        user.getRefreshTokens().add(token);
    }


    /**
     * Basically, a way to remove expired tokens (but without explicit jwtService dependency)
     * @param filter predicate that determines if token should be retained
     */
    @Transactional
    public void filterUserRefreshTokens(Predicate<String> filter, long userId) {
        var user = userRepo.findById(userId).orElse(null);
        if (user == null) {
            log.warn("Tried to clear refresh tokens for non existent user {}", userId);
            return;
        }
        user.setRefreshTokens(
                user.getRefreshTokens().stream().filter(filter).toList()
        );
    }


    /**
     * Finds or creates user with given Google id
     */
    @Transactional
    public User getOrCreateUserWithGoogleId(
            @NotNull String googleId,
            String nickname,
            String pictureUrl
    ) {
        User user = userRepo.findUserByGoogleId(googleId);

        if (user != null) return user;

        user = new User(googleId, pictureUrl, nickname, OffsetDateTime.now());
        userRepo.save(user);
        return user;
    }


    @Nullable
    public User getUserById(Long id) {
        return userRepo.findById(id).orElse(null);
    }
}
