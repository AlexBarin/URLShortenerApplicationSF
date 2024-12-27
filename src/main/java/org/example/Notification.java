package org.example;

import java.util.ArrayList;
import java.util.UUID;

public class Notification {

    private static ArrayList<Node> listNotification = new ArrayList<>();
    public static void clearListNotification(){
        listNotification.clear();
    }
    public static boolean addNotification(UUID id, String shortURL){
        listNotification.add(new Node(id, shortURL));
        return false;
    }
    private static int indexOfUUID(UUID id) {
        for (int i = 0; i < listNotification.size(); i++) {
            if (listNotification.get(i).id.equals(id)) {
                return i;
            }
        }
        return -1;
    }
    public static void printNotification(UUID uuid){
        for (int i = 0; i < listNotification.size(); i++) {
            if(listNotification.get(i).id.equals(uuid)){
                System.out.println("Ссылка удалена от времени: ShortURL: " + listNotification.get(i).shortURL);
            }
        }
    }

    public static boolean isExistNotification(UUID uuid){
        for (int i = 0; i < listNotification.size(); i++) {
            if(listNotification.get(i).id.equals(uuid)){
                return true;
            }
        }
        return false;
    }

    public static void removeNotification(UUID uuid){
        while(isExistNotification(uuid)){
            listNotification.remove(indexOfUUID(uuid));
        }

    }

    private static class Node{
        private final UUID id;
        private final String shortURL;

        public Node(UUID id, String shortURL){
            this.id = id;
            this.shortURL = shortURL;
        }
    }

}
