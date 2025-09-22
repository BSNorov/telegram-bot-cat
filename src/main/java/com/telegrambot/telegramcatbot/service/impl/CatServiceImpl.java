package com.telegrambot.telegramcatbot.service.impl;

import com.telegrambot.telegramcatbot.entity.Cat;
import com.telegrambot.telegramcatbot.entity.Like;
import com.telegrambot.telegramcatbot.entity.User;
import com.telegrambot.telegramcatbot.repository.CatRepository;
import com.telegrambot.telegramcatbot.repository.LikeRepository;
import com.telegrambot.telegramcatbot.repository.UserRepository;
import com.telegrambot.telegramcatbot.service.CatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class CatServiceImpl implements CatService {

    private final UserRepository userRepository;
    private final CatRepository catRepository;
    private final LikeRepository likeRepository;

    @Override
    @Transactional
    public User findOrCreateUser(Long telegramId, String username) {
        return userRepository.findByTelegramId(telegramId)
                .orElseGet(() -> {
                    User user = User.builder()
                            .telegramId(telegramId)
                            .username(username)
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .build();
                    return userRepository.save(user);
                });
    }

    @Override
    @Transactional
    public User updateUserCatName(Long telegramId, String catName) {
        User user = userRepository.findByTelegramId(telegramId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        user.setCatName(catName);
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public Cat createCat(Long ownerTelegramId, String name, String description, String fileId) {
        log.info("Creating cat for owner {}: name='{}', description='{}', fileId='{}'", ownerTelegramId, name, description, fileId);
        User owner = userRepository.findByTelegramId(ownerTelegramId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        log.info("Found owner: {}", owner.getUsername());
        
        Cat cat = Cat.builder()
                .name(name)
                .description(description)
                .fileId(fileId)
                .owner(owner)
                .likesCount(0)
                .dislikesCount(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        Cat savedCat = catRepository.save(cat);
        log.info("Cat saved with ID: {}", savedCat.getId());
        return savedCat;
    }

    @Override
    @Transactional(readOnly = true)
    public Cat getCatById(Long catId) {
        return catRepository.findById(catId).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Cat> getUserCats(Long telegramId, int page, int size) {
        log.info("Getting cats for user {} on page {} with size {}", telegramId, page, size);
        User user = userRepository.findByTelegramId(telegramId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        log.info("Found user: {}", user.getUsername());
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Cat> result = catRepository.findByOwner(user, pageable);
        log.info("Query returned {} cats for user {}", result.getTotalElements(), telegramId);
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Cat> getCatsForViewing(Long viewerTelegramId, int page, int size) {
        log.info("Getting cats for viewing for user {} on page {} with size {}", viewerTelegramId, page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<Cat> result = catRepository.findUnratedCatsForUser(viewerTelegramId, pageable);
        log.info("Found {} unrated cats for user {}", result.getTotalElements(), viewerTelegramId);
        return result;
    }

    @Override
    @Transactional
    public boolean deleteCat(Long catId, Long ownerTelegramId) {
        return catRepository.findById(catId)
                .filter(cat -> cat.getOwner().getTelegramId().equals(ownerTelegramId))
                .map(cat -> {
                    catRepository.delete(cat);
                    return true;
                })
                .orElse(false);
    }

    @Override
    @Transactional
    public void likeCat(Long userTelegramId, Long catId, boolean isLike) {
        User user = userRepository.findByTelegramId(userTelegramId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        
        Cat cat = catRepository.findById(catId)
                .orElseThrow(() -> new RuntimeException("Котик не найден"));
        
        likeRepository.findByUserTelegramIdAndCatId(userTelegramId, catId)
                .ifPresentOrElse(
                        existingLike -> {
                            if (existingLike.getType() != (isLike ? Like.LikeType.LIKE : Like.LikeType.DISLIKE)) {
                                if (existingLike.getType() == Like.LikeType.LIKE) {
                                    cat.setLikesCount(cat.getLikesCount() - 1);
                                } else {
                                    cat.setDislikesCount(cat.getDislikesCount() - 1);
                                }
                                
                                existingLike.setType(isLike ? Like.LikeType.LIKE : Like.LikeType.DISLIKE);
                                likeRepository.save(existingLike);
                                
                                if (isLike) {
                                    cat.setLikesCount(cat.getLikesCount() + 1);
                                } else {
                                    cat.setDislikesCount(cat.getDislikesCount() + 1);
                                }
                                catRepository.save(cat);
                            }
                        },
                        () -> {
                            Like like = Like.builder()
                                    .user(user)
                                    .cat(cat)
                                    .type(isLike ? Like.LikeType.LIKE : Like.LikeType.DISLIKE)
                                    .createdAt(LocalDateTime.now())
                                    .build();
                            likeRepository.save(like);
                            
                            if (isLike) {
                                cat.setLikesCount(cat.getLikesCount() + 1);
                            } else {
                                cat.setDislikesCount(cat.getDislikesCount() + 1);
                            }
                            catRepository.save(cat);
                        }
                );
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasUserRatedCat(Long userTelegramId, Long catId) {
        return likeRepository.existsByUserTelegramIdAndCatId(userTelegramId, catId);
    }
}
