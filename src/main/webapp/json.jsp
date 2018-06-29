<%-- 
    Document   : json
    Created on : Jun 29, 2018, 1:26:15 PM
    Author     : S410U
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <h1>Hello World!</h1>
        <%String json = (String)request.getAttribute("json"); %>
        <%=json %>
    </body>
</html>
