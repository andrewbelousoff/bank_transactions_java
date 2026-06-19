package com.example.bankcards;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.service.CardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CardServiceTest {

    @Mock
    private CardRepository cardRepository;

    @InjectMocks
    private CardService cardService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testTransferMoney_Success() {
        Card fromCard = new Card("1111222233334444", "ivan_petrov", LocalDate.now().plusYears(1), CardStatus.ACTIVE, new BigDecimal("500.00"));
        Card toCard = new Card("5555666677778888", "ivan_petrov", LocalDate.now().plusYears(1), CardStatus.ACTIVE, new BigDecimal("100.00"));
        fromCard.setId(1L);
        toCard.setId(2L);

        when(cardRepository.findById(1L)).thenReturn(Optional.of(fromCard));
        when(cardRepository.findById(2L)).thenReturn(Optional.of(toCard));

        cardService.transferMoney("ivan_petrov", 1L, 2L, new BigDecimal("200.00"));

        assertEquals(new BigDecimal("300.00"), fromCard.getBalance());
        assertEquals(new BigDecimal("300.00"), toCard.getBalance());

        verify(cardRepository, times(1)).save(fromCard);
        verify(cardRepository, times(1)).save(toCard);
    }

    @Test
    void testTransferMoney_InsufficientFunds_ThrowsException() {
        Card fromCard = new Card("1111222233334444", "ivan_petrov", LocalDate.now().plusYears(1), CardStatus.ACTIVE, new BigDecimal("50.00"));
        Card toCard = new Card("5555666677778888", "ivan_petrov", LocalDate.now().plusYears(1), CardStatus.ACTIVE, new BigDecimal("100.00"));
        fromCard.setId(1L);
        toCard.setId(2L);

        when(cardRepository.findById(1L)).thenReturn(Optional.of(fromCard));
        when(cardRepository.findById(2L)).thenReturn(Optional.of(toCard));

        assertThrows(IllegalStateException.class, () -> {
            cardService.transferMoney("ivan_petrov", 1L, 2L, new BigDecimal("200.00"));
        });
    }

    @Test
    void testTransferMoney_WrongOwner_ThrowsException() {
        Card fromCard = new Card("1111222233334444", "ivan_petrov", LocalDate.now().plusYears(1), CardStatus.ACTIVE, new BigDecimal("500.00"));
        Card toCard = new Card("5555666677778888", "hacker_bob", LocalDate.now().plusYears(1), CardStatus.ACTIVE, new BigDecimal("100.00"));
        fromCard.setId(1L);
        toCard.setId(2L);

        when(cardRepository.findById(1L)).thenReturn(Optional.of(fromCard));
        when(cardRepository.findById(2L)).thenReturn(Optional.of(toCard));

        assertThrows(SecurityException.class, () -> {
            cardService.transferMoney("ivan_petrov", 1L, 2L, new BigDecimal("100.00"));
        });
    }
}
