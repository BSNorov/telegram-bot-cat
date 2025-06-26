package com.telegrambot.telegramcatbot.bot;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;

import java.io.InputStream;
import java.io.Serializable;

@Component
@RequiredArgsConstructor
public class BotExecutorImpl implements BotExecutor {

    private final @Lazy TelegramBotClient bot;

    @Override
    public <T extends Serializable> void execute(BotApiMethod<T> method) {
        bot.executeMethod(method);
    }

    @Override
    public void execute(SendPhoto photo) {
        bot.sendPhoto(photo);
    }

    @Override
    public InputStream download(String fileId) {
        return bot.download(fileId);
    }
}
