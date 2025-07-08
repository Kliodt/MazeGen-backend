package ru.mazegen.model.grids;

import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

class GridTest {
    
    private Grid emptyGrid;
    private Grid filledGrid;
    private static final int TEST_SIZE_X = 3;
    private static final int TEST_SIZE_Y = 4;
    
    @BeforeEach
    void setUp() {
        emptyGrid = new Grid(TEST_SIZE_X, TEST_SIZE_Y, false);
        filledGrid = new Grid(TEST_SIZE_X, TEST_SIZE_Y, true);
    }
    
    @Test
    void testConstructorValidSizes() {
        Grid grid = new Grid(5, 10, false);
        assertEquals(5, grid.getSizeX());
        assertEquals(10, grid.getSizeY());
    }
    
    @Test
    void testConstructorInvalidSizes() {
        assertThrows(RuntimeException.class, () -> new Grid(0, 5, false));
        assertThrows(RuntimeException.class, () -> new Grid(5, 0, false));
        assertThrows(RuntimeException.class, () -> new Grid(-1, 5, false));
        assertThrows(RuntimeException.class, () -> new Grid(5, -1, false));
        assertThrows(RuntimeException.class, () -> new Grid(-1, -1, false));
    }

    @Test
    void testEmptyAndFilledGrid() {
        // Test all edges for all cells
        for (int y = 0; y < emptyGrid.getSizeY(); y++) {
            for (int x = 0; x < emptyGrid.getSizeX(); x++) {
                for (Grid.Edge edge : Grid.Edge.values()) {
                    assertFalse(emptyGrid.isCellEdgeActive(x, y, edge));
                    assertTrue(filledGrid.isCellEdgeActive(x, y, edge));
                }
            }
        }
    }
    
    @Test
    void testMaxCellX() {
        assertEquals(TEST_SIZE_X - 1, emptyGrid.maxCellX());
        assertEquals(2, emptyGrid.maxCellX());
    }
    
    @Test
    void testMaxCellY() {
        assertEquals(TEST_SIZE_Y - 1, emptyGrid.maxCellY());
        assertEquals(3, emptyGrid.maxCellY());
    }
    
    @Test
    void testActivateCellEdges() {
        // Test activating single edge
        emptyGrid.activateCellEdges(1, 1, Grid.Edge.TOP);
        assertTrue(emptyGrid.isCellEdgeActive(1, 1, Grid.Edge.TOP));
        assertFalse(emptyGrid.isCellEdgeActive(1, 1, Grid.Edge.BOTTOM));
        assertFalse(emptyGrid.isCellEdgeActive(1, 1, Grid.Edge.LEFT));
        assertFalse(emptyGrid.isCellEdgeActive(1, 1, Grid.Edge.RIGHT));
        
        // Test activating two more edges
        emptyGrid.activateCellEdges(1, 1, Grid.Edge.LEFT, Grid.Edge.RIGHT);
        assertTrue(emptyGrid.isCellEdgeActive(1, 1, Grid.Edge.TOP));
        assertTrue(emptyGrid.isCellEdgeActive(1, 1, Grid.Edge.LEFT));
        assertTrue(emptyGrid.isCellEdgeActive(1, 1, Grid.Edge.RIGHT));
        assertFalse(emptyGrid.isCellEdgeActive(1, 1, Grid.Edge.BOTTOM));
        
        // Test activating all edges
        emptyGrid.activateCellEdges(0, 0, Grid.Edge.TOP, Grid.Edge.BOTTOM, Grid.Edge.LEFT, Grid.Edge.RIGHT);
        assertTrue(emptyGrid.isCellEdgeActive(0, 0, Grid.Edge.TOP));
        assertTrue(emptyGrid.isCellEdgeActive(0, 0, Grid.Edge.BOTTOM));
        assertTrue(emptyGrid.isCellEdgeActive(0, 0, Grid.Edge.LEFT));
        assertTrue(emptyGrid.isCellEdgeActive(0, 0, Grid.Edge.RIGHT));
    }

