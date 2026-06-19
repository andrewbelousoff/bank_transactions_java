package com.example.bankcards.service;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.repository.CardRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Random;

@Service
public class CardService {

    private final CardRepository cardRepository;

    public CardService(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    // Создание новой карты (Администратор)
    public Card createCard(String owner) {
        String generatedNumber = generateRandomCardNumber();
        Card card = new Card(
            generatedNumber, // Для простоты пока пишем сырой, шифрование JWT/Security прикрутим позже
            owner,
            LocalDate.now().plusYears(5), // Срок действия 5 лет
            CardStatus.ACTIVE,
            BigDecimal.ZERO
        );
        return cardRepository.save(card);
    }

    // Просмотр карт пользователя с пагинацией
    public Page<Card> getCardsByOwner(String owner, Pageable pageable) {
        return cardRepository.findByOwner(owner, pageable);
    }

    // Запрос на блокировку карты (Пользователь / Администратор)
    @Transactional
    public Card blockCard(Long cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException("Карта не найдена"));
        card.setStatus(CardStatus.BLOCKED);
        return cardRepository.save(card);
    }

    // Перевод между своими картами
    @Transactional
    public void transferMoney(String owner, Long fromCardId, Long toCardId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Сумма перевода должна быть больше нуля");
        }

        Card fromCard = cardRepository.findById(fromCardId)
                .orElseThrow(() -> new RuntimeException("Карта списания не найдена"));
        Card toCard = cardRepository.findById(toCardId)
                .orElseThrow(() -> new RuntimeException("Карта зачисления не найдена"));

        // Проверка владельца (перевод только между своими картами)
        if (!fromCard.getOwner().equals(owner) || !toCard.getOwner().equals(owner)) {
            throw new SecurityException("Перевод возможен только между собственными картами");
        }

        // Проверка статусов карт
        if (fromCard.getStatus() != CardStatus.ACTIVE || toCard.getStatus() != CardStatus.ACTIVE) {
            throw new IllegalStateException("Обе карты должны быть активны для выполнения перевода");
        }

        // Проверка баланса
        if (fromCard.getBalance().compareTo(amount) < 0) {
            throw new IllegalStateException("Недостаточно средств на карте списания");
        }

        // Выполнение транзакции
        fromCard.setBalance(fromCard.getBalance().subtract(amount));
        toCard.setBalance(toCard.getBalance().add(amount));

        cardRepository.save(fromCard);
        cardRepository.save(toCard);
    }

    // Генератор случайного 16-значного номера карты
    private String generateRandomCardNumber() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
}
