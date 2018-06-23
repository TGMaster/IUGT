/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import javax.json.JsonException;

/**
 *
 * @author TGMaster
 */
public class Json {

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    public static JsonObject readJsonFromUrl(String url) throws IOException, JsonException {
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            Gson gson = new GsonBuilder().create();
            
            // How to compact these lines?
            JsonObject json = gson.fromJson(jsonText, JsonElement.class).getAsJsonObject();
            JsonObject response = json.get("response").getAsJsonObject(); 
            JsonObject players = response.get("players").getAsJsonObject(); 
            JsonArray player = players.get("player").getAsJsonArray(); 
            JsonObject obj = player.get(0).getAsJsonObject();
            
            return obj;
        } finally {
            is.close();
        }
    }
}
