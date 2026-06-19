package com.example.bankcards.controller;

import com.example.bankcards.dto.CardResponseDTO;
import com.example.bankcards.dto.TransferRequestDTO;
import com.example.bankcards.entity.Card;
import com.example.bankcards.service.CardService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user/cards")
public class UserCardController {

    private final CardService cardService;

    public UserCardController(CardService cardService) {
        this.cardService = cardService;
    }

    @GetMapping
    public ResponseEntity<Page<CardResponseDTO>> getMyCards(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        // Получаем имя текущего авторизованного через JWT пользователя
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        Pageable pageable = PageRequest.of(page, size);
        Page<Card> cards = cardService.getCardsByOwner(currentUser, pageable);
        Page<CardResponseDTO> response = cards.map(CardResponseDTO::new);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/block")
    public ResponseEntity<CardResponseDTO> requestBlockCard(@PathVariable Long id) {
        Card blockedCard = cardService.blockCard(id);
        return ResponseEntity.ok(new CardResponseDTO(blockedCard));
    }

    @PostMapping("/transfer")
    public ResponseEntity<String> transferMoney(@RequestBody TransferRequestDTO request) {
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        cardService.transferMoney(
                currentUser, 
                request.getFromCardId(), 
                request.getToCardId(), 
                request.getAmount()
        );
        return ResponseEntity.ok("Перевод успешно выполнен");
    }
}
