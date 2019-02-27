package com.incuube.receiver.repositories.api;

import com.incuube.rcs.datamodel.exceptions.RbmDatabaseException;
import com.incuube.rcs.datamodel.messages.income.UserMessage;

public interface IncomeMessageRepository {

    UserMessage incomeMessageSave(UserMessage userMessage) throws RbmDatabaseException;

    UserMessage incomeMessageUpdate(UserMessage userMessage) throws RbmDatabaseException;

    boolean checkIfExistsMessage(String messageId) throws RbmDatabaseException;
}
