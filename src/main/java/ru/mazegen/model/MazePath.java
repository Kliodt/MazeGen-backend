package ru.mazegen.model;

import com.nimbusds.jose.shaded.gson.Gson;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(indexes = {
        // optimize search by maze
        @Index(name = "mazePath_maze_index", columnList = "maze_id")
})
public class MazePath {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Lob
    @Column(nullable = false)
    @Basic(fetch = FetchType.EAGER)
    @Convert(converter = PathPointsToStringConverter.class)
    private int[][] points;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    private @NotNull Maze maze;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    private @NotNull User user;

    boolean isMazeCompleted = false;
    private @Nullable OffsetDateTime completionDate = null;


    public MazePath(int[][] points, @NotNull Maze maze, @NotNull User user) throws IllegalArgumentException {
        this.setPoints(points);
        this.maze = maze;
        this.user = user;
    }

    public void setPoints(int[][] points) throws IllegalArgumentException {
        if (points == null) throw new IllegalArgumentException();
        for (var p : points) {
            if (p == null || p.length != 2) throw new IllegalArgumentException();
        }
        this.points = points;
    }

    @Converter
    private static class PathPointsToStringConverter implements AttributeConverter<int[][], String> {
        @Override
        public String convertToDatabaseColumn(int[][] points) {
            return new Gson().toJson(points);
        }

        @Override
        public int[][] convertToEntityAttribute(String dbData) {
            return new Gson().fromJson(dbData, int[][].class);
        }
    }

}
