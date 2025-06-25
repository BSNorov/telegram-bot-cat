package com.telegrambot.telegramcatbot.service;

import com.telegrambot.telegramcatbot.model.CatPhoto;

import java.io.InputStream;
import java.util.List;

public interface CatService {
    void savePhoto(Long userId, InputStream inputStream);
    List<CatPhoto> getUserPhotos(Long userId);
    boolean deletePhoto(Long userId, int index);
}
