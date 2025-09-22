package com.telegrambot.telegramcatbot.config;

import com.telegrambot.telegramcatbot.bot.TelegramCatBot;
import com.telegrambot.telegramcatbot.handler.BotCommandHandler;
import com.telegrambot.telegramcatbot.service.TelegramBotService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class BotConfig {

    private final BotCommandHandler botCommandHandler;
    private final TelegramBotService telegramBotService;
    
    @Value("${telegram.bot.username:}")
    private String botUsername;
    
    @Value("${telegram.bot.token:}")
    private String botToken;

    @PostConstruct
    public void registerBot() {
        if (botUsername.isEmpty() || botToken.isEmpty() || 
            botUsername.equals("your_bot_username") || botToken.equals("your_bot_token")) {
            log.warn("⚠️ Учетные данные бота настроены неправильно. Пропускаем регистрацию бота.");
            log.info("Для включения бота установите переменные окружения BOT_USERNAME и BOT_TOKEN или обновите application.properties");
            log.info("Пример: BOT_USERNAME=your_bot_username BOT_TOKEN=your_bot_token");
            return;
        }
        
        try {
            DefaultBotOptions botOptions = new DefaultBotOptions();
            botOptions.setMaxThreads(1);
            
            TelegramCatBot telegramCatBot = new TelegramCatBot(botCommandHandler, botOptions);
            telegramCatBot.setBotUsername(botUsername);
            telegramCatBot.setBotToken(botToken);
            
            telegramBotService.setBot(telegramCatBot);
            
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(telegramCatBot);
            log.info("✅ Телеграм бот успешно зарегистрирован: @{}", botUsername);
        } catch (Exception e) {
            log.error("❌ Ошибка регистрации бота: {}", e.getMessage());
            log.warn("Приложение продолжит работу, но бот не будет функционировать.");
            log.info("Пожалуйста, проверьте учетные данные бота и попробуйте снова.");
        }
    }
}
