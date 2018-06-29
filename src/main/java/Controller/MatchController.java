/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import Config.MatchConfig;
import Config.TeamConfig;
import POJO.Player;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.PrintWriter;
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

        Player p = (Player) session.getAttribute("player");
        if (p == null) {
            response.sendRedirect("users");
        } else {

            String action = request.getParameter("action");
            if (action == null) {
                response.sendRedirect("users");
            } else if (action.equals("Start")) {
                request.setCharacterEncoding("UTF-8");
                String[] team1 = request.getParameterValues("Team1");
                String[] team2 = request.getParameterValues("Team2");
                String[] name_team1 = request.getParameterValues("Team1Name");
                String[] name_team2 = request.getParameterValues("Team2Name");
                int maps = Integer.parseInt(request.getParameter("numMaps"));

                TeamConfig t1 = new TeamConfig();
                t1.setPlayers(team1);
                String name = new String(name_team1[0].getBytes("Shift_JIS"));
                t1.setName("team_" + name);

                TeamConfig t2 = new TeamConfig();
                t2.setPlayers(team2);
                name = new String(name_team2[0].getBytes("Shift_JIS"));
                t2.setName("team_" + name);

                MatchConfig m = new MatchConfig();

                Gson gson = new Gson();
                String jout = gson.toJson(m.SetConfig(maps, t1, t2));
                
//                response.setContentType("application/json");
//                try (PrintWriter out = response.getWriter()) {
//                    /* TODO output your page here. You may use following sample code. */
//                    out.println(jout);
//                }
                request.setAttribute("json", jout);
                rd = sc.getRequestDispatcher("/json.jsp");
                rd.forward(request, response);
            }

        }
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
