package ru.mazegen.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.mazegen.model.grids.Grid;

import static org.junit.jupiter.api.Assertions.*;

class MazeTest {


    @Test
    void isValidMoveTest() {

        var emptyMaze3x3 = new Maze(new Grid(3, 3, false), 0, 0, 2, 2);

        // diagonal
        assertFalse(emptyMaze3x3.isValidMove(0,0,1,1));
        assertFalse(emptyMaze3x3.isValidMove(0,0,2,1));

        // too long
        assertFalse(emptyMaze3x3.isValidMove(1,0,1,2));

        // no move
        assertFalse(emptyMaze3x3.isValidMove(1,1,1,1));

        // out of bounds
        assertFalse(emptyMaze3x3.isValidMove(0,0,0,-1));
        assertFalse(emptyMaze3x3.isValidMove(0,0,-1,0));
        assertFalse(emptyMaze3x3.isValidMove(2,2,3,2));
        assertFalse(emptyMaze3x3.isValidMove(2,2,2,3));

        // no edges
        assertTrue(emptyMaze3x3.isValidMove(1,1,0,1));
        assertTrue(emptyMaze3x3.isValidMove(1,1,1,0));
        assertTrue(emptyMaze3x3.isValidMove(1,1,2,1));
        assertTrue(emptyMaze3x3.isValidMove(1,1,1,2));

        // all edges
        var maze1 = new Maze(new Grid(3, 3, false), 0, 0, 2, 2);
        maze1.getGrid().activateCellEdges(1,1, Grid.Edge.BOTTOM, Grid.Edge.LEFT, Grid.Edge.TOP, Grid.Edge.RIGHT);

        assertFalse(maze1.isValidMove(1,1,0,1));
        assertFalse(maze1.isValidMove(1,1,1,0));
        assertFalse(maze1.isValidMove(1,1,2,1));
        assertFalse(maze1.isValidMove(1,1,1,2));


        // single horizontal edge
        var maze2 = new Maze(new Grid(3, 4, false), 0, 0, 2, 2);
        maze2.getGrid().activateCellEdges(1,1, Grid.Edge.BOTTOM);

        assertTrue(maze2.isValidMove(1,2,0,2));
        assertTrue(maze2.isValidMove(1,2,2,2));
        assertFalse(maze2.isValidMove(1,2,1,1));
        assertTrue(maze2.isValidMove(1,2,1,3));

        assertTrue(maze2.isValidMove(1,1,0,1));
        assertTrue(maze2.isValidMove(1,1,2,1));
        assertTrue(maze2.isValidMove(1,1,1,0));
        assertFalse(maze2.isValidMove(1,1,1,2));

        // single vertical edge
        var maze3 = new Maze(new Grid(4, 3, false), 0, 0, 2, 2);
        maze3.getGrid().activateCellEdges(1,1, Grid.Edge.RIGHT);

        assertTrue(maze3.isValidMove(1,1,0,1));
        assertFalse(maze3.isValidMove(1,1,2,1));
        assertTrue(maze3.isValidMove(1,1,1,0));
        assertTrue(maze3.isValidMove(1,1,1,2));

        assertFalse(maze3.isValidMove(2,1,1,1));
        assertTrue(maze3.isValidMove(2,1,3,1));
        assertTrue(maze3.isValidMove(2,1,2,0));
        assertTrue(maze3.isValidMove(2,1,2,2));
    }
}