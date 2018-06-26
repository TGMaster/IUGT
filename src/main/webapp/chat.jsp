<%-- 
    Document   : chat
    Created on : Jun 24, 2018, 6:08:41 PM
    Author     : TGMaster
--%>

<%@page import="Config.Config, POJO.Player"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Chat Page</title>

        <%Player p = (Player) session.getAttribute("player");%>
        <script type="text/javascript">
            var socketUrl = "ws://localhost:8080/IUGT/chatServer";
            var userId = <%=p.getId()%>;
            var userName = "<%=p.getName()%>";
            var userImg = "<%=p.getAvatar()%>";
            var userUrl = "<%=p.getUrl()%>";
        </script>

    </head>
    <body onbeforeunload="return closeSocket()">
        <h2>Chat Room</h2>

        <input id="textMessage" type="text" />
        <input onclick="sendMessage()" value="Send Message" type="button" id="sendMsg" /> <br/><br/>

        <textarea id="textAreaMessage" rows="10" cols="50"></textarea>

        <div>
            <h4>Online List:</h4>
            <div id="onlineList"></div>
        </div>
        
        <script src="js/websocket.js"></script>
        <script type="text/javascript">
            start();
        </script>
    </body>
</html>
