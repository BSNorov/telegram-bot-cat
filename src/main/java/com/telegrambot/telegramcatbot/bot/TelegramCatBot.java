package com.telegrambot.telegramcatbot.bot;

import com.telegrambot.telegramcatbot.handler.BotCommandHandler;
import com.telegrambot.telegramcatbot.handler.BotRequest;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
public class TelegramCatBot extends TelegramLongPollingBot {

    private String botUsername;
    private String botToken;

    private final BotCommandHandler commandHandler;

    public TelegramCatBot(BotCommandHandler commandHandler, DefaultBotOptions botOptions) {
        super(botOptions);
        this.commandHandler = commandHandler;
    }
    
    public void setBotUsername(String botUsername) {
        this.botUsername = botUsername;
    }
    
    public void setBotToken(String botToken) {
        this.botToken = botToken;
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            if (update.hasCallbackQuery()) {
                handleCallbackQuery(update);
                return;
            }
            
            if (update.getMessage() == null || update.getMessage().getFrom() == null) return;

            Long userId = update.getMessage().getFrom().getId();
            String userName = update.getMessage().getFrom().getUserName();
            String text = update.getMessage().getText();
            String photoFileId = null;
            
            if (update.getMessage().hasPhoto()) {
                photoFileId = update.getMessage().getPhoto()
                        .stream()
                        .max((p1, p2) -> Integer.compare(p1.getFileSize(), p2.getFileSize()))
                        .map(org.telegram.telegrambots.meta.api.objects.PhotoSize::getFileId)
                        .orElse(null);
            }

            BotRequest request = new BotRequest(userId, userName, text, photoFileId);
            commandHandler.handleRequest(request);
        } catch (Exception e) {
            log.error("Error processing update: ", e);
        }
    }
    
    private void handleCallbackQuery(Update update) {
        var callbackQuery = update.getCallbackQuery();
        Long userId = callbackQuery.getFrom().getId();
        String userName = callbackQuery.getFrom().getUserName();
        String callbackData = callbackQuery.getData();
        Integer messageId = callbackQuery.getMessage().getMessageId();
        
        BotRequest request = new BotRequest(userId, userName, "callback:" + callbackData, null, messageId);
        commandHandler.handleRequest(request);
        
        try {
            var answerCallback = new org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery();
            answerCallback.setCallbackQueryId(callbackQuery.getId());
            execute(answerCallback);
        } catch (Exception e) {
            log.error("Error answering callback query: ", e);
        }
    }
}
