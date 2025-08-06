package ru.mazegen.model;

import com.nimbusds.jose.shaded.gson.Gson;
import com.nimbusds.jose.shaded.gson.GsonBuilder;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.mazegen.model.grids.Grid;
import ru.mazegen.model.grids.GridFormatException;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;


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

    boolean isCompletableWithPath(MazePath path) {
        for (var point : path.getPoints()) {

        }
        return false;
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
