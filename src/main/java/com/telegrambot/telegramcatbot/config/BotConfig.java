package com.telegrambot.telegramcatbot.config;

import com.telegrambot.telegramcatbot.bot.TelegramCatBot;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Configuration
@RequiredArgsConstructor
public class BotConfig {

    private final TelegramCatBot telegramCatBot;

    @PostConstruct
    public void registerBot() {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(telegramCatBot);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при регистрации бота", e);
        }
    }
}
