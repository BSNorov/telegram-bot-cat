package com.telegrambot.telegramcatbot.bot;

import com.telegrambot.telegramcatbot.handler.BotCommandHandler;
import com.telegrambot.telegramcatbot.handler.BotRequest;
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

    public TelegramCatBot(BotCommandHandler commandHandler) {
        this.commandHandler = commandHandler;
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
        if (update.getMessage() == null || update.getMessage().getFrom() == null) return;

        Long userId = update.getMessage().getFrom().getId();
        String userName = update.getMessage().getFrom().getUserName();
        String text = update.getMessage().getText();

        BotRequest request = new BotRequest(userId, userName, text);
        commandHandler.handleRequest(request);
    }
}
