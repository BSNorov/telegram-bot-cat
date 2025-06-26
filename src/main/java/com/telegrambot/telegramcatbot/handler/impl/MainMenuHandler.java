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

import java.io.File;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MainMenuHandler implements BotRequestHandler {

    private final MessageService messageService;
    private final CatService catService;
    private final UserSessionRepository session;

    @Override
    public boolean supports(UserState state) {
        return state == UserState.MAIN_MENU;
    }

    @Override
    public void handle(BotRequest request, TelegramBotClient bot) {
        String text = request.getMessageText();
        Long userId = request.getUserId();

        switch (text) {
            case BotCommands.ADD_PHOTO -> {
                session.setState(userId, UserState.ADD_NAME);
                messageService.sendTextMessage(userId, "Как зовут котика?");
            }

            case BotCommands.VIEW_PHOTOS -> {
                List<CatPhoto> photos = catService.getUserPhotos(userId);
                if (photos == null || photos.isEmpty()) {
                    messageService.sendTextMessage(userId, "У вас пока нет сохранённых фото 😿");
                } else {
                    session.setPhotoIndex(userId, 0);
                    showCurrentPhoto(userId, photos, 0);
                }
            }

            case BotCommands.NEXT -> {
                List<CatPhoto> photos = catService.getUserPhotos(userId);
                int current = session.getPhotoIndex(userId);
                if (photos != null && current + 1 < photos.size()) {
                    int nextIndex = current + 1;
                    session.setPhotoIndex(userId, nextIndex);
                    showCurrentPhoto(userId, photos, nextIndex);
                } else {
                    messageService.sendTextMessage(userId, "📌 Это было последнее фото");
                }
            }

            case BotCommands.DELETE_PHOTO -> {
                List<CatPhoto> photos = catService.getUserPhotos(userId);
                if (photos == null || photos.isEmpty()) {
                    messageService.sendTextMessage(userId, "Нет фото для удаления 😿");
                } else {
                    session.setPhotoIndex(userId, 0);
                    session.setState(userId, UserState.DELETE_SELECT);
                    CatPhoto photo = photos.get(0);
                    File file = photo.getPath().toFile();
                    String caption = "❌ Удалить это фото?\n🐱 Котик: " + photo.getCatName();
                    messageService.sendPhotoWithCaption(userId, file, caption, false);
                }
            }

            case BotCommands.BACK -> {
                session.resetPhotoIndex(userId);
                session.setState(userId, UserState.MAIN_MENU);
                messageService.sendStartMenu(userId);
            }

            default -> messageService.sendTextMessage(userId, "Неизвестная команда.");
        }
    }

    private void showCurrentPhoto(Long userId, List<CatPhoto> photos, int index) {
        CatPhoto photo = photos.get(index);
        File file = photo.getPath().toFile();
        boolean hasNext = index + 1 < photos.size();

        String caption = "🐱 Котик: " + photo.getCatName();
        caption += hasNext ? "\n➡️ Следующее фото →" : "\n📌 Это было последнее фото";

        messageService.sendPhotoWithCaption(userId, file, caption, hasNext);
    }
}
