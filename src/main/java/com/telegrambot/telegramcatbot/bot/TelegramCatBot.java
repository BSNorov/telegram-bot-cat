package com.telegrambot.telegramcatbot.bot;

import com.telegrambot.telegramcatbot.config.BotConfig;
import com.telegrambot.telegramcatbot.handler.BotRequest;
import com.telegrambot.telegramcatbot.handler.BotRequestDispatcher;
import com.telegrambot.telegramcatbot.service.PhotoHandlerService;
import com.telegrambot.telegramcatbot.session.UserSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.InputStream;
import java.io.Serializable;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class TelegramCatBot extends TelegramLongPollingBot implements TelegramBotClient {

    private final BotConfig config;
    private final BotRequestDispatcher dispatcher;
    private final UserSessionRepository sessionRepository;
    private final PhotoHandlerService photoHandler;

    private BotExecutor botExecutor;

    @Autowired
    public void setBotExecutor(@Lazy BotExecutor botExecutor) {
        this.botExecutor = botExecutor;
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        log.info("Получен апдейт: {}", update);

        if (update.hasMessage() && update.getMessage().getFrom() != null) {
            Long userId = update.getMessage().getFrom().getId();
            String userName = update.getMessage().getFrom().getUserName();

            if (update.getMessage().hasText()) {
                handleTextMessage(update, userId, userName);
            }

            if (update.getMessage().hasPhoto()) {
                handlePhotoMessage(update, userId);
            }
        }

        if (update.hasCallbackQuery()) {
            var callback = update.getCallbackQuery();
            Long userId = callback.getFrom().getId();
            String userName = callback.getFrom().getUserName();
            String data = callback.getData();

            var userState = sessionRepository.getState(userId);
            dispatcher.dispatch(new BotRequest(userId, userName, data, userState), this);
        }
    }

    private void handleTextMessage(Update update, Long userId, String userName) {
        String text = update.getMessage().getText();

        if ("/start".equals(text)) {
            sessionRepository.clearState(userId);
        }

        var userState = sessionRepository.getState(userId);
        dispatcher.dispatch(new BotRequest(userId, userName, text, userState), this);
    }

    private void handlePhotoMessage(Update update, Long userId) {
        var userState = sessionRepository.getState(userId);

        if (userState != UserState.ADD_PHOTO) {
            log.info("Фото получено вне контекста ожидания — игнорируем");
            executeMethod(new SendMessage(userId.toString(), "Сначала нажми «Добавить котика» 😺"));
            return;
        }

        try {
            List<PhotoSize> photos = update.getMessage().getPhoto();
            if (photos == null || photos.isEmpty()) return;

            PhotoSize bestPhoto = photos.get(photos.size() - 1);
            String fileId = bestPhoto.getFileId();

            InputStream inputStream = download(fileId);
            String catName = sessionRepository.getTempCatName(userId);

            photoHandler.queueSave(userId, catName, inputStream);

            sessionRepository.clearState(userId);
            executeMethod(new SendMessage(userId.toString(), "Фото котика \"" + catName + "\" успешно загружено!"));
            dispatcher.dispatch(new BotRequest(userId, null, BotCommands.BACK, UserState.MAIN_MENU), this);

        } catch (Exception e) {
            log.error("Непредвиденная ошибка при загрузке фото userId={}", userId, e);
            executeMethod(new SendMessage(userId.toString(), "Произошла ошибка при загрузке фото 😿"));
        }
    }


    @Override
    public <T extends Serializable> void executeMethod(BotApiMethod<T> method) {
        try {
            execute(method);
        } catch (TelegramApiException e) {
            log.error("Ошибка Telegram API при выполнении метода", e);
        } catch (Exception e) {
            log.error("Ошибка при выполнении метода", e);
        }
    }

    @Override
    public void sendPhoto(SendPhoto photo) {
        try {
            execute(photo);
        } catch (TelegramApiException e) {
            log.error("Ошибка Telegram API при отправке фото", e);
        } catch (Exception e) {
            log.error("Ошибка при отправке фото", e);
        }
    }

    @Override
    public InputStream download(String fileId) {
        try {
            org.telegram.telegrambots.meta.api.objects.File file = execute(new GetFile(fileId));
            return downloadFileAsStream(file);
        } catch (TelegramApiException e) {
            log.error("Ошибка Telegram API при загрузке файла", e);
            throw new RuntimeException("Ошибка при загрузке файла", e);
        }
    }

}
