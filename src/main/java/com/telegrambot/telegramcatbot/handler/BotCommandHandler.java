package com.telegrambot.telegramcatbot.handler;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface BotCommandHandler {
    void handleUpdate(Update update);
}
