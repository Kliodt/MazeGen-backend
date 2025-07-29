package ru.mazegen.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mazegen.model.Maze;
import ru.mazegen.model.MazeGenerator;
import ru.mazegen.repository.MazeRepository;

@Service
@RequiredArgsConstructor
public class MazeService {

    private final MazeRepository mazeRepository;

    /**
     * Find maze by id, return null if not exists
     */
    public Maze getMazeById(long id) {
        return mazeRepository.findById(id).orElse(null);
    }

    /**
     * Save to database
     */
    public void saveMaze(Maze m) {
        mazeRepository.save(m);
    }

//    public Maze generateMaze(MazeGenerator gen) {
////        mazeRepository.fin
//    }
}
