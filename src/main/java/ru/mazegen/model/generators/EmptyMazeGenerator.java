package ru.mazegen.model.generators;

import lombok.NonNull;
import ru.mazegen.model.Maze;
import ru.mazegen.model.grids.Grid;


public class EmptyMazeGenerator implements MazeGenerator {

    @Override
    @NonNull
    public Maze generateMaze(@NonNull MazeGeneratorParameters par) {
        Grid grid = new Grid(par.width, par.height, false);
        Maze maze = new Maze(grid, par.startX, par.startY, par.finishX, par.finishY);

        grid.addBorder();

        return maze;
    }
}
