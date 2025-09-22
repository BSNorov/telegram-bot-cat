package com.telegrambot.telegramcatbot.service.impl;

import com.telegrambot.telegramcatbot.service.TelegramBotService;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

@Service
public class TelegramBotServiceImpl implements TelegramBotService {
    
    private TelegramLongPollingBot bot;
    
    @Override
    public TelegramLongPollingBot getBot() {
        return bot;
    }
    
    @Override
    public void setBot(TelegramLongPollingBot bot) {
        this.bot = bot;
    }
}
