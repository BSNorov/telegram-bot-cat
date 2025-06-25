package com.telegrambot.telegramcatbot.service.impl;

import com.telegrambot.telegramcatbot.model.CatPhoto;
import com.telegrambot.telegramcatbot.service.CatService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;

/**
 * TODO:
 * В дальнейшем будет заменён на работу с сущностью Cat.
 * Сейчас реализует сохранение и получение фото по userId.
 */
@Service
public class CatServiceImpl implements CatService {

    @Value("${photos.dir:cat-photos}")
    private String photosDir;

    private Path BASE_DIR;

    @PostConstruct
    public void init() {
        // TODO: Создание базовой директории для хранения фото
    }

    @Override
    public void savePhoto(Long userId, InputStream inputStream) {
        // TODO: В будущем: saveCat(Cat cat) с полями catId, name и т.д.
    }

    @Override
    public List<CatPhoto> getUserPhotos(Long userId) {
        // TODO: В будущем: вернуть List<Cat> вместо List<CatPhoto>
        return null;
    }

    @Override
    public boolean deletePhoto(Long userId, int index) {
        // TODO: В будущем: deleteCat(Long catId)
        return false;
    }
}
