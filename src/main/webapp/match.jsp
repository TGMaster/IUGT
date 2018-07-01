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
            var socketUrl = "ws://localhost:8080/game";
            var userId = "<%=p.getId()%>";
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
            <form id="teamList">
                <div>Team 1 : <span id="TeamCT"></span></div>
                <div>Team 2 : <span id="TeamT"></span></div>
                <div id="onlyOwner">
                    Match type:
                    <select id="numMaps">
                        <option value="1" selected>Bo1</option>
                        <option value="2">Bo2</option>
                        <option value="3">Bo3</option>
                        <option value="5">Bo5</option>
                    </select>
                    <button type="submit">Start</button>
                </div>
            </form>
        </div>

        <div>
            <a href="<%=Config.CONNECT_URL%>"><span id="server"></span></a>
            <div id="server_cmd"></div>
        </div>

        <div>
            <a href="users" class="button">Back to homepage</a>
        </div>
        <script src="js/jquery/jquery-3.3.1.min.js"></script>
        <script src="js/match.js"></script>
        <script type="text/javascript">start();</script>
    </body>
</html>
