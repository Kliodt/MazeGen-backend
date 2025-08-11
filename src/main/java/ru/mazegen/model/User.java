package ru.mazegen.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
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

    @Column(unique = true, length = 64)
    private @Nullable String googleId; // auth with Google

    @Column(nullable = false, columnDefinition = "text")
    @ElementCollection(fetch = FetchType.LAZY)
    private @NotNull List<@NotNull String> refreshTokens = new ArrayList<>();

    @Column(nullable = false, length = 64) // todo: validation (including name that we got from google)
    private @NotNull String nickname;


    private @Nullable String profilePictureUrl;
    private @Nullable OffsetDateTime registrationDate;

    @Column(nullable = false, length = 32)
    @Enumerated(EnumType.STRING)
    private @NotNull Role role = Role.USER;

//    @Column(nullable = false)
//    @ElementCollection(fetch = FetchType.LAZY)
//    private @NotNull List<@NotNull MazePath> mazePaths = new ArrayList<>();

    public User(@Nullable String googleId,
                @Nullable String profilePictureUrl,
                @NotNull String nickname,
                @Nullable OffsetDateTime registrationDate
    ) {
        this.googleId = googleId;
        this.profilePictureUrl = profilePictureUrl;
        this.registrationDate = registrationDate;
        this.nickname = nickname;
    }
}
