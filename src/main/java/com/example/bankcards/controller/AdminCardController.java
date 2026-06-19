package com.example.bankcards.controller;

import com.example.bankcards.dto.CardResponseDTO;
import com.example.bankcards.entity.Card;
import com.example.bankcards.service.CardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/cards")
public class AdminCardController {

    private final CardService cardService;

    public AdminCardController(CardService cardService) {
        this.cardService = cardService;
    }

    // Создание новой карты для указанного пользователя
    @PostMapping
    public ResponseEntity<CardResponseDTO> createCard(@RequestParam String owner) {
        Card newCard = cardService.createCard(owner);
        return ResponseEntity.ok(new CardResponseDTO(newCard));
    }

    // Блокировка / активация карты администратором напрямую
    @PutMapping("/{id}/status")
    public ResponseEntity<CardResponseDTO> changeStatus(@PathVariable Long id) {
        Card updatedCard = cardService.blockCard(id); // Пока используем общую блокировку
        return ResponseEntity.ok(new CardResponseDTO(updatedCard));
    }
}
