<%-- 
    Document   : chat
    Created on : Jun 24, 2018, 6:08:41 PM
    Author     : TGMaster
--%>

<%@page import="Config.Config, POJO.Player" %>
<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>IU Gaming Tour</title>
    <%Player p = (Player) session.getAttribute("player");%>

    <script type="text/javascript">
        var socketUrl = "ws://<%=Config.HOST_URL%>/game";
        var userId = "<%=p.getId()%>";
        var userName = "<%=p.getName()%>";
        var userImg = "<%=p.getAvatar()%>";
        var userUrl = "<%=p.getUrl()%>";
    </script>

    <link rel="stylesheet" href="assets/bootstrap/css/bootstrap.min.css">
    <link rel="stylesheet" href="assets/css/match.css">
    <link rel="stylesheet" href="assets/fonts/font-awesome.min.css">

</head>
<body onbeforeunload="return closeSocket()">

<div class="btn-group btn-control" role="group">
    <button class="btn btn-default" id="startBtn" onclick="document.getElementById('teamList').submit();" type="button">
        <i class="fa fa-play" style="font-size:17px;"></i> <strong>START</strong></strong>
    </button>

    <button class="btn btn-default" id="switchBtn" onclick="swapTeam()" type="button">
        <i class="fa fa-exchange" style="font-size:17px;"></i> <strong>SWITCH</strong></strong>
    </button>

    <a href="users" class="btn btn-default btn-leave" role="button"><i class="fa fa-times" style="font-size:17px;"></i>
        <strong>LEAVE</strong>
    </a>
</div>

<div class="row" id="upper-container">
    <!--Team List-->
    <form id="teamList">
        <div class="teamview">
            <!--CT Team-->
            <div class="col-md-3 col-lg-3"><img class="img-responsive" src="assets/images/GR.png"></div>
            <div class="col-sm-2 team-info">
                <div id="TeamCT"></div>
            </div>
            <!--End of CT Team-->

            <!--Match Info-->
            <div class="col-sm-2 match-info">
                <div class="server-info">
                    <img src="assets/images/versus.png" class="img-responsive" alt="VS"/>
                    <div id="onlyOwner"></div>
                    <div class="server-connect">
                        <div id="server"></div>
                    </div>
                </div>
            </div>
            <!--End of Match Info-->

            <!--T Team-->
            <div class="col-sm-2 team-info">
                <div id="TeamT"></div>
            </div>
            <div class="col-md-3 col-lg-3"><img class="img-responsive" src="assets/images/BL.png"></div>
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
            <input type="text" class="form-control" id="textMessage" placeholder="Message..."/>
        </div>

    </div>
    <div class="col-sm-4"></div>
</div>
<!--End of Chat Room-->

<script src="assets/js/jquery/jquery.min.js"></script>
<script src="assets/bootstrap/js/bootstrap.min.js"></script>
<script src="assets/js/match.js"></script>
<script type="text/javascript">start();</script>

</body>
</html>
