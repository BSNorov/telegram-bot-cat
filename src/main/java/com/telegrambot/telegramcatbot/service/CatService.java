package com.telegrambot.telegramcatbot.service;

import com.telegrambot.telegramcatbot.entity.Cat;
import com.telegrambot.telegramcatbot.entity.User;
import org.springframework.data.domain.Page;

public interface CatService {
    
    User findOrCreateUser(Long telegramId, String username);
    User updateUserCatName(Long telegramId, String catName);
    
    Cat createCat(Long ownerTelegramId, String name, String description, String fileId);
    Cat getCatById(Long catId);
    Page<Cat> getUserCats(Long telegramId, int page, int size);
    Page<Cat> getCatsForViewing(Long viewerTelegramId, int page, int size);
    boolean deleteCat(Long catId, Long ownerTelegramId);
    
    void likeCat(Long userTelegramId, Long catId, boolean isLike);
    boolean hasUserRatedCat(Long userTelegramId, Long catId);
}
