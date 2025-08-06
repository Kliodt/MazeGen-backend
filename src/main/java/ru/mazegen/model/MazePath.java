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
import java.util.Arrays;

@Data
@NoArgsConstructor
@Embeddable
public class MazePath {

    @Lob
    @Column(nullable = false)
    @Basic(fetch = FetchType.LAZY)
    @Convert(converter = PathPointsToStringConverter.class)
    private int[][] points;

    @ManyToOne(
            fetch = FetchType.EAGER,
            optional = false
    )
    private @NotNull Maze maze;

    boolean isMazeCompleted;
    private @Nullable OffsetDateTime completionDate;


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
