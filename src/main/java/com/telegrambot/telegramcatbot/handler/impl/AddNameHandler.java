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
public class AddNameHandler implements BotRequestHandler {

    private final UserSessionRepository session;
    private final MessageService messageService;

    @Override
    public boolean supports(UserState state) {
        return state == UserState.ADD_NAME;
    }

    @Override
    public void handle(BotRequest request, TelegramBotClient bot) {
        Long userId = request.getUserId();
        String name = request.getMessageText();

        if (name == null || name.isBlank()) {
            messageService.sendTextMessage(userId, "Имя не должно быть пустым. Попробуйте снова 🐾");
            return;
        }

        session.setTempCatName(userId, name.trim());
        session.setState(userId, UserState.ADD_PHOTO);

        messageService.sendTextMessage(userId, "Отлично! Теперь пришлите фото котика 🐱");
    }
}
