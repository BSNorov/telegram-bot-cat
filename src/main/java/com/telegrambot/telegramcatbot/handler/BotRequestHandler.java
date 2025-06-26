package com.telegrambot.telegramcatbot.handler;

import com.telegrambot.telegramcatbot.bot.UserState;
import com.telegrambot.telegramcatbot.bot.TelegramBotClient;


public interface BotRequestHandler {
    boolean supports(UserState state);
    void handle(BotRequest request, TelegramBotClient bot);
}
