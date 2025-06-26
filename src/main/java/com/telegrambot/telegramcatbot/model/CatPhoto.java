package com.telegrambot.telegramcatbot.model;

import java.nio.file.Path;
import java.time.LocalDateTime;

/**
 * Модель представления загруженного фото кота.
 * В будущем может быть преобразована в полноценную сущность Cat с полями:
 * id, имя, описание, автор (userId), fileId и т.д.
 */
public class CatPhoto {
    /**
     * Путь к файлу на диске.
     */
    private final Path path;

    /**
     * Дата и время загрузки.
     */
    private final LocalDateTime uploadedAt;

    /**
     * Имя котика (извлечено из имени файла).
     */
    private final String catName;

    public CatPhoto(Path path) {
        this.path = path;
        this.uploadedAt = LocalDateTime.now();
        this.catName = extractNameFromPath(path);
    }

    public Path getPath() {
        return path;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    public String getCatName() {
        return catName;
    }

    private String extractNameFromPath(Path path) {
        // Пример имени: photo_1719422393852_Murzik.jpg
        String filename = path.getFileName().toString();
        int underscoreIndex = filename.lastIndexOf("_");
        int dotIndex = filename.lastIndexOf(".");
        if (underscoreIndex != -1 && dotIndex != -1 && underscoreIndex < dotIndex) {
            return filename.substring(underscoreIndex + 1, dotIndex);
        }
        return "Котик без имени"; // fallback на случай некорректного имени файла
    }
}
