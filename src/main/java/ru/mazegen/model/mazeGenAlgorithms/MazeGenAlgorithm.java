package ru.mazegen.model.mazeGenAlgorithms;

import org.jetbrains.annotations.NotNull;
import ru.mazegen.model.Maze;
import ru.mazegen.model.MazeGenerator;

public interface MazeGenAlgorithm {

    /**
     * Create a new maze on a grid
     */
    @NotNull
    Maze generateMaze(@NotNull MazeGenerator generator);

    @NotNull
    String getFullAlgorithmName();
}
