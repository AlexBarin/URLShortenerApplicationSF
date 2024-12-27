package org.example;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.Scanner;
import java.util.UUID;

public class ShortURLApplication {

    public static void start() {
        Scanner scanner = new Scanner(System.in);
        UUID uuid = null;
        boolean b = true;
        System.out.println("*********************************");
        System.out.println("**********URL Shortener**********");
        System.out.println("*********************************");
        System.out.println();
        System.out.println("Режимы работы:");
        System.out.println("Без атрибута: введите короткую ссылку для перехода на основной ресурс." +
                "\n В случае отсутствия совпадения, проверьте корректность ссылки.");
        while (b) {
            Dao.deleteTimeOut();
            System.out.println("Redirect : Ввести короткую ссылку.");
            if (uuid == null) {
                System.out.println("Create : Создание короткой ссылки и UUID.");
                System.out.println("Login : Авторизоваться по существующему UUID.");
            } else {
                System.out.println("Create : Создание короткой ссылки.");
                System.out.println("List : Вывести список ссылок.");
                System.out.println("Logout : Выйти из учетной записи.");
            }
            System.out.println("Exit : Выход из программы.");
            if (Notification.isExistNotification(uuid)) {
                Notification.printNotification(uuid);
                Notification.removeNotification(uuid);
            }

            String sInput = scanner.nextLine();
            sInput = sInput.toLowerCase().trim();
            String urlInput = "";
            String shortURL = "";

            switch (sInput) {
                case "create":
                    if (uuid == null) {
                        uuid = UUID.randomUUID();
                        System.out.println("Вам присвоен id: " + uuid);
                    }
                    while (shortURL == null || shortURL.isEmpty()) {
                        System.out.print("Введите полную ссылку: ");
                        urlInput = scanner.nextLine();
                        urlInput = ChangerURL.validateAndNormalizeURL(urlInput);
                        shortURL = ChangerURL.originalURLToShortenURL(urlInput);
                        System.out.println("ShortURL: " + shortURL);
                    }
                    System.out.print("Введите лимит переходов по ссылке: ");
                    int limit = scanner.nextInt();
                    scanner.nextLine();
                    Dao.add(uuid, urlInput, shortURL, limit, LocalDateTime.now());
                    break;
                case "login":
                    if (uuid != null) {
                        System.out.println("Вы уже авторизованны.");
                    }
                    while (uuid == null) {
                        try {
                            System.out.println("Введите ваш UUID:");
                            String userInput = scanner.nextLine();
                            uuid = UUID.fromString(userInput);
                            if (Dao.isExist(uuid)) {
                                System.out.println("Ваш UUID: " + uuid);
                                Notification.printNotification(uuid);
                            } else {
                                System.out.println("UUID не найден");
                                uuid = null;
                            }
                        } catch (IllegalArgumentException e) {
                            System.out.println("Ошибка: введена некорректная строка для UUID.");
                        }
                    }
                    break;
                case "list":
                    System.out.println("Работа с сылками: *ссылка* *ключ*. Ключи delete, edit ; " +
                            "Пример: click.ru/hh3u4g delete");
                    System.out.println("Список ссылок: ");
                    System.out.println(Dao.listShortURL(uuid));

                    String input = scanner.nextLine();
                    String[] parts = input.split(" ", 2);
                    if (parts.length == 2) {
                        shortURL = parts[0];
                        String key = parts[1];

                        if (!Dao.isExist(shortURL)) {
                            System.out.println("URL invalid");
                        } else if (!Dao.getUUID(shortURL).equals(uuid)) {
                            System.out.println("Permission denied");
                        } else {
                            if (key.equals("delete")) {
                                Dao.delete(shortURL);
                                System.out.println("Ссылка удалена.");
                            }
                            if (key.equals("edit")) {
                                System.out.print("Введите новое число лимита перехода: ");
                                int ne = scanner.nextInt();
                                scanner.nextLine();
                                if (ne == 0) {
                                    System.out.println("Use delete");
                                }
                                Dao.editLimit(shortURL, ne, false);
                                System.out.println("Успешно.");
                            }
                        }
                    } else {
                        System.out.println("Ошибка: Некорректный формат строки.");
                    }
                    break;
                case "logout":
                    if (uuid != null) {
                        uuid = null;
                    } else {
                        System.out.println("Вы еще не авторизированны.");
                    }
                    break;
                case "redirect":
                    System.out.print("Введите короткую ссылку: ");
                    shortURL = scanner.nextLine();
                    if (Dao.isExist(shortURL)) {
                        String originalURL = Dao.getOriginalURL(shortURL);
                        System.out.println("Полная ссылка: " + originalURL);
                        try {
                            Desktop.getDesktop().browse(new URI(originalURL));
                        } catch (IOException | URISyntaxException e) {
                            throw new RuntimeException(e);
                        }
                        Dao.editLimit(shortURL, 0, true);
                        if (Dao.checkClickOutLimit(shortURL)) {
                            if (Dao.getUUID(shortURL).equals(uuid)) {
                                System.out.println("ShortURL была удалена. Лимит переходов исчерпан");

                            } else {
                                Notification.addNotification(uuid, shortURL);
                            }
                            Dao.delete(shortURL);
                        }
                        // Create notification /////////////////////////////////////////////////////////////////
                    } else {
                        System.out.println("Wrong url.");
                    }
                    break;
                case "exit":
                    b = false;

            }

        }

        scanner.close();
    }
}
