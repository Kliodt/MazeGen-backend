package ru.mazegen.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;
import ru.mazegen.model.Maze;
import ru.mazegen.model.User;
import ru.mazegen.repository.MazeRepository;
import ru.mazegen.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MazeService {

    private final MazeRepository mazeRepository;
    private final UserRepository userRepository;

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
    public boolean saveMazeForAuthor(@NotNull Maze maze, long authorId) {
        User user = userRepository.findById(authorId).orElse(null);
        if (user == null) return false;
        maze.setAuthor(user);
        mazeRepository.save(maze);
        return true;
    }

    /**
     * Get maze by user
     */
    @Transactional
    public List<Maze> getAllMazesByAuthor(long authorId) {
        return mazeRepository.findAllByAuthorId(authorId);
    }

//    public Maze generateMaze(MazeGenerator gen) {
////        mazeRepository.fin
//    }
}
