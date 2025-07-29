package ru.mazegen.controllers;

import graphql.schema.DataFetchingEnvironment;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import ru.mazegen.model.MazeGenerator;
import ru.mazegen.model.Maze;
import ru.mazegen.repository.MazeRepository;
import ru.mazegen.controllers.Error.Code;
import ru.mazegen.services.MazeService;

import java.time.format.DateTimeFormatter;


@Controller
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000"})
public class MazeController {

    private final MazeService mazeService;

    @QueryMapping
    public Object getMazeById(@Argument @NonNull String id) {
        try {
            var ret = mazeService.getMazeById(Long.parseLong(id));
            if (ret != null) {
                return ret;
            }
            return new Error(Code.NOT_EXISTS, "Maze id '" + id + "' not exists!");
        } catch (NumberFormatException e) {
            return new Error(Code.BAD_FORMAT, "Id must be integer!");
        }
    }


    @MutationMapping
    public Object generateMaze(@Argument @NonNull MazeGenerator parameters, @Argument String userId) {
        var ret = parameters.generate();
        if (ret == null) {
            return new Error(Code.INVALID_PARAMETERS, "Invalid parameters! " +
                    "Check that 'algorithmKeyStr' is an existing key!");
        }
        if (userId != null) {
//            mazeRepository.save(ret);
            // todo: save user id with maze
        }
        return ret;
    }

    // ---------------------- converters ----------------------

    @SchemaMapping(typeName = "Maze", field = "genDate")
    public String creationDateAsString(Maze maze) {
        return DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss").format(maze.getGenDate());
    }

    @SchemaMapping(typeName = "Maze", field = "grid")
    public byte[][] resolveGridField(Maze maze) {
        return maze.getGrid().getEdges();
    }
}
