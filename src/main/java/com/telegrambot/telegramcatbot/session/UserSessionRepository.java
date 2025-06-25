package com.telegrambot.telegramcatbot.session;

import com.telegrambot.telegramcatbot.bot.UserState;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class UserSessionRepository {

    private final ConcurrentHashMap<Long, UserState> userStates = new ConcurrentHashMap<>();

    public void setState(Long userId, UserState state) {
        userStates.put(userId, state);
    }

    public UserState getState(Long userId) {
        return userStates.getOrDefault(userId, UserState.JOINED);
    }

    public void clearState(Long userId) {
        userStates.remove(userId);
    }
}
