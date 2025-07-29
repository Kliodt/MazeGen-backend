package ru.mazegen.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CompositeType;

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

    @Column(unique = true, nullable = false)
    private String googleId; // auth with Google

    @ElementCollection(fetch = FetchType.LAZY)
    private List<String> refreshTokens = new ArrayList<>();

    @Column(nullable = false)
    private String nickname;

    private String profilePictureUrl;

    private LocalDateTime registrationDate;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;

    @OneToMany(
            orphanRemoval = true,
            cascade = {CascadeType.ALL},
            fetch = FetchType.LAZY
    )
    private List<MazePath> mazePaths = new ArrayList<>();


    public User(String googleId, String profilePictureUrl, String nickname,
                LocalDateTime registrationDate) {
        this.googleId = googleId;
        this.profilePictureUrl = profilePictureUrl;
        this.registrationDate = registrationDate;
        this.nickname = nickname;
    }
}
