package com.telegrambot.telegramcatbot.controller;

import com.telegrambot.telegramcatbot.handler.BotCommandHandler;
import com.telegrambot.telegramcatbot.model.CatPhoto;
import com.telegrambot.telegramcatbot.service.CatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.InputStream;
import java.util.List;

/**
 * TODO:
 * В будущем этот класс может быть заменён на отдельный CommandRouter/Dispatcher
 * с более масштабируемой маршрутизацией команд.
 */
@Component
@RequiredArgsConstructor
public class CatController implements BotCommandHandler {

    private final CatService catService;

    @Override
    public void handleUpdate(Update update) {
        // TODO: Логика обработки сообщений и команд будет добавлена в следующем MR
    }

    public void handlePhoto(Long userId, InputStream inputStream) {
        // TODO: Вызов сохранения фото в сервисе
    }

    public List<CatPhoto> getPhotos(Long userId) {
        // TODO: Получение фото пользователя
        return null;
    }

    public boolean hasPhotos(Long userId) {
        // TODO: Проверка наличия фото
        return false;
    }

    public boolean deletePhoto(Long userId, int index) {
        // TODO: Удаление фото
        return false;
    }
}
