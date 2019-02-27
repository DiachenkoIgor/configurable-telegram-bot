package com.incuube.agent.services.api;

import com.incuube.rcs.datamodel.pubsub.PushSubscriptionCreationMessage;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.async.DeferredResult;

public interface ConfigService {
    void createPushSubscription(PushSubscriptionCreationMessage config, String googleProjectId, String subscriptionName, DeferredResult<ResponseEntity<?>> deferredResult);

    void deleteSubscription(String googleProjectId, String subscriptionName, DeferredResult<ResponseEntity<?>> deferredResult);
}
