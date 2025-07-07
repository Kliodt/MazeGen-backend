package ru.mazegen.model;

import lombok.AllArgsConstructor;
import ru.mazegen.model.grids.Grid;

@AllArgsConstructor
public final class Maze {
    public final Grid grid;
    public final int startX;
    public final int startY;
    public final int exitX;
    public final int exitY;
}
