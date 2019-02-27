package com.incuube.receiver.config;


import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClient;
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.config.QueueMessageHandlerFactory;
import org.springframework.cloud.aws.messaging.config.SimpleMessageListenerContainerFactory;
import org.springframework.cloud.aws.messaging.listener.QueueMessageHandler;
import org.springframework.cloud.aws.messaging.listener.SimpleMessageListenerContainer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("com.incuube.receiver")
public class MessageConfig {


    @Value("${aws.sqs.listener.messageNumber}")
    private int sqsListenerNumberOfMessage;


    @Value("${aws.sqs.listener.waitTimeout}")
    private int sqsWaitTimeout;

    @Bean(destroyMethod = "shutdown")
    public AmazonSQSAsync amazonSqs() {

        AmazonSQSAsyncClientBuilder builder = AmazonSQSAsyncClient.asyncBuilder();

        return builder.build();
    }


    @Bean
    public SimpleMessageListenerContainer simpleMessageListenerContainer(SimpleMessageListenerContainerFactory factory, QueueMessageHandler queueMessageHandler) {

        SimpleMessageListenerContainer msgListenerContainer = factory.createSimpleMessageListenerContainer();
        msgListenerContainer.setMessageHandler(queueMessageHandler);

        return msgListenerContainer;
    }

    @Bean
    public SimpleMessageListenerContainerFactory simpleMessageListenerContainerFactory(AmazonSQSAsync amazonSqs) {

        SimpleMessageListenerContainerFactory msgListenerContainerFactory = new SimpleMessageListenerContainerFactory();
        msgListenerContainerFactory.setAmazonSqs(amazonSqs);

        msgListenerContainerFactory.setMaxNumberOfMessages(sqsListenerNumberOfMessage);
        msgListenerContainerFactory.setWaitTimeOut(sqsWaitTimeout);

        return msgListenerContainerFactory;
    }

    @Bean
    public QueueMessageHandler queueMessageHandler(AmazonSQSAsync amazonSqsAsync) {

        QueueMessageHandlerFactory queueMsgHandlerFactory = new QueueMessageHandlerFactory();
        queueMsgHandlerFactory.setAmazonSqs(amazonSqsAsync);

        return queueMsgHandlerFactory.createQueueMessageHandler();
    }
}
