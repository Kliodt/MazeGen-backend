package ru.mazegen.repository;


import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.mazegen.model.Maze;

import java.util.List;


@Repository
public interface MazeRepository extends CrudRepository<Maze, Long> {

    /**
     * Find maze by author id.
     *
     * @param pivotMazeId - id for correct pagination
     */
    List<Maze> findMazesByAuthorIdAndIdLessThan(
            long authorId, long pivotMazeId, Pageable page
    );


    /**
     * Find the most recent mazes
     *
     * @param pivotMazeId - id for correct pagination
     */
    List<Maze> findAllByIdLessThan(long pivotMazeId, Pageable page);


}
