package com.telegrambot.telegramcatbot.bot;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final @Lazy BotExecutor botExecutor;

    public void sendTextMessage(Long chatId, String text) {
        SendMessage message = new SendMessage(chatId.toString(), text);
        botExecutor.execute(message);
    }

    public void sendStartMenu(Long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText("🐾 Главное меню:");

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(false);

        List<KeyboardRow> rows = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        row1.add(BotCommands.ADD_PHOTO);
        row1.add(BotCommands.VIEW_PHOTOS);

        KeyboardRow row2 = new KeyboardRow();
        row2.add(BotCommands.DELETE_PHOTO);

        rows.add(row1);
        rows.add(row2);

        keyboardMarkup.setKeyboard(rows);
        message.setReplyMarkup(keyboardMarkup);

        botExecutor.execute(message);
    }

    public void sendPhotoWithNavigation(Long chatId, File file, boolean hasNext) {
        sendPhotoWithCaption(chatId, file, hasNext ? "Следующее фото →" : "Последнее фото", hasNext);
    }

    public void sendPhotoWithCaption(Long chatId, File file, String caption, boolean hasNext) {
        SendPhoto photo = new SendPhoto();
        photo.setChatId(chatId.toString());
        photo.setPhoto(new InputFile(file));
        photo.setCaption(caption);

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        if (hasNext) {
            InlineKeyboardButton next = new InlineKeyboardButton("➡️ Далее");
            next.setCallbackData(BotCommands.NEXT);
            rows.add(List.of(next));
        }

        InlineKeyboardButton back = new InlineKeyboardButton("🔙 В меню");
        back.setCallbackData(BotCommands.BACK);
        rows.add(List.of(back));

        markup.setKeyboard(rows);
        photo.setReplyMarkup(markup);

        botExecutor.execute(photo);
    }
    public void sendPhotoWithConfirm(Long chatId, String fileId, String caption) {
        SendPhoto photo = new SendPhoto();
        photo.setChatId(chatId.toString());
        photo.setPhoto(new InputFile(fileId));
        photo.setCaption(caption);

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        InlineKeyboardButton confirm = new InlineKeyboardButton("✅ Сохранить");
        confirm.setCallbackData("CONFIRM_SAVE");

        InlineKeyboardButton restart = new InlineKeyboardButton("🔁 Заново");
        restart.setCallbackData("RETRY");

        rows.add(List.of(confirm, restart));
        markup.setKeyboard(rows);
        photo.setReplyMarkup(markup);

        botExecutor.execute(photo);
    }

}
