package org.example;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ShortURLApplicationAdditionalTests {

    @BeforeEach
    void setUp() {
        Dao.getBaseURL().clear(); // Очищаем базу перед каждым тестом
        Notification.clearListNotification(); // Сбрасываем уведомления
    }

    @Test
    void testUniqueShortURLsForDifferentUsers() {
        String originalURL = "http://example.com";

        UUID user1 = UUID.randomUUID();
        UUID user2 = UUID.randomUUID();

        String shortURL1 = ChangerURL.originalURLToShortenURL(originalURL);
        String shortURL2 = ChangerURL.originalURLToShortenURL(originalURL);

        Dao.add(user1, originalURL, shortURL1, 10, LocalDateTime.now());
        Dao.add(user2, originalURL, shortURL2, 10, LocalDateTime.now());

        assertNotEquals(shortURL1, shortURL2, "Короткие ссылки для разных пользователей должны быть уникальными.");
    }

    @Test
    void testBlockRedirectAfterLimitExhausted() {
        UUID uuid = UUID.randomUUID();
        String shortURL = "click.ru/test";
        String originalURL = "http://example.com";

        // Добавляем ссылку с лимитом 1
        Dao.add(uuid, originalURL, shortURL, 1, LocalDateTime.now());

        // Первое перенаправление — успешно
        Dao.editLimit(shortURL, 0, true);
        assertTrue(Dao.checkClickOutLimit(shortURL), "Лимит должен быть исчерпан.");

    }

    @Test
    void testDeleteExpiredLinks() {
        UUID uuid = UUID.randomUUID();
        String shortURL = "click.ru/test";
        String originalURL = "http://example.com";

        // Добавляем ссылку с датой создания 2 часа назад
        Dao.add(uuid, originalURL, shortURL, 10, LocalDateTime.now().minusHours(2));

        // Вызываем удаление по таймауту
        Dao.deleteTimeOut();

        assertFalse(Dao.isExist(shortURL), "Ссылки с истекшим сроком действия должны удаляться.");
    }

    @Test
    void testNotificationForLimitExhaustion() {
        UUID uuid = UUID.randomUUID();
        String shortURL = "click.ru/test";
        String originalURL = "http://example.com";

        // Добавляем ссылку с лимитом 1
        Dao.add(uuid, originalURL, shortURL, 1, LocalDateTime.now());

        // Исчерпываем лимит
        Dao.editLimit(shortURL, 0, true);
        assertTrue(Dao.checkClickOutLimit(shortURL), "Лимит должен быть исчерпан.");

        // Проверяем уведомление
        Notification.addNotification(uuid, shortURL);
        assertTrue(Notification.isExistNotification(uuid), "Пользователь должен получать уведомление об исчерпании лимита.");
    }

    @Test
    void testNotificationForExpiredLinks() {
        UUID uuid = UUID.randomUUID();
        String shortURL = "click.ru/test";
        String originalURL = "http://example.com";

        // Добавляем ссылку с датой создания 2 часа назад
        Dao.add(uuid, originalURL, shortURL, 10, LocalDateTime.now().minusHours(2));

        // Вызываем удаление по таймауту
        Dao.deleteTimeOut();

        // Проверяем уведомление
        assertTrue(Notification.isExistNotification(uuid), "Пользователь должен получать уведомление об удалении просроченной ссылки.");
    }
}

