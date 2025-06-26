package com.telegrambot.telegramcatbot.handler;

import com.telegrambot.telegramcatbot.bot.UserState;
import com.telegrambot.telegramcatbot.bot.TelegramBotClient;
import com.telegrambot.telegramcatbot.session.UserSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BotRequestDispatcher {

    private final ObjectProvider<BotRequestHandler> handlerProvider;
    private final UserSessionRepository session;

    public void dispatch(BotRequest request, TelegramBotClient bot) {
        UserState state = request.getUserState();

        for (BotRequestHandler handler : handlerProvider) {
            if (handler.supports(state)) {
                handler.handle(request, bot);
                return;
            }
        }

        throw new IllegalStateException(
                "Нет обработчика для состояния: " + state + " (userId=" + request.getUserId() + ")"
        );
    }
}
