package ru.mazegen.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.mazegen.model.Maze;

@Repository
public interface MazeRepository extends CrudRepository<Maze, Long> {

//    Maze findById(long id);

}
