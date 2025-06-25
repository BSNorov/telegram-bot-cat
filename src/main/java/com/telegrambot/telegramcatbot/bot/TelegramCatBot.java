package com.telegrambot.telegramcatbot.bot;

import com.telegrambot.telegramcatbot.controller.CatController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class TelegramCatBot extends TelegramLongPollingBot {

    @Value("${telegram.bot.username}")
    private String botUsername;

    @Value("${telegram.bot.token}")
    private String botToken;

    private final Map<Long, UserState> userStates = new HashMap<>();
    private final Map<Long, String> userNames = new HashMap<>();

    private final CatController catController;

    public TelegramCatBot(CatController catController) {
        this.catController = catController;
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        // TODO: Реализация обработки апдейтов
    }

    // TODO: Основная логика меню и обратных вызовов будет добавлена в следующем MR
}
