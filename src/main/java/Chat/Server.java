/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Chat;

/**
 *
 * @author TGMaster
 */
import POJO.Player;
import Config.Config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint(value = "/game")
public class Server {

    private String username;
    //static Set<Session> users = Collections.synchronizedSet(new HashSet<Session>());

    @OnOpen
    public void handleOpen(Session session) {

    }

    @OnMessage
    public void handleMessage(String message, Session session) {
        Gson gson = new GsonBuilder().create();
        JsonObject json = gson.fromJson(message, JsonElement.class).getAsJsonObject();
        if (Config.JOIN_CHAT.equals(json.get("action").getAsString())) {
            joinChat(json, session);
        }
        if (Config.LEAVE_CHAT.equals(json.get("action").getAsString())) {
            leaveChat(json, session);
        }
        if (Config.CHAT_MSG.equals(json.get("action").getAsString())) {
            chatMessage(json, session);
        }
        if (Config.SWAP_TEAM.equals(json.get("action").getAsString())) {
            swapTeam(json, session);
        }
        if (Config.START_GAME.equals(json.get("action").getAsString())) {
            startGame(json, session);
        }
        /*
        for (Session s : users) {
            s.getBasicRemote().sendText(username + ": " + message);
        }
         */
    }

    @OnClose
    public void handleClose(Session session) {
        //users.remove(session);
    }

    @OnError
    public void handleError(Throwable t) {
        //t.printStackTrace();
    }

    private void joinChat(JsonObject json, Session session) {
        Player p = new Player();
        p.setId(json.get("id").getAsLong());
        p.setName(json.get("name").getAsString());
        p.setUrl(json.get("url").getAsString());
        p.setAvatar(json.get("img").getAsString());
        UserManager.joinChat(p, session);
    }

    private void leaveChat(JsonObject json, Session session) {
        Long id = json.get("id").getAsLong();
        UserManager.leaveChat(id, session);
    }

    private void chatMessage(JsonObject json, Session session) {
        Long id = json.get("id").getAsLong();
        String msg = json.get("message").getAsString();
        UserManager.sendMessage(id, msg);
    }

    private void swapTeam(JsonObject json, Session session) {
        Long id = json.get("id").getAsLong();
        UserManager.swapTeam(id);
    }

    private void startGame(JsonObject json, Session session) {
        Long id = json.get("id").getAsLong();
        UserManager.startGame(id);
    }
}
