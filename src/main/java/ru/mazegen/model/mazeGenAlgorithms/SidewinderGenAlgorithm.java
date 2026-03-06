package ru.mazegen.model.mazeGenAlgorithms;


import org.jetbrains.annotations.NotNull;
import ru.mazegen.model.Maze;
import ru.mazegen.model.MazeGenerator;
import ru.mazegen.model.grids.Grid;

import java.util.Random;


public class SidewinderGenAlgorithm implements MazeGenAlgorithm {


    private static final Random random = new Random();


    @Override
    public @NotNull Maze generateMaze(@NotNull MazeGenerator generator) {

        Grid grid = new Grid(generator.getWidth(), generator.getHeight(), true);
        Maze maze = new Maze(grid, generator.getStartX(), generator.getStartY(),
                generator.getFinishX(), generator.getFinishY());

        // clear 1st row
        for (int i = 0; i < grid.getSizeX(); i++) {
            grid.deactivateCellEdges(i, 0, Grid.Edge.RIGHT);
        }

        // other rows
        for (int y = 1; y < grid.getSizeY(); y++) {
            int groupFirst = 0;
            int groupSize = 1;

            for (int x = 0; x < grid.getSizeX(); x++) {
                if (x + 1 == grid.getSizeX() || random.nextBoolean()) {
                    // beak the top wall
                    int idx = groupFirst + random.nextInt(groupSize);

                    grid.deactivateCellEdges(idx, y, Grid.Edge.TOP);

                    groupFirst = x + 1;
                    groupSize = 1;
                } else {
                    // extend group
                    groupSize++;
                    grid.deactivateCellEdges(x, y, Grid.Edge.RIGHT);
                }
            }
        }

        grid.addBorder();
        return maze;
    }


    @Override
    public @NotNull String getFullAlgorithmName() {
        return "Sidewinder";
    }
}
