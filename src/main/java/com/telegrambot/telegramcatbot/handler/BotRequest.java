package com.telegrambot.telegramcatbot.handler;

import com.telegrambot.telegramcatbot.bot.UserState;
import lombok.Value;

/**
 * Доменный объект, представляющий абстрактный запрос пользователя.
 * Используется для передачи текстов команд, callback'ов и состояния пользователя.
 */
@Value
public class BotRequest {
    Long userId;
    String userName;
    String messageText;
    UserState userState;

    public boolean hasPhoto() {
        return messageText != null && messageText.startsWith("fileId:");
    }

    public String getPhotoFileId() {
        if (hasPhoto()) {
            return messageText.substring("fileId:".length());
        }
        return null;
    }
}
