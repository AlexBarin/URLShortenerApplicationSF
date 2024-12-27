package org.example;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChangerURL {
    public static String originalURLToShortenURL(String originalURL) {

        String regex = "^((https?|ftp):\\/\\/)?(www\\.)?[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}(:\\d+)?(\\/[^\\s]*)?$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(originalURL);
        if (matcher.find()) {
            return "click.ru/" + generateRandomString(6);
        } else
            return null;
    }


    private static String generateRandomString(int length) {
        String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < length; i++) {
            int index = ThreadLocalRandom.current().nextInt(characters.length());
            result.append(characters.charAt(index));
        }
        return result.toString();
    }

    public static String validateAndNormalizeURL(String inputURL) {
        try {
            // Если URL уже корректный, возвращаем его как есть
            URL url = new URL(inputURL);
            return url.toString();
        } catch (MalformedURLException e) {

            try {
                URL url = new URL("http://" + inputURL);
                return url.toString();
            } catch (MalformedURLException ex) {
                System.out.println("Invalid URL: " + inputURL);
                return null;
            }
        }

    }
}