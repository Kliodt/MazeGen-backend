package ru.mazegen.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
public class MazePath {

    private record PathPoint (int x, int y){}

    @Converter
    private static class PathPointConverter implements AttributeConverter<PathPoint[], String> {
        @Override
        public String convertToDatabaseColumn(PathPoint[] points) {
            var sb = new StringBuilder(points.length * 4);
            for (PathPoint p : points) {
                sb.append(Integer.toHexString(p.x)).append(',');
                sb.append(Integer.toHexString(p.y)).append(',');
            }
            return sb.toString();
        }

        @Override
        public PathPoint[] convertToEntityAttribute(String dbData) {
            if (dbData != null && !dbData.isEmpty()) {
                var pointsStr = dbData.split(",");
                var ret = new PathPoint[pointsStr.length / 2];
                for (int i = 0; i < ret.length; i++) {
                    ret[i] = new PathPoint(
                            Integer.parseInt(pointsStr[i * 2], 16),
                            Integer.parseInt(pointsStr[i * 2 + 1], 16));
                }
                return ret;
            }
            return new PathPoint[0];
        }
    }


    @Id
    @GeneratedValue
    private long id;

    @ManyToOne(
            fetch = FetchType.EAGER,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            optional = false
    )
    private Maze maze;

    @Convert(converter = PathPointConverter.class)
    @Column(nullable = false)
    private PathPoint[] path;

    private boolean isMazeCompleted;


}
