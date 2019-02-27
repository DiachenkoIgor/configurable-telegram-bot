package com.incuube.rcs.datamodel.pubsub;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class PushConfig {
    @NotNull
    private String pushEndpoint;
}
