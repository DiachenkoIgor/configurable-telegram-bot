package com.incuube.agent.repositories;

import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.incuube.agent.repositories.api.StatisticMessageRepository;
import com.incuube.rcs.datamodel.statistic.StatisticMessage;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Repository
@Log4j2
public class StatisticDynamoDBMessageRepository implements StatisticMessageRepository {

    private DynamoDB dynamoDB;
    @Value("${aws.messages.table}")
    private String messageTable;
    private final String filterExpression = "(sendTime between :start and :end) or " +
            "(attribute_not_exists(sendTime) and (deliveredEvent.sendTime between :start and :end or readEvent.sendTime between :start and :end))";

    @Autowired
    public StatisticDynamoDBMessageRepository(DynamoDB dynamoDB) {
        this.dynamoDB = dynamoDB;
    }

    @Override
    public List<StatisticMessage> allStatistic() {
        List<StatisticMessage> messages = new LinkedList<>();

        Table sqsMessages = dynamoDB.getTable(messageTable);

        ScanSpec scanSpec = new ScanSpec();
        ItemCollection<ScanOutcome> items = sqsMessages.scan(scanSpec);

        for (Item item : items) {
            StatisticMessage message = new StatisticMessage();
            message.setMessageId(item.getString("messageId"));
            message.setPhoneNumber(item.getString("senderPhoneNumber"));

            if (item.isPresent("deliveredEvent")) {
                Map<String, Object> deliveredEvent = item.getMap("deliveredEvent");
                BigDecimal sendTime = (BigDecimal) deliveredEvent.get("sendTime");
                message.setDeliveryTime(Instant.ofEpochMilli(sendTime.longValue()).toString());
                message.setDelivered(true);
            }

            if (item.isPresent("readEvent")) {
                Map<String, Object> readEvent = item.getMap("readEvent");
                BigDecimal sendTime = (BigDecimal) readEvent.get("sendTime");
                message.setReadTime(Instant.ofEpochMilli(sendTime.longValue()).toString());
                message.setRead(true);
            }

            if (item.isPresent("sender")) {
                message.setSender("Agent");
                message.setDeliveryTime(Instant.ofEpochMilli(item.getNumber("sendTime").longValue()).toString());
            } else {
                message.setSender("User");
            }
            messages.add(message);
        }
        return messages;
    }

    @Override
    public void deleteRange(long from, long to) {
        Table sqsMessages = dynamoDB.getTable(messageTable);

        ScanSpec scanSpec = new ScanSpec();
        scanSpec.withProjectionExpression("messageId, sendTime, deliveredEvent, readEvent").withFilterExpression(filterExpression)
                .withValueMap(new ValueMap().withNumber(":start", from).withNumber(":end", to));

        ItemCollection<ScanOutcome> scanResult = sqsMessages.scan(scanSpec);

        scanResult.forEach(item -> deleteItem(item.getString("messageId"), sqsMessages));

    }

    private void deleteItem(String messageId, Table table) {
        table.deleteItem(new PrimaryKey("messageId", messageId));
    }
}
