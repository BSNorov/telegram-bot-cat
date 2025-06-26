package com.telegrambot.telegramcatbot.session;

import com.telegrambot.telegramcatbot.bot.UserState;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class UserSessionRepository {

    private final ConcurrentHashMap<Long, UserState> userStates = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, Integer> photoIndexes = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, String> tempCatNames = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, String> tempFileIds = new ConcurrentHashMap<>();

    // FSM: состояние пользователя
    public void setState(Long userId, UserState state) {
        userStates.put(userId, state);
    }

    public UserState getState(Long userId) {
        return userStates.getOrDefault(userId, UserState.JOINED);
    }

    public void clearState(Long userId) {
        userStates.remove(userId);
        photoIndexes.remove(userId);
        tempCatNames.remove(userId);
        tempFileIds.remove(userId);
    }

    // Индекс фото (для навигации)
    public void setPhotoIndex(Long userId, int index) {
        photoIndexes.put(userId, index);
    }

    public int getPhotoIndex(Long userId) {
        return photoIndexes.getOrDefault(userId, 0);
    }

    public void resetPhotoIndex(Long userId) {
        photoIndexes.remove(userId);
    }

    // Временное имя котика
    public void setTempCatName(Long userId, String name) {
        tempCatNames.put(userId, name);
    }

    public String getTempCatName(Long userId) {
        return tempCatNames.get(userId);
    }

    // Временный fileId фото
    public void setTempFileId(Long userId, String fileId) {
        tempFileIds.put(userId, fileId);
    }

    public String getTempFileId(Long userId) {
        return tempFileIds.get(userId);
    }
}
