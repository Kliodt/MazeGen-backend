package ru.mazegen.model.generators;

import lombok.NonNull;
import ru.mazegen.model.Maze;

public interface MazeGenerator {

    /**
     * Create a new maze on a grid
     */
    @NonNull
    Maze generateMaze(@NonNull MazeGeneratorParameters par);
}
