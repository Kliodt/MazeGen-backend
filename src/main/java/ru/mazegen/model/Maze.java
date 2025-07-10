package ru.mazegen.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.mazegen.model.grids.Grid;
import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@Entity
@Table(indexes = {
        // optimize search by author (including user's own mazes)
        @Index(name = "maze_author_index", columnList = "author")
})
public class Maze {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;


    @OneToOne(
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE},
            fetch = FetchType.LAZY
    )
    @JoinColumn(name = "grid_id", referencedColumnName = "id")
    private Grid grid;


    @ManyToOne(
            cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            fetch = FetchType.EAGER
    )
    @JoinColumn(name = "author_id", referencedColumnName = "id")
    private User author;


    private int startX;
    private int startY;
    private int finishX;
    private int finishY;
    private String algorithm;
    private LocalDateTime genDate;
    private int genDurationMs;


    public Maze(Grid grid, int startX, int startY, int finishX, int finishY) {
        this.grid = grid;
        this.startX = startX;
        this.startY = startY;
        this.finishX = finishX;
        this.finishY = finishY;
    }

    public void setMetaInformation(String algorithm, LocalDateTime genDate, int genDurationMs) {
        this.algorithm = algorithm;
        this.genDate = genDate;
        this.genDurationMs = genDurationMs;
    }

    boolean isCompletableWithPath(MazePath path) {
        // todo:
        return false;
    }
}
