package com.telegrambot.telegramcatbot.bot;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;

import java.io.InputStream;
import java.io.Serializable;

public interface TelegramBotClient {
    <T extends Serializable> void executeMethod(BotApiMethod<T> method);
    void sendPhoto(SendPhoto photo);
    InputStream download(String fileId);
}
