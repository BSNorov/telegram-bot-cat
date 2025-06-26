package com.telegrambot.telegramcatbot.handler.impl;

import com.telegrambot.telegramcatbot.bot.MessageService;
import com.telegrambot.telegramcatbot.bot.TelegramBotClient;
import com.telegrambot.telegramcatbot.bot.UserState;
import com.telegrambot.telegramcatbot.handler.BotRequest;
import com.telegrambot.telegramcatbot.handler.BotRequestHandler;
import com.telegrambot.telegramcatbot.session.UserSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JoinedHandler implements BotRequestHandler {

    private final MessageService messageService;
    private final UserSessionRepository session;

    @Override
    public boolean supports(UserState state) {
        return state == UserState.JOINED;
    }

    @Override
    public void handle(BotRequest request, TelegramBotClient bot) {
        Long userId = request.getUserId();
        messageService.sendTextMessage(userId, "Привет! Давай добавим твоего котика 🐱\nКак его зовут?");
        session.setState(userId, UserState.ADD_NAME);
    }
}
