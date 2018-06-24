<%-- 
    Document   : info
    Created on : Jun 24, 2018, 1:19:19 AM
    Author     : TGMaster
--%>

<%@page import="POJO.Player"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <h1>Hello World!</h1>

        <%
            boolean isNull = false;
            Player player = (Player) session.getAttribute("player");
            if (player == null) {
                isNull = true;
            }
            if (!isNull) {
        %>
        <p>ID: <%=player.getId()%></p>
        <p>Name: <%=player.getName()%></p>
        <p>URL: <a href="<%=player.getUrl()%>"><%=player.getUrl()%></a></p>
        <p><img src="<%=player.getAvatar()%>"></p>
        <a href="chat">Chat Room</a>
        <a href="users?action=logout">Logout</a>
            <% } else {%>
        <a href="users?action=login">Login</a>
        <% }%>
    </body>
</html>
