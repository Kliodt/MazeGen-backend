package ru.mazegen.mazeGenAlgorithms;

import java.util.ArrayList;
import java.util.Random;

import lombok.NonNull;
import ru.mazegen.model.Maze;
import ru.mazegen.model.MazeGenerator;
import ru.mazegen.model.grids.Grid;
import ru.mazegen.model.grids.Grid.Edge;


/**
 * Generates completely random maze. Maybe impossible. This generator exists only 
 * for testing purposes (and fuzzy testing in the future)
 */
public class RandomMazeGenAlgorithm implements MazeGenAlgorithm {
    private final Random random = new Random();

    @Override
    @NonNull
    public Maze generateMaze(@NonNull MazeGenerator par) {
        Grid grid = new Grid(par.getWidth(), par.getHeight(), false);
        Maze maze = new Maze(grid, par.getStartX(), par.getStartY(), par.getFinishX(), par.getFinishY());

        for (int y = 0; y < grid.getSizeY(); y++) {
            for (int x = 0; x < grid.getSizeX(); x++) {

                var edges = new ArrayList<Edge>();

                for (var val : Edge.values()) {
                    if (random.nextInt(2) == 0) {
                        edges.add(val);
                    }
                }
  
                grid.setAllCellEdges(x, y, edges.toArray(new Edge[0]));
            }
        }

        grid.addBorder();

        return maze;
    }
}
