package ru.mazegen.controllers.graphql;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import ru.mazegen.model.User;
import ru.mazegen.security.JWTUserInfo;
import ru.mazegen.services.UserService;

@Controller
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @QueryMapping
    public User getUserById(@Argument @NotNull Long userId) {
        return userService.getUserById(userId);
    }


    // --------------------- resolve private fields ---------------------

    @SchemaMapping(typeName = "User", field = "role")
    public String resolveRole(User user, @AuthenticationPrincipal JWTUserInfo userInfo) {
        if (user.getId() != userInfo.getUserId()) return null;
        return user.getRole().toString();
    }
}
