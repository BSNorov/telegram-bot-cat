package com.telegrambot.telegramcatbot.controller;

import com.telegrambot.telegramcatbot.handler.BotCommandHandler;
import com.telegrambot.telegramcatbot.handler.BotRequest;
import com.telegrambot.telegramcatbot.model.CatPhoto;
import com.telegrambot.telegramcatbot.service.CatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CatController implements BotCommandHandler {

    private final CatService catService;

    @Override
    public void handleRequest(BotRequest request) {
        // TODO: В следующем MR: логика обработки команд на основе текста
    }

    public void handlePhoto(Long userId, InputStream inputStream) {
        // TODO: Сохраняем фото
    }

    public List<CatPhoto> getPhotos(Long userId) {
        return null;
    }

    public boolean hasPhotos(Long userId) {
        return false;
    }

    public boolean deletePhoto(Long userId, int index) {
        return false;
    }
}
