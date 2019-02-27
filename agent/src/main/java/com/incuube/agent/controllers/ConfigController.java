package com.incuube.agent.controllers;

import com.incuube.agent.services.api.ConfigService;
import com.incuube.rcs.datamodel.pubsub.PushSubscriptionCreationMessage;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import javax.validation.Valid;

@RestController
@RequestMapping("/config")
@Log4j2
public class ConfigController {

    private ConfigService configService;

    @Autowired
    public ConfigController(ConfigService configService) {
        this.configService = configService;
    }


    @PutMapping(value = "/subscription", consumes = MediaType.APPLICATION_JSON_VALUE)
    public DeferredResult<ResponseEntity<?>> createSubscriptionHandler(@RequestBody @Valid PushSubscriptionCreationMessage config,
                                                                       @RequestParam("subscription") String subscriptionName,
                                                                       @RequestParam("id") String googleProjectId) {

        DeferredResult<ResponseEntity<?>> response = new DeferredResult<>();

        log.debug("Try to create subscription -  " + subscriptionName);

        configService.createPushSubscription(config, googleProjectId, subscriptionName, response);

        return response;
    }

    @DeleteMapping(value = "/subscription")
    public DeferredResult<ResponseEntity<?>> deleteSubscriptionHandler(@RequestParam("subscription") String subscriptionName,
                                                                       @RequestParam("id") String googleProjectId) {

        DeferredResult<ResponseEntity<?>> response = new DeferredResult<>();

        log.debug("Try to delete subscription -  " + subscriptionName);

        configService.deleteSubscription(googleProjectId, subscriptionName, response);

        return response;
    }

}
