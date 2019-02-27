package com.incuube.bot.database;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.incuube.rcs.datamodel.exceptions.RbmDatabaseException;
import com.incuube.rcs.datamodel.messages.income.UserMessage;
import com.incuube.receiver.repositories.api.IncomeMessageRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
@Log4j2
public class IncomeDynamoDBMessageRepository implements IncomeMessageRepository {


    private DynamoDB dynamoDB;
    @Value("${aws.messages.table}")
    private String messageTable;
    @Value("${aws.files.table}")
    private String fileTable;

    @Autowired
    public IncomeDynamoDBMessageRepository(DynamoDB dynamoDB) {

        this.dynamoDB = dynamoDB;

    }

    public UserMessage incomeMessageSave(UserMessage userMessage) throws RbmDatabaseException {
        try {
            Table sqsMessages = dynamoDB.getTable(messageTable);

            sqsMessages.putItem(userMessage.getCreateModelObjectForDB());

            log.info("Saved new message with id - " + userMessage.getMessageId());

            return userMessage;
        } catch (Exception ex) {
            throw throwDatabaseException(ex);
        }
    }

    public UserMessage incomeMessageUpdate(UserMessage userMessage) throws RbmDatabaseException {
        try {
            Table sqsMessages = dynamoDB.getTable(messageTable);

            sqsMessages.updateItem(userMessage.getUpdateModelObjectForDB());

            log.info("Updated income message with id - " + userMessage.getMessageId());

            return userMessage;

        } catch (Exception ex) {
            throw throwDatabaseException(ex);
        }


    }

    public boolean checkIfExistsMessage(String messageId) throws RbmDatabaseException {
        try {
            Table sqsMessages = dynamoDB.getTable(messageTable);

            return sqsMessages.getItem(new GetItemSpec().withPrimaryKey("messageId", messageId)) != null;
        } catch (Exception ex) {
            throw throwDatabaseException(ex);

        }
    }

    private RbmDatabaseException throwDatabaseException(Exception ex) throws RbmDatabaseException {

        log.error("Database problem - " + ex.getMessage());

        RbmDatabaseException databaseException = new RbmDatabaseException();
        databaseException.setMessage(ex.getMessage());
        databaseException.initCause(ex);

        throw databaseException;
    }


}
