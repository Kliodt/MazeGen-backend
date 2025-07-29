package ru.mazegen.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.mazegen.model.Maze;

import java.util.List;

@Repository
public interface MazeRepository extends CrudRepository<Maze, Long> {

    List<Maze> findAllByAuthorId(long authorId);



}
