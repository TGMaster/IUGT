<%-- 
    Document   : chat
    Created on : Jun 24, 2018, 6:08:41 PM
    Author     : TGMaster
--%>

<%@page import="Config.Config"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Chat Page</title>
    </head>
    <body>
        <h2>Demo WebSocket Chat Room</h2>
        <input id="textMessage" type="text" />
        <input onclick="sendMessage()" value="Send Message" type="button" /> <br/><br/>

        <textarea id="textAreaMessage" rows="10" cols="50"></textarea>

        <script type="text/javascript">
            var websocket = new WebSocket("ws://<%=Config.Host%>/IUGT/chatServer");
            websocket.onopen = function (message) {
                processOpen(message);
            };
            websocket.onmessage = function (message) {
                processMessage(message);
            };
            websocket.onclose = function (message) {
                processClose(message);
            };
            websocket.onerror = function (message) {
                processError(message);
            };

            function processOpen(message) {
                textAreaMessage.value += "Server connected... \n";
            }
            function processMessage(message) {
                console.log(message);
                textAreaMessage.value += message.data + " \n";
            }
            function processClose(message) {
                textAreaMessage.value += "Server Disconnect... \n";
            }
            function processError(message) {
                textAreaMessage.value += "Error... " + message + " \n";
            }

            function sendMessage() {
                if (typeof websocket != 'undefined' && websocket.readyState == WebSocket.OPEN) {
                    websocket.send(textMessage.value);
                    textMessage.value = "";
                }
            }

        </script>
    </body>
</html>
