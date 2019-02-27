package com.incuube.agent.repositories;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.incuube.agent.repositories.api.OutcomeMessageRepository;
import com.incuube.rcs.datamodel.exceptions.RbmDatabaseException;
import com.incuube.rcs.datamodel.exceptions.RbmDatabaseItemNotFound;
import com.incuube.rcs.datamodel.messages.outcome.OutcomeMessage;
import com.incuube.rcs.datamodel.rest.RcsFile;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
@Log4j2
public class OutcomeDynamoDBMessageRepository implements OutcomeMessageRepository {


    private DynamoDB dynamoDB;
    @Value("${aws.messages.table}")
    private String messageTable;
    @Value("${aws.files.table}")
    private String fileTable;

    @Autowired
    public OutcomeDynamoDBMessageRepository(DynamoDB dynamoDB) {

        this.dynamoDB = dynamoDB;

    }


    public RcsFile saveFile(RcsFile rcsFile) throws RbmDatabaseException {
        try {
            Table sqsMessages = dynamoDB.getTable(fileTable);

            Item item = new Item();
            item.withPrimaryKey("name", rcsFile.getName())
                    .withString("url", rcsFile.getUrl())
                    .withString("googleName", rcsFile.getGoogleName());

            if (rcsFile.getThumbnailUrl() != null) {
                item.withString("thumbnailUrl", rcsFile.getThumbnailUrl());
            }

            sqsMessages.putItem(item);

            log.info("Saved new file with id - " + rcsFile.getName());

            return rcsFile;
        } catch (Exception ex) {
            throw throwDatabaseException(ex);
        }
    }

    public OutcomeMessage outcomeMessageSave(OutcomeMessage outcomeMessage) throws RbmDatabaseException {
        try {
            Table sqsMessages = dynamoDB.getTable(messageTable);

            if (checkIfExistsMessage(outcomeMessage.getMessageId())) {

                sqsMessages.updateItem(outcomeMessage.getUpdateModelObjectForDB());
                log.info("Updated outcome Message with id - " + outcomeMessage.getMessageId());
            } else {

                sqsMessages.putItem(outcomeMessage.getCreateModelObjectForDB());
                log.info("Saved new outcome Message with id - " + outcomeMessage.getMessageId());
            }
            return outcomeMessage;
        } catch (Exception ex) {
            throw throwDatabaseException(ex);
        }
    }

    public String getGoogleFileName(String fileName) throws RbmDatabaseException, RbmDatabaseItemNotFound {
        try {
            Table rcsFiles = dynamoDB.getTable(fileTable);

            GetItemSpec getRequest = new GetItemSpec().withPrimaryKey("name", fileName);
            Item item = rcsFiles.getItem(getRequest);
            if (item == null) {
                RbmDatabaseItemNotFound itemNotFound = new RbmDatabaseItemNotFound();
                itemNotFound.setItemName("fileName");
                itemNotFound.setItemType("File");
                throw itemNotFound;
            }
            String googleName = item.getString("googleName");

            log.info("File with name   - " + fileName + " was found.");

            return googleName;
        } catch (RbmDatabaseItemNotFound ex) {
            throw ex;
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
