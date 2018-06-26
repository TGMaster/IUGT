<%-- 
    Document   : game
    Created on : Jun 24, 2018, 6:36:15 PM
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
        <h2>Choose Your Team</h2>
        <form action="match" method="POST">
            <button type="submit" name="team" value="1">Team 1</button>
            <button type="submit" name="team" value="2">Team 2</button>
        </form>
        <%
            Player p = (Player) session.getAttribute("player");
            String team = (String) request.getAttribute("team");
            if (team != null) {
        %>
        <table>
            <tr>
                <th>Team 1</th>
                <th>Team 2</th>
            </tr>
            <%
                if (team.equals("Team1")) {
            %>
            <tr>
                <td><%=p.getName()%></td>
                <td></td>
            </tr>
            <%
            } else {
            %>
            <tr>
                <td></td>
                <td><%=p.getName()%></td>
            </tr>
            <% }%>
        </table>
        <% }%>

    </body>
</html>
