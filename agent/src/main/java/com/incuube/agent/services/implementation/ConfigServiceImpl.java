package com.incuube.agent.services.implementation;

import com.incuube.agent.googlelib.RbmApiHelper;
import com.incuube.agent.services.api.ConfigService;
import com.incuube.rcs.datamodel.pubsub.PushSubscriptionCreationMessage;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;


@Service
@Log4j2
public class ConfigServiceImpl implements ConfigService {

    private RbmApiHelper rbmApiHelper;

    @Autowired
    public ConfigServiceImpl(RbmApiHelper rbmApiHelper) {
        this.rbmApiHelper = rbmApiHelper;
    }

    @Override
    public void createPushSubscription(PushSubscriptionCreationMessage config, String googleProjectId, String subscriptionName, DeferredResult<ResponseEntity<?>> deferredResult) {
        try {
            rbmApiHelper.createPushSubscription(googleProjectId, subscriptionName, config);
            log.info("Successful creation subscription - " + subscriptionName);
            deferredResult.setResult(ResponseEntity.ok().build());
        } catch (Exception e) {
            deferredResult.setErrorResult(e);
        }
    }

    @Override
    public void deleteSubscription(String googleProjectId, String subscriptionName, DeferredResult<ResponseEntity<?>> deferredResult) {
        try {
            rbmApiHelper.deleteSubscription(googleProjectId, subscriptionName);
            log.info("Successful deletion subscription - " + subscriptionName);
            deferredResult.setResult(ResponseEntity.ok().build());
        } catch (Exception e) {
            deferredResult.setErrorResult(e);
        }
    }
}
