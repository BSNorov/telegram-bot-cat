package com.telegrambot.telegramcatbot.repository;

import com.telegrambot.telegramcatbot.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
    
    Optional<Like> findByUserTelegramIdAndCatId(Long userId, Long catId);
    
    boolean existsByUserTelegramIdAndCatId(Long userId, Long catId);
    
    void deleteByUserTelegramIdAndCatId(Long userId, Long catId);
}

