package com.incuube.rcs.datamodel.statistic;

import lombok.Data;

@Data
public class StatisticMessage {

    private String phoneNumber;

    private String messageId;

    private String deliveryTime;

    private String readTime;

    private boolean delivered;

    private boolean read;

    private String sender;

}
