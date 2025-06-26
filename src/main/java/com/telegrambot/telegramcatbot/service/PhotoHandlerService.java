package com.telegrambot.telegramcatbot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class PhotoHandlerService {

    private final CatService catService;

    /**
     * Подготовка к асинхронной архитектуре.
     * Пока сохраняет фото напрямую, но можно легко заменить на отправку события.
     */
    public void queueSave(Long userId, String catName, InputStream photoStream) {
        if (photoStream == null) {
            throw new IllegalArgumentException("Поток изображения не может быть null");
        }

        if (catName == null || catName.isBlank()) {
            catName = "noname";
        }

        try {
            // Считываем поток в byte[]
            byte[] imageBytes = readBytes(photoStream);

            // В проде здесь должно быть: broker.sendEvent(new PhotoUploadCommand(...))
            catService.savePhoto(userId, catName, imageBytes);

        } catch (IOException e) {
            throw new RuntimeException("Ошибка при чтении изображения", e);
        }
    }

    private byte[] readBytes(InputStream inputStream) throws IOException {
        try (inputStream; ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
            byte[] data = new byte[4096];
            int nRead;
            while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            return buffer.toByteArray();
        }
    }
}
