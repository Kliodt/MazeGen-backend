package ru.mazegen.model;

import lombok.RequiredArgsConstructor;
import ru.mazegen.model.grids.Grid;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Date;

@RequiredArgsConstructor
public final class Maze {

    public long id;

    public final Grid grid;
    public final int startX;
    public final int startY;
    public final int finishX;
    public final int finishY;

    public User author;
    public String algorithm;
    public LocalDateTime genDate;
    public int genDurationMs;





//    public Maze(Grid grid, int startX, int startY, int finishX, int finishY) {
//        this.grid = grid;
//        this.startX = startX;
//        this.startY = startY;
//        this.finishX = finishX;
//        this.finishY = finishY;
//    }
}
