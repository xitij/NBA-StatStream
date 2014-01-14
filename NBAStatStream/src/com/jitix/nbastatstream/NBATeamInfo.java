package com.jitix.nbastatstream;

import java.util.Hashtable;
import java.util.Map;
import android.app.Application;
import android.util.Log;

public class NBATeamInfo extends Application {

	private static final String TAG = "NBAStatStream";
	
	//
	// Map that holds all the important Team Specific information for all NBA Teams
	// It contains the Team class with holds: City, name, logo resource, color, etc..
	//
	private static Map<String, Team> NBATeamInfo = new Hashtable<String, Team>();

	// Helper function to get the NBATeamInfo object
	public Map<String, Team> getNBATeamInfo() {
		return NBATeamInfo;
	}
	
	// Helper function to populate the NBATeamInfo object
	public void setNBATeamInfo() {
		initializeNBATeamInfo();
	}
	
	// Helper function to return the size of the Map. This should always be 32 (30 + NJNets, NO Hornets)
	public int getNBATeamInfoSize() {
		return NBATeamInfo.size();
	}
	
	// Helper function that returns the team color
	public int getTeamColor(String teamName, boolean primary) {
		int teamColor;
		
		Team myTeam = NBATeamInfo.get(teamName);
		if(myTeam != null) {
			if(primary) {
				teamColor = myTeam.getColorMain();
			} else {
				teamColor = myTeam.getColorSecond();
			}
		} else {
			Log.d(TAG, "NBATeamInfo : getTeamColor : couldn't find team = " + teamName);
			teamColor = R.color.BLACK;
		}
		return teamColor;
	}
	
	// Helper function that returns the team image resource
	public int getTeamLogo(String teamName) {
		int teamLogo;
		
		Team myTeam = NBATeamInfo.get(teamName);
		if(myTeam != null) {
			teamLogo = myTeam.getImageResource();
		} else {
			Log.d(TAG, "NBATeamInfo : getTeamLogo : couldn't find team = " + teamName);
			teamLogo = R.drawable.was_logo;
		}
		return teamLogo;
	}
	
