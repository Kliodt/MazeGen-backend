package ru.mazegen.model;

import lombok.*;
import ru.mazegen.mazeGenAlgorithms.EmptyMazeGenAlgorithm;
import ru.mazegen.mazeGenAlgorithms.MazeGenAlgorithm;
import ru.mazegen.mazeGenAlgorithms.RandomMazeGenAlgorithm;

import java.util.*;

/**
 * Parameters for the maze generators.
 * Depending on the maze generator parameter may be used or ignored.
 * Required parameters are in the constructor.
 * @see MazeGenAlgorithm
 */
@Data
public final class MazeGenerator {
    private int width;
    private int height;
    private String algorithm;

    private int startX;
    private int startY;
    private int finishX;
    private int finishY;


    private static final Map<String, MazeGenAlgorithm> generators = Map.of(
            "empty", new EmptyMazeGenAlgorithm(),
            "random", new RandomMazeGenAlgorithm()
    );


    // utility method
    private static int clamp(int val, int min, int max) {
        return Math.min(Math.max(val, min), max);
    }


    // make fields valid where possible and return true, return false otherwise
    private boolean validateFields() {
        if (algorithm == null) return false;

        width = clamp(width, 3, 1000);
        height = clamp(height, 3, 1000);

        algorithm = algorithm.toLowerCase();

        startX = clamp(startX, 0, width - 1);
        startY = clamp(startY, 0, height - 1);
        finishX = clamp(finishX, 0, width - 1);
        finishY = clamp(finishY, 0, height - 1);

        if (startX == finishX && startY == finishY) {
            startX = 0;
            startY = 0;
            finishX = width - 1;
            finishY = height - 1;
        }

        return true;
    }


    public Maze generateMaze() {
        if (!validateFields()) {
            return null;
        }

        var generator = generators.get(algorithm);

        if (generator == null) {
            return null;
        }

        return generator.generateMaze(this);
    }

}
