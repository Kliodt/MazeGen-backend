package ru.mazegen.model.generators;

import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * Parameters for the maze generators.
 * Depending on the maze generator parameter may be used or ignored.
 * Required parameters are in the constructor.
 * @see MazeGenerator
 */
@RequiredArgsConstructor
@ToString
public final class MazeGeneratorParameters {
    public final int width;
    public final int height;
    public final int startX;
    public final int startY;
    public final int finishX;
    public final int finishY;
}
