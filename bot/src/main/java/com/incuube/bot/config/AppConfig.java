package com.incuube.bot.config;

import com.incuube.bot.services.handlers.IncomeMessageHandler;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.List;
import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AppConfig {

    @Value("${bot.async.corePoolSize}")
    private int corePoolSize;

    @Value("${bot.async.maxPoolSize}")
    private int maxPoolSize;

    @Value("${bot.async.queueCapacity}")
    private int queueCapacity;

    @Value("${bot.database.name}")
    private String databaseName;


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

    @Bean
    public MongoClient mongoClient() {
        return MongoClients.create("mongodb://127.0.0.1:27017");
    }

    @Bean
    public MongoDatabase sheraMongoDatabase(MongoClient mongoClient) {
        return mongoClient.getDatabase(databaseName);
    }


}
