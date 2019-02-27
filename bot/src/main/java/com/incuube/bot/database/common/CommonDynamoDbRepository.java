package com.incuube.bot.database.common;

import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.internal.IteratorSupport;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.incuube.bot.model.common.util.DbInstance;
import com.incuube.bot.model.exceptions.BotDabaseException;
import com.incuube.bot.util.JsonConverter;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Log4j2
@Repository
public abstract class CommonDynamoDbRepository {

    protected DynamoDB dynamoDB;

    protected <T> Optional<T> getInstanceFromDynamoDbById(String tableName, String idName, String value, Class<T> clazz) {
        try {
            Table rcsUsers = dynamoDB.getTable(tableName);

            QuerySpec querySpec = new QuerySpec()
                    .withKeyConditionExpression(idName + "=:id")
                    .withValueMap(new ValueMap()
                            .withString(":id", value));

            ItemCollection<QueryOutcome> itemsCollection = rcsUsers.query(querySpec);

            IteratorSupport<Item, QueryOutcome> iterator = itemsCollection.iterator();

            List<Item> items = new ArrayList<>();

            while (iterator.hasNext()) {
                items.add(iterator.next());
            }
            if (items.isEmpty()) {
                return Optional.empty();
            }
            return JsonConverter.convertJson(items.get(0).toJSON(), clazz);
        } catch (Exception ex) {
            throw throwDatabaseException(ex);
        }
    }

    protected void updateInstanceDynamoDbById(String tableName, DbInstance dbInstance) {
        try {
            Table someTable = dynamoDB.getTable(tableName);

            someTable.updateItem(dbInstance.getUpdateModelObjectForDB());

        } catch (Exception ex) {
            throw throwDatabaseException(ex);
        }
    }

    protected void saveInstanceToDynamoDb(String tableName, DbInstance dbInstance) {
        try {
            Table someTable = dynamoDB.getTable(tableName);

            someTable.putItem(dbInstance.getCreateModelObjectForDB());
        } catch (Exception ex) {
            throw throwDatabaseException(ex);
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
