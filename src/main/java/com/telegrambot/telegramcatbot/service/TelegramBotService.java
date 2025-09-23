package com.telegrambot.telegramcatbot.service;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;

public interface TelegramBotService {
    TelegramLongPollingBot getBot();
    void setBot(TelegramLongPollingBot bot);
}

