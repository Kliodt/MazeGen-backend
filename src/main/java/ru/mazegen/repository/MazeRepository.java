package ru.mazegen.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.mazegen.model.Maze;

import java.util.List;

@Repository
public interface MazeRepository extends CrudRepository<Maze, Long> {

//    @Query("""
//        select m from Maze m
//        where m.id < :maxId
//        and m.author.id = :authorId
//        order by m.id desc
//        limit :count
//    """)
//    List<Maze> findMazesByAuthor(
//            @Param("authorId") long authorId,
//            @Param("maxId") long maxId,
//            @Param("count") int count
//    );
//
//    @Query("""
//        select m from Maze m
//        where m.id < :maxId
//        order by m.id desc
//        limit :count
//    """)
//    List<Maze> findMostRecentMazes(
//            @Param("maxId") long maxId,
//            @Param("count") int count
//    );

    /**
     * Find maze by author id.
     * @param pivotMazeId - id for correct pagination
     */
    List<Maze> findMazesByAuthorIdAndIdLessThan(
            long authorId, long pivotMazeId, Pageable page);


    /**
     * Find the most recent mazes
     * @param pivotMazeId - id for correct pagination
     */
    List<Maze> findAllByIdLessThan(long pivotMazeId, Pageable page);


    /**
     * Find the most recent mazes by author
     * @param pivotMazeId - id for correct pagination
     */
    List<Maze> findAllByAuthorIdAndIdLessThan(long authorId, long pivotMazeId, Pageable page);
}
