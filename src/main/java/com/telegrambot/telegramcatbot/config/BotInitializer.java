package com.telegrambot.telegramcatbot.config;

import com.telegrambot.telegramcatbot.bot.TelegramCatBot;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Profile("!test")
@Component
@RequiredArgsConstructor
public class BotInitializer {

    private final TelegramCatBot telegramCatBot;

    @PostConstruct
    public void init() {
        System.out.println(">> BotInitializer запускается");

        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(telegramCatBot);
            System.out.println(">> Bot зарегистрирован через LongPolling");
        } catch (TelegramApiRequestException e) {
            String causeMessage = e.getCause() != null ? e.getCause().getMessage() : "";
            if (e.getMessage().contains("404") || causeMessage.contains("404")) {
                System.out.println("Telegram Webhook not found — можно игнорировать. Бот работает через long polling.");
            } else {
                System.out.println(">> TelegramApiRequestException: " + e.getMessage());
                e.printStackTrace();
            }
        } catch (TelegramApiException e) {
            System.out.println(">> TelegramApiException: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println(">> Ошибка при регистрации бота: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
