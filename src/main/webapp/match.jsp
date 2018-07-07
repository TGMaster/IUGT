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

        <link href="css/bootstrap-3.3.7/css/bootstrap.min.css" rel="stylesheet" type="text/css"/>
        <link href="css/match.css" rel="stylesheet" type="text/css"/>
        <link href="css/fontawesome-4.7.0/font-awesome.min.css" rel="stylesheet">

    </head>
    <body onbeforeunload="return closeSocket()">
        <div class="row" id="upper-container">
            <!--Team List-->
            <form id="teamList">
                <div class="teamview">
                    <div class="col-sm-1"></div>
                    <!--CT Team-->
                    <div class="col-sm-3 team-info" style="height: 476px">
                        <div class="team-tag"> Team 1 </div>
                        <div id="TeamCT"></div>
                    </div>
                    <!--End of CT Team-->

                    <!--Match Info-->
                    <div class="col-sm-4 match-info">
                        <div class="server-info">
                            <img src="images/vs.jpg" width="64" height="64" alt="VS"/>
                            <div id="onlyOwner"></div>
                            <button class="form-control btn-warning" id="switchBtn" onclick="swapTeam()" type="button">Switch</button>
                            <div class="server-connect">
                                <div id="server"></div>
                            </div>
                        </div>
                    </div>
                    <!--End of Match Info-->

                    <!--T Team-->
                    <div class="col-sm-3 team-info" style="height: 476px">
                        <div class="team-tag"> Team 2 </div>
                        <div id="TeamT"></div>
                    </div>
                    <!--End of T Team-->
                </div>
            </form>
        </div>

        <!--Chat Room-->
        <div class="row">
            <div class="col-sm-4"></div>
            <div class="form-group col-sm-4">
                <textarea class="form-control rounded-0" id="textAreaMessage" rows="15" readonly></textarea>
                <div class="inner-addon right-addon"> 
                    <input type="text" class="form-control" id="textMessage"/>
                </div>

            </div>
            <div class="col-sm-4"></div>
        </div>
        <!--End of Chat Room-->

        <div>
            <a href="users" class="button">Back to homepage</a>
        </div>

        <script src="js/jquery/jquery-3.3.1.min.js"></script>
        <script src="js/match.js"></script>
        <script type="text/javascript">start();</script>
        <script src="css/bootstrap-3.3.7/js/bootstrap.min.js" type="text/javascript"></script>
    </body>
</html>
