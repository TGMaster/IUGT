/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Chat;

import Config.Config;
import POJO.Player;

import com.google.gson.JsonObject;
import java.io.IOException;
import java.util.*;
import javax.websocket.Session;

/**
 *
 * @author TGMaster
 */
public class UserManager {

    private static int count = 0;
    private final static Set<Player> players = new HashSet<>();
    private final static HashMap<Long, Set<Player>> onlinePlayers = new HashMap<>();
    private final static HashMap<Long, Session> pSession = new HashMap<>();

    public static void joinChat(Player p, Session session) {
        if (!players.contains(p)) {
            players.add(p);
            count++;
        }
        pSession.put(p.getId(), session);
        prepareOnlineList(p);
        //System.out.println("Count = " + count);
    }

    public static void leaveChat(long id, Session session) {
        count--;
        //System.out.println("Count = " + count);
        Player p = getUserById(id);
        if (p != null) {

            players.remove(p);
            pSession.remove(p.getId());

            JsonObject removeMsg = new JsonObject();
            removeMsg.addProperty("action", Config.LEAVE_CHAT);
            removeMsg.addProperty("id", id);
            removeMsg.addProperty("name", p.getName());
            removeMsg.addProperty("message", "has left");

            //todo-task
            //notify server if someone offline
            for (Player player : onlinePlayers.get(id)) {
                Set<Player> list = getOnlineList();
                if (pSession.containsKey(player.getId())) {
                    sendToSession(pSession.get(player.getId()), removeMsg);
                }
            }
        }
    }

    public static void sendMessage(long sender, String message) {
        JsonObject chatMessage = createMessage(sender, message);

        sendToSession(pSession.get(sender), chatMessage);

        //broadcast messages to others...
        for (Player p : onlinePlayers.get(sender)) {
            if (pSession.containsKey(p.getId()) && p.getId() != sender) {
                sendToSession(pSession.get(p.getId()), chatMessage);
            }
        }
    }

    private static JsonObject createMessage(long sender, String message) {
        Player player = getUserById(sender);
        JsonObject addMessage = new JsonObject();
        addMessage.addProperty("action", Config.CHAT_MSG);
        addMessage.addProperty("id", sender);
        addMessage.addProperty("name", (player != null) ? player.getName() : "null");
        addMessage.addProperty("message", message);
        return addMessage;
    }

    /* Stuffs */
    private static void prepareOnlineList(Player player) {
        Set<Player> list = getOnlineList(); // Get current online list
        onlinePlayers.put(player.getId(), list); // that player holds the online list
        JsonObject addMessage;
        for (Player p : list) { // loop each player in that online list
            if (pSession.containsKey(p.getId())) {
                addMessage = createUpdateMessage(player);
                sendToSession(pSession.get(p.getId()), addMessage);
            }
            if (p == player) {
                addMessage = createMyJoinMessage(p); // create message of new player for me
            } else {
                addMessage = createOtherJoinMessage(p); // create message of new player for old ones
            }
            sendToSession(pSession.get(player.getId()), addMessage);
        }
    }

    private static Player getUserById(long id) {
        for (Player p : players) {
            if (p.getId() == id) {
                return p;
            }
        }
        return null;
    }

    private static Set<Player> getOnlineList() {
        return players;
    }

    private static void sendToSession(Session session, JsonObject message) {
        try {
            if (session.isOpen()) {
                session.getBasicRemote().sendText(message.toString());
            }
        } catch (IOException ex) {
            //Logger.getLogger(UserSessionHandler.class.getName()).log(Level.SEVERE, null, ex);
            //System.out.println("Session already destroyed..");
        }
    }

    private static JsonObject createMyJoinMessage(Player p) {
        JsonObject joinMessage = new JsonObject();
        joinMessage.addProperty("action", Config.JOIN_CHAT);
        joinMessage.addProperty("id", p.getId());
        joinMessage.addProperty("name", p.getName());
        joinMessage.addProperty("url", p.getUrl());
        joinMessage.addProperty("img", p.getAvatar());
        joinMessage.addProperty("message", "has joined");
        return joinMessage;
    }

    private static JsonObject createOtherJoinMessage(Player p) {
        JsonObject joinMessage = new JsonObject();
        joinMessage.addProperty("action", Config.JOIN_CHAT);
        joinMessage.addProperty("id", p.getId());
        joinMessage.addProperty("name", p.getName());
        joinMessage.addProperty("url", p.getUrl());
        joinMessage.addProperty("img", p.getAvatar());
        return joinMessage;
    }

    private static JsonObject createUpdateMessage(Player p) {
        JsonObject updateMessage = new JsonObject();
        updateMessage.addProperty("action", Config.UPDATE_LIST);
        updateMessage.addProperty("id", p.getId());
        updateMessage.addProperty("name", p.getName());
        updateMessage.addProperty("url", p.getUrl());
        updateMessage.addProperty("img", p.getAvatar());
        updateMessage.addProperty("message", "has joined");
        return updateMessage;
    }
}
