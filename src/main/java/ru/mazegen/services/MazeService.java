package ru.mazegen.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.mazegen.model.Maze;
import ru.mazegen.model.User;
import ru.mazegen.repository.PathRepository;
import ru.mazegen.repository.MazeRepository;
import ru.mazegen.repository.UserRepository;

import java.util.List;


@Service
@RequiredArgsConstructor
public class MazeService {

    private final MazeRepository mazeRepository;
    private final UserRepository userRepository;
    private final PathRepository mazePathRepository;

    /**
     * Find maze by id, return null if not exists
     */
    @Nullable
    public Maze getMazeById(long id) {
        return mazeRepository.findById(id).orElse(null);
    }

    /**
     * Save to database
     */
    @Transactional
    public void saveMazeForAuthor(@NotNull Maze maze, @Nullable Long authorId) {
        if (authorId != null) {
            User user = userRepository.findById(authorId).orElse(null);
            maze.setAuthor(user);
        }
        mazeRepository.save(maze);
    }

    /**
     * Get recent mazes by user. pivotId, pageNum, pageSize are used for pagination
     */
    @Transactional
    public List<Maze> getMazesByAuthor(long authorId, long pivotId, int pageNum, int pageSize) {
        var sort = Sort.by(Sort.Direction.DESC, "id");
        var page = PageRequest.of(pageNum, pageSize, sort);
        return mazeRepository.findMazesByAuthorIdAndIdLessThan(authorId, pivotId + 1, page);
    }


    /**
     * Get recent mazes. All parameters are used for pagination
     */
    @Transactional
    public List<Maze> getRecentMazes(long pivotId, int pageNum, int pageSize) {
        var sort = Sort.by(Sort.Direction.DESC, "id");
        var page = PageRequest.of(pageNum, pageSize, sort);
        return mazeRepository.findAllByIdLessThan(pivotId + 1, page);
    }
}
