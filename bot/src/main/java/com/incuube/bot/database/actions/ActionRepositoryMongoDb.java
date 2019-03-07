package com.incuube.bot.database.actions;

import com.incuube.bot.database.common.CommonMongoDbRepository;
import com.incuube.bot.model.common.Action;
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
public class ActionRepositoryMongoDb extends CommonMongoDbRepository implements ActionRepository {

    @Value("${bot.database.actions.name}")
    private String collectionName;

    private JsonWriterSettings jsonWriterSettings;

    @Autowired
    public ActionRepositoryMongoDb(@Qualifier("sheraMongoDatabase") MongoDatabase mongoDatabase) {
        super.mongoDatabase = mongoDatabase;
        this.jsonWriterSettings = JsonWriterSettings.builder().build();
    }

    @Override
    public Optional<Action> getActionById(String id) {
        return super.getInstanceFromMongoDbById(this.collectionName, id, Action.class, this.jsonWriterSettings);
    }

    @Override
    public void saveAction(Action action) {
        super.saveInstanceToMongoDb(this.collectionName, action);
    }
}
