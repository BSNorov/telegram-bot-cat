package com.telegrambot.telegramcatbot.model;

import java.nio.file.Path;
import java.time.LocalDateTime;

/**
 * TODO:
 * В будущем эту модель стоит расширить или преобразовать в сущность Cat,
 * добавив поля: id, имя, автор (userId), описание и т.д.
 * Также возможно стоит заменить Path на fileId или URL, если перейдём на хранение в БД/облаке.
 */
public class CatPhoto {
    private Path path;
    private LocalDateTime uploadedAt;

    public CatPhoto(Path path) {
        this.path = path;
        this.uploadedAt = LocalDateTime.now();
    }

    public Path getPath() {
        return path;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }
}