    @Test
    void testActivateCellEdgesConsistency() {
        // Test that multiple near cells/edges don't affect each other when activated
        emptyGrid.activateCellEdges(1, 1, Grid.Edge.TOP, Grid.Edge.RIGHT);
        emptyGrid.activateCellEdges(1, 0, Grid.Edge.LEFT, Grid.Edge.RIGHT, Grid.Edge.BOTTOM);
        emptyGrid.activateCellEdges(0, 1, Grid.Edge.TOP, Grid.Edge.BOTTOM, Grid.Edge.LEFT, Grid.Edge.RIGHT);

        assertFalse(emptyGrid.isCellEdgeActive(0, 0, Grid.Edge.TOP));
        assertTrue(emptyGrid.isCellEdgeActive(0, 0, Grid.Edge.BOTTOM));
        assertFalse(emptyGrid.isCellEdgeActive(0, 0, Grid.Edge.LEFT));
        assertTrue(emptyGrid.isCellEdgeActive(0, 0, Grid.Edge.RIGHT));

        assertFalse(emptyGrid.isCellEdgeActive(1, 0, Grid.Edge.TOP));
        assertTrue(emptyGrid.isCellEdgeActive(1, 0, Grid.Edge.BOTTOM));
        assertTrue(emptyGrid.isCellEdgeActive(1, 0, Grid.Edge.LEFT));
        assertTrue(emptyGrid.isCellEdgeActive(1, 0, Grid.Edge.RIGHT));

        assertTrue(emptyGrid.isCellEdgeActive(0, 1, Grid.Edge.TOP));
        assertTrue(emptyGrid.isCellEdgeActive(0, 1, Grid.Edge.BOTTOM));
        assertTrue(emptyGrid.isCellEdgeActive(0, 1, Grid.Edge.LEFT));
        assertTrue(emptyGrid.isCellEdgeActive(0, 1, Grid.Edge.RIGHT));

        assertTrue(emptyGrid.isCellEdgeActive(1, 1, Grid.Edge.TOP));
        assertFalse(emptyGrid.isCellEdgeActive(1, 1, Grid.Edge.BOTTOM));
        assertTrue(emptyGrid.isCellEdgeActive(1, 1, Grid.Edge.LEFT));
        assertTrue(emptyGrid.isCellEdgeActive(1, 1, Grid.Edge.RIGHT));
    }

    @Test
    void testDeactivateCellEdges() {
        // Test deactivating single edge
        filledGrid.deactivateCellEdges(1, 1, Grid.Edge.TOP);
        assertFalse(filledGrid.isCellEdgeActive(1, 1, Grid.Edge.TOP));
        assertTrue(filledGrid.isCellEdgeActive(1, 1, Grid.Edge.BOTTOM));
        assertTrue(filledGrid.isCellEdgeActive(1, 1, Grid.Edge.LEFT));
        assertTrue(filledGrid.isCellEdgeActive(1, 1, Grid.Edge.RIGHT));
        
        // Test deactivating multiple edges
        filledGrid.deactivateCellEdges(1, 1, Grid.Edge.LEFT, Grid.Edge.RIGHT);
        assertFalse(filledGrid.isCellEdgeActive(1, 1, Grid.Edge.TOP));
        assertFalse(filledGrid.isCellEdgeActive(1, 1, Grid.Edge.LEFT));
        assertFalse(filledGrid.isCellEdgeActive(1, 1, Grid.Edge.RIGHT));
        assertTrue(filledGrid.isCellEdgeActive(1, 1, Grid.Edge.BOTTOM));
        
        // Test deactivating all edges
        filledGrid.deactivateCellEdges(0, 0, Grid.Edge.TOP, Grid.Edge.BOTTOM, Grid.Edge.LEFT, Grid.Edge.RIGHT);
        assertFalse(filledGrid.isCellEdgeActive(0, 0, Grid.Edge.TOP));
        assertFalse(filledGrid.isCellEdgeActive(0, 0, Grid.Edge.BOTTOM));
        assertFalse(filledGrid.isCellEdgeActive(0, 0, Grid.Edge.LEFT));
        assertFalse(filledGrid.isCellEdgeActive(0, 0, Grid.Edge.RIGHT));
        
        // Test deactivating edges of empty grid
        emptyGrid.deactivateCellEdges(1, 1, Grid.Edge.TOP, Grid.Edge.BOTTOM, Grid.Edge.LEFT, Grid.Edge.RIGHT);
        assertFalse(emptyGrid.isCellEdgeActive(1, 1, Grid.Edge.TOP));
        assertFalse(emptyGrid.isCellEdgeActive(1, 1, Grid.Edge.BOTTOM));
        assertFalse(emptyGrid.isCellEdgeActive(1, 1, Grid.Edge.LEFT));
        assertFalse(emptyGrid.isCellEdgeActive(1, 1, Grid.Edge.RIGHT));
    }

