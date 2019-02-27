package com.incuube.agent.repositories.api;

import com.incuube.rcs.datamodel.exceptions.RbmDatabaseException;
import com.incuube.rcs.datamodel.exceptions.RbmDatabaseItemNotFound;
import com.incuube.rcs.datamodel.messages.outcome.OutcomeMessage;
import com.incuube.rcs.datamodel.rest.RcsFile;

public interface OutcomeMessageRepository {

    RcsFile saveFile(RcsFile rcsFile) throws RbmDatabaseException;

    OutcomeMessage outcomeMessageSave(OutcomeMessage outcomeMessage) throws RbmDatabaseException;

    String getGoogleFileName(String fileName) throws RbmDatabaseException, RbmDatabaseItemNotFound;
}
