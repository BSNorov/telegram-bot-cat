package com.telegrambot.telegramcatbot.service;

import com.telegrambot.telegramcatbot.model.CatPhoto;

import java.io.InputStream;
import java.util.List;

/**
 * TODO:
 * В следующем этапе планируется переход от userId к catId.
 * Фото станет частью сущности Cat, а методы будут работать по Cat.id
 */
public interface CatService {
    void savePhoto(Long userId, InputStream inputStream); // TODO: заменить на saveCat(Cat cat)
    List<CatPhoto> getUserPhotos(Long userId); // TODO: заменить на getUserCats(Long userId)
    boolean deletePhoto(Long userId, int index); // TODO: заменить на deleteCat(Long catId)
}