    @Test
    void testSetAllCellEdges() {
        // Test setting specific edges (others should be deactivated)
        emptyGrid.setAllCellEdges(1, 1, Grid.Edge.TOP, Grid.Edge.BOTTOM);
        assertTrue(emptyGrid.isCellEdgeActive(1, 1, Grid.Edge.TOP));
        assertTrue(emptyGrid.isCellEdgeActive(1, 1, Grid.Edge.BOTTOM));
        assertFalse(emptyGrid.isCellEdgeActive(1, 1, Grid.Edge.LEFT));
        assertFalse(emptyGrid.isCellEdgeActive(1, 1, Grid.Edge.RIGHT));
        
        // Test setting different edges (previous should be deactivated)
        emptyGrid.setAllCellEdges(1, 1, Grid.Edge.LEFT);
        assertFalse(emptyGrid.isCellEdgeActive(1, 1, Grid.Edge.TOP));
        assertFalse(emptyGrid.isCellEdgeActive(1, 1, Grid.Edge.BOTTOM));
        assertTrue(emptyGrid.isCellEdgeActive(1, 1, Grid.Edge.LEFT));
        assertFalse(emptyGrid.isCellEdgeActive(1, 1, Grid.Edge.RIGHT));
        
        // Test setting no edges (all should be deactivated)
        filledGrid.setAllCellEdges(1, 1);
        assertFalse(filledGrid.isCellEdgeActive(1, 1, Grid.Edge.TOP));
        assertFalse(filledGrid.isCellEdgeActive(1, 1, Grid.Edge.BOTTOM));
        assertFalse(filledGrid.isCellEdgeActive(1, 1, Grid.Edge.LEFT));
        assertFalse(filledGrid.isCellEdgeActive(1, 1, Grid.Edge.RIGHT));
        
        // Test setting all edges
        emptyGrid.setAllCellEdges(0, 0, Grid.Edge.TOP, Grid.Edge.BOTTOM, Grid.Edge.LEFT, Grid.Edge.RIGHT);
        assertTrue(emptyGrid.isCellEdgeActive(0, 0, Grid.Edge.TOP));
        assertTrue(emptyGrid.isCellEdgeActive(0, 0, Grid.Edge.BOTTOM));
        assertTrue(emptyGrid.isCellEdgeActive(0, 0, Grid.Edge.LEFT));
        assertTrue(emptyGrid.isCellEdgeActive(0, 0, Grid.Edge.RIGHT));
    }

    @Test
    void testGetCellActiveEdges() {
        // Test empty grid
        List<Grid.Edge> activeEdges = emptyGrid.getCellActiveEdges(1, 1);
        assertTrue(activeEdges.isEmpty());
        
        // Test after activating edges
        emptyGrid.activateCellEdges(1, 1, Grid.Edge.TOP, Grid.Edge.LEFT);
        activeEdges = emptyGrid.getCellActiveEdges(1, 1);
        assertEquals(2, activeEdges.size());
        assertTrue(activeEdges.contains(Grid.Edge.TOP));
        assertTrue(activeEdges.contains(Grid.Edge.LEFT));
        assertFalse(activeEdges.contains(Grid.Edge.BOTTOM));
        assertFalse(activeEdges.contains(Grid.Edge.RIGHT));
        
        // Test filled grid
        List<Grid.Edge> filledActiveEdges = filledGrid.getCellActiveEdges(1, 1);
        assertEquals(4, filledActiveEdges.size());
        assertTrue(filledActiveEdges.contains(Grid.Edge.TOP));
        assertTrue(filledActiveEdges.contains(Grid.Edge.BOTTOM));
        assertTrue(filledActiveEdges.contains(Grid.Edge.LEFT));
        assertTrue(filledActiveEdges.contains(Grid.Edge.RIGHT));
        
        // Test after deactivating some edges
        filledGrid.deactivateCellEdges(1, 1, Grid.Edge.TOP, Grid.Edge.BOTTOM);
        activeEdges = filledGrid.getCellActiveEdges(1, 1);
        assertEquals(2, activeEdges.size());
        assertTrue(activeEdges.contains(Grid.Edge.LEFT));
        assertTrue(activeEdges.contains(Grid.Edge.RIGHT));
    }

