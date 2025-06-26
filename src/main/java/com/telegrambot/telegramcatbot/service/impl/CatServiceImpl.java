package com.telegrambot.telegramcatbot.service.impl;

import com.telegrambot.telegramcatbot.model.CatPhoto;
import com.telegrambot.telegramcatbot.service.CatService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Реализация сервиса для работы с фото котов.
 * Хранит файлы на диске в директории по userId.
 */
@Service
public class CatServiceImpl implements CatService {

    @Value("${photos.dir:cat-photos}")
    private String photosDir;

    private Path BASE_DIR;

    @PostConstruct
    public void init() {
        BASE_DIR = Paths.get(photosDir);
        try {
            Files.createDirectories(BASE_DIR);
        } catch (IOException e) {
            throw new RuntimeException("Не удалось создать базовую директорию", e);
        }
    }

    @Override
    public void savePhoto(Long userId, String catName, InputStream inputStream) {
        if (inputStream == null) {
            throw new IllegalArgumentException("Пустой поток изображения");
        }
        try {
            savePhoto(userId, catName, inputStream.readAllBytes());
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при чтении изображения", e);
        }
    }

    @Override
    public void savePhoto(Long userId, String catName, byte[] imageBytes) {
        if (imageBytes == null || imageBytes.length == 0) {
            throw new IllegalArgumentException("Пустое изображение");
        }

        Path userDir = BASE_DIR.resolve(userId.toString());
        try {
            Files.createDirectories(userDir);
            String safeName = catName.replaceAll("[^a-zA-Z0-9а-яА-Я_-]", "_");
            String filename = "photo_" + System.currentTimeMillis() + "_" + safeName + ".jpg";
            Path photoPath = userDir.resolve(filename);
            Files.write(photoPath, imageBytes, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при сохранении фото", e);
        }
    }

    @Override
    public List<CatPhoto> getUserPhotos(Long userId) {
        Path userDir = BASE_DIR.resolve(userId.toString());
        List<CatPhoto> photos = new ArrayList<>();

        if (!Files.exists(userDir)) return photos;

        try (DirectoryStream<Path> files = Files.newDirectoryStream(userDir)) {
            for (Path path : files) {
                if (Files.isRegularFile(path)) {
                    photos.add(new CatPhoto(path));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при чтении фотографий", e);
        }

        return photos;
    }

    @Override
    public boolean deletePhoto(Long userId, int index) {
        List<CatPhoto> photos = getUserPhotos(userId);
        if (index < 0 || index >= photos.size()) return false;

        Path toDelete = photos.get(index).getPath();
        try {
            return Files.deleteIfExists(toDelete);
        } catch (IOException e) {
            return false;
        }
    }
}
