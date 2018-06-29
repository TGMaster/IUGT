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
            var socketUrl = "ws://localhost:8080/chatServer";
            var userId = <%=p.getId()%>;
            var userName = "<%=p.getName()%>";
            var userImg = "<%=p.getAvatar()%>";
            var userUrl = "<%=p.getUrl()%>";
        </script>

        <style>
            a.button {
                -webkit-appearance: button;
                -moz-appearance: button;
                appearance: button;

                padding: 5px;
                text-decoration: none;
                color: initial;
            }
        </style>

    </head>
    <body onbeforeunload="return closeSocket()">
        <h2>Chat Room</h2>

        <input id="textMessage" type="text" />
        <input onclick="sendMessage()" value="Send Message" type="button" id="sendMsg" /> <br/><br/>

        <textarea id="textAreaMessage" rows="10" cols="50"></textarea>

        <!--        <div>
                    <h4>Online list:</h4>
                    <table>
                        <tr id="myRow"></tr>
                    </table>
                </div>-->

        <div>
            <button id="switchBtn" onclick="swapTeam()">Switch</button>
            <form action="match" method="POST">
                <div>Team 1 : <span id="TeamCT"></span></div>
                <div>Team 2 : <span id="TeamT"></span></div>
                <input type="text" name="numMaps">
                <input type="submit" value="Start" name="action">
            </form>
        </div>

        <a href="users" class="button">Back to homepage</a>

        <script src="js/websocket.js"></script>
        <script type="text/javascript">start();</script>
    </body>
</html>
