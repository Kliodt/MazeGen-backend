package ru.mazegen.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.mazegen.model.MazePath;

@Repository
public interface MazePathRepository extends CrudRepository<MazePath, Long> {
    MazePath findByMazeIdAndUserId(long mazeId, long userId);
}
