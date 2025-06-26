package com.telegrambot.telegramcatbot.bot;

public enum UserState {
    JOINED,
    MAIN_MENU,

    // Добавление фото
    ADD_NAME,
    ADD_PHOTO,
    CONFIRM_PHOTO,

    // Просмотр и удаление
    VIEWING,
    DELETE_SELECT
}
