package com.jitix.nbastatstream;

import java.util.Hashtable;
import java.util.Map;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;

public class NBAStatStream extends Activity implements OnClickListener {

	private static final String TAG = "NBAStatStream";
	
	class TeamInfo {
		String 	abbrev;
		int 	image_resource;
		int		color_main;
		int 	color_secondary;
	}
	
	// Map to hold the Team information
	// Key = Team full name (ex. Detroit Pistons)
	// Object = TeamInfo class
	public static final Map<String, TeamInfo> NBATeamInfo = new Hashtable<String, TeamInfo>(); 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_nbastat_stream);
		
		// Set up click listeners for the buttons
		View livegame = findViewById(R.id.live_game_button);
		livegame.setOnClickListener(this);
		View archivedgame = findViewById(R.id.archived_game_button);
		archivedgame.setOnClickListener(this);
		
		//Initialize the NBATeamInfo Map
		initializeTeamInfo();
	}

	private void initializeTeamInfo() {
		// 30 NBA teams
		TeamInfo team = new TeamInfo();
		team.abbrev = "ATL";
		team.image_resource = R.drawable.atl_logo;
		team.color_main = R.color.HAWKS_RED;
		team.color_secondary = R.color.HAWKS_BLUE;
		NBATeamInfo.put("Atlanta Hawks", team);
		
		team = new TeamInfo();
		team.abbrev = "BOS";
		team.image_resource = R.drawable.bos_logo;
		team.color_main = R.color.CELTICS_GREEN;
		team.color_secondary = R.color.CELTICS_WHITE;
		NBATeamInfo.put("Boston Celtics", team);
		
		team = new TeamInfo();
		team.abbrev = "BRK";
		team.image_resource = R.drawable.brk_logo;
		team.color_main = R.color.NETS_BLACK;
		team.color_secondary = R.color.NETS_WHITE;
		NBATeamInfo.put("Brooklyn Nets", team);
		
		team = new TeamInfo();
		team.abbrev = "CHA";
		team.image_resource = R.drawable.cha_logo;
		team.color_main = R.color.BOBCATS_NAVY;
		team.color_secondary = R.color.BOBCATS_ORANGE;
		NBATeamInfo.put("Charlotte Bobcats", team);
		
		team = new TeamInfo();
		team.abbrev = "CHI";
		team.image_resource = R.drawable.chi_logo;
		team.color_main = R.color.BULLS_RED;
		team.color_secondary = R.color.BULLS_BLACK;
		NBATeamInfo.put("Chicago Bulls", team);
		
		team = new TeamInfo();
		team.abbrev = "CLE";
		team.image_resource = R.drawable.cle_logo;
		team.color_main = R.color.CAVS_RED;
		team.color_secondary = R.color.CAVS_YELLOW;
		NBATeamInfo.put("Cleveland Cavaliers", team);
		
		team = new TeamInfo();
		team.abbrev = "DAL";
		team.image_resource = R.drawable.dal_logo;
		team.color_main = R.color.MAVS_LIGHT_BLUE;
		team.color_secondary = R.color.MAVS_DARK_BLUE;
		NBATeamInfo.put("Dallas Mavericks", team);
		
		team = new TeamInfo();
		team.abbrev = "DEN";
		team.image_resource = R.drawable.den_logo;
		team.color_main = R.color.NUGGETS_LIGHT_BLUE;
		team.color_secondary = R.color.NUGGETS_GOLD;
		NBATeamInfo.put("Denver Nuggets", team);
		
		team = new TeamInfo();
		team.abbrev = "DET";
		team.image_resource = R.drawable.det_logo;
		team.color_main = R.color.PISTONS_BLUE;
		team.color_secondary = R.color.PISTONS_RED;
		NBATeamInfo.put("Detroit Pistons", team);
		
		team = new TeamInfo();
		team.abbrev = "GS";
		team.image_resource = R.drawable.gs_logo;
		team.color_main = R.color.WARRIORS_YELLOW;
		team.color_secondary = R.color.WARRIORS_BLUE;
		NBATeamInfo.put("Golden State Warriors", team);
		
		team = new TeamInfo();
		team.abbrev = "HOU";
		team.image_resource = R.drawable.hou_logo;
		team.color_main = R.color.ROCKETS_RED;
		team.color_secondary = R.color.ROCKETS_SILVER;
		NBATeamInfo.put("Houston Rockets", team);
		
		team = new TeamInfo();
		team.abbrev = "IND";
		team.image_resource = R.drawable.ind_logo;
		team.color_main = R.color.PACERS_YELLOW;
		team.color_secondary = R.color.PACERS_BLUE;
		NBATeamInfo.put("Indiana Pacers", team);
		
		team = new TeamInfo();
		team.abbrev = "LAC";
		team.image_resource = R.drawable.lac_logo;
		team.color_main = R.color.CLIPPERS_RED;
		team.color_secondary = R.color.CLIPPERS_BLUE;
		NBATeamInfo.put("Los Angeles Clippers", team);
		
		team = new TeamInfo();
		team.abbrev = "LAL";
		team.image_resource = R.drawable.lal_logo;
		team.color_main = R.color.LAKERS_PURPLE;
		team.color_secondary = R.color.LAKERS_YELLOW;
		NBATeamInfo.put("Los Angeles Lakers", team);
		
		team = new TeamInfo();
		team.abbrev = "MEM";
		team.image_resource = R.drawable.mem_logo;
		team.color_main = R.color.GRIZZLIES_DARK_BLUE;
		team.color_secondary = R.color.GRIZZLIES_SKY_BLUE;
		NBATeamInfo.put("Memphis Grizzlies", team);
		
		team = new TeamInfo();
		team.abbrev = "MIA";
		team.image_resource = R.drawable.mia_logo;
		team.color_main = R.color.HEAT_RED;
		team.color_secondary = R.color.HEAT_BLACK;
		NBATeamInfo.put("Miami Heat", team);
		
		team = new TeamInfo();
		team.abbrev = "MIL";
		team.image_resource = R.drawable.mil_logo;
		team.color_main = R.color.BUCKS_GREEN;
		team.color_secondary = R.color.BUCKS_RED;
		NBATeamInfo.put("Milwaukee Bucks", team);
		
		team = new TeamInfo();
		team.abbrev = "MIN";
		team.image_resource = R.drawable.min_logo;
		team.color_main = R.color.TWOLVES_BLUE;
		team.color_secondary = R.color.TWOLVES_GREEN;
		NBATeamInfo.put("Minnesota Timberwolves", team);
		
		team = new TeamInfo();
		team.abbrev = "NOR";
		team.image_resource = R.drawable.nor_logo;
		team.color_main = R.color.PELICANS_DARK_BLUE;
		team.color_secondary = R.color.PELICANS_GOLD;
		NBATeamInfo.put("New Orleans Pelicans", team);
		
		team = new TeamInfo();
		team.abbrev = "NY";
		team.image_resource = R.drawable.ny_logo;
		team.color_main = R.color.KNICKS_ORANGE;
		team.color_secondary = R.color.KNICKS_BLUE;
		NBATeamInfo.put("New York Knicks", team);
		
		team = new TeamInfo();
		team.abbrev = "OKC";
		team.image_resource = R.drawable.okc_logo;
		team.color_main = R.color.THUNDER_BLUE;
		team.color_secondary = R.color.THUNDER_ORANGE;
		NBATeamInfo.put("Oklahoma City Thunder", team);
		
		team = new TeamInfo();
		team.abbrev = "ORL";
		team.image_resource = R.drawable.orl_logo;
		team.color_main = R.color.MAGIC_BLUE;
		team.color_secondary = R.color.MAGIC_GRAY;
		NBATeamInfo.put("Orlando Magic", team);
		
		team = new TeamInfo();
		team.abbrev = "PHI";
		team.image_resource = R.drawable.phi_logo;
		team.color_main = R.color.SIXERS_BLUE;
		team.color_secondary = R.color.SIXERS_RED;
		NBATeamInfo.put("Philadelphia 76ers", team);
		
		team = new TeamInfo();
		team.abbrev = "PHO";
		team.image_resource = R.drawable.pho_logo;
		team.color_main = R.color.SUNS_ORANGE;
		team.color_secondary = R.color.SUNS_BLACK;
		NBATeamInfo.put("Phoenix Suns", team);
		
		team = new TeamInfo();
		team.abbrev = "POR";
		team.image_resource = R.drawable.por_logo;
		team.color_main = R.color.BLAZERS_RED;
		team.color_secondary = R.color.BLAZERS_BLACK;
		NBATeamInfo.put("Portland Trail Blazers", team);
		
		team = new TeamInfo();
		team.abbrev = "SAC";
		team.image_resource = R.drawable.sac_logo;
		team.color_main = R.color.KINGS_PURPLE;
		team.color_secondary = R.color.KINGS_GRAY;
		NBATeamInfo.put("Sacramento Kings", team);
		
		team = new TeamInfo();
		team.abbrev = "SA";
		team.image_resource = R.drawable.sa_logo;
		team.color_main = R.color.SPURS_SILVER;
		team.color_secondary = R.color.SPURS_BLACK;
		NBATeamInfo.put("San Antonio Spurs", team);
		
		team = new TeamInfo();
		team.abbrev = "TOR";
		team.image_resource = R.drawable.tor_logo;
		team.color_main = R.color.RAPTORS_RED;
		team.color_secondary = R.color.RAPTORS_BLACK;
		NBATeamInfo.put("Toronto Raptors", team);
		
		team = new TeamInfo();
		team.abbrev = "UTA";
		team.image_resource = R.drawable.uta_logo;
		team.color_main = R.color.JAZZ_BLUE;
		team.color_secondary = R.color.JAZZ_YELLOW;
		NBATeamInfo.put("Utah Jazz", team);
		
		team = new TeamInfo();
		team.abbrev = "WAS";
		team.image_resource = R.drawable.was_logo;
		team.color_main = R.color.WIZARDS_RED;
		team.color_secondary = R.color.WIZARDS_BLUE;
		NBATeamInfo.put("Washington Wizards", team);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.nbastat_stream, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.live_game_button:
			startLiveGame();
			break;
		case R.id.archived_game_button:
			startArchivedGame();
			break;
		default:
			break;
		}
	}
	
	private void startLiveGame() {
		Log.d(TAG, "Clicked on live game.");
		Intent intent = new Intent(this, LiveGame.class);
		startActivity(intent);
	}
	
	private void startArchivedGame() {
		Log.d(TAG, "Clicked on archived game.");
		Intent intent = new Intent(this, ArchivedGame.class);
		startActivity(intent);
	}

}
