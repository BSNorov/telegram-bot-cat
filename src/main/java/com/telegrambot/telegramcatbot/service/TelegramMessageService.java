package com.telegrambot.telegramcatbot.service;

import com.telegrambot.telegramcatbot.entity.Cat;
import org.springframework.data.domain.Page;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

public interface TelegramMessageService {
    void sendMessage(Long chatId, String text);
    void sendMessage(Long chatId, String text, InlineKeyboardMarkup keyboard);
    void sendPhoto(Long chatId, String fileId, String caption);
    void sendPhoto(Long chatId, String fileId, String caption, InlineKeyboardMarkup keyboard);
    Integer sendPhotoWithReturn(Long chatId, String fileId, String caption, InlineKeyboardMarkup keyboard);
    void editMessageCaption(Long chatId, Integer messageId, String caption, InlineKeyboardMarkup keyboard);
    
    InlineKeyboardMarkup buildMainMenuKeyboard();
    InlineKeyboardMarkup buildCatViewingKeyboard(Long catId, boolean hasNext, boolean hasPrevious);
    InlineKeyboardMarkup buildMyCatsKeyboard(Long catId, Page<Cat> catsPage);
    InlineKeyboardMarkup buildPaginationKeyboard(int currentPage, int totalPages, String callbackPrefix);
}