	// Function to initialize the Map
	private void initializeNBATeamInfo() {
		// 30 NBA teams
		Team team = new Team();
		team.setAbbrev("ATL");
		team.setImageResource(R.drawable.atl_logo);
		team.setColorMain(R.color.HAWKS_RED);
		team.setColorSecond(R.color.HAWKS_BLUE);
		NBATeamInfo.put("Atlanta Hawks", team);
		
		team = new Team();
		team.setAbbrev("BOS");
		team.setImageResource(R.drawable.bos_logo);
		team.setColorMain(R.color.CELTICS_GREEN);
		team.setColorSecond(R.color.CELTICS_WHITE);
		NBATeamInfo.put("Boston Celtics", team);
		
		team = new Team();
		team.setAbbrev("BRK");
		team.setImageResource(R.drawable.brk_logo);
		team.setColorMain(R.color.NETS_BLACK);
		team.setColorSecond(R.color.NETS_WHITE);
		NBATeamInfo.put("Brooklyn Nets", team);
		
		team = new Team();
		team.setAbbrev("CHA");
		team.setImageResource(R.drawable.cha_logo);
		team.setColorMain(R.color.BOBCATS_NAVY);
		team.setColorSecond(R.color.BOBCATS_ORANGE);
		NBATeamInfo.put("Charlotte Bobcats", team);
		
		team = new Team();
		team.setAbbrev("CHI");
		team.setImageResource(R.drawable.chi_logo);
		team.setColorMain(R.color.BULLS_RED);
		team.setColorSecond(R.color.BULLS_BLACK);
		NBATeamInfo.put("Chicago Bulls", team);
		
		team = new Team();
		team.setAbbrev("CLE");
		team.setImageResource(R.drawable.cle_logo);
		team.setColorMain(R.color.CAVS_RED);
		team.setColorSecond(R.color.CAVS_YELLOW);
		NBATeamInfo.put("Cleveland Cavaliers", team);
		
		team = new Team();
		team.setAbbrev("DAL");
		team.setImageResource(R.drawable.dal_logo);
		team.setColorMain(R.color.MAVS_LIGHT_BLUE);
		team.setColorSecond(R.color.MAVS_DARK_BLUE);
		NBATeamInfo.put("Dallas Mavericks", team);
		
		team = new Team();
		team.setAbbrev("DEN");
		team.setImageResource(R.drawable.den_logo);
		team.setColorMain(R.color.NUGGETS_LIGHT_BLUE);
		team.setColorSecond(R.color.NUGGETS_GOLD);
		NBATeamInfo.put("Denver Nuggets", team);
		
		team = new Team();
		team.setAbbrev("DET");
		team.setImageResource(R.drawable.det_logo);
		team.setColorMain(R.color.PISTONS_BLUE);
		team.setColorSecond(R.color.PISTONS_RED);
		NBATeamInfo.put("Detroit Pistons", team);
		
		team = new Team();
		team.setAbbrev("GS");
		team.setImageResource(R.drawable.gs_logo);
		team.setColorMain(R.color.WARRIORS_YELLOW);
		team.setColorSecond(R.color.WARRIORS_BLUE);
		NBATeamInfo.put("Golden State Warriors", team);
		
		team = new Team();
		team.setAbbrev("HOU");
		team.setImageResource(R.drawable.hou_logo);
		team.setColorMain(R.color.ROCKETS_RED);
		team.setColorSecond(R.color.ROCKETS_SILVER);
		NBATeamInfo.put("Houston Rockets", team);
		
		team = new Team();
		team.setAbbrev("IND");
		team.setImageResource(R.drawable.ind_logo);
		team.setColorMain(R.color.PACERS_YELLOW);
		team.setColorSecond(R.color.PACERS_BLUE);
		NBATeamInfo.put("Indiana Pacers", team);
		
		team = new Team();
		team.setAbbrev("LAC");
		team.setImageResource(R.drawable.lac_logo);
		team.setColorMain(R.color.CLIPPERS_RED);
		team.setColorSecond(R.color.CLIPPERS_BLUE);
		NBATeamInfo.put("Los Angeles Clippers", team);
		
		team = new Team();
		team.setAbbrev("LAL");
		team.setImageResource(R.drawable.lal_logo);
		team.setColorMain(R.color.LAKERS_PURPLE);
		team.setColorSecond(R.color.LAKERS_YELLOW);
		NBATeamInfo.put("Los Angeles Lakers", team);
		
		team = new Team();
		team.setAbbrev("MEM");
		team.setImageResource(R.drawable.mem_logo);
		team.setColorMain(R.color.GRIZZLIES_DARK_BLUE);
		team.setColorSecond(R.color.GRIZZLIES_SKY_BLUE);
		NBATeamInfo.put("Memphis Grizzlies", team);
		
		team = new Team();
		team.setAbbrev("MIA");
		team.setImageResource(R.drawable.mia_logo);
		team.setColorMain(R.color.HEAT_RED);
		team.setColorSecond(R.color.HEAT_BLACK);
		NBATeamInfo.put("Miami Heat", team);
		
		team = new Team();
		team.setAbbrev("MIL");
		team.setImageResource(R.drawable.mil_logo);
		team.setColorMain(R.color.BUCKS_GREEN);
		team.setColorSecond(R.color.BUCKS_RED);
		NBATeamInfo.put("Milwaukee Bucks", team);
		
		team = new Team();
		team.setAbbrev("MIN");
		team.setImageResource(R.drawable.min_logo);
		team.setColorMain(R.color.TWOLVES_BLUE);
		team.setColorSecond(R.color.TWOLVES_GREEN);
		NBATeamInfo.put("Minnesota Timberwolves", team);
		
		team = new Team();
		team.setAbbrev("NJ");
		team.setImageResource(R.drawable.nj_logo);
		team.setColorMain(R.color.NETS_NAVY_BLUE);
		team.setColorSecond(R.color.NETS_RED);
		NBATeamInfo.put("New Jersey Nets", team);
		
		team = new Team();
		team.setAbbrev("NOR");
		team.setImageResource(R.drawable.nor_hor_logo);
		team.setColorMain(R.color.HORNETS_TEAL);
		team.setColorSecond(R.color.HORNETS_PURPLE);
		NBATeamInfo.put("New Orleans Hornets", team);
		
		team = new Team();
		team.setAbbrev("NOR");
		team.setImageResource(R.drawable.nor_logo);
		team.setColorMain(R.color.PELICANS_DARK_BLUE);
		team.setColorSecond(R.color.PELICANS_GOLD);
		NBATeamInfo.put("New Orleans Pelicans", team);
		
		team = new Team();
		team.setAbbrev("NY");
		team.setImageResource(R.drawable.ny_logo);
		team.setColorMain(R.color.KNICKS_ORANGE);
		team.setColorSecond(R.color.KNICKS_BLUE);
		NBATeamInfo.put("New York Knicks", team);
		
		team = new Team();
		team.setAbbrev("OKC");
		team.setImageResource(R.drawable.okc_logo);
		team.setColorMain(R.color.THUNDER_BLUE);
		team.setColorSecond(R.color.THUNDER_ORANGE);
		NBATeamInfo.put("Oklahoma City Thunder", team);
		
		team = new Team();
		team.setAbbrev("ORL");
		team.setImageResource(R.drawable.orl_logo);
		team.setColorMain(R.color.MAGIC_BLUE);
		team.setColorSecond(R.color.MAGIC_GRAY);
		NBATeamInfo.put("Orlando Magic", team);
		
		team = new Team();
		team.setAbbrev("PHI");
		team.setImageResource(R.drawable.phi_logo);
		team.setColorMain(R.color.SIXERS_BLUE);
		team.setColorSecond(R.color.SIXERS_RED);
		NBATeamInfo.put("Philadelphia 76ers", team);
		
		team = new Team();
		team.setAbbrev("PHO");
		team.setImageResource(R.drawable.pho_logo);
		team.setColorMain(R.color.SUNS_ORANGE);
		team.setColorSecond(R.color.SUNS_BLACK);
		NBATeamInfo.put("Phoenix Suns", team);
		
		team = new Team();
		team.setAbbrev("POR");
		team.setImageResource(R.drawable.por_logo);
		team.setColorMain(R.color.BLAZERS_RED);
		team.setColorSecond(R.color.BLAZERS_BLACK);
		NBATeamInfo.put("Portland Trail Blazers", team);
		
		team = new Team();
		team.setAbbrev("SAC");
		team.setImageResource(R.drawable.sac_logo);
		team.setColorMain(R.color.KINGS_PURPLE);
		team.setColorSecond(R.color.KINGS_GRAY);
		NBATeamInfo.put("Sacramento Kings", team);
		
		team = new Team();
		team.setAbbrev("SA");
		team.setImageResource(R.drawable.sa_logo);
		team.setColorMain(R.color.SPURS_SILVER);
		team.setColorSecond(R.color.SPURS_BLACK);
		NBATeamInfo.put("San Antonio Spurs", team);
		
		team = new Team();
		team.setAbbrev("TOR");
		team.setImageResource(R.drawable.tor_logo);
		team.setColorMain(R.color.RAPTORS_RED);
		team.setColorSecond(R.color.RAPTORS_BLACK);
		NBATeamInfo.put("Toronto Raptors", team);
		
		team = new Team();
		team.setAbbrev("UTA");
		team.setImageResource(R.drawable.uta_logo);
		team.setColorMain(R.color.JAZZ_BLUE);
		team.setColorSecond(R.color.JAZZ_YELLOW);
		NBATeamInfo.put("Utah Jazz", team);
		
		team = new Team();
		team.setAbbrev("WAS");
		team.setImageResource(R.drawable.was_logo);
		team.setColorMain(R.color.WIZARDS_RED);
		team.setColorSecond(R.color.WIZARDS_BLUE);
		NBATeamInfo.put("Washington Wizards", team);
	}
}