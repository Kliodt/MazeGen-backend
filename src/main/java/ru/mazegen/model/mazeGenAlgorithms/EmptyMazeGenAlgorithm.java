package ru.mazegen.model.mazeGenAlgorithms;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.mazegen.model.Maze;
import ru.mazegen.model.MazeGenerator;
import ru.mazegen.model.grids.Grid;


public class EmptyMazeGenAlgorithm implements MazeGenAlgorithm {

    @Override
    @NotNull
    public Maze generateMaze(@NotNull MazeGenerator generator) {
        Grid grid = new Grid(generator.getWidth(), generator.getHeight(), false);
        Maze maze = new Maze(grid, generator.getStartX(), generator.getStartY(), generator.getFinishX(), generator.getFinishY());

        grid.addBorder();

        return maze;
    }

    @Override
    @NotNull
    public String getFullAlgorithmName() {
        return "Empty";
    }
}
