package ru.mazegen.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@Entity
public class MazePath {

    private record Point(int x, int y){}

    @Id
    @GeneratedValue
    private long id;

    @ManyToOne(
            fetch = FetchType.EAGER,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            optional = false
    )
    private Maze maze;

    @Embedded
    private Point[] path;

    private boolean isMazeCompleted;


}
