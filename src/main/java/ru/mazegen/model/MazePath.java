package ru.mazegen.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.Arrays;

@Data
@NoArgsConstructor
@Embeddable
public class MazePath {

    private static class PathPoint {
        final int x, y;

        public String asHexStr() {
            return String.format("%x,%x", x, y); // %x - hex
        }

        public PathPoint(String hexStr) throws IllegalArgumentException {
            var xy = hexStr.split(",");
            if (xy.length != 2) throw new IllegalArgumentException();
            this.x = Integer.parseInt(xy[0], 16);
            this.y = Integer.parseInt(xy[1], 16);
        }

        public int[] asArray() {
            return new int[]{x, y};
        }
    }


    @Converter
    private static class PathPointConverter implements AttributeConverter<PathPoint[], String> {
        @Override
        public String convertToDatabaseColumn(PathPoint[] points) {
            return String.join(";",
                    Arrays.stream(points).map(PathPoint::asHexStr).toList()
            );
        }

        @Override
        public PathPoint[] convertToEntityAttribute(String dbData) {
            return (PathPoint[]) Arrays.stream(dbData.split(";"))
                    .map(PathPoint::new).toArray();
        }
    }


    @Convert(converter = PathPointConverter.class)
    @Column(nullable = false)
    private @NotNull PathPoint @NotNull [] path = new PathPoint[0];

    @ManyToOne(
            fetch = FetchType.EAGER,
            optional = false,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE}
    )
    private @NotNull Maze maze;

    boolean isMazeCompleted;
    private @Nullable LocalDateTime completionDate;

}
