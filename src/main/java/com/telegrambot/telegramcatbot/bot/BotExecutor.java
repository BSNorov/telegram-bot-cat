package com.telegrambot.telegramcatbot.bot;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;

import java.io.Serializable;
import java.io.InputStream;

public interface BotExecutor {
    <T extends Serializable> void execute(BotApiMethod<T> method);
    void execute(SendPhoto photo);

    InputStream download(String fileId);
}
