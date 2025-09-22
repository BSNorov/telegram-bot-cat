package com.telegrambot.telegramcatbot.controller;

import com.telegrambot.telegramcatbot.bot.UserState;
import com.telegrambot.telegramcatbot.entity.Cat;
import com.telegrambot.telegramcatbot.entity.User;
import com.telegrambot.telegramcatbot.handler.BotCommandHandler;
import com.telegrambot.telegramcatbot.handler.BotRequest;
import com.telegrambot.telegramcatbot.service.CatService;
import com.telegrambot.telegramcatbot.service.TelegramMessageService;
import com.telegrambot.telegramcatbot.session.CatCreationSession;
import com.telegrambot.telegramcatbot.session.UserSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
@Component
@RequiredArgsConstructor
public class CatController implements BotCommandHandler {

    private final CatService catService;
    private final TelegramMessageService messageService;
    private final UserSessionRepository sessionRepository;
    
    private static final int CATS_PER_PAGE = 5;

    @Override
    public void handleRequest(BotRequest request) {
        try {
            Long userId = request.getUserId();
            String username = request.getUserName();
            String text = request.getMessageText();
            String photoFileId = request.getPhotoFileId();
            
            log.info("Handling request from user {}: text='{}', hasPhoto={}", 
                    userId, text, request.hasPhoto());

            User user = catService.findOrCreateUser(userId, username);
            UserState currentState = sessionRepository.getState(userId);
            
            if (text != null && text.startsWith("callback:")) {
                handleCallbackQuery(userId, text.substring(9), request);
                return;
            }
            
            if (text != null) {
                if (text.equals("/start")) {
                    handleStartCommand(userId, user);
                    return;
                }
            }
            
            switch (currentState) {
                case NEW_USER:
                    handleNewUser(userId, user, text);
                    break;
                case SETTING_CAT_NAME:
                    handleSetCatName(userId, text);
                    break;
                case ADD_CAT_NAME:
                    handleAddCatName(userId, text);
                    break;
                case ADD_CAT_DESCRIPTION:
                    handleAddCatDescription(userId, text);
                    break;
                case ADD_CAT_PHOTO:
                    handleAddCatPhoto(userId, photoFileId);
                    break;
                case VIEWING_CATS:
                    handleViewingCats(userId, text);
                    break;
                case VIEWING_MY_CATS:
                    handleViewingMyCats(userId, text);
                    break;
                default:
                    handleMainMenu(userId);
                    break;
            }
            
        } catch (Exception e) {
            log.error("Error handling request: ", e);
            messageService.sendMessage(request.getUserId(), 
                "Извините, произошла ошибка. Пожалуйста, попробуйте снова или используйте /start для перезапуска.");
        }
    }
    
    
    private void handleCallbackQuery(Long userId, String callbackData, BotRequest request) {
        log.info("Handling callback query from user {}: {}", userId, callbackData);
        
        if (callbackData.equals("main_menu")) {
            sessionRepository.setState(userId, UserState.MAIN_MENU);
            handleMainMenu(userId);
        } else if (callbackData.equals("view_cats")) {
            log.info("User {} requested view_cats", userId);
            sessionRepository.setState(userId, UserState.VIEWING_CATS);
            sessionRepository.setUserPage(userId, 0);
            showCatsForViewing(userId, 0);
        } else if (callbackData.equals("add_cat")) {
            sessionRepository.setState(userId, UserState.ADD_CAT_NAME);
            sessionRepository.setCatCreationSession(userId, new CatCreationSession());
            messageService.sendMessage(userId, "🐱 Давайте добавим нового котика!\n\nШаг 1 из 3: Как зовут вашего котика?");
        } else if (callbackData.equals("my_cats")) {
            log.info("User {} requested my_cats", userId);
            sessionRepository.setState(userId, UserState.VIEWING_MY_CATS);
            sessionRepository.setUserPage(userId, 0);
            showMyCats(userId, 0);
        } else if (callbackData.startsWith("like_")) {
            Long catId = Long.parseLong(callbackData.substring(5));
            handleLikeCat(userId, catId, true, request.getMessageId());
        } else if (callbackData.startsWith("dislike_")) {
            Long catId = Long.parseLong(callbackData.substring(8));
            handleLikeCat(userId, catId, false, request.getMessageId());
        } else if (callbackData.equals("next_cat")) {
            handleNextCat(userId);
        } else if (callbackData.equals("prev_cat")) {
            handlePreviousCat(userId);
        } else if (callbackData.startsWith("my_cats_page_")) {
            int page = Integer.parseInt(callbackData.substring(13));
            sessionRepository.setUserPage(userId, page);
            showMyCats(userId, page);
        }
    }

