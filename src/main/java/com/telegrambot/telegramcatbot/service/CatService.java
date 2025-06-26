package com.telegrambot.telegramcatbot.service;

import com.telegrambot.telegramcatbot.model.CatPhoto;

import java.io.InputStream;
import java.util.List;

/**
 * Сервис для работы с фотографиями котов.
 * Отвечает за сохранение, получение и удаление изображений пользователя.
 */
public interface CatService {

    /**
     * Сохраняет фото кота для пользователя (новый метод).
     *
     * @param userId     идентификатор пользователя
     * @param catName    имя котика
     * @param imageBytes байты изображения
     */
    void savePhoto(Long userId, String catName, byte[] imageBytes);

    /**
     * Старый способ — при необходимости.
     */
    default void savePhoto(Long userId, String catName, InputStream inputStream) {
        throw new UnsupportedOperationException("Этот метод не реализован");
    }

    /**
     * Получает список всех фото котов, загруженных пользователем.
     *
     * @param userId идентификатор пользователя
     * @return список фотографий
     */
    List<CatPhoto> getUserPhotos(Long userId);

    /**
     * Удаляет фото по индексу в списке.
     *
     * @param userId идентификатор пользователя
     * @param index  индекс фотографии в списке
     * @return true, если удаление прошло успешно
     */
    boolean deletePhoto(Long userId, int index);
}
