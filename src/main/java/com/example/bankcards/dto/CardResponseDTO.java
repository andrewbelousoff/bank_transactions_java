package com.example.bankcards.dto;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.util.CardUtils;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CardResponseDTO {
    private Long id;
    private String cardNumber; // Здесь будет маска
    private String owner;
    private LocalDate expirationDate;
    private CardStatus status;
    private BigDecimal balance;

    public CardResponseDTO(Card card) {
        this.id = card.getId();
        // Применяем маскирование из нашего утилитного класса
        this.cardNumber = CardUtils.maskCardNumber(card.getCardNumber());
        this.owner = card.getOwner();
        this.expirationDate = card.getExpirationDate();
        this.status = card.getStatus();
        this.balance = card.getBalance();
    }

    // Геттеры
    public Long getId() { return id; }
    public String getCardNumber() { return cardNumber; }
    public String getOwner() { return owner; }
    public LocalDate getExpirationDate() { return expirationDate; }
    public CardStatus getStatus() { return status; }
    public BigDecimal getBalance() { return balance; }
}
