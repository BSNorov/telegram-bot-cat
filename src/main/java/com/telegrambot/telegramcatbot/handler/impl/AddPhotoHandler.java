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
public class AddPhotoHandler implements BotRequestHandler {

    private final UserSessionRepository session;
    private final MessageService messageService;

    @Override
    public boolean supports(UserState state) {
        return state == UserState.ADD_PHOTO;
    }

    @Override
    public void handle(BotRequest request, TelegramBotClient bot) {
        Long userId = request.getUserId();

        if (!request.hasPhoto()) {
            messageService.sendTextMessage(userId, "Пожалуйста, пришлите фото 🐱");
            return;
        }

        String fileId = request.getPhotoFileId();
        session.setTempFileId(userId, fileId);
        session.setState(userId, UserState.CONFIRM_PHOTO);

        String catName = session.getTempCatName(userId);
        messageService.sendPhotoWithConfirm(userId, fileId, "Котик: " + catName + "\nВсё верно?");
    }
}
