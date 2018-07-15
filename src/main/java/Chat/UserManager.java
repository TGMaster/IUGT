/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Chat;

import Config.Config;
import Controller.MatchController;
import POJO.Player;

import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.websocket.CloseReason;
import javax.websocket.Session;

/**
 * @author TGMaster
 */
public class UserManager {

    private final static Set<Player> players = new HashSet<>();
    private final static HashMap<String, Set<Player>> onlinePlayers = new HashMap<>();
    private final static HashMap<String, Session> pSession = new HashMap<>();
    private final static Set<Player> teamCT = new HashSet<>();
    private final static Set<Player> teamT = new HashSet<>();
    private final static HashMap<String, Boolean> isOwner = new HashMap<>();

    protected static void joinChat(Player p, Session session) {
        boolean isJoined = false;
        // Check owner
        int count = Server.getNumPlayers();
        if (count == 1) {
            isOwner.put(p.getId(), true);
            players.add(p);
        } else {
            for (Iterator<Player> iterator = players.iterator(); iterator.hasNext(); ) {
                Player player = iterator.next();
                if (player.getId().equals(p.getId())) {
                    isJoined = true;
                    break;
                }
            }

            if (isJoined) {
                if (session.isOpen()) {
                    try {
                        CloseReason rs = new CloseReason(CloseReason.CloseCodes.VIOLATED_POLICY, "You have joined the room");
                        session.close(rs);
                        Server.setNumPlayers(count - 1);
                        return;
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            } else {
                isOwner.put(p.getId(), false);
                players.add(p);
            }
        }

        pSession.put(p.getId(), session);

        prepareOnlineList(p);
        prepareTeamList(p);

        // Automatically join team
        if (teamCT.size() < 5) {
            chooseTeam(p, "team1");
        } else {
            chooseTeam(p, "team2");
        }

        //System.out.println("Count = " + count);
    }

    protected static void leaveChat(String id) {

        Player p = getUserById(id);
        if (p != null) {
            if (teamCT.contains(p)) {
                teamCT.remove(p);
            } else {
                teamT.remove(p);
            }

            players.remove(p);
            pSession.remove(p.getId());

            // Check if it is owner
            JsonObject removeMsg = new JsonObject();
            if (isOwner.get(id)) {
                removeMsg.addProperty("action", Config.REMOVE_MATCH);
                removeMsg.addProperty("id", id);
                removeMsg.addProperty("message", "The owner has left, the match will be canceled.");
            } else {
                removeMsg.addProperty("action", Config.LEAVE_CHAT);
                removeMsg.addProperty("id", id);
                removeMsg.addProperty("name", p.getName());
                removeMsg.addProperty("message", "has left");
            }

            isOwner.remove(p.getId());

            //notify server if someone left
            for (Player player : onlinePlayers.get(id)) {
                if (pSession.containsKey(player.getId())) {
                    sendToSession(pSession.get(player.getId()), removeMsg);
                }
            }
        }
    }

    protected static void sendMessage(String sender, String message) {
        JsonObject chatMessage = createMessage(sender, message);

        //sendToSession(pSession.get(sender), chatMessage);
        //broadcast messages to others...
        for (Player p : onlinePlayers.get(sender)) {
            if (pSession.containsKey(p.getId())) {
                sendToSession(pSession.get(p.getId()), chatMessage);
            }
        }
    }

    protected static void chooseTeam(Player player, String team) {
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

    protected static void swapTeam(String id) {
        Player player = getUserById(id);
        if (player != null) {
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
    }

    protected static void startGame(String id) {
//        if (teamCT.size() == 5 && teamT.size() == 5) {
        JsonObject startMsg = new JsonObject();
        startMsg.addProperty("action", Config.START_GAME);
        startMsg.addProperty("url", MatchController.getStart_msg());
        startMsg.addProperty("ip", MatchController.getStart_ip());
        for (Player player : onlinePlayers.get(id)) {
            if (pSession.containsKey(player.getId())) {
                sendToSession(pSession.get(player.getId()), startMsg);
            }
        }
//        }
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
                addMessage = createJoinMessage(p, true); // create message of new player for me
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

    private static Player getUserById(String id) {
        for (Player p : players) {
            if (p.getId().equals(id)) {
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
    private static JsonObject createMessage(String sender, String message) {
        Player player = getUserById(sender);
        if (player != null) {
            JsonObject addMessage = new JsonObject();
            addMessage.addProperty("action", Config.CHAT_MSG);
            addMessage.addProperty("id", sender);
            addMessage.addProperty("name", player.getName());
            addMessage.addProperty("message", message);
            return addMessage;
        } else {
            return null;
        }
    }

    private static JsonObject createJoinMessage(Player p, boolean same) {
        JsonObject joinMessage = new JsonObject();
        joinMessage.addProperty("action", Config.JOIN_CHAT);
        joinMessage.addProperty("id", p.getId());
        joinMessage.addProperty("name", p.getName());
        joinMessage.addProperty("url", p.getUrl());
        joinMessage.addProperty("img", p.getAvatar());
        if (same) {
            joinMessage.addProperty("owner", isOwner.get(p.getId()));
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
    
    // Get Team as String Array and Team name
    public static String[] getTeamCT() {
        String[] gamers = new String[5];
        int i = 0;
        for (Player p : teamCT) {
            gamers[i] = p.getId();
            i++;
        }
        return gamers;
    }

    public static String[] getTeamT() {
        String[] gamers = new String[5];
        int i = 0;
        for (Player p : teamT) {
            gamers[i] = p.getId();
            i++;
        }
        return gamers;
    }

    public static String getTeamNameCT() {
        String name = "";
        if (!teamCT.isEmpty()) {
            name = teamCT.iterator().next().getName();
        }
        return name;
    }

    public static String getTeamNameT() {
        String name = "";
        if (!teamT.isEmpty()) {
            name = teamT.iterator().next().getName();
        }
        return name;
    }
}