    private void handleStartCommand(Long userId, User user) {
        if (user.getCatName() == null || user.getCatName().isEmpty()) {
            sessionRepository.setState(userId, UserState.SETTING_CAT_NAME);
            messageService.sendMessage(userId, 
                "🐾 Добро пожаловать в Котобот! 🐾\n\n" +
                "Для начала расскажите, как зовут вашего котика:");
        } else {
            sessionRepository.setState(userId, UserState.MAIN_MENU);
            messageService.sendMessage(userId, 
                "🐾 С возвращением, хозяин " + user.getCatName() + "! 🐾\n\n" +
                "Что будем делать?", 
                messageService.buildMainMenuKeyboard());
        }
    }

    private void handleNewUser(Long userId, User user, String text) {
        handleStartCommand(userId, user);
    }

    private void handleSetCatName(Long userId, String catName) {
        if (catName == null || catName.trim().isEmpty()) {
            messageService.sendMessage(userId, "Пожалуйста, введите правильное имя котика:");
            return;
        }
        
        catService.updateUserCatName(userId, catName.trim());
        sessionRepository.setState(userId, UserState.MAIN_MENU);
        
        messageService.sendMessage(userId, 
            "Отлично! Рад познакомиться с " + catName.trim() + "! 🐱\n\n" +
            "Что будем делать?", 
            messageService.buildMainMenuKeyboard());
    }

    private void handleMainMenu(Long userId) {
        sessionRepository.setState(userId, UserState.MAIN_MENU);
        messageService.sendMessage(userId, 
            "🐾 Главное меню 🐾\n\nЧто будем делать?", 
            messageService.buildMainMenuKeyboard());
    }

    private void handleAddCatName(Long userId, String name) {
        if (name == null || name.trim().isEmpty()) {
            messageService.sendMessage(userId, "Пожалуйста, введите правильное имя котика:");
            return;
        }
        
        CatCreationSession session = sessionRepository.getCatCreationSession(userId);
        session.setName(name.trim());
        sessionRepository.setCatCreationSession(userId, session);
        sessionRepository.setState(userId, UserState.ADD_CAT_DESCRIPTION);
        
        messageService.sendMessage(userId, 
            "Шаг 2 из 3: Пожалуйста, добавьте описание для " + name.trim() + ":");
    }

    private void handleAddCatDescription(Long userId, String description) {
        if (description == null || description.trim().isEmpty()) {
            messageService.sendMessage(userId, "Пожалуйста, введите правильное описание:");
            return;
        }
        
        CatCreationSession session = sessionRepository.getCatCreationSession(userId);
        session.setDescription(description.trim());
        sessionRepository.setCatCreationSession(userId, session);
        sessionRepository.setState(userId, UserState.ADD_CAT_PHOTO);
        
        messageService.sendMessage(userId, 
            "Шаг 3 из 3: Пожалуйста, отправьте фото " + session.getName() + ":");
    }

    private void handleAddCatPhoto(Long userId, String photoFileId) {
        if (photoFileId == null || photoFileId.isEmpty()) {
            messageService.sendMessage(userId, "Пожалуйста, отправьте фото:");
            return;
        }
        
        CatCreationSession session = sessionRepository.getCatCreationSession(userId);
        log.info("Creating cat for user {}: name='{}', description='{}'", userId, session.getName(), session.getDescription());
        
        Cat cat = catService.createCat(userId, session.getName(), session.getDescription(), photoFileId);
        log.info("Cat created successfully with ID: {}", cat.getId());
        
        sessionRepository.clearCatCreationSession(userId);
        sessionRepository.setState(userId, UserState.MAIN_MENU);
        
        messageService.sendMessage(userId, 
            "🎉 " + cat.getName() + " успешно добавлен!\n\n" +
            "Что будем делать дальше?", 
            messageService.buildMainMenuKeyboard());
    }