    @Test
    void testIsCellEdgeActive() {
        // Test initial state
        assertFalse(emptyGrid.isCellEdgeActive(1, 1, Grid.Edge.TOP));
        assertTrue(filledGrid.isCellEdgeActive(1, 1, Grid.Edge.TOP));
        
        // Test after activation
        emptyGrid.activateCellEdges(1, 1, Grid.Edge.BOTTOM);
        assertTrue(emptyGrid.isCellEdgeActive(1, 1, Grid.Edge.BOTTOM));
        assertFalse(emptyGrid.isCellEdgeActive(1, 1, Grid.Edge.TOP));
        
        // Test after deactivation
        filledGrid.deactivateCellEdges(1, 1, Grid.Edge.LEFT);
        assertFalse(filledGrid.isCellEdgeActive(1, 1, Grid.Edge.LEFT));
        assertTrue(filledGrid.isCellEdgeActive(1, 1, Grid.Edge.RIGHT));
    }

    @Test
    void testAddBorder() {
        emptyGrid.addBorder();
        
        // Test top border
        for (int x = 0; x < emptyGrid.getSizeX(); x++) {
            assertTrue(emptyGrid.isCellEdgeActive(x, 0, Grid.Edge.TOP));
        }
        
        // Test bottom border
        for (int x = 0; x < emptyGrid.getSizeX(); x++) {
            assertTrue(emptyGrid.isCellEdgeActive(x, emptyGrid.maxCellY(), Grid.Edge.BOTTOM));
        }
        
        // Test left border
        for (int y = 0; y < emptyGrid.getSizeY(); y++) {
            assertTrue(emptyGrid.isCellEdgeActive(0, y, Grid.Edge.LEFT));
        }
        
        // Test right border
        for (int y = 0; y < emptyGrid.getSizeY(); y++) {
            assertTrue(emptyGrid.isCellEdgeActive(emptyGrid.maxCellX(), y, Grid.Edge.RIGHT));
        }
        
        // Test that inner cells were not affected
        if (emptyGrid.getSizeX() > 1 && emptyGrid.getSizeY() > 1) {
            assertFalse(emptyGrid.isCellEdgeActive(1, 1, Grid.Edge.TOP));
            assertFalse(emptyGrid.isCellEdgeActive(1, 1, Grid.Edge.BOTTOM));
            assertFalse(emptyGrid.isCellEdgeActive(1, 1, Grid.Edge.LEFT));
            assertFalse(emptyGrid.isCellEdgeActive(1, 1, Grid.Edge.RIGHT));
        }
    }
    
    @Test
    void testCorners() {
        // Test corner cells
        var allEdges = Grid.Edge.values();
        
        // Top left
        emptyGrid.setAllCellEdges(0, 0, allEdges);
        for (Grid.Edge edge : allEdges) {
            assertTrue(emptyGrid.isCellEdgeActive(0, 0, edge));
        }
        
        // Top right
        emptyGrid.setAllCellEdges(emptyGrid.maxCellX(), 0, allEdges);
        for (Grid.Edge edge : allEdges) {
            assertTrue(emptyGrid.isCellEdgeActive(emptyGrid.maxCellX(), 0, edge));
        }
        
        // Bottom left
        emptyGrid.setAllCellEdges(0, emptyGrid.maxCellY(), allEdges);
        for (Grid.Edge edge : allEdges) {
            assertTrue(emptyGrid.isCellEdgeActive(0, emptyGrid.maxCellY(), edge));
        }
        
        // Bottom right
        emptyGrid.setAllCellEdges(emptyGrid.maxCellX(), emptyGrid.maxCellY(), allEdges);
        for (Grid.Edge edge : allEdges) {
            assertTrue(emptyGrid.isCellEdgeActive(emptyGrid.maxCellX(), emptyGrid.maxCellY(), edge));
        }
    }

    @Test
    void testCompactStringConstructorValidation() {
        // Test that we fail successfully

        // Test null input
        assertThrows(GridFormatException.class, () -> new Grid(null));
        
        // Test empty array
        assertThrows(GridFormatException.class, () -> new Grid(new byte[][]{}));
        
        // Test null row
        assertThrows(GridFormatException.class, () -> new Grid(new byte[][]{{0, 0, 0}, null, {0, 0, 0}}));
                
        // Test inconsistent row lengths
        assertThrows(GridFormatException.class, () -> new Grid(new byte[][]{{0, 0, 0}, {0, 1, 0, 0}, {0, 0, 0}}));
        
        // Test invalid grid structure (even number of rows)
        assertThrows(GridFormatException.class, () -> new Grid(new byte[][]{{0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}}));
        
        // Test too few rows
        assertThrows(GridFormatException.class, () -> new Grid(new byte[][]{{0, 0, 0}, {0, 0, 0}}));

        // Test too few columns
        assertThrows(GridFormatException.class, () -> new Grid(new byte[][]{{0}, {0}, {0}}));
    }
}
