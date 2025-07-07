package ru.mazegen.model.grids;

import lombok.Getter;
import ru.mazegen.model.exceptions.GridFormatException;

import java.util.*;

public class Grid {

    /**
     * This enum represents edges of each individual cell of the grid
     */
    public enum Edge {
        TOP, BOTTOM, RIGHT, LEFT
    }

    /**
     * Aka grid width
     */
    @Getter
    private final int sizeX;

    /**
     * Aka grid height
     */
    @Getter
    private final int sizeY;


    private final ArrayList<ArrayList<Boolean>> edges;


    public Grid(int sizeX, int sizeY, boolean filledGrid) {
        if (sizeX <= 0 || sizeY <= 0) throw new RuntimeException("Negative grid size");
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.edges = new ArrayList<>();
        // init edges matrix
        for (int i = 0; i < sizeY * 2 + 1; i++) {
            var next = new ArrayList<Boolean>(sizeX * 2 + 1);
            for (int j = 0; j < sizeX + 1; j++) {
                next.add(filledGrid);
            }
            this.edges.add(next);
        }
    }

    public Grid(String[] compactStringRepresentation) throws GridFormatException {
        validateCompactRepresentation(compactStringRepresentation);
        this.edges = parseCompactRepresentation(compactStringRepresentation);
        validateGridStructure();
        
        // Calculate grid dimensions from edges matrix
        this.sizeY = (edges.size() - 1) / 2;
        this.sizeX = edges.get(0).size() - 1;
    }

    private void validateCompactRepresentation(String[] compactStringRepresentation) throws GridFormatException{
        if (compactStringRepresentation == null) {
            throw new GridFormatException("Compact representation cannot be null");
        } 
        if (compactStringRepresentation.length == 0) {
            throw new GridFormatException("Compact representation cannot be empty");
        }
    }

    private ArrayList<ArrayList<Boolean>> parseCompactRepresentation(String[] compactStringRepresentation) throws GridFormatException {
        var parsedEdges = new ArrayList<ArrayList<Boolean>>();
        
        for (int i = 0; i < compactStringRepresentation.length; i++) {
            String row = compactStringRepresentation[i];
            if (row == null) {
                throw new GridFormatException("Row " + i + " in compact representation cannot be null");
            }
            
            var edgeRow = new ArrayList<Boolean>(row.length());
            for (int j = 0; j < row.length(); j++) {
                char c = row.charAt(j);
                if (c == '1') {
                    edgeRow.add(true);
                } else if (c == '0') {
                    edgeRow.add(false);
                } else {
                    throw new GridFormatException("Invalid character '" + c + "' at position [" + i + "][" + j + "]. Only '0' and '1' are allowed");
                }
            }
            parsedEdges.add(edgeRow);
        }
        
        return parsedEdges;
    }

    private void validateGridStructure() throws GridFormatException {
        if (edges.size() < 3 || edges.size() % 2 == 0) {
            throw new GridFormatException("Invalid grid structure: edges matrix must have odd number of rows >= 3");
        }
        
        // Check that all rows have the same length
        int expectedRowLength = edges.get(0).size();
        for (int i = 1; i < edges.size(); i++) {
            if (edges.get(i).size() != expectedRowLength) {
                throw new GridFormatException("Inconsistent row lengths: row 0 has " + expectedRowLength + " elements, but row " + i + " has " + edges.get(i).size() + " elements");
            }
        }
        
        // Validate calculated dimensions
        int calculatedSizeY = (edges.size() - 1) / 2;
        int calculatedSizeX = expectedRowLength - 1;
        
        if (calculatedSizeX <= 0 || calculatedSizeY <= 0) {
            throw new GridFormatException("Invalid grid dimensions calculated: sizeX=" + calculatedSizeX + ", sizeY=" + calculatedSizeY);
        }
    }


    public final int maxCellX() {
        return sizeX - 1;
    }

    public final int maxCellY() {
        return sizeY - 1;
    }


    private void setCellEdge(int cellX, int cellY, Edge edge, boolean value) {
        switch (edge) {
            case TOP -> edges.get(cellY * 2).set(cellX, value);
            case BOTTOM -> edges.get(cellY * 2 + 2).set(cellX, value);
            case LEFT -> edges.get(cellY * 2 + 1).set(cellX, value);
            case RIGHT -> edges.get(cellY * 2 + 1).set(cellX + 1, value);
        }
    }


    private boolean getCellEdge(int cellX, int cellY, Edge edge) {
        return switch (edge) {
            case TOP -> edges.get(cellY * 2).get(cellX);
            case BOTTOM -> edges.get(cellY * 2 + 2).get(cellX);
            case LEFT -> edges.get(cellY * 2 + 1).get(cellX);
            case RIGHT -> edges.get(cellY * 2 + 1).get(cellX + 1);
        };
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
        for (int x = 0; x < sizeX; x++) {
            setCellEdge(x, 0, Edge.TOP, true);
            setCellEdge(x, sizeY - 1, Edge.BOTTOM, true);
        }
        for (int y = 0; y < sizeY; y++) {
            setCellEdge(0, y, Edge.LEFT, true);
            setCellEdge(sizeX - 1, y, Edge.RIGHT, true);
        }
    }

    
    /**
     * Space-efficient grid representation used to store and transfer the grid
     */
    public String[] getCompactStringRepresentation() {
        var ret = new String[edges.size()];
        for (int i = 0; i < edges.size(); i++) {
            var row = edges.get(i);
            var bytes = new char[row.size()];
            for (int j = 0; j < row.size(); j++) {
                bytes[j] = row.get(j) ? '1' : '0';
            }
            ret[i] = new String(bytes);
        }
        return ret;
    }
}