    private void showCatsForViewing(Long userId, int page) {
        log.info("Showing cats for viewing for user {} on page {}", userId, page);
        Page<Cat> catsPage = catService.getCatsForViewing(userId, page, 1);
        log.info("Found {} cats for viewing for user {}", catsPage.getTotalElements(), userId);
        
        if (catsPage.isEmpty()) {
            messageService.sendMessage(userId, 
                "Сейчас нет котиков для просмотра. 😿\n\n" +
                "Попробуйте добавить котиков первыми!", 
                messageService.buildMainMenuKeyboard());
            sessionRepository.setState(userId, UserState.MAIN_MENU);
            return;
        }
        
        Cat cat = catsPage.getContent().get(0);
        String caption = String.format(
            "🐱 %s\n\n%s\n\n👍 %d лайков | 👎 %d дизлайков\n\nОт: @%s",
            cat.getName(),
            cat.getDescription(),
            cat.getLikesCount(),
            cat.getDislikesCount(),
            cat.getOwner().getUsername()
        );
        
        messageService.sendPhoto(userId, cat.getFileId(), caption);
        messageService.sendMessage(userId, "Оцените котика:", 
            messageService.buildCatViewingKeyboard(cat.getId(), catsPage.hasNext(), catsPage.hasPrevious()));
        sessionRepository.setCurrentCatId(userId, cat.getId());
    }

    private void showMyCats(Long userId, int page) {
        log.info("Showing my cats for user {} on page {}", userId, page);
        Page<Cat> catsPage = catService.getUserCats(userId, page, 1);
        log.info("Found {} cats for user {}", catsPage.getTotalElements(), userId);
        
        if (catsPage.isEmpty()) {
            log.info("No cats found for user {}", userId);
            messageService.sendMessage(userId, 
                "Вы еще не добавили ни одного котика. 😿\n\n" +
                "Используйте 'Добавить котика' чтобы поделиться первым!", 
                messageService.buildMainMenuKeyboard());
            sessionRepository.setState(userId, UserState.MAIN_MENU);
            return;
        }
        
        Cat cat = catsPage.getContent().get(0);
        String caption = String.format(
            "📱 Ваш котик (Страница %d из %d)\n\n🐱 %s\n\n%s\n\n👍 %d лайков | 👎 %d дизлайков",
            page + 1,
            catsPage.getTotalPages(),
            cat.getName(),
            cat.getDescription(),
            cat.getLikesCount(),
            cat.getDislikesCount()
        );
        
        messageService.sendPhoto(userId, cat.getFileId(), caption);
        messageService.sendMessage(userId, "Управление:", messageService.buildMyCatsKeyboard(catsPage));
    }

    private void handleLikeCat(Long userId, Long catId, boolean isLike, Integer messageId) {
        try {
            catService.likeCat(userId, catId, isLike);
            
            if (messageId != null) {
                refreshCurrentCatDisplay(userId, catId, messageId);
            } else {
                Integer currentMessageId = sessionRepository.getCurrentMessageId(userId);
                if (currentMessageId != null) {
                    refreshCurrentCatDisplay(userId, catId, currentMessageId);
                }
            }
            
        } catch (Exception e) {
            log.error("Error handling like/dislike: ", e);
            messageService.sendMessage(userId, "Извините, не удалось обработать вашу реакцию. Попробуйте снова!");
        }
    }
    
    private void refreshCurrentCatDisplay(Long userId, Long catId, Integer messageId) {
        try {
            Cat cat = catService.getCatById(catId);
            if (cat != null) {
                String caption = String.format(
                    "🐱 %s\n\n%s\n\n👍 %d лайков | 👎 %d дизлайков\n\nОт: @%s",
                    cat.getName(),
                    cat.getDescription(),
                    cat.getLikesCount(),
                    cat.getDislikesCount(),
                    cat.getOwner().getUsername()
                );
                
                Page<Cat> catsPage = catService.getCatsForViewing(userId, sessionRepository.getUserPage(userId), 1);
                messageService.editMessageCaption(userId, messageId, caption, 
                    messageService.buildCatViewingKeyboard(cat.getId(), catsPage.hasNext(), catsPage.hasPrevious()));
            }
        } catch (Exception e) {
            log.error("Error refreshing cat display: ", e);
        }
    }

    private void handleNextCat(Long userId) {
        int currentPage = sessionRepository.getUserPage(userId);
        sessionRepository.setUserPage(userId, currentPage + 1);
        showCatsForViewing(userId, currentPage + 1);
    }

    private void handlePreviousCat(Long userId) {
        int currentPage = sessionRepository.getUserPage(userId);
        if (currentPage > 0) {
            sessionRepository.setUserPage(userId, currentPage - 1);
            showCatsForViewing(userId, currentPage - 1);
        }
    }

    private void handleViewingCats(Long userId, String text) {
        handleMainMenu(userId);
    }

    private void handleViewingMyCats(Long userId, String text) {
        handleMainMenu(userId);
    }
}
