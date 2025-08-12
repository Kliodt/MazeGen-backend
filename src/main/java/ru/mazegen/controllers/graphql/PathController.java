package ru.mazegen.controllers.graphql;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import ru.mazegen.model.MazePath;
import ru.mazegen.security.JWTUserInfo;
import ru.mazegen.services.PathService;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class PathController {

    private final PathService pathService;


    @MutationMapping
    public boolean saveMazePath(
            @Argument List<List<Integer>> points,
            @Argument long mazeId,
            @AuthenticationPrincipal JWTUserInfo userInfo
    ) {
        if (userInfo == null) {
            log.warn("Path save from unauthenticated user");
            return false;
        }
        return null != pathService.updatePathPointsForUserAndMaze(points, userInfo.getUserId(), mazeId, false);
    }


    @MutationMapping
    @Nullable
    public MazePath submitMazeCompletion(
            @Argument List<List<Integer>> points,
            @Argument long mazeId,
            @AuthenticationPrincipal JWTUserInfo userInfo
    ) {
        if (userInfo == null) {
            log.warn("Maze completion from unauthenticated user");
            return null;
        }
        return pathService.updatePathPointsForUserAndMaze(points, userInfo.getUserId(), mazeId, true);
    }

}