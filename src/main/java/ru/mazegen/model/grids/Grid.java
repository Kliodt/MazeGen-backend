package ru.mazegen.model.grids;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;

import java.util.*;

import jakarta.annotation.Nonnull;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
public class Grid {

    /**
     * This enum represents edges of each individual cell of the grid
     */
    public enum Edge {
        TOP, BOTTOM, RIGHT, LEFT
    }

    @Id
    @GeneratedValue
    private long id;

    @Lob
    @Column(nullable = false)
    private byte[][] edges; // Matrix representing the maze edges


    public Grid(int sizeX, int sizeY, boolean filledGrid) {
        if (sizeX <= 0 || sizeY <= 0) throw new RuntimeException("Negative grid size");
        this.edges = new byte[sizeY * 2 + 1][];
        // init edges matrix
        for (int i = 0; i < this.edges.length; i++) {
            this.edges[i] = new byte[sizeX + 1];
            Arrays.fill(this.edges[i], (byte)(filledGrid ? 1 : 0));
        }
    }


    public Grid(byte[][] edges) throws GridFormatException {
        if (edges == null) throw new GridFormatException("Grid can't be null");
        validateGridStructure(edges);
        this.edges = edges;
    }


    private void validateGridStructure(@Nonnull byte[][] edges) throws GridFormatException {
        if (edges.length < 3 || edges.length % 2 == 0) {
            throw new GridFormatException("Invalid grid structure: edges matrix must have odd number of rows >= 3, but actual is " + edges.length);
        }

        // Check nulls
        for (int i = 1; i < edges.length; i++) {
            if (edges[i] == null) {
                throw new GridFormatException("Grid row can't be null: row " + i + " is null");
            }
        }
        
        // Check that all rows have the same length >= 2
        int expectedRowLength = edges[0].length;
        for (int i = 1; i < edges.length; i++) {
            if (edges[i].length != expectedRowLength) {
                throw new GridFormatException("Inconsistent row lengths: row 0 has " + expectedRowLength + " elements, but row " + i + " has " + edges[i].length + " elements");
            }
        }

        if (expectedRowLength < 2) {
            throw new GridFormatException("Invalid grid structure: matrix must have number of columns >= 2, but actual is " + expectedRowLength);
        }
    }
        

    public final int getSizeX() {
        return edges.length > 0 ? edges[0].length - 1: 0;
    }

    public final int getSizeY() {
        return (edges.length - 1) / 2;
    }

    public final int maxCellX() {
        return getSizeX() - 1;
    }

    public final int maxCellY() {
        return getSizeY() - 1;
    }


    private void setCellEdge(int cellX, int cellY, Edge edge, boolean value) {
        var val = (byte) (value ? 1 : 0);
        switch (edge) {
            case TOP    -> edges[cellY * 2]    [cellX]     = val;
            case BOTTOM -> edges[cellY * 2 + 2][cellX]     = val;
            case LEFT   -> edges[cellY * 2 + 1][cellX]     = val;
            case RIGHT  -> edges[cellY * 2 + 1][cellX + 1] = val;
        }
    }


    private boolean getCellEdge(int cellX, int cellY, Edge edge) {
        var val = switch (edge) {
            case TOP    -> edges[cellY * 2]    [cellX];
            case BOTTOM -> edges[cellY * 2 + 2][cellX];
            case LEFT   -> edges[cellY * 2 + 1][cellX];
            case RIGHT  -> edges[cellY * 2 + 1][cellX + 1];
        };
        return val != 0;
    }


    /**
     * Activate given edges, deactivate others
     */
    public void setAllCellEdges(int cellX, int cellY, Edge... activeEdges) {
        Set<Edge> remaining = new HashSet<>(Set.of(Edge.values()));
        for (Edge edge : activeEdges) {
            setCellEdge(cellX, cellY, edge, true);
            remaining.remove(edge);
        }
        for (Edge edge : remaining) {
            setCellEdge(cellX, cellY, edge, false);
        }
    }


    /**
     * Activate specified edges for the cell
     */
    public void activateCellEdges(int cellX, int cellY, Edge... edges) {
        for (var edge : edges) {
            setCellEdge(cellX, cellY, edge, true);
        }
    }


    /**
     * Deactivate specified edges for the cell
     */
    public void deactivateCellEdges(int cellX, int cellY, Edge... edges) {
        for (var edge : edges) {
            setCellEdge(cellX, cellY, edge, false);
        }
    }


    /**
     * Get active edges for the cell
     */
    public List<Edge> getCellActiveEdges(int cellX, int cellY) {
        return Arrays.stream(Edge.values())
                .filter(edge -> getCellEdge(cellX, cellY, edge))
                .toList();
    }

    /**
     * Is a certain edge of a certain cell active
     */
    public boolean isCellEdgeActive(int cellX, int cellY, Edge edge) {
        return getCellEdge(cellX, cellY, edge);
    }


    /**
     * Add border to the grid
     */
    public void addBorder() {
        for (int x = 0; x < getSizeX(); x++) {
            setCellEdge(x, 0, Edge.TOP, true);
            setCellEdge(x, getSizeY() - 1, Edge.BOTTOM, true);
        }
        for (int y = 0; y < getSizeY(); y++) {
            setCellEdge(0, y, Edge.LEFT, true);
            setCellEdge(getSizeX() - 1, y, Edge.RIGHT, true);
        }
    }
}
