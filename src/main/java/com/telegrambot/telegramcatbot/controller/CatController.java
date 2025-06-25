package com.telegrambot.telegramcatbot.controller;

import com.telegrambot.telegramcatbot.model.CatPhoto;
import com.telegrambot.telegramcatbot.service.CatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CatController {

    private final CatService catService;

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
