/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Config;

import Chat.UserManager;

/**
 *
 * @author S410U
 */
public class MatchConfig {

    private class JsonStructure {

        String matchid = "scrim";
        Integer num_maps;
        Integer players_per_team = 5;
        Integer min_players_to_ready = UserManager.getTeamCT().length + UserManager.getTeamT().length;
        Integer min_spectators_to_ready = 0;
        Boolean skip_veto = false;
        String side_type = "standard";
        String[] maplist
                = {
                    "de_cache",
                    "de_inferno",
                    "de_mirage",
                    "de_nuke",
                    "de_overpass",
                    "de_train",
                    "de_dust2"
                };
        TeamConfig team1;
        TeamConfig team2;
        Cvars cvars;

        public JsonStructure() {
        }

        public JsonStructure(Integer num_maps, TeamConfig team1, TeamConfig team2, Cvars cvars) {
            this.num_maps = num_maps;
            this.team1 = team1;
            this.team2 = team2;
            this.cvars = cvars;
        }

    }

    public JsonStructure SetConfig(int maps, TeamConfig team1, TeamConfig team2) {
        Cvars cv = new Cvars();
        JsonStructure js = new JsonStructure();
        js.num_maps = maps;
        js.team1 = team1;
        js.team2 = team2;
        js.cvars = cv;
        return js;
    }
}
