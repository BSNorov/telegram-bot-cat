package com.telegrambot.telegramcatbot.handler;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BotRequest {
    private final Long userId;
    private final String userName;
    private final String messageText;
    private final String photoFileId;
    private final Integer messageId;
    
    public BotRequest(Long userId, String userName, String messageText) {
        this.userId = userId;
        this.userName = userName;
        this.messageText = messageText;
        this.photoFileId = null;
        this.messageId = null;
    }
    
    public BotRequest(Long userId, String userName, String messageText, String photoFileId) {
        this.userId = userId;
        this.userName = userName;
        this.messageText = messageText;
        this.photoFileId = photoFileId;
        this.messageId = null;
    }
    
    public boolean hasPhoto() {
        return photoFileId != null && !photoFileId.isEmpty();
    }
}
