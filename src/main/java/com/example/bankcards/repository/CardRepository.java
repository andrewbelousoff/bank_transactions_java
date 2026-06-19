package com.example.bankcards.repository;

import com.example.bankcards.entity.Card;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {
    // Поиск всех карт конкретного владельца с пагинацией
    Page<Card> findByOwner(String owner, Pageable pageable);
    
    // Поиск конкретной карты по зашифрованному номеру
    Optional<Card> findByCardNumber(String cardNumber);
}
