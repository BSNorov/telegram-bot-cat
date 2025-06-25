package com.telegrambot.telegramcatbot.bot;

import com.telegrambot.telegramcatbot.handler.BotCommandHandler;
import com.telegrambot.telegramcatbot.session.UserSessionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
@Component
public class TelegramCatBot extends TelegramLongPollingBot {

    @Value("${telegram.bot.username}")
    private String botUsername;

    @Value("${telegram.bot.token}")
    private String botToken;

    private final BotCommandHandler commandHandler;
    private final UserSessionRepository sessionRepository;

    public TelegramCatBot(BotCommandHandler commandHandler, UserSessionRepository sessionRepository) {
        this.commandHandler = commandHandler;
        this.sessionRepository = sessionRepository;
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
        // TODO: делегируем обработку обновлений
        commandHandler.handleUpdate(update);
    }

    // TODO: Основная логика меню и команд будет добавлена в следующем MR
}
