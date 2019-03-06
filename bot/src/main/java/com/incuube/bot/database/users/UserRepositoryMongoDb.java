package com.incuube.bot.database.users;

import com.incuube.bot.database.common.CommonMongoDbRepository;
import com.incuube.bot.database.util.MongoDbConverterSettings;
import com.incuube.bot.model.common.users.RcsUser;
import com.incuube.bot.model.common.users.TelegramUser;
import com.incuube.bot.model.common.users.User;
import com.incuube.bot.model.income.util.Messengers;

import com.mongodb.client.MongoDatabase;
import lombok.extern.log4j.Log4j2;
import org.bson.json.JsonWriterSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Log4j2
public class UserRepositoryMongoDb extends CommonMongoDbRepository implements UserRepository {

    private JsonWriterSettings jsonWriterSettings;

    @Value("${bot.database.telegram_users.name}")
    private String rcsCollectionName;

    @Autowired
    public UserRepositoryMongoDb(@Qualifier("sheraMongoDatabase") MongoDatabase mongoDatabase) {
        super.mongoDatabase = mongoDatabase;
        this.jsonWriterSettings = MongoDbConverterSettings.jsonWriterSettingsForLongField();
    }

    @Override
    public Optional<User> getUserFromDb(String id, Messengers network) {
        if (network == Messengers.TELEGRAM) {
            return getTelegramUserFromDb(id, this.rcsCollectionName);
        }
        return Optional.empty();
    }

    private Optional<User> getTelegramUserFromDb(String id, String collectionName) {
        Optional<User> result = super.getInstanceFromMongoDbById(collectionName, id, User.class, this.jsonWriterSettings);
      //  log.info("Retrieved Telegram user with id - " + id);
        return result;
    }

    @Override
    public void saveUserToDb(User user) {
        if (user.getMessenger() == Messengers.TELEGRAM) {
            TelegramUser telegramUser = (TelegramUser) user;
            super.saveInstanceToMongoDb(this.rcsCollectionName, user);
      //      log.info("Save new Telegram user with id - " + telegramUser.getId());
        }
    }

    @Override
    public void updateUser(User user) {
        if (user.getMessenger() == Messengers.TELEGRAM) {
            TelegramUser telegramUser = (TelegramUser) user;
            super.updateInstanceInMongoDb(this.rcsCollectionName, user);
      //      log.info("Update Telegram user with id - " + telegramUser.getId());
        }
    }

}
