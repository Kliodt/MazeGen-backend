package ru.mazegen.controllers.graphql;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import ru.mazegen.model.Maze;
import ru.mazegen.model.MazeGenerator;
import ru.mazegen.model.MazePath;
import ru.mazegen.repository.MazePathRepository;
import ru.mazegen.repository.MazeRepository;
import ru.mazegen.security.JWTUserInfo;
import ru.mazegen.services.MazeService;

import java.util.List;


@Controller
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000"})
@Slf4j
public class MazeController {


    public record GeneratorResult(Maze maze, int errorCode, String errorDescription) {
    }

    private final MazeService mazeService;


    @QueryMapping
    public Maze getMazeById(@Argument @NotNull Long mazeId) {
        return mazeService.getMazeById(mazeId);
    }


    @QueryMapping
    public List<Maze> getMazesByUser(
            @Argument long userId,
            @Argument long pivotId,
            @Argument int pageNum,
            @Argument int pageSize
    ) {
        return mazeService.getMazesByAuthor(userId, pivotId, pageNum, pageSize);
    }


    @QueryMapping
    public List<Maze> getRecentMazes(
            @Argument long pivotId,
            @Argument int pageNum,
            @Argument int pageSize
    ) {
        return mazeService.getRecentMazes(pivotId, pageNum, pageSize);
    }


    @MutationMapping
    public GeneratorResult generateMaze(
        @Argument @NotNull MazeGenerator parameters,
        @AuthenticationPrincipal JWTUserInfo userInfo
    ) {
        var maze = parameters.generate();
        if (maze == null) {
            return new GeneratorResult(null, -1, "Invalid parameters! Check that 'algorithmKeyStr' is an existing key!");
        }

        if (userInfo != null) {
            mazeService.saveMazeForAuthor(maze, userInfo.getUserId());
        } else {
            mazeService.saveMazeForAuthor(maze, null);
        }

        return new GeneratorResult(maze, 0, null);
    }


    // ---------------------- converters ----------------------

    @SchemaMapping(typeName = "Maze", field = "grid")
    public byte[][] resolveGridField(@NotNull Maze maze) {
        return maze.getGrid().getEdges();
    }

    // ---------------------- synthetic fields ----------------------

    @SchemaMapping(typeName = "Maze", field = "path")
    public MazePath getPathForUser(@NotNull Maze maze, @AuthenticationPrincipal JWTUserInfo userInfo) {
        if (userInfo == null) return null;
        return mazeService.getPathByUserAndMaze(userInfo.getUserId(), maze.getId());
    }


}
