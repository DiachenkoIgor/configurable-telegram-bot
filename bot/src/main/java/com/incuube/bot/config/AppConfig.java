package com.incuube.bot.config;


import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.incuube.bot.services.handlers.IncomeMessageHandler;
import com.incuube.receiver.config.MessageConfig;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.List;
import java.util.concurrent.Executor;

@Import(MessageConfig.class)
@Configuration
@EnableAsync
public class AppConfig {

    @Value("${bot.async.corePoolSize}")
    private int corePoolSize;

    @Value("${bot.async.maxPoolSize}")
    private int maxPoolSize;

    @Value("${bot.async.queueCapacity}")
    private int queueCapacity;

    @Bean
    public DynamoDBMapper dynamoDBMapper(AmazonDynamoDB amazonDynamoDB) {
        return new DynamoDBMapper(amazonDynamoDB);
    }


    @Bean
    public DynamoDB dynamoDB(AmazonDynamoDB amazonDynamoDB) {
        return new DynamoDB(amazonDynamoDB);
    }


    @Bean
    public AmazonDynamoDB amazonDynamoDB() {
        return AmazonDynamoDBClientBuilder.standard().build();
    }

    @Bean
    public Executor messageReceiverTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix("BotLogic");
        executor.initialize();
        return executor;
    }

    @Bean(name = "firstHandler")
    public IncomeMessageHandler firstHandler(List<IncomeMessageHandler> chain) {
        IncomeMessageHandler first = chain.get(0);
        for (int i = 0; i < chain.size() - 1; i++) {
            chain.get(i).setNext(chain.get(i + 1));
        }
        return first;
    }

    @Bean
    public OkHttpClient okHttpClient() {
        return new OkHttpClient.Builder().retryOnConnectionFailure(true).build();
    }

}
