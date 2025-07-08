package ru.mazegen.mazeGenAlgorithms;

import lombok.NonNull;
import ru.mazegen.model.Maze;
import ru.mazegen.model.MazeGenerator;
import ru.mazegen.model.grids.Grid;


public class EmptyMazeGenAlgorithm implements MazeGenAlgorithm {

    @Override
    @NonNull
    public Maze generateMaze(@NonNull MazeGenerator par) {
        Grid grid = new Grid(par.getWidth(), par.getHeight(), false);
        Maze maze = new Maze(grid, par.getStartX(), par.getStartY(), par.getFinishX(), par.getFinishY());

        grid.addBorder();

        return maze;
    }
}
