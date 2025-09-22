package com.telegrambot.telegramcatbot.session;

import com.telegrambot.telegramcatbot.bot.UserState;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class UserSessionRepository {

    private final ConcurrentHashMap<Long, UserState> userStates = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, CatCreationSession> catCreationSessions = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, Integer> userPages = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, Integer> currentMessageIds = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, Long> currentCatIds = new ConcurrentHashMap<>();

    public void setState(Long userId, UserState state) {
        userStates.put(userId, state);
    }

    public UserState getState(Long userId) {
        return userStates.getOrDefault(userId, UserState.NEW_USER);
    }

    public void clearState(Long userId) {
        userStates.remove(userId);
        catCreationSessions.remove(userId);
        userPages.remove(userId);
        currentMessageIds.remove(userId);
        currentCatIds.remove(userId);
    }
    
    public void setCatCreationSession(Long userId, CatCreationSession session) {
        catCreationSessions.put(userId, session);
    }
    
    public CatCreationSession getCatCreationSession(Long userId) {
        return catCreationSessions.getOrDefault(userId, new CatCreationSession());
    }
    
    public void clearCatCreationSession(Long userId) {
        catCreationSessions.remove(userId);
    }
    
    public void setUserPage(Long userId, int page) {
        userPages.put(userId, page);
    }
    
    public int getUserPage(Long userId) {
        return userPages.getOrDefault(userId, 0);
    }
    
    public void clearUserPage(Long userId) {
        userPages.remove(userId);
    }
    
    public void setCurrentMessageId(Long userId, Integer messageId) {
        currentMessageIds.put(userId, messageId);
    }
    
    public Integer getCurrentMessageId(Long userId) {
        return currentMessageIds.get(userId);
    }
    
    public void clearCurrentMessageId(Long userId) {
        currentMessageIds.remove(userId);
    }
    
    public void setCurrentCatId(Long userId, Long catId) {
        currentCatIds.put(userId, catId);
    }
    
    public Long getCurrentCatId(Long userId) {
        return currentCatIds.get(userId);
    }
    
    public void clearCurrentCatId(Long userId) {
        currentCatIds.remove(userId);
    }
}
