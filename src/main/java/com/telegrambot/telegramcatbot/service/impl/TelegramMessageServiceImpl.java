package com.telegrambot.telegramcatbot.service.impl;

import com.telegrambot.telegramcatbot.entity.Cat;
import com.telegrambot.telegramcatbot.service.TelegramBotService;
import com.telegrambot.telegramcatbot.service.TelegramMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageCaption;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class TelegramMessageServiceImpl implements TelegramMessageService {

    private final TelegramBotService telegramBotService;
    
    public TelegramMessageServiceImpl(TelegramBotService telegramBotService) {
        this.telegramBotService = telegramBotService;
    }

    @Override
    public void sendMessage(Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);
        
        try {
            telegramBotService.getBot().execute(message);
        } catch (Exception e) {
            log.error("Error sending message to chat {}: {}", chatId, e.getMessage());
        }
    }

    @Override
    public void sendMessage(Long chatId, String text, InlineKeyboardMarkup keyboard) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);
        message.setReplyMarkup(keyboard);
        
        try {
            telegramBotService.getBot().execute(message);
        } catch (Exception e) {
            log.error("Error sending message with keyboard to chat {}: {}", chatId, e.getMessage());
        }
    }

    @Override
    public void sendPhoto(Long chatId, String fileId, String caption) {
        SendPhoto photo = new SendPhoto();
        photo.setChatId(chatId.toString());
        photo.setPhoto(new InputFile(fileId));
        photo.setCaption(caption);
        
        try {
            telegramBotService.getBot().execute(photo);
        } catch (Exception e) {
            log.error("Error sending photo to chat {}: {}", chatId, e.getMessage());
        }
    }

    @Override
    public void sendPhoto(Long chatId, String fileId, String caption, InlineKeyboardMarkup keyboard) {
        SendPhoto photo = new SendPhoto();
        photo.setChatId(chatId.toString());
        photo.setPhoto(new InputFile(fileId));
        photo.setCaption(caption);
        photo.setReplyMarkup(keyboard);
        
        try {
            telegramBotService.getBot().execute(photo);
        } catch (Exception e) {
            log.error("Error sending photo with keyboard to chat {}: {}", chatId, e.getMessage());
            log.error("Attempting to send without keyboard...");
            try {
                SendPhoto fallbackPhoto = new SendPhoto();
                fallbackPhoto.setChatId(chatId.toString());
                fallbackPhoto.setPhoto(new InputFile(fileId));
                fallbackPhoto.setCaption(caption);
                telegramBotService.getBot().execute(fallbackPhoto);
                sendMessage(chatId, "Используйте кнопки ниже:", keyboard);
            } catch (Exception fallbackException) {
                log.error("Fallback photo send also failed: {}", fallbackException.getMessage());
            }
        }
    }

    @Override
    public Integer sendPhotoWithReturn(Long chatId, String fileId, String caption, InlineKeyboardMarkup keyboard) {
        SendPhoto photo = new SendPhoto();
        photo.setChatId(chatId.toString());
        photo.setPhoto(new InputFile(fileId));
        photo.setCaption(caption);
        photo.setReplyMarkup(keyboard);
        
        try {
            var message = telegramBotService.getBot().execute(photo);
            return message.getMessageId();
        } catch (Exception e) {
            log.error("Error sending photo with keyboard to chat {}: {}", chatId, e.getMessage());
            log.error("Attempting fallback without keyboard...");
            try {
                SendPhoto fallbackPhoto = new SendPhoto();
                fallbackPhoto.setChatId(chatId.toString());
                fallbackPhoto.setPhoto(new InputFile(fileId));
                fallbackPhoto.setCaption(caption);
                var message = telegramBotService.getBot().execute(fallbackPhoto);
                sendMessage(chatId, "Используйте кнопки ниже:", keyboard);
                return message.getMessageId();
            } catch (Exception fallbackException) {
                log.error("Fallback photo send also failed: {}", fallbackException.getMessage());
                return null;
            }
        }
    }

    @Override
    public void editMessageCaption(Long chatId, Integer messageId, String caption, InlineKeyboardMarkup keyboard) {
        EditMessageCaption editCaption = new EditMessageCaption();
        editCaption.setChatId(chatId.toString());
        editCaption.setMessageId(messageId);
        editCaption.setCaption(caption);
        editCaption.setReplyMarkup(keyboard);
        
        try {
            telegramBotService.getBot().execute(editCaption);
        } catch (Exception e) {
            log.error("Error editing message caption in chat {}: {}", chatId, e.getMessage());
        }
    }

    @Override
    public InlineKeyboardMarkup buildMainMenuKeyboard() {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        List<InlineKeyboardButton> row1 = new ArrayList<>();
        InlineKeyboardButton viewCatsBtn = new InlineKeyboardButton();
        viewCatsBtn.setText("🐱 Смотреть котиков");
        viewCatsBtn.setCallbackData("view_cats");
        row1.add(viewCatsBtn);

        InlineKeyboardButton addCatBtn = new InlineKeyboardButton();
        addCatBtn.setText("➕ Добавить котика");
        addCatBtn.setCallbackData("add_cat");
        row1.add(addCatBtn);

        List<InlineKeyboardButton> row2 = new ArrayList<>();
        InlineKeyboardButton myCatsBtn = new InlineKeyboardButton();
        myCatsBtn.setText("📱 Мои котики");
        myCatsBtn.setCallbackData("my_cats");
        row2.add(myCatsBtn);

        rows.add(row1);
        rows.add(row2);
        keyboard.setKeyboard(rows);
        
        return keyboard;
    }

    @Override
    public InlineKeyboardMarkup buildCatViewingKeyboard(Long catId, boolean hasNext, boolean hasPrevious) {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        List<InlineKeyboardButton> row1 = new ArrayList<>();
        InlineKeyboardButton likeBtn = new InlineKeyboardButton();
        likeBtn.setText("👍 Нравится");
        likeBtn.setCallbackData("like_" + catId);
        row1.add(likeBtn);

        InlineKeyboardButton dislikeBtn = new InlineKeyboardButton();
        dislikeBtn.setText("👎 Не нравится");
        dislikeBtn.setCallbackData("dislike_" + catId);
        row1.add(dislikeBtn);

        List<InlineKeyboardButton> row2 = new ArrayList<>();
        if (hasPrevious) {
            InlineKeyboardButton prevBtn = new InlineKeyboardButton();
            prevBtn.setText("⬅️ Предыдущий");
            prevBtn.setCallbackData("prev_cat");
            row2.add(prevBtn);
        }
        
        if (hasNext) {
            InlineKeyboardButton nextBtn = new InlineKeyboardButton();
            nextBtn.setText("➡️ Следующий");
            nextBtn.setCallbackData("next_cat");
            row2.add(nextBtn);
        }

        List<InlineKeyboardButton> row3 = new ArrayList<>();
        InlineKeyboardButton backBtn = new InlineKeyboardButton();
        backBtn.setText("🔙 В главное меню");
        backBtn.setCallbackData("main_menu");
        row3.add(backBtn);

        rows.add(row1);
        if (!row2.isEmpty()) {
            rows.add(row2);
        }
        rows.add(row3);
        keyboard.setKeyboard(rows);
        
        return keyboard;
    }

    @Override
    public InlineKeyboardMarkup buildMyCatsKeyboard(Long catId, Page<Cat> catsPage) {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        // Actions row: like/dislike/delete
        List<InlineKeyboardButton> actionsRow = new ArrayList<>();
        InlineKeyboardButton likeBtn = new InlineKeyboardButton();
        likeBtn.setText("👍 Нравится");
        likeBtn.setCallbackData("like_" + catId);
        actionsRow.add(likeBtn);

        InlineKeyboardButton dislikeBtn = new InlineKeyboardButton();
        dislikeBtn.setText("👎 Не нравится");
        dislikeBtn.setCallbackData("dislike_" + catId);
        actionsRow.add(dislikeBtn);

        InlineKeyboardButton deleteBtn = new InlineKeyboardButton();
        deleteBtn.setText("🗑️ Удалить");
        deleteBtn.setCallbackData("delete_" + catId);
        actionsRow.add(deleteBtn);

        rows.add(actionsRow);

        if (catsPage.getTotalPages() > 1) {
            List<InlineKeyboardButton> paginationRow = new ArrayList<>();
            
            if (catsPage.hasPrevious()) {
                InlineKeyboardButton prevBtn = new InlineKeyboardButton();
                prevBtn.setText("⬅️ Предыдущая");
                prevBtn.setCallbackData("my_cats_page_" + (catsPage.getNumber() - 1));
                paginationRow.add(prevBtn);
            }
            
            InlineKeyboardButton pageInfo = new InlineKeyboardButton();
            pageInfo.setText((catsPage.getNumber() + 1) + "/" + catsPage.getTotalPages());
            pageInfo.setCallbackData("page_info");
            paginationRow.add(pageInfo);
            
            if (catsPage.hasNext()) {
                InlineKeyboardButton nextBtn = new InlineKeyboardButton();
                nextBtn.setText("➡️ Следующая");
                nextBtn.setCallbackData("my_cats_page_" + (catsPage.getNumber() + 1));
                paginationRow.add(nextBtn);
            }
            
            rows.add(paginationRow);
        }

        List<InlineKeyboardButton> backRow = new ArrayList<>();
        InlineKeyboardButton backBtn = new InlineKeyboardButton();
        backBtn.setText("🔙 В главное меню");
        backBtn.setCallbackData("main_menu");
        backRow.add(backBtn);
        rows.add(backRow);

        keyboard.setKeyboard(rows);
        return keyboard;
    }

    @Override
    public InlineKeyboardMarkup buildPaginationKeyboard(int currentPage, int totalPages, String callbackPrefix) {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        if (totalPages > 1) {
            List<InlineKeyboardButton> paginationRow = new ArrayList<>();
            
            if (currentPage > 0) {
                InlineKeyboardButton prevBtn = new InlineKeyboardButton();
                prevBtn.setText("⬅️ Предыдущая");
                prevBtn.setCallbackData(callbackPrefix + "_page_" + (currentPage - 1));
                paginationRow.add(prevBtn);
            }
            
            InlineKeyboardButton pageInfo = new InlineKeyboardButton();
            pageInfo.setText((currentPage + 1) + "/" + totalPages);
            pageInfo.setCallbackData("page_info");
            paginationRow.add(pageInfo);
            
            if (currentPage < totalPages - 1) {
                InlineKeyboardButton nextBtn = new InlineKeyboardButton();
                nextBtn.setText("➡️ Следующая");
                nextBtn.setCallbackData(callbackPrefix + "_page_" + (currentPage + 1));
                paginationRow.add(nextBtn);
            }
            
            rows.add(paginationRow);
        }

        List<InlineKeyboardButton> backRow = new ArrayList<>();
        InlineKeyboardButton backBtn = new InlineKeyboardButton();
        backBtn.setText("🔙 В главное меню");
        backBtn.setCallbackData("main_menu");
        backRow.add(backBtn);
        rows.add(backRow);

        keyboard.setKeyboard(rows);
        return keyboard;
    }
}

