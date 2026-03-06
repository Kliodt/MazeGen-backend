package ru.mazegen.model.mazeGenAlgorithms;


import org.jetbrains.annotations.NotNull;
import ru.mazegen.model.Maze;
import ru.mazegen.model.MazeGenerator;
import ru.mazegen.model.grids.Grid;

import java.util.Random;


// https://weblog.jamisbuck.org/2010/12/29/maze-generation-eller-s-algorithm


public class EllerGenAlgorithm implements MazeGenAlgorithm {


    private static final Random random = new Random();


    private static class CellSet {
        boolean hasVerticalConnection = false;
    }


    private void combineSets(CellSet[] row, CellSet set1, CellSet set2) {
        assert set1 != set2 : "sets are the same";
        var newSet = new CellSet();
        for (int i = 0; i < row.length; i++) {
            if (row[i] == set1 || row[i] == set2) {
                newSet.hasVerticalConnection |= row[i].hasVerticalConnection;
                row[i] = newSet;
            }
        }
    }


    @Override
    public @NotNull Maze generateMaze(@NotNull MazeGenerator generator) {
        Grid grid = new Grid(generator.getWidth(), generator.getHeight(), true);
        Maze maze = new Maze(grid, generator.getStartX(), generator.getStartY(),
                generator.getFinishX(),
                generator.getFinishY());

        CellSet[] row = new CellSet[grid.getSizeX()];

        for (int y = 0; ; y++) {

            for (int x = 0; x < row.length; x++) {
                if (row[x] == null)
                    row[x] = new CellSet();
            }

            // last row
            if (y == grid.maxCellY()) {
                for (int x = 0; x < row.length - 1; x++) {
                    if (row[x] != row[x + 1]) {
                        grid.deactivateCellEdges(x, y, Grid.Edge.RIGHT);
                        combineSets(row, row[x + 1], row[x]);
                    }
                }
                break;
            }

            // horizontal connections
            for (int x = 0; x < row.length - 1; x++) {
                if (row[x] != row[x + 1] && random.nextBoolean()) {
                    grid.deactivateCellEdges(x, y, Grid.Edge.RIGHT);
                    combineSets(row, row[x + 1], row[x]);
                }
            }

            // vertical connections
            for (int x = 0; x < row.length; x++) {
                if (random.nextBoolean()) {
                    grid.deactivateCellEdges(x, y, Grid.Edge.BOTTOM);
                    row[x].hasVerticalConnection = true;
                }
            }
            for (int x = 0; x < row.length; x++) {
                if (!row[x].hasVerticalConnection) {
                    grid.deactivateCellEdges(x, y, Grid.Edge.BOTTOM);
                    row[x].hasVerticalConnection = true;
                }
            }

            // next row
            for (int x = 0; x < row.length; x++) {
                if (grid.isCellEdgeActive(x, y, Grid.Edge.BOTTOM)) {
                    row[x] = null;
                } else {
                    row[x].hasVerticalConnection = false;
                }
            }
        }

        grid.addBorder();
        return maze;
    }


    @Override
    public @NotNull String getFullAlgorithmName() {
        return "Eller";
    }
}
