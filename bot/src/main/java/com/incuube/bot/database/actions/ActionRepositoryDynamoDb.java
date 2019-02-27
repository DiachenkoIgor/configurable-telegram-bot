package com.incuube.bot.database.actions;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.incuube.bot.database.common.CommonDynamoDbRepository;
import com.incuube.bot.model.common.Action;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Log4j2
public class ActionRepositoryDynamoDb extends CommonDynamoDbRepository implements ActionRepository {

    @Value("${aws.actions.table}")
    private String actionTable;

    @Autowired
    public ActionRepositoryDynamoDb(DynamoDB dynamoDB) {
        super.dynamoDB = dynamoDB;
    }

    @Override
    public Optional<Action> getActionById(String id) {
        return super.getInstanceFromDynamoDbById(actionTable, "id", id, Action.class);
    }

    @Override
    public void saveAction(Action action) {
        try {
            Table botActions = dynamoDB.getTable(actionTable);

            botActions.putItem(action.getCreateModelObjectForDB());

            log.info("Saved new action with id - " + action.getId());

        } catch (Exception ex) {
            throw throwDatabaseException(ex);
        }
    }


}

