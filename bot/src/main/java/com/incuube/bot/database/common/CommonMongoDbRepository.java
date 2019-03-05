package com.incuube.bot.database.common;

import com.incuube.bot.model.common.util.DbInstance;
import com.incuube.bot.model.exceptions.BotDabaseException;
import com.incuube.bot.model.exceptions.DbParamNotFoundException;
import com.incuube.bot.util.JsonConverter;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import lombok.extern.log4j.Log4j2;
import org.bson.Document;
import org.bson.json.JsonWriterSettings;

import java.util.Optional;

@Log4j2
public abstract class CommonMongoDbRepository {

    protected MongoDatabase mongoDatabase;

    protected <T> Optional<T> getInstanceFromMongoDbById(String collectionName, String idValue, Class<T> clazz, JsonWriterSettings jsonWriterSettings) {
        try {
            MongoCollection<Document> someCollection = mongoDatabase.getCollection(collectionName);

            FindIterable<Document> documents = someCollection.find(new Document().append("_id", idValue));

            MongoCursor<Document> iterator = documents.iterator();
            Document document;

            if (iterator.hasNext()) {
                document = iterator.next();
            } else {
                return Optional.empty();
            }

            return JsonConverter.convertJson(document.toJson(jsonWriterSettings), clazz);
        } catch (Exception ex) {
            throw throwDatabaseException(ex);
        }
    }


    protected void saveInstanceToMongoDb(String collectionName, DbInstance dbInstance) {
        try {
            MongoCollection<Document> someCollection = mongoDatabase.getCollection(collectionName);

            someCollection.insertOne(dbInstance.getCreateModelObjectForDB());
        } catch (Exception ex) {
            throwDatabaseException(ex);
        }
    }


    protected void updateInstanceInMongoDb(String collectionName, DbInstance dbInstance) {
        try {
            MongoCollection<Document> someCollection = mongoDatabase.getCollection(collectionName);

            Document updateModelObjectForDB = dbInstance.getUpdateModelObjectForDB();

            Object id = updateModelObjectForDB.remove("_id");

            if (!someCollection.find(new Document("_id", id)).iterator().hasNext()) {
                throw new DbParamNotFoundException("Document wasn't found for update. Id - " + id);
            }

            Document filter = new Document("_id", id);


            someCollection.updateOne(
                    filter,
                    updateModelObjectForDB);
        } catch (Exception ex) {
            throwDatabaseException(ex);
        }
    }

    public static BotDabaseException throwDatabaseException(Exception ex) {

        log.error("Bot database problem - " + ex.getMessage());

        BotDabaseException botDabaseException = new BotDabaseException();
        botDabaseException.setMessage(ex.getMessage());
        botDabaseException.initCause(ex);

        throw botDabaseException;
    }
}
