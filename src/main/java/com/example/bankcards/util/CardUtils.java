package com.example.bankcards.util;

public class CardUtils {
    
    // Метод принимает полный номер карты и возвращает маску **** **** **** 1234
    public static String maskCardNumber(String rawCardNumber) {
        if (rawCardNumber == null || rawCardNumber.length() < 4) {
            return "**** **** **** ****";
        }
        String lastFour = rawCardNumber.substring(rawCardNumber.length() - 4);
        return "**** **** **** " + lastFour;
    }
}
