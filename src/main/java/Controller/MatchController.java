/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import Config.*;
import POJO.Player;
import Rcon.Rcon;
import Util.*;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.*;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author TGMaster
 */
public class MatchController extends HttpServlet {

    public static String start_msg;
    public static String start_ip;

    public static String getStart_msg() {
        return start_msg;
    }

    public static void setStart_msg(String start_msg) {
        MatchController.start_msg = start_msg;
    }

    public static String getStart_ip() {
        return start_ip;
    }

    public static void setStart_ip(String start_ip) {
        MatchController.start_ip = start_ip;
    }

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Call Servlet Context
        ServletContext sc = getServletContext();

        // Declare requestDispatcher
        RequestDispatcher rd;

        // Call session
        HttpSession session = request.getSession();

        String action = request.getParameter("action");
        if (action == null) {
            response.sendRedirect("users");
        } else if (action.equals("start")) {
            Player p = (Player) session.getAttribute("player");
            if (p == null) {
                response.sendRedirect("users");
            } else {
                String[] team1 = request.getParameterValues("Team1[]");
                String[] team2 = request.getParameterValues("Team2[]");
                String name_team1 = request.getParameter("Team1Name");
                String name_team2 = request.getParameter("Team2Name");
                int maps = Integer.parseInt(request.getParameter("numMaps"));

                if (!isValidMatchConfig(team1, team2, name_team1, name_team2)) {
                    response.setContentType("application/json;charset=UTF-8");
                    JsonObject response_msg = new JsonObject();
                    response_msg.addProperty("status", 199);
                    response_msg.addProperty("msg", "Not enough player to start");
                    Gson gs = new Gson();
                    response.getWriter().write(gs.toJson(response_msg));
                    return;
                }

                TeamConfig t1 = new TeamConfig();
                t1.setPlayers(team1);
                t1.setName("team_" + name_team1);

                TeamConfig t2 = new TeamConfig();
                t2.setPlayers(team2);
                t2.setName("team_" + name_team2);

                MatchConfig m = new MatchConfig();

                Gson gson = new Gson();
                String jout = gson.toJson(m.SetConfig(maps, t1, t2));

                // Save temp file for server to load
                File tempdir = (File) sc.getAttribute(ServletContext.TEMPDIR);
                String name = Util.generateRandomStr(5);
                File matchFile = new File(tempdir, name + ".json");
                BufferedWriter out = null;
                try {
                    out = new BufferedWriter(new FileWriter(matchFile));
                    out.write(jout);
                } catch (IOException e) {
                } finally {
                    if (out != null) {
                        out.close();
                    }
                }

                // Send rcon to server
                if (!Config.DEBUG) {
                    try {
                        Rcon rcon = new Rcon(Config.SERVER_IP, 27015, "iugt1234".getBytes());
                        String cmd = "get5_loadmatch_url \"http://" + Config.HOST_URL + "/match?action=file&name=" + name + "\"";
                        System.out.println(cmd);
                        rcon.command(cmd);
                    } catch (AuthenticationException ex) {
                    }
                }
                MatchController.setStart_msg(Config.CONNECT_URL);
                MatchController.setStart_ip("connect " + Config.SERVER_IP);

                response.setContentType("application/json;charset=UTF-8");
                JsonObject response_msg = new JsonObject();
                response_msg.addProperty("status", 200);
                response_msg.addProperty("msg", "OK - Path: " + matchFile.getAbsolutePath());
                Gson gs = new Gson();
                response.getWriter().write(gs.toJson(response_msg));
            }

        } else if (action.equals("file")) {
            String name = request.getParameter("name");
            File tempdir = (File) sc.getAttribute(ServletContext.TEMPDIR);
            File match = new File(tempdir, name + ".json");
            BufferedReader in = null;
            String jout = "";
            try {
                in = new BufferedReader(new FileReader(match));
                jout = in.readLine();
            } catch (IOException e) {
            } finally {
                if (in != null) {
                    in.close();
                }
            }

            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().print(jout);
        }

    }

    private boolean isValidMatchConfig(String[] team1, String[] team2, String name1, String name2) {
        if (team1 == null || team2 == null || name1 == null || name2 == null
                || name1.equals("") || name2.equals(""))
            return false;
        return true;
    }

// <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
