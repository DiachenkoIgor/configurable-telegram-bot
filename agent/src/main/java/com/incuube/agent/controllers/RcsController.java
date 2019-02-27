package com.incuube.agent.controllers;


import com.incuube.agent.services.api.RcsService;
import com.incuube.agent.util.RestValidator;
import com.incuube.rcs.datamodel.rest.RcsFile;
import com.incuube.rcs.datamodel.rest.RcsSuggestionActionMessage;
import com.incuube.rcs.datamodel.rest.RcsSuggestionMessage;
import com.incuube.rcs.datamodel.rest.RcsTextMessage;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

//TODO(diachenko): Text validation maybe for urls /suggestions/*
@RestController
@RequestMapping("/rcs")
@Log4j2

public class RcsController {


    private RcsService rcsService;

    @Autowired
    public RcsController(RcsService rcsService) {
        this.rcsService = rcsService;
    }

    @PostMapping(value = "/text", consumes = MediaType.APPLICATION_JSON_VALUE)
    public DeferredResult<ResponseEntity<?>> sendTextMessageRequestHandler(@RequestBody @Valid RcsTextMessage rcsTextMessage) {

        DeferredResult<ResponseEntity<?>> response = new DeferredResult<>();

        log.info("Try to send buttonText message to " + rcsTextMessage.getNumber());

        rcsService.sendTextMessage(rcsTextMessage, response);

        return response;
    }

    @PostMapping(value = "/suggestions/replies", consumes = MediaType.APPLICATION_JSON_VALUE)
    public DeferredResult<ResponseEntity<?>> suggestionRequestHandler(@RequestBody List<RcsSuggestionMessage> rcsSuggestionMessages,
                                                                      @RequestParam("phone") String phoneNumber,
                                                                      @RequestParam("text") String text
    ) {
        DeferredResult<ResponseEntity<?>> response = new DeferredResult<>();


        Optional<String> suggestionValidation = RestValidator.validateRcsSuggestion(rcsSuggestionMessages);

        if (suggestionValidation.isPresent()) {
            response.setResult(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(suggestionValidation.get()));
            return response;
        }

        log.info("Try to send suggestion to " + phoneNumber);

        rcsService.sendSuggestion(rcsSuggestionMessages, phoneNumber, text, response);

        return response;
    }

    @PostMapping(value = "/suggestions/actions", consumes = MediaType.APPLICATION_JSON_VALUE)
    public DeferredResult<ResponseEntity<?>> suggestionActionRequestHandler(@RequestBody List<RcsSuggestionActionMessage> rcsSuggestionActionMessages,
                                                                            @RequestParam("phone") String phoneNumber,
                                                                            @RequestParam("text") String text
    ) {
        DeferredResult<ResponseEntity<?>> response = new DeferredResult<>();

        Optional<String> suggestionValidation = RestValidator.validateRcsSuggestionAction(rcsSuggestionActionMessages);

        if (suggestionValidation.isPresent()) {
            response.setResult(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(suggestionValidation.get()));
            return response;
        }

        log.info("Try to send suggestion to " + phoneNumber);

        rcsService.sendSuggestionActionMessages(rcsSuggestionActionMessages, response, phoneNumber, text);

        return response;
    }


    @PutMapping(value = "/files/upload", consumes = MediaType.APPLICATION_JSON_VALUE)
    public DeferredResult<ResponseEntity<?>> uploadFileRequestHandler(@RequestBody @Valid RcsFile rcsFile) {

        DeferredResult<ResponseEntity<?>> response = new DeferredResult<>();

        log.info("Try to upload file " + rcsFile.getName());

        rcsService.uploadFile(rcsFile, response);

        return response;
    }

    @PostMapping(value = "/files")
    public DeferredResult<ResponseEntity<?>> sendFileRequestHandler(@RequestParam("phone") String phoneNumber,
                                                                    @RequestParam("file") String fileName) {


        DeferredResult<ResponseEntity<?>> response = new DeferredResult<>();

        log.info("Try to send file to " + phoneNumber);

        rcsService.sendFile(phoneNumber, fileName, response);

        return response;
    }

    @GetMapping(value = "/devices/check", produces = MediaType.APPLICATION_JSON_VALUE)
    public DeferredResult<ResponseEntity<?>> checkDeviceRequestHandler(@RequestParam("phone") String phoneNumber) {

        DeferredResult<ResponseEntity<?>> response = new DeferredResult<>();

        log.info("Try to send capability request " + phoneNumber);

        rcsService.sendCheckRequest(phoneNumber, response);

        return response;
    }

    @PutMapping(value = "/events/read")
    public DeferredResult<ResponseEntity<?>> readEvenRequestHandler(@RequestParam("phone") String phoneNumber,
                                                                    @RequestParam("id") String messageId) {

        DeferredResult<ResponseEntity<?>> response = new DeferredResult<>();

        log.info("Try to send readEvent request to " + phoneNumber);

        rcsService.sendReadEvent(messageId, phoneNumber, response);

        return response;
    }


}
