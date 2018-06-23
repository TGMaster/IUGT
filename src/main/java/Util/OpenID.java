/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Util;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import org.expressme.openid.Association;
import org.expressme.openid.Endpoint;
import org.expressme.openid.OpenIdManager;

/**
 *
 * @author TGMaster
 */
public class OpenID extends HttpServlet {

    private static final String STEAM_OPENID = "https://steamcommunity.com/openid";
    private static final String STEAM_RETURN = "http://localhost:8080/IUGT/users";
    private static final String STEAM_REALM = "http://localhost:8080";
    private static final String ATTR_MAC = "openid_mac";
    private static final String ATTR_ALIAS = "openid_alias";
    private OpenIdManager manager;

    public OpenID() {
        manager = new OpenIdManager();
        manager.setReturnTo(STEAM_RETURN);
        manager.setRealm(STEAM_REALM);
    }

    public String login(HttpServletRequest request) {
        Endpoint endpoint = manager.lookupEndpoint(STEAM_OPENID);
        Association association = manager.lookupAssociation(endpoint);
        request.getSession().setAttribute(ATTR_MAC, association.getRawMacKey());
        request.getSession().setAttribute(ATTR_ALIAS, endpoint.getAlias());
        String url = manager.getAuthenticationUrl(endpoint, association);
        return url;
    }

    public String authentication(HttpServletRequest request) {
        //Useless Authentication
        String identity = request.getParameter("openid.identity");
        return identity;
    }
}
