package com.incuube.bot.database.common;

import com.incuube.bot.model.common.util.DbSaverEntity;
import com.incuube.bot.model.exceptions.BotDabaseException;
import com.incuube.bot.model.exceptions.DbParamNotFoundException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.extern.log4j.Log4j2;
import org.bson.Document;
import org.bson.json.JsonWriterSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
@Log4j2
public class ParamDynamoDbRepository {

    private MongoDatabase mongoDatabase;

    @Autowired
    public ParamDynamoDbRepository(@Qualifier("sheraMongoDatabase") MongoDatabase mongoDatabase) {
        this.mongoDatabase = mongoDatabase;
    }

    public void saveParamToSomeTable(DbSaverEntity dbSaverEntity) {
        try {
            MongoCollection<Document> actionsDb = mongoDatabase.getCollection(dbSaverEntity.getTable());

            if (!actionsDb.find(new Document("_id", dbSaverEntity.getIdValue())).iterator().hasNext()) {
                throw new DbParamNotFoundException("Document wasn't found for setting params. Param path - " + dbSaverEntity.getField());
            }

            actionsDb.updateOne(
                    new Document("_id", dbSaverEntity.getIdValue()),
                    new Document("$set", new Document(dbSaverEntity.getField(), dbSaverEntity.getParamValue())));
        } catch (Exception ex) {
            CommonMongoDbRepository.throwDatabaseException(ex);
        }
    }
}


