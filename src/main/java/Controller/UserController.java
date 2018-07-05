/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import Config.Config;
import POJO.Player;
import Util.Json;
import Util.OpenID;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import com.google.gson.JsonObject;

/**
 *
 * @author TGMaster
 */
public class UserController extends HttpServlet {

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

        OpenID op = new OpenID();
        String action = request.getParameter("action");
        if (action == null) {
            rd = sc.getRequestDispatcher("/info.jsp");
            rd.forward(request, response);
        } else if (action.equals("openid")) {
            // Create object
            Player player = new Player();
            String identity = op.authentication(request);
            identity = identity.replace("https://steamcommunity.com/openid/id/", "");

            // Get json
            String sURL = Config.LINK_API + Config.STEAM_API + "&steamids=" + identity;
            JsonObject json = Json.readJsonFromUrl(sURL);

            // Set attributes
            player.setId(Long.parseLong(identity));
            player.setName(json.get("personaname").getAsString());
            player.setUrl(json.get("profileurl").getAsString());
            player.setAvatar(json.get("avatarmedium").getAsString());

            session.setAttribute("player", player);
            response.sendRedirect("users");

        } else if (action.equals("login")) {
            String url = op.login(request);
            response.sendRedirect(url);
        } else if (action.equals("logout")) {
            request.getSession().removeAttribute("player");
            response.sendRedirect("users");
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
