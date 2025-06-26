package com.telegrambot.telegramcatbot.handler.impl;

import com.telegrambot.telegramcatbot.bot.MessageService;
import com.telegrambot.telegramcatbot.handler.BotRequest;
import com.telegrambot.telegramcatbot.handler.BotRequestHandler;
import com.telegrambot.telegramcatbot.bot.UserState;
import com.telegrambot.telegramcatbot.session.UserSessionRepository;
import com.telegrambot.telegramcatbot.bot.TelegramBotClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AskNameHandler implements BotRequestHandler {

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

        // Сохраняем имя котика во временную сессию
        session.setTempCatName(userId, name.trim());

        // Переход в следующее состояние
        session.setState(userId, UserState.ADD_PHOTO);

        // Запрос фото
        messageService.sendTextMessage(userId, "Отлично! Теперь пришлите фото котика 🐱");
    }
}
