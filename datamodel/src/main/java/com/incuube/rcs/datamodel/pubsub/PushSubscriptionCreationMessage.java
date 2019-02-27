package com.incuube.rcs.datamodel.pubsub;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class PushSubscriptionCreationMessage {

    @NotNull
    private String topic;
    @NotNull
    private PushConfig pushConfig;

}
