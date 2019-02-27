package com.incuube.bot.database.common;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.incuube.bot.model.common.util.DbSaverEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ParamDynamoDbRepository {
    private DynamoDB dynamoDB;

    @Autowired
    public ParamDynamoDbRepository(DynamoDB dynamoDB) {
        this.dynamoDB = dynamoDB;
    }

    public void saveParamToSomeTable(DbSaverEntity dbSaverEntity) {
        try {
            Table sqsMessages = dynamoDB.getTable(dbSaverEntity.getTable());

            UpdateItemSpec updateItemSpec = new UpdateItemSpec()
                    .withPrimaryKey(dbSaverEntity.getIdName(), dbSaverEntity.getIdValue())
                    .withUpdateExpression(String.format("set %s=:paramValue", dbSaverEntity.getField()))
                    .withValueMap(new ValueMap().withString(":paramValue", dbSaverEntity.getParamValue()));

            sqsMessages.updateItem(updateItemSpec);
        } catch (Exception ex) {
            throw CommonDynamoDbRepository.throwDatabaseException(ex);
        }
    }
}
