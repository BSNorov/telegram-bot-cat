package com.telegrambot.telegramcatbot.handler.impl;

import com.telegrambot.telegramcatbot.bot.TelegramBotClient;
import com.telegrambot.telegramcatbot.bot.UserState;
import com.telegrambot.telegramcatbot.handler.BotRequest;
import com.telegrambot.telegramcatbot.handler.BotRequestHandler;
import com.telegrambot.telegramcatbot.service.PhotoHandlerService;
import com.telegrambot.telegramcatbot.session.UserSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Component
@RequiredArgsConstructor
public class ConfirmPhotoHandler implements BotRequestHandler {

    private final UserSessionRepository session;
    private final PhotoHandlerService photoHandler;

    @Override
    public boolean supports(UserState state) {
        return state == UserState.CONFIRM_PHOTO;
    }

    @Override
    public void handle(BotRequest request, TelegramBotClient bot) {
        Long userId = request.getUserId();
        String command = request.getMessageText();

        switch (command) {
            case "CONFIRM_SAVE" -> {
                String fileId = session.getTempFileId(userId);
                String catName = session.getTempCatName(userId);

                if (fileId == null || catName == null) {
                    bot.executeMethod(new SendMessage(
                            userId.toString(),
                            "Что-то пошло не так. Попробуйте ещё раз 🐾"
                    ));
                    session.setState(userId, UserState.ADD_NAME);
                    return;
                }

                var inputStream = bot.download(fileId);
                photoHandler.queueSave(userId, catName, inputStream);

                session.clearState(userId);
                bot.executeMethod(new SendMessage(
                        userId.toString(),
                        "Фото котика \"" + catName + "\" успешно сохранено! 🐾"
                ));
            }

            case "RETRY" -> {
                session.setState(userId, UserState.ADD_NAME);
                bot.executeMethod(new SendMessage(
                        userId.toString(),
                        "Хорошо, давай начнем заново. Как зовут котика?"
                ));
            }

            default -> bot.executeMethod(new SendMessage(
                    userId.toString(),
                    "Пожалуйста, выбери действие с кнопок ниже ⬇️"
            ));
        }
    }
}
