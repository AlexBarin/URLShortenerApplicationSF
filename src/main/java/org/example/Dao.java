package org.example;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

public class Dao {
    ////
    public static ArrayList<Node> getBaseURL() {
        return baseURL;
    }

    public void print() {
        for (Node n : baseURL) {
            System.out.println(n);
        }
    }
    /////


    private static ArrayList<Node> baseURL = new ArrayList<>();

    public static void deleteTimeOut() {
        Node n;
        for (int i = 0; i < baseURL.size(); i++) {
            n = baseURL.get(i);
            if (checkTimeLimit(n.dateCreate)) {
                baseURL.remove(n);
                Notification.addNotification(n.id,n.shortURL);
                i--;
            }
        }
    }

    public static boolean isExist(String shortURL){
        return indexOfShortURL(shortURL) != -1;
    }

    public static ArrayList<String> listShortURL(UUID uuid){
        ArrayList<String> list = new ArrayList<>();
        for (Node n : baseURL) {
            if(n.id.equals(uuid)){
                list.add(n.shortURL);
            }
        }
        return list;
    }

    public static boolean isExist(UUID uuid){
        return indexOfUUID(uuid) != -1;
    }

    public static UUID getUUID(String shortURL){
       return baseURL.get(indexOfShortURL(shortURL)).id;
    }

    public static String getOriginalURL(String shortURL){
        return baseURL.get(indexOfShortURL(shortURL)).originalURL;
    }

    public static boolean checkClickOutLimit(String shortURL){
        return baseURL.get(indexOfShortURL(shortURL)).limit == 0;
    }

    private static boolean checkTimeLimit(LocalDateTime dateTime) {
        int days = ConfigManager.getIntProperty("time.limit.days");
        int hours = ConfigManager.getIntProperty("time.limit.hours");
        int minutes = ConfigManager.getIntProperty("time.limit.minutes");

        LocalDateTime modifiedDateTime = dateTime.plusHours(hours).plusDays(days).plusMinutes(minutes);
        LocalDateTime currentDateTime = LocalDateTime.now();

        return currentDateTime.isAfter(modifiedDateTime);
    }

    public static void add(UUID id, String originalURL, String shortURL, int limit, LocalDateTime date) {
        baseURL.add(new Node(id, originalURL, shortURL, limit, date));
    }

    public static void delete(String shortURL) {
        baseURL.remove(indexOfShortURL(shortURL));
    }

    /*public static Node getNode(String shortURL) {
        return baseURL.get(indexOfShortURL(shortURL));
    }*/

    public static void editLimit(String shortURL, int limit, boolean isMinus) {
        Node node = baseURL.get(indexOfShortURL(shortURL));
        if (isMinus)
            node.limit--;
        else
            node.limit = limit;
    }

    private static int indexOfShortURL(String shortURL) {
        for (int i = 0; i < baseURL.size(); i++) {
            if (baseURL.get(i).shortURL.equals(shortURL)) {
                return i;
            }
        }
        return -1;
    }

    private static int indexOfUUID(UUID uuid) {
        for (int i = 0; i < baseURL.size(); i++) {
            if (baseURL.get(i).id.equals(uuid)) {
                return i;
            }
        }
        return -1;
    }

    private static class Node {
        private UUID id;
        private String originalURL;
        private String shortURL;
        private int limit;
        private LocalDateTime dateCreate;

        private Node(UUID id, String originalURL, String shortURL, int limit, LocalDateTime dateCreate) {
            this.id = id;
            this.originalURL = originalURL;
            this.shortURL = shortURL;
            this.limit = limit;
            this.dateCreate = dateCreate;
        }

        @Override
        public String toString() {
            return "Node{" +
                    "id=" + id +
                    ", originalURL='" + originalURL + '\'' +
                    ", shortURL='" + shortURL + '\'' +
                    ", limit=" + limit +
                    ", dateCreate=" + dateCreate +
                    '}';
        }
    }

}
