package com.telegrambot.telegramcatbot.handler.impl;

import com.telegrambot.telegramcatbot.bot.BotCommands;
import com.telegrambot.telegramcatbot.bot.MessageService;
import com.telegrambot.telegramcatbot.bot.TelegramBotClient;
import com.telegrambot.telegramcatbot.bot.UserState;
import com.telegrambot.telegramcatbot.handler.BotRequest;
import com.telegrambot.telegramcatbot.handler.BotRequestHandler;
import com.telegrambot.telegramcatbot.model.CatPhoto;
import com.telegrambot.telegramcatbot.service.CatService;
import com.telegrambot.telegramcatbot.session.UserSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.io.File;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DeletePhotoHandler implements BotRequestHandler {

    private final UserSessionRepository session;
    private final CatService catService;
    private final MessageService messageService;

    @Override
    public boolean supports(UserState state) {
        return state == UserState.DELETE_SELECT;
    }

    @Override
    public void handle(BotRequest request, TelegramBotClient bot) {
        Long userId = request.getUserId();
        String command = request.getMessageText();

        switch (command) {
            case BotCommands.DELETE -> {
                int index = session.getPhotoIndex(userId);
                boolean deleted = catService.deletePhoto(userId, index);

                List<CatPhoto> remaining = catService.getUserPhotos(userId);
                if (remaining == null || remaining.isEmpty()) {
                    session.clearState(userId);
                    messageService.sendTextMessage(userId, "Фото удалено. Больше фото нет 😿");
                    messageService.sendStartMenu(userId);
                } else {
                    int newIndex = Math.min(index, remaining.size() - 1);
                    session.setPhotoIndex(userId, newIndex);

                    CatPhoto photo = remaining.get(newIndex);
                    File file = photo.getPath().toFile();
                    String caption = "❌ Удалить это фото?\n🐱 Котик: " + photo.getCatName();

                    messageService.sendPhotoWithCaption(userId, file, caption, false);
                }
            }

            case BotCommands.BACK -> {
                session.setState(userId, UserState.MAIN_MENU);
                messageService.sendStartMenu(userId);
            }

            default -> {
                messageService.sendTextMessage(userId, "Нажмите кнопку ❌ Удалить или 🔙 Назад");
            }
        }
    }
}
