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

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.servlet.http.HttpSession;

import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint(value = "/chatServer", configurator = HttpSessionConfigurator.class)
public class Server {

    private String username;
    private HttpSession httpSession;
    static Set<Session> users = Collections.synchronizedSet(new HashSet<Session>());

    @OnOpen
    public void handleOpen(Session session, EndpointConfig config) throws IOException {
        // HttpSession stuff
        this.httpSession = (HttpSession) config.getUserProperties().get(HttpSession.class.getName());
        Player p = (Player) httpSession.getAttribute("player");
        username = p.getName();

        // Session settings
        session.getBasicRemote().sendText("Hi " + username);
        session.getUserProperties().put("username", username);
        users.add(session);
    }

    @OnMessage
    public void handleMessage(String message) throws IOException {
        for (Session session : users) {
            session.getBasicRemote().sendText(username + ": " + message);
        }
    }

    @OnClose
    public void handleClose(Session session) {
        users.remove(session);
    }

    @OnError
    public void handleError(Throwable t) {
        t.printStackTrace();
    }

}
