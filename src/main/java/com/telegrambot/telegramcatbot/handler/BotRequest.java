package com.telegrambot.telegramcatbot.handler;

import lombok.Value;

@Value
public class BotRequest {
    Long userId;
    String userName;
    String messageText;
}
