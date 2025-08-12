package ru.mazegen.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;
import ru.mazegen.model.MazePath;
import ru.mazegen.repository.PathRepository;
import ru.mazegen.repository.MazeRepository;
import ru.mazegen.repository.UserRepository;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PathService {

    private final PathRepository mazePathRepository;
    private final UserRepository userRepository;
    private final MazeRepository mazeRepository;

    @Transactional
    @Nullable
    public MazePath getPathByUserAndMaze(long userId, long mazeId) {
        return mazePathRepository.findByMazeIdAndUserId(mazeId, userId);
    }

    @Transactional
    @Nullable
    public MazePath updatePathPointsForUserAndMaze(List<List<Integer>> points, long userId, long mazeId, boolean checkCompletion) {
        MazePath path = mazePathRepository.findByMazeIdAndUserId(mazeId, userId);

        var pointsArray = new int[points.size()][2];
        for (int i = 0; i < points.size(); i++) {
            var point = points.get(i);
            if (point.size() != 2) return null; // invalid path
            pointsArray[i][0] = point.get(0);
            pointsArray[i][1] = point.get(1);
        }

        // on first submission
        if (path == null) {
            var user = userRepository.findById(userId).orElse(null);
            if (user == null) {
                log.warn("User {} not found", userId);
                return null;
            }
            var maze = mazeRepository.findById(mazeId).orElse(null);
            if (maze == null) {
                log.warn("Maze {} not found", mazeId);
                return null;
            }
            try {
                path = mazePathRepository.save(new MazePath(pointsArray, maze, user));
            } catch (IllegalArgumentException e) {
                return null;
            }
        }

        // path is in 'managed' state now

        try {
            path.setPoints(pointsArray);
        } catch (IllegalArgumentException e) {
            return null;
        }

        if (checkCompletion) {
            var maze = path.getMaze();
            var completed = maze.isCompletableWithPath(path);
            var date = completed ? OffsetDateTime.now() : null;

            path.setMazeCompleted(completed);
            path.setCompletionDate(date);

            log.info("User {} completed the maze {} = {}", userId, mazeId, completed);
        } else {
            path.setCompletionDate(null);
            path.setMazeCompleted(false);
        }

        return path;
    }

}
