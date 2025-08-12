package ru.mazegen.model;

import com.nimbusds.jose.shaded.gson.Gson;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.mazegen.model.grids.Grid;
import ru.mazegen.model.grids.GridFormatException;

import java.time.OffsetDateTime;


@Data
@NoArgsConstructor
@Entity
@Table(indexes = {
        // optimize search by author (including user's own mazes)
        @Index(name = "maze_author_index", columnList = "author_id")
})
public class Maze {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Convert(converter = GridToStringConverter.class)
    private @NotNull Grid grid;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "author_id", referencedColumnName = "id")
    private @Nullable User author;

    private int startX;
    private int startY;
    private int finishX;
    private int finishY;

    @Column(length = 64)
    private @Nullable String algorithm;
    private @Nullable OffsetDateTime genDate;

    private int genDurationMs;


    public Maze(@NotNull Grid grid, int startX, int startY, int finishX, int finishY) {
        this.grid = grid;
        this.startX = startX;
        this.startY = startY;
        this.finishX = finishX;
        this.finishY = finishY;
    }

    public void setMetaInformation(@NotNull String algorithm, @NotNull OffsetDateTime genDate, int genDurationMs) {
        this.algorithm = algorithm;
        this.genDate = genDate;
        this.genDurationMs = genDurationMs;
    }


    /**
     * Valid move is the move by exactly 1 cell that is not crossing the edge.
     */
    public boolean isValidMove(int fromX, int fromY, int toX, int toY) {
        if (Math.abs(fromX - toX) + Math.abs(fromY - toY) != 1) return false;
        if (fromX < 0 || fromY < 0 || toX < 0 || toY < 0) return false;
        if (fromX >= grid.getSizeX() || fromY >= grid.getSizeY() || toX >= grid.getSizeX() || toY >= grid.getSizeY())
            return false;

        Grid.Edge edge;
        if (fromX - toX != 0) {
            edge = fromX - toX > 0 ? Grid.Edge.LEFT : Grid.Edge.RIGHT;
        } else {
            edge = fromY - toY > 0 ? Grid.Edge.TOP : Grid.Edge.BOTTOM;
        }
        return !grid.isCellEdgeActive(fromX, fromY, edge);
    }

    public boolean isValidPath(MazePath path) {
        var points = path.getPoints();
        if (points.length == 0) return false;

        // check start
        if (points[0][0] != startX || points[0][1] != startY) return false;

        // make sure that all moves are valid
        var from = points[0];
        for (int i = 1; i < points.length; i++) {
            var to = points[i];
            if (!isValidMove(from[0], from[1], to[0], to[1])) {
                return false;
            }
            from = to;
        }
        return true;
    }

    public boolean isCompletableWithPath(MazePath path) {
        var points = path.getPoints();
        if (points[points.length - 1][0] != finishX || points[points.length - 1][1] != finishY) return false;
        return isValidPath(path);
    }


    @Converter
    private static class GridToStringConverter implements AttributeConverter<Grid, String> {
        @Override
        public String convertToDatabaseColumn(Grid grid) {
            return new Gson().toJson(grid.getEdges());
        }

        @Override
        public Grid convertToEntityAttribute(String dbData) throws GridFormatException {
            return new Grid(new Gson().fromJson(dbData, byte[][].class));
        }
    }
}
