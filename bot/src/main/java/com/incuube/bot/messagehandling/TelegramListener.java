package com.incuube.bot.messagehandling;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.annotation.PostConstruct;

@Service
public class TelegramListener extends TelegramLongPollingBot {

    private TelegramIncomeMessageServiceBotLogic telegramIncomeMessageServiceBotLogic;

    @Autowired
    public TelegramListener(TelegramIncomeMessageServiceBotLogic telegramIncomeMessageServiceBotLogic) {
        this.telegramIncomeMessageServiceBotLogic = telegramIncomeMessageServiceBotLogic;
    }

    public TelegramListener() {
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasChannelPost()) {
            telegramIncomeMessageServiceBotLogic.handleMessage(update);
        }
    }

    @Override
    public String getBotUsername() {
        return "ShermansTripBot";
    }


    @Override
    public String getBotToken() {
        return "630159811:AAFPkekTa5glCS5YBtNsf29JX8SeJia77xc";
    }


    @PostConstruct
    public void setUpBot() {

        // Instantiate Telegram Bots API
        TelegramBotsApi botsApi = new TelegramBotsApi();

        // Register our bot
        try {
            botsApi.registerBot(this);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
