package com.telegrambot.telegramcatbot.model;

import java.nio.file.Path;
import java.time.LocalDateTime;

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
