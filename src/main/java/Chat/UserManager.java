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
    private final static Set<Player> teamCT = new HashSet<>();
    private final static Set<Player> teamT = new HashSet<>();

    public static void joinChat(Player p, Session session) {
        if (!players.contains(p)) {
            players.add(p);
            count++;
        }
        pSession.put(p.getId(), session);
        prepareOnlineList(p);
        prepareTeamList(p);
        
        // Automatically join team
        if (teamCT.size() < 5) chooseTeam(p, "team1");
        else chooseTeam(p, "team2");
        
        //System.out.println("Count = " + count);
    }

    public static void leaveChat(long id, Session session) {
        count--;
        //System.out.println("Count = " + count);
        Player p = getUserById(id);
        if (p != null) {
            if (teamCT.contains(p)) {
                teamCT.remove(p);
            } else if (teamT.contains(p)) {
                teamT.remove(p);
            }

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

        //sendToSession(pSession.get(sender), chatMessage);
        //broadcast messages to others...
        for (Player p : onlinePlayers.get(sender)) {
            if (pSession.containsKey(p.getId())) {
                sendToSession(pSession.get(p.getId()), chatMessage);
            }
        }
    }

    public static void chooseTeam(Player player, String team) {
        if (team.equals(Config.TEAM_CT)) {
            teamCT.add(player);
        } else if (team.equals(Config.TEAM_T)) {
            teamT.add(player);
        }

        JsonObject teamMessage = createTeamMessage(player, team);
        for (Player p : onlinePlayers.get(player.getId())) {
            if (pSession.containsKey(p.getId())) {
                sendToSession(pSession.get(p.getId()), teamMessage);
            }
        }
    }

    public static void swapTeam(long id) {
        Player player = getUserById(id);
        String team = "";
        if (teamCT.contains(player)) {
            teamCT.remove(player);
            teamT.add(player);
            team = "team2";
        } else if (teamT.contains(player)) {
            teamT.remove(player);
            teamCT.add(player);
            team = "team1";
        }

        JsonObject swapMessage = createSwapMessage(player, team);
        for (Player p : onlinePlayers.get(id)) {
            if (pSession.containsKey(p.getId())) {
                sendToSession(pSession.get(p.getId()), swapMessage);
            }
        }
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
                addMessage = createJoinMessage(player, true); // create message of new player for me
            } else {
                addMessage = createJoinMessage(p, false); // create message of new player for old ones
            }
            sendToSession(pSession.get(player.getId()), addMessage);
        }
    }

    private static void prepareTeamList(Player player) {
        Set<Player> list = getOnlineList(); // Get current online list
        JsonObject teamMessage;
        for (Player i : list) {
            if (teamCT.contains(i)) {
                teamMessage = createTeamMessage(i, Config.TEAM_CT);
            } else if (teamT.contains(i)) {
                teamMessage = createTeamMessage(i, Config.TEAM_T);
            } else {
                teamMessage = createTeamMessage(i, Config.TEAM_SPEC);
            }
            sendToSession(pSession.get(player.getId()), teamMessage);
        }
    }

    public static Player getUserById(long id) {
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

    // Create Message
    private static JsonObject createMessage(long sender, String message) {
        Player player = getUserById(sender);
        JsonObject addMessage = new JsonObject();
        addMessage.addProperty("action", Config.CHAT_MSG);
        addMessage.addProperty("id", sender);
        addMessage.addProperty("name", player.getName());
        addMessage.addProperty("message", message);
        return addMessage;
    }

    private static JsonObject createJoinMessage(Player p, boolean same) {
        JsonObject joinMessage = new JsonObject();
        joinMessage.addProperty("action", Config.JOIN_CHAT);
        joinMessage.addProperty("id", p.getId());
        joinMessage.addProperty("name", p.getName());
        joinMessage.addProperty("url", p.getUrl());
        joinMessage.addProperty("img", p.getAvatar());
        if (same) {
            joinMessage.addProperty("message", "has joined");
        }
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

    private static JsonObject createTeamMessage(Player p, String team) {
        JsonObject teamMessage = new JsonObject();
        teamMessage.addProperty("action", Config.JOIN_TEAM);
        teamMessage.addProperty("id", p.getId());
        teamMessage.addProperty("name", p.getName());
        teamMessage.addProperty("url", p.getUrl());
        teamMessage.addProperty("img", p.getAvatar());
        teamMessage.addProperty("team", team);
        return teamMessage;
    }

    private static JsonObject createSwapMessage(Player p, String team) {
        JsonObject swapMessage = new JsonObject();
        swapMessage.addProperty("action", Config.SWAP_TEAM);
        swapMessage.addProperty("id", p.getId());
        swapMessage.addProperty("name", p.getName());
        swapMessage.addProperty("url", p.getUrl());
        swapMessage.addProperty("img", p.getAvatar());
        swapMessage.addProperty("team", team);
        return swapMessage;
    }
}
