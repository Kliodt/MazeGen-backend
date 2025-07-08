package ru.mazegen.controllers;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import ru.mazegen.model.MazeGenerator;
import ru.mazegen.model.Maze;
import ru.mazegen.model.grids.Grid;

import java.time.format.DateTimeFormatter;

@Controller
public class MazeController {
    @QueryMapping
    public Maze getMazeById(@Argument String id) {
        var ret = new Maze(new Grid(3, 3, true), 0, 0, 3, 3);
//        ret.genDate = new Date();
        ret.algorithm = id;
        ret.genDurationMs = 123456;
//        ret.id = ;
        return ret;
    }

    @MutationMapping
    public Maze generateMaze(@Argument MazeGenerator parameters, @Argument String userId) {
        var ret = new Maze(new Grid(3, 3, true), 0, 0, 3, 3);
        ret.id = parameters.getStartX();
        ret.algorithm = userId;
        return ret;
    }

    @SchemaMapping(typeName = "Maze", field = "genDate")
    public String creationDateAsString(Maze maze) {
        return DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss").format(maze.genDate);
    }

    @SchemaMapping(typeName = "Maze", field = "grid")
    public byte[][] resolveGridField(Maze maze) {
        return maze.grid.getEdges();
    }
}
