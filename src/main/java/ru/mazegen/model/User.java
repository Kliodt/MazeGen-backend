package ru.mazegen.model;

import java.time.LocalDateTime;
import java.util.List;

public final class User {

    public long id;
    public String nickname;
    public LocalDateTime registrationDate;

    public List<Maze> mazes;

}
