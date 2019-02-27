package com.incuube.agent.services.api;

import com.incuube.rcs.datamodel.rest.RcsFile;
import com.incuube.rcs.datamodel.rest.RcsSuggestionActionMessage;
import com.incuube.rcs.datamodel.rest.RcsSuggestionMessage;
import com.incuube.rcs.datamodel.rest.RcsTextMessage;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.List;

public interface RcsService {

    void sendTextMessage(RcsTextMessage message, DeferredResult<ResponseEntity<?>> deferredResult);

    void sendSuggestion(List<RcsSuggestionMessage> suggestionMessages, String phone, String text, DeferredResult<ResponseEntity<?>> deferredResult);

    void sendFile(String number, String fileName, DeferredResult<ResponseEntity<?>> deferredResult);

    void uploadFile(RcsFile rcsFile, DeferredResult<ResponseEntity<?>> deferredResult);

    void sendCheckRequest(String number, DeferredResult<ResponseEntity<?>> deferredResult);

    void sendReadEvent(String messageId, String number, DeferredResult<ResponseEntity<?>> deferredResult);

    void sendSuggestionActionMessages(List<RcsSuggestionActionMessage> actionMessages, DeferredResult<ResponseEntity<?>> deferredResult, String phone, String text);

}
