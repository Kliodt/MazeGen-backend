package ru.mazegen.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.mazegen.model.User;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    User findUserByGoogleId(String googleId);
}
