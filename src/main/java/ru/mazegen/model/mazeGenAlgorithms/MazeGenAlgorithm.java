package ru.mazegen.model.mazeGenAlgorithms;

import lombok.NonNull;
import ru.mazegen.model.Maze;
import ru.mazegen.model.MazeGenerator;

public interface MazeGenAlgorithm {

    /**
     * Create a new maze on a grid
     */
    @NonNull
    Maze generateMaze(@NonNull MazeGenerator par);

    @NonNull
    String getFullAlgorithmName();
}
