package com.telegrambot.telegramcatbot.repository;

import com.telegrambot.telegramcatbot.entity.Cat;
import com.telegrambot.telegramcatbot.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CatRepository extends JpaRepository<Cat, Long> {
    
    Page<Cat> findByOwner(User owner, Pageable pageable);
    
    @Query("SELECT c FROM Cat c WHERE c.owner.telegramId != :userId ORDER BY c.createdAt DESC")
    Page<Cat> findCatsForViewing(@Param("userId") Long userId, Pageable pageable);
    
    @Query("SELECT c FROM Cat c WHERE c.owner.telegramId != :userId " +
           "AND c.id NOT IN (SELECT l.cat.id FROM Like l WHERE l.user.telegramId = :userId) " +
           "ORDER BY c.createdAt DESC")
    Page<Cat> findUnratedCatsForUser(@Param("userId") Long userId, Pageable pageable);
    
    List<Cat> findByOwnerTelegramIdOrderByCreatedAtDesc(Long telegramId);
}

