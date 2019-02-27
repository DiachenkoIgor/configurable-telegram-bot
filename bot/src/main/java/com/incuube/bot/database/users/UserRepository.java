package com.incuube.bot.database.users;

import com.incuube.bot.model.common.users.User;
import com.incuube.bot.model.income.util.Messengers;
import org.springframework.stereotype.Repository;

import java.util.Optional;

//TODO(igordiachenko): Create logic for user table
@Repository
public interface UserRepository {

    Optional<User> getUserFromDb(String id, Messengers network);

    void saveUserToDb(User user);

    void updateUser(User user);
}
