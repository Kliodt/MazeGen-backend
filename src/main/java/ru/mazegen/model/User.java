package ru.mazegen.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "users")
public final class User {

    public enum Role {
        USER, ADMIN
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true)
    private @Nullable String googleId; // auth with Google

    @Column(nullable = false)
    @ElementCollection(fetch = FetchType.LAZY)
    private @NotNull List<@NotNull String> refreshTokens = new ArrayList<>();

    @Column(nullable = false)
    private @NotNull String nickname;

    private @Nullable String profilePictureUrl;
    private @Nullable LocalDateTime registrationDate;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private @NotNull Role role = Role.USER;

    @Column(nullable = false)
    @ElementCollection(fetch = FetchType.LAZY)
    private @NotNull List<@NotNull MazePath> mazePaths = new ArrayList<>();


    public User(String googleId, String profilePictureUrl, @NotNull String nickname,
                LocalDateTime registrationDate) {
        this.googleId = googleId;
        this.profilePictureUrl = profilePictureUrl;
        this.registrationDate = registrationDate;
        this.nickname = nickname;
    }
}
