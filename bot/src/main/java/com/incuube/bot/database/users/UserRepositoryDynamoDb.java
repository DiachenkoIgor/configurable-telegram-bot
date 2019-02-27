package com.incuube.bot.database.users;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.incuube.bot.database.common.CommonDynamoDbRepository;
import com.incuube.bot.model.common.users.RcsUser;
import com.incuube.bot.model.common.users.User;
import com.incuube.bot.model.income.util.Messengers;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.Optional;

//TODO(diachenko): Check 31 line for casting beetwen User and RcsUser
@Repository
@Log4j2
public class UserRepositoryDynamoDb extends CommonDynamoDbRepository implements UserRepository {

    @Value("${aws.rcs_users.table}")
    private String rcsUserTable;

    @Autowired
    public UserRepositoryDynamoDb(DynamoDB dynamoDB) {
        super.dynamoDB = dynamoDB;
    }

    @Override
    public Optional<User> getUserFromDb(String id, Messengers network) {
        if (network == Messengers.RCS) {
            return super.getInstanceFromDynamoDbById(rcsUserTable, "number_id", id, User.class);
        }
        return Optional.empty();
    }

    @Override
    public void saveUserToDb(User user) {
        if (user.getMessenger() == Messengers.RCS) {
            saveUserToDbForRcs(user);
        }
    }

    @Override
    public void updateUser(User user) {
        if (user.getMessenger() == Messengers.RCS) {
            updateUserForRcs(user);
        }
    }

    private void updateUserForRcs(User user) {
        RcsUser rcsUser = (RcsUser) user;
        super.updateInstanceDynamoDbById(rcsUserTable, rcsUser);
        log.info("Update RCS user with id - " + rcsUser.getNumber());
    }

    private void saveUserToDbForRcs(User user) {
        RcsUser rcsUser = (RcsUser) user;
        super.saveInstanceToDynamoDb(rcsUserTable, user);
        log.info("Saved new RCS user with id - " + rcsUser.getNumber());
    }


}
